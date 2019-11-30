package com.jacamars.dsp.rtb.jmq;


import com.amazonaws.services.kinesis.model.*;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.jacamars.dsp.rtb.shared.BidCachePool;

import io.krakens.grok.api.Grok;
import io.krakens.grok.api.GrokCompiler;
import io.krakens.grok.api.Match;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import org.eclipse.jetty.server.Server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Subscriber. This CONNECTS to a publisher. It is S (many) -> P (single)
 * 
 * @author ben
 *
 */
public class Subscriber extends TailerListenerAdapter implements Runnable, SubscriberIF, ConsumerRebalanceListener {

	public static ObjectMapper mapper = new ObjectMapper();

	Context context = JMQContext.getInstance();
	EventIF handler;
	Socket subscriber;
	Thread me;

	KafkaConsumer<String, String> consumer;
	String topic;

	JedisPoolConfig poolConfig;
	JedisPool jedisPool;
	Jedis subscriberJedis;
	JedisSubscriber jedisSubscriber;

	HazelcastInstance inst;
	HazelcastSubscriber hazel;

	PipeTailer piper;

    WebHandler webHandler;

    Tailer tailer;
    int delay = 100;
    boolean reopen = false;
    boolean end = true;
    int bufsize = 4096;
    String address;

	public static GrokCompiler grokCompiler = GrokCompiler.newInstance();
	static {
		grokCompiler.registerDefaultPatterns();
	}
	public Grok grok;

	public static void main(String... args) {
		// Prepare our context and subscriber
	}

	public Subscriber() {

	}


	/**
	 * A subscriber using Kafka
	 * @param handler EventIF, the classes that will get the messages.
	 * @param props Properties. The kafka properties
	 * @throws Exception on Errors setting up with Kafka
	 */
	public Subscriber(EventIF handler, Properties props, String topic) throws Exception {
		this.handler = handler;
		consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList(topic), this);
		this.topic = topic;

		me = new Thread(this);
		me.start();
	}
	
	/**
	 * A subscriber using Hazelcast
	 * @param handler EventIF, the classes that will get the messages.
	 * @param props Properties. The kafka properties
	 * @throws Exception on Errors setting up with Kafka
	 */
	public Subscriber(HazelcastInstance inst, EventIF handler, String address) throws Exception {
		this.inst = inst;
		this.handler = handler;
		this.address = address;
	}

	public Subscriber(EventIF handler, String address) throws Exception {
		this.handler = handler;
		this.address = address;
		init();
	}
		
	private void init() throws Exception {

		if (address.equals("loopback")) {
			this.topic = "loopback";
			runLoopBack();
			return;
		}

		if (address.startsWith("kinesis://")) {
			processKineses(address);
			return;
		}

		if (address.startsWith("file://")) {
		    processLogTailer(address);
		    return;
        }

        if (address.startsWith("pipe://")) {
		    processPipeTailer(address);
		    return;
        }

        if (address.startsWith("redis://")) {
        	processRedis(address);
        	return;
		}

        if (address.startsWith("http://")) {
        	processHttp(address);
        	return;
		}

        if (address.startsWith("hazelcast://")) {
        	processHazel(address);
        	return;
		}

		if (address.startsWith("zeromq://")) {
			subscriber = context.socket(ZMQ.SUB);
			subscriber.connect(address);
			subscriber.setHWM(100000);
		}

		if (address.startsWith("kafka")) {
			if (address.contains("groupid")==false) {
                address += "&groupid=a";
            }
			KafkaConfig c = new KafkaConfig(address);
			consumer = new KafkaConsumer<>(c.getProperties());
			consumer.subscribe(Collections.singletonList(c.getTopic()), this);

			this.topic = c.topic;
		}
		me = new Thread(this);
		me.start();
	}

	public void setGrok(String pattern) {
		grok = grokCompiler.compile(pattern);
		if (jedisSubscriber != null)
			jedisSubscriber.setGrok(grok);
		if (webHandler != null)
		    webHandler.setGrok(grok);
	}

	String useGrok(String text) {

		try {
			if (grok != null) {
				Match gm = grok.match(text);
				return mapper.writeValueAsString(gm.capture());
			}
		} catch (Exception error) {
			error.printStackTrace();
		}
		return text;

	}

	public void runLoopBack() {
		Runnable t = () -> {
			while(!Thread.interrupted()) {
				try {
					Thread.sleep(1000);
				} catch (Exception error) {
					return;
				}
			}
		};
		me = new Thread(t);
		me.start();
	}

	public String getTopic() {
		return topic;
	}

	public String getAddress() {
		return address;
	}

    /**
     * Subscribe to the topic - used by 0mq.
     * @param topic
     */
	public void subscribe(String topic) {
		if (consumer != null) {
		//	consumer.subscribe(Collections.singletonList(topic), this);
		} else
			subscriber.subscribe(topic.getBytes());
	}

	void processKineses(String address) throws Exception {
		KinesisConfig cfg = new KinesisConfig(address);

		GetShardIteratorRequest getShardIteratorRequest = new GetShardIteratorRequest();
		getShardIteratorRequest.setStreamName(cfg.getStream());
		getShardIteratorRequest.setShardId(cfg.getShard());
		getShardIteratorRequest.setShardIteratorType(cfg.getIterator_type());
		GetShardIteratorResult getShardIteratorResult = cfg.getKinesis().getShardIterator(getShardIteratorRequest);
		String shardIterator = getShardIteratorResult.getShardIterator();

		GetRecordsRequest getRecordsRequest = new GetRecordsRequest();
		getRecordsRequest.setShardIterator(shardIterator);
		getRecordsRequest.setLimit(cfg.getRecord_limit());

		topic = cfg.getStream();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
                    GetRecordsResult result;
                    GetShardIteratorResult res;
                    String si;
                    while (true) {
                        result = cfg.getKinesis().getRecords(getRecordsRequest);

                        List<Record> records = result.getRecords();
                        if (records.size() == 0)
                            Thread.sleep(cfg.getSleep_time());
                        else {

                            res = cfg.getKinesis().getShardIterator(getShardIteratorRequest);
                            si = res.getShardIterator();
                            getRecordsRequest.setShardIterator(si);


                            for (Record r : records) {
                                //System.out.println(r.getSequenceNumber());
                                //System.out.println(r.getPartitionKey());
								byte [] bytes = null;
								if (grok != null) {
									String str = new String(r.getData().array());
									str = useGrok(str);
									bytes = str.getBytes();
								} else
                                	bytes = r.getData().array();
                                handle(new String(bytes));
                                //System.out.println(new String(bytes));
                            }
                        }
                    }
                } catch (Exception e) {
					handler.shutdown();
				}
				}
			}).start();

	}

	void processHttp(String address) throws Exception {
		String saved = address;
		address = address.substring(7);
		int port = 8080;
		String parts [] = address.split("&");
		for (int i=0;i<parts.length;i++) {
			String t[] = parts[i].trim().split("=");
			if (t.length != 2) {
				throw new Exception("Not a proper parameter (a=b)");
			}
			t[0] = t[0].trim();
			t[1] = t[1].trim();
			switch(t[0]) {
				case "port":
					port = Integer.parseInt(t[1].trim());
					break;
			}
		}

		webHandler = new WebHandler(saved,port, handler,grok);
	}

	void processHazel(String address) throws Exception {
		String saved = address;
		address = address.substring(12);
		int port = 8080;
		String parts [] = address.split("&");
		for (int i=0;i<parts.length;i++) {
			String t[] = parts[i].trim().split("=");
			if (t.length != 2) {
				throw new Exception("Not a proper parameter (a=b)");
			}
			t[0] = t[0].trim();
			t[1] = t[1].trim();
			switch(t[0]) {
				case "topic":
					topic = t[1].trim();
					break;
				default:
			}
		}

		hazel = new HazelcastSubscriber(inst,handler,topic,grok);
	}

	void processRedis(String address) throws Exception {
		poolConfig = new JedisPoolConfig();
		List<String> channel = new ArrayList();
		String auth = null;
		String host = "localhost";
		int port = 6379;
		int timeout = 0;

		address = address.substring(8);
		String parts [] = address.split("&");
		for (int i=1;i<parts.length;i++) {
			String t[] = parts[i].trim().split("=");
			if (t.length != 2) {
				throw new Exception("Not a proper parameter (a=b)");
			}
			t[0] = t[0].trim();
			t[1] = t[1].trim();
			switch(t[0]) {
				case "auth":
					auth = t[1].trim();
					break;
				case "host":
					host = t[1].trim();
					break;
				case "port":
					port = Integer.parseInt(t[1].trim());
					break;
				case "topic":
					topic = t[1].trim();
					channel.add(topic);
					break;
				case "timeout":
					timeout = Integer.parseInt(t[1].trim());
					break;
			}
		}

		jedisPool = new JedisPool(poolConfig, host,port,timeout);

		subscriberJedis = jedisPool.getResource();
		if (auth != null)
			subscriberJedis.auth(auth);
		jedisSubscriber = new JedisSubscriber(handler,grok);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					subscriberJedis.subscribe(jedisSubscriber, channel.get(0));
					handler.shutdown();
				} catch (Exception e) {
					handler.shutdown();
				}
			}
		}).start();

	}

    /**
     * Setup a file tailer.
     * @param buf String. The Address buffer.
     * @throws Exception on setup exception.
     */
	void processLogTailer(String buf) throws Exception {
       // String buf = "file://log.txt&whence=eof&delay=500&reopen=false&bufsize=4096";
        buf = buf.substring(7);
        String parts [] = buf.split("&");
        String fileName = parts[0];
        for (int i=1;i<parts.length;i++) {
            String t[] = parts[i].trim().split("=");
            if (t.length != 2) {
                throw new Exception("Not a proper parameter (a=b)");
            }
            t[0] = t[0].trim();
            t[1] = t[1].trim();
            switch(t[0]) {
                case "whence":
                    if (t[1].equalsIgnoreCase("bof"))
                        end = false;
                    else
                        end = true;
                    break;
                case "delay":
                    delay = Integer.parseInt(t[1]);
                    break;
                case "reopen":
                    reopen = Boolean.parseBoolean(t[1]);
                    break;
                case "bufsize":
                    bufsize = Integer.parseInt(t[1]);
                    break;
            }
        }
        topic = fileName;
        tailer = Tailer.create(new File(fileName), this, delay, end, reopen, bufsize  );
	}

    /**
     * Process a named pipe
     * @param fileName String. The name of the pipe.
     * @throws Exception if the file does not exist or is not a file.
     */
	public void processPipeTailer(String fileName) throws Exception {
	    fileName = fileName.substring(7);
	    topic = fileName;
        piper = new PipeTailer(this, fileName);
    }

    public void setHandler(EventIF h) {
		handler = h;
		if (jedisSubscriber != null) {
			jedisSubscriber.setHandler(h,grok);
		}
		if (webHandler != null) {
			webHandler.setHandler(h,grok);
		}
		if (hazel != null) {
			hazel.setHandler(h,grok);
		}
	}

    /**
     * Handle a file tailer entry.
     * @param line String. The line we got from the file.
     */
    @Override
    public void handle(String line) {
    	line = useGrok(line);
        handler.handleMessage("", line.trim());
    }

    /**
     * Catch fileNotFound - can happen on log rotation
     */
    @Override
    public void fileNotFound() {

    }

    /**
     * Catch file rotated event.
     */
    @Override
    public void fileRotated() {

    }

    /**
     * Handle an error, this probably fatal
     * @param ex
     */
    @Override
    public void handle(Exception ex) {
        ex.printStackTrace();
    }


    /**
     * Handles 0MQ and kafka. File subscriber handled by the tailer.
     */
	@Override
	public void run() {

		if (consumer != null) {
			while(true) {
				ConsumerRecords<String, String> records = consumer.poll(1000);
				for (ConsumerRecord<String, String> record : records) {
					String line = useGrok(record.value());
					handler.handleMessage(topic,line);
				}

				if (consumer == null)
					return;
				if (!records.isEmpty())
					consumer.commitSync();
			}
		}

		while (me.isInterrupted()==false) {
			// Read envelope with address
			String address = subscriber.recvStr();
			// Read message contents
			String contents = subscriber.recvStr();
			contents = useGrok(contents);
			handler.handleMessage(address, contents);
		}

        if (subscriber != null)
            subscriber.close();
        if (consumer != null) {
            consumer.close();
            consumer = null;
        }
	}

    /**
     * Signal the thread it is time to shutdown
     */
	public void shutdown() {

	    if (tailer != null) {
	        tailer.stop();
	        return;
        }

        if (piper != null) {
	        piper.stop();
	        return;
        }

        if (me != null)
        	me.interrupt();
	}
	
	public void close() {
		shutdown();
	}

	@Override
	public void onPartitionsRevoked(Collection<TopicPartition> collection) {

	}

	@Override
	public void onPartitionsAssigned(Collection<TopicPartition> collection) {

	}
}

class HazelcastSubscriber implements com.hazelcast.core.MessageListener<String> {

	Thread me;
	EventIF handler;
	Grok grok;
	String topic;
	ITopic<String> itopic;
	HazelcastInstance inst;
	public HazelcastSubscriber(HazelcastInstance inst, EventIF handler, String topic, Grok grok) {
		this.handler = handler;
		this.grok = grok;
		this.inst = inst;

		ITopic<String> itopic = BidCachePool.getInstance(inst).getReliableTopic(topic);
		itopic.addMessageListener(this);
	}

	public void setGrok(Grok grok) {
		this.grok = grok;
	}

	public void setHandler(EventIF handler, Grok grok) {
		this.handler = handler;
		this.grok = grok;
	}

	@Override
	public void onMessage(Message<String> message) {
		String str = message.getMessageObject();
		if (str.length()==0 || str.equals("{}"))
			return;
		if (grok != null) {
			Match gm = grok.match(str);
			Map<String,Object> m = gm.capture();
			try {
				str = Subscriber.mapper.writeValueAsString(m);
			} catch (Exception error) {
				error.printStackTrace();
			}
		}
		handler.handleMessage(topic,str);
	}
}

class JedisSubscriber extends JedisPubSub {
	EventIF handler;
	Grok grok;

	public void setHandler(EventIF handler, Grok grok) {
		this.handler = handler;
		this.grok = grok;
	}

	public JedisSubscriber(EventIF handler, Grok grok) {
		this.handler = handler;
		this.grok = grok;
	}

	public void setGrok(Grok grok) {
		this.grok = grok;
	}

	String useGrok(String text) {

		if (text.contains("Ping")) {
			System.out.println("HERE");
		}
		try {
			if (grok != null) {
				Match gm = grok.match(text);
				Map<String,Object> m = gm.capture();
				text = Subscriber.mapper.writeValueAsString(m);
			}
		} catch (Exception error) {
			error.printStackTrace();
		}
		return text;

	}

	@Override
	public void onMessage(String channel, String message) {
		if (message.length()==0 || message.equals("{}"))
			return;
		message = useGrok(message);
		handler.handleMessage(channel,message);
	}

	@Override
	public void onPMessage(String pattern, String channel, String message) {

	}

	@Override
	public void onSubscribe(String channel, int subscribedChannels) {

	}

	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {

	}

	@Override
	public void onPUnsubscribe(String pattern, int subscribedChannels) {

	}

	@Override
	public void onPSubscribe(String pattern, int subscribedChannels) {

	}
}

////////////////////

class WebHandler implements Runnable  {
	Server server;
	Thread me;
	Grok grok;
	SimpleHandler simple;

	public WebHandler(String address, int port, EventIF handler, Grok grok) {
		server = new Server(port);
		simple = new SimpleHandler(address,handler,grok);
		server.setHandler(simple);
		this.grok = grok;
		me = new Thread(this);
		me.start();
	}

	public void setHandler(EventIF handler, Grok grok) {
		simple.setHandler(handler,grok);
		this.grok = grok;
	}

	public void run() {
		try {
			server.start();
			server.join();
		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	public void setGrok(Grok grok) {
		this.grok = grok;
		simple.setGrok(grok);
	}
}

 class SimpleHandler extends AbstractHandler  {

	 EventIF handler;
	 String topic;
	 Grok grok;

	 public SimpleHandler(String topic, EventIF handler, Grok grok) {
	 	this.topic=topic;
	 	this.handler = handler;
	 	this.grok = grok;
	 }

	 public void setHandler(EventIF handler, Grok grok) {
		 this.handler = handler;
		 this.grok = grok;
	 }

	 public void setGrok(Grok grok) {
	 	this.grok = grok;
	 }

	@Override
	public void handle( String target,
						Request baseRequest,
						HttpServletRequest request,
						HttpServletResponse response ) throws IOException, ServletException
	{
		response.setContentType("application/json; charset=utf-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Headers", "Content-Type");

		InputStream body = request.getInputStream();
		response.setStatus(HttpServletResponse.SC_OK);

		if (request.getHeader("Content-Encoding") != null && request.getHeader("Content-Encoding").equals("gzip"))
			body = new GZIPInputStream(body);


		if (handler != null) {
			String str = new String(IOUtils.toByteArray(body));
			String [] p = str.split("\n");
			for (String s : p) {
				handler.handleMessage(topic,useGrok(s));
			}
		}

		baseRequest.setHandled(true);
	}

	 String useGrok(String text) {

		 try {
			 if (grok != null) {
				 Match gm = grok.match(text);
				 return Subscriber.mapper.writeValueAsString(gm.capture());
			 }
		 } catch (Exception error) {
			 error.printStackTrace();
		 }
		 return text;

	 }
}