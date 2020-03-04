package com.jacamars.dsp.rtb.jmq;

import com.amazonaws.services.kinesis.model.PutRecordsRequest;

import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.amazonaws.services.kinesis.model.PutRecordsResult;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import com.jacamars.dsp.rtb.common.HttpPostGet;
import com.jacamars.dsp.rtb.shared.BidCachePool;
import com.jacamars.dsp.rtb.tools.JdbcTools;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.kafka.clients.producer.*;

import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A publisher for ZeroMQ, File, and Logstash/http based messages, sharable by
 * multiple threads.
 *
 * @author Ben M. Faul
 */
public class ZPublisher implements Runnable, Callback {

    static final Logger clogger = LoggerFactory.getLogger(ZPublisher.class);
    // The objects thread
    protected Thread me;
    // The connection used
    String channel;
    // The topic of messages
    Publisher logger;
    // The queue of messages
    protected ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();

    // Filename, if not using ZeroMQ
    protected String fileName;
    // The timestamp part of the name
    String tailstamp;
    // Logger time, how many minuutes before you clip the log
    protected int time;
    // count down time
    protected long countdown;
    // Strinbuilder for file ops
    volatile protected StringBuilder sb = new StringBuilder();
    // Object to JSON formatter
    protected ObjectMapper mapper;
    // Set if error occurs
    protected boolean errored = false;
    // Logging formatter yyyy-mm-dd-hh:ss part.
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");

    ReentrantLock lockA = new ReentrantLock();

    Pinger ping;

    JedisPool jedisPool;

    RestClient esClient;
    List<Macro> esIndex;

    HazelcastInstance inst;
    ITopic<String> hazel;

    // The kafka producer, when specifed
    Producer<String, String> producer;
    // The kafka topic
    String topic;
    int partition = 0;

    Connection sqlConnect = null;

    // Http endpoint
    HttpPostGet http;
    // Http url
    String url;
    // The time to buffer
    double total = 0;
    double count = 0;
    long errors = 0;
    double pe = 0;
    double bp = 0;
    double latency = 0;

    String address;

    boolean isPipe = false;
    PrintWriter pipe;
    KinesisConfig kinesis;

    String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Default constructor
     */
    public ZPublisher() {

    }

    /**
     * A non hazelcast publisher
     *
     * @param address String. The topology.
     * @param topic   String. The topic to publish to.
     * @throws Exception
     */
    public ZPublisher(String address, String topic) throws Exception {
        clogger.info("Setting zpublisher at: {} on topic: {}", address, topic);
        this.address = address;

        logger = new Publisher(address, topic);
        me = new Thread(this);
        me.start();
    }

    /**
     * The HTTP Post, Zeromq, Redis and file logging constructor.
     *
     * @param address
     * String. Either http://... or file:// form for the loggert.
     * @throws Exception
     * on file IO errors.
     */
    static int k = 0;

    public ZPublisher(HazelcastInstance inst, String address) throws Exception {
    	this.inst = inst;

        if (address == null || clogger == null) // this can happen if some sub object is not configured by the top level logger
            return;

        this.address = address;
        clogger.info("Setting zpublisher at: {}", address);
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        if (address.startsWith("kinesis://")) {
            doKineses(address);
        } else if (address.startsWith("hazelcast://")) {
            doHazelcast(address);
        } else
        if (address.startsWith("kafka://")) {
            doKafka(address);
        } else if (address.startsWith("file://")) {
            int i = address.indexOf("file://");
            if (i > -1) {
                address = address.substring(7);
                String[] parts = address.split("&");
                if (parts.length > 1) {
                    address = parts[0];
                    String[] x = parts[1].split("=");
                    time = Integer.parseInt(x[1]);
                    time *= 60000;
                    setTime();
                }
            }
            if (address.contains("/")) {
                File f = new File(address);
                f.getParentFile().mkdirs();      // make any directories if they are needed
            }

            this.fileName = address;
        } else if (address.startsWith("pipe://")) {
            address = address.substring(7);
            this.topic = address;
            isPipe = true;
        } else if (address.startsWith("redis")) {
            doRedis(address);
        } else if (address.startsWith("jdbc://")) {
            doJdbc(address);
        } else if (address.startsWith("elastic")) {
            doElastic(address);
        } else if (address.startsWith("http")) {
            http = new HttpPostGet();
            int i = address.indexOf("&");
            if (i > -1) {
                address = address.substring(0, i);
                String[] parts = address.split("&");
                if (parts.length > 1) {
                    String[] x = parts[1].split("=");
                    time = Integer.parseInt(x[1]);
                }
            } else {
                url = address;
                time = 100;
            }
        } else {
            String[] parts = address.split("&");
            try {
                logger = new Publisher(parts[0], parts[1]);
                topic = parts[1];
            } catch (Exception e) {
                clogger.error("Can't open 0MQ channel {}/{} because: {}", parts[0], parts[1], e.toString());
                throw e;
            }
        }
        me = new Thread(this);
        me.start();

        // If it's not a kafka thingie, then create a pinger.
        if (producer == null && !isPipe)
            ping = new Pinger(this);
    }

    public void close() {
        me.interrupt();
    }

    public String getTopic() {
        return topic;
    }

    public String getAddress() {
        return address;
    }

    // kafka://[a:b,b:c]&topic=bids&partition=0
    void doKafka(String saddress) throws Exception {
        KafkaConfig c = new KafkaConfig(saddress);
        Properties props = c.getProperties();
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);

        if (c.getTopic() == null)
            throw new Exception("Kafka publisher needs a topic: " + saddress);

        topic = c.getTopic();
        producer = new KafkaProducer<String, String>(props);
    }

    void doJdbc(String address) throws Exception {
        address = address.substring(7);
        var props = address.split("&");
        String class4name = null;
        String type = null;
        String host = null;
        String db = null;
        String user = null;
        String password = null;

        for (int i = 0; i < props.length; i++) {
            String[] q = props[i].split("=");
            q[0] = q[0].trim();
            q[1] = q[1].trim();
            switch (q[0]) {
                case "class":
                    class4name = q[1];
                    break;
                case "type":
                    type = q[1];
                    break;
                case "host":
                    host = q[1];
                    break;
                case "db":
                    db = q[1];
                    break;
                case "user":
                    user = q[1];
                    break;
                case "password":
                    password = q[1];
                    break;
                case "index":
                case "topic":
                    topic = q[1];
                    break;
                default:
            }

            String str = "type://" + host + "/" + db;
            if (user != null)
                str += "?" + user;
            if (password != null)
                str += "?" + password;

            Class.forName("com.mysql.jdbc.Driver");

            // Get the connection
            DriverManager.setLoginTimeout(20);
            sqlConnect = DriverManager.getConnection(str);
        }
    }

    void doElastic(String address) throws Exception {
        address = address.substring(10).trim();


        int i = address.indexOf("&");
        String hosts = address.substring(1, i - 1).trim();

        topic = address.substring(i + 1);
        String[] q = topic.split("=");
        topic = q[1];
        q = hosts.split(",");
        int[] p = new int[q.length];
        String[] h = new String[q.length];

        for (i = 0; i < q.length; i++) {
            q[i] = q[i].trim();
            String[] s = q[i].split(":");
            h[i] = s[i];
            p[i] = Integer.parseInt(s[1]);
        }

        /**
         * Create the index. If pair.getValue() returns true it means this is an object substitution
         */
        String[] tokens = topic.split("/");
        esIndex = new ArrayList();
        for (var s : tokens) {
            if (s.length() > 0) {
                Macro macro = new Macro(s);
                esIndex.add(macro);
            }
        }

        esClient = RestClient.builder(
                new HttpHost(h[0], p[0], "http")).build();

    }

    void doRedis(String saddress) throws Exception {
        JedisPoolConfig config = new JedisPoolConfig();
        RedisConfig c = new RedisConfig(saddress);
        topic = c.getTopic();
        if (c.getAuth() != null)
            jedisPool = new JedisPool(config, c.getHost(), c.getPort(), c.getTimeout(), c.getAuth());
        else
            jedisPool = new JedisPool(config, c.getHost(), c.getPort(), c.getTimeout());
    }

    void doHazelcast(String address) throws Exception {
        address = address.substring(12);
        var props = address.split("&");

        for (int i = 0; i < props.length; i++) {
            String[] q = props[i].split("=");
            q[0] = q[0].trim();
            q[1] = q[1].trim();
            switch (q[0]) {
                case "topic":
                    topic = q[1];
                    break;
                default:
            }
            hazel = BidCachePool.getInstance(inst).getReliableTopic(topic);
        }
    }

    void doKineses(String address) throws Exception {
        kinesis = new KinesisConfig(address);
    }

    /**
     * Set the countdown timer when used for chopping off the current log and
     * making a new one.
     */
    void setTime() {
        countdown = System.currentTimeMillis() + time;
    }

    public Map getBp() {
        Map m = null;
        if (http == null)
            return null;

        if (errors != 0) {
            pe = 100 * errors / count;
        }
        if (count != 0) {
            bp = total / (count * this.time);
            latency = total / count;

        }

        m = new HashMap();
        m.put("url", url);
        m.put("latency", latency);
        m.put("wbp", bp);
        m.put("errors", errors);

        total = count = errors = 0;
        return m;
    }

    /**
     * Run the http post logger.
     */
    public void runHttpLogger() {
        Object obj = null;

        long elapsed = System.currentTimeMillis();
        String errorString = null;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        while (!me.isInterrupted()) {
            try {
                Thread.sleep(this.time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                if (sb.length() != 0) {
                    try {
                        if (lockA.tryLock(10, TimeUnit.SECONDS)) {
                            count++;
                            long time = System.currentTimeMillis();
                            http.sendPost(url, sb.toString());
                            int code = http.getResponseCode();
                            if (code == 200) {
                                time = System.currentTimeMillis() - time;
                                total += time;
                            } else {
                                errors++;
                            }
                        } else {
                            clogger.error("Erorr writing data to HTTP");
                        }

                    } catch (Exception error) {
                        // error.printStackTrace();
                        errorString = error.toString();
                        errors++;
                    }
                    sb.setLength(0);
                    sb.trimToSize();
                }
            } catch (Exception error) {
                errored = true;
                errors++;
                errorString = error.toString();
                // error.printStackTrace();
                sb.setLength(0);
            } finally {
                if (lockA.isHeldByCurrentThread()) lockA.unlock();
            }
        }
    }

    public String serialize(Object msg) {
        if (msg instanceof String)
            return (String) msg;
        return Tools.serialize(mapper, msg);
    }

    public void runPipeLogger() {

        PipeWriter p;
        try {
            p = new PipeWriter(topic);
        } catch (Exception error) {
            clogger.error("Pipe is broken for {}, error: {}", topic, error.getMessage());
            return;
        }
        while (!me.isInterrupted()) {
            try {
                Object msg;
                while ((msg = queue.poll()) != null) {
                    if (ping != null)
                        ping.cancelPing();
                    String str = serialize(msg);
                    p.write(str);
                }
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
                // return;
            }
        }
    }

    /**
     * Run the kineses logger in a loop
     */
    public void runKinesisLogger() {
        Object msg = null;
        String str = null;
        int i;
        List <PutRecordsRequestEntry> putRecordsRequestEntryList = new ArrayList<>();
        while (!me.isInterrupted()) {
            try {
                if ((msg = queue.poll()) != null) {
                    i = 1;
                    PutRecordsRequest putRecordsRequest = null;
                    while(msg != null) {
                        str = serialize(msg);
                        var bytes = str.getBytes();
                        PutRecordsRequestEntry putRecordsRequestEntry  = new PutRecordsRequestEntry();
                        putRecordsRequestEntry.setPartitionKey(kinesis.getPartition());
                        putRecordsRequestEntry.setData(ByteBuffer.wrap(bytes));
                        putRecordsRequestEntryList.add(putRecordsRequestEntry);

                        if (i++ == 100)
                            msg = null;
                        else
                            msg = queue.poll();
                    }
                    putRecordsRequest.setRecords(putRecordsRequestEntryList);
                    PutRecordsResult putRecordsResult  = kinesis.getKinesis().putRecords(putRecordsRequest);
                    putRecordsRequestEntryList.clear();
                }
                Thread.sleep(1);


                /*while ((msg = queue.poll()) != null) {
                    str = serialize(msg);
                    var bytes = str.getBytes();
                    PutRecordRequest putRecord = new PutRecordRequest();
                    putRecord.setStreamName(kinesis.getStream());
                    putRecord.setPartitionKey(kinesis.getPartition());
                    putRecord.setData(ByteBuffer.wrap(bytes));

                    try {
                        kinesis.getKinesis().putRecord(putRecord);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                Thread.sleep(1); */
            } catch (Exception e) {
                e.printStackTrace();
                // return;
            }
        }
    }

    public void runHazel() {
        Object msg;
        while (!me.isInterrupted()) {
            try {
                while ((msg = queue.poll()) != null) {
                    if (ping != null)
                        ping.cancelPing();
                    if (msg instanceof Ping == false) {
                    	String str = (String) msg;
                    	hazel.publish(str);
                    }
                }
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
                // return;
            }
        }
    }

    /**
     * Run the kafka logger in a loop
     */
    public void runKafkaLogger() {
        Object msg = null;
        String str = null;
        while (!me.isInterrupted()) {
            try {
                while ((msg = queue.poll()) != null) {
                    if (ping != null)
                        ping.cancelPing();
                    str = serialize(msg);
                    ProducerRecord record = new ProducerRecord<String, String>(topic, "key", str);
                    producer.send(record, this);
                }
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
                // return;
            }
        }
    }

    /**
     * Is the queue empty.
     *
     * @return boolean. Returns true if empty else false.
     */
    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    /**
     * Run the file logger in a loop.
     */
    public void runFileLogger() {
        Object obj = null;

        String thisFile = this.fileName;

        if (countdown != 0) {
            tailstamp = "-" + sdf.format(new Date());
            thisFile += tailstamp;
        } else
            tailstamp = "";

        while (!me.isInterrupted()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (sb.length() != 0) {
                try {
                    if (lockA.tryLock(10, TimeUnit.SECONDS) == false) {
                        clogger.error("Error can'tobtain lock on write log for: {}", thisFile);
                    }
                    AppendToFile.item(thisFile, sb);
                } catch (Exception error) {
                    errored = true;
                    clogger.error("Publisher log error on {}: {}", fileName, error.toString());
                    error.printStackTrace();
                } finally {
                    sb.setLength(0);
                    sb.trimToSize();
                    if (lockA.isHeldByCurrentThread()) lockA.unlock();
                }
            }
            if (countdown != 0 && System.currentTimeMillis() > countdown) {
                thisFile = this.fileName + tailstamp;

                tailstamp = "-" + sdf.format(new Date());
                thisFile = this.fileName + tailstamp;
                setTime();
            }
        }
    }

    /**
     * PUBLISH writer = `{"age": 10, "dateOfBirth": 1471466076564, "fullName":"John Doe"}`;
     * The logger run method in a loop.
     */
    public void run() {
        try {

            if (hazel != null) {
                runHazel();
            }

            if (sqlConnect != null) {   // sql
                runJdbcLogger();
            }

            if (esClient != null)     // elastic search
                runElasticLogger();

            if (kinesis != null) {     // kinesis
                runKinesisLogger();
            }

            if (isPipe) {              // named pipe
                runPipeLogger();
            }

            if (producer != null)     // kafka
                runKafkaLogger();

            if (logger != null)       // jmq
                runJmqLogger();

            if (http != null)         // http
                runHttpLogger();

            if (jedisPool != null)    // redis
                runRedisLogger();

            runFileLogger();          // file
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    /**
     * Run the Redis logger in a loop.
     */
    public void runRedisLogger() throws Exception {
        Object msg = null;
        while (!me.isInterrupted()) {
            try {
                while ((msg = queue.poll()) != null) {
                    if (ping != null)
                        ping.cancelPing();
                    jedisPool.getResource().publish(topic, msg.toString());
                }
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
                // return;
            }
        }
    }

    /**
     * Run the ElasticSearch logger in a loop.
     */

    public void runJdbcLogger() throws Exception {
        Object msg = null;
        Map<String, Object> document;
        while (!me.isInterrupted()) {
            try {
                while ((msg = queue.poll()) != null) {
                    ping.cancelPing();
                    if (msg instanceof String)
                        document = mapper.convertValue((String) msg, Map.class);
                    else
                        document = (Map<String, Object>) msg;
                    String q = JdbcTools.jsonToInsert(topic, document);
                    var stmt = sqlConnect.createStatement();
                    stmt.executeUpdate(q);
                }
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
                // return;
            }
        }
    }

    public void runElasticLogger() throws Exception {
        Object msg = null;

        while (!me.isInterrupted()) {
            try {
                while ((msg = queue.poll()) != null) {
                    if (ping != null)
                        ping.cancelPing();
                    String document = null;
                    String xtopic = null;

                    if (msg instanceof String) {
                        document = (String) msg;
                        xtopic = getElasticSearchIndex(document);
                    } else {
                        document = mapper.writeValueAsString(msg);
                        xtopic = getElasticSearchIndex(msg);
                    }

                    StringBuilder topo = new StringBuilder();

                    Map<String, String> params = Collections.emptyMap();
                    HttpEntity entity = new NStringEntity(document, ContentType.APPLICATION_JSON);
                    Response response = esClient.performRequest("PUT", xtopic, params, entity);
                    System.out.println(response);

                }
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
                // return;
            }
        }
    }

    JsonNode convertToJsonNode(Object x) {
        JsonNode n = null;
        try {
            if (x instanceof JsonNode)
                n = (JsonNode) x;
            else if (x instanceof Map) {
                n = mapper.valueToTree(x);
            } else if (x instanceof String) {
                n = mapper.readTree((String) x);
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
        return n;
    }

    String getElasticSearchIndex(Object x) {
        JsonNode n = convertToJsonNode(x);


        StringBuilder sb = new StringBuilder("");
        esIndex.forEach(e -> {
            sb.append("/");
            if (e.isMacro())
                sb.append(walkTree(n, e.getParts()));
            else
                sb.append(e.getName());
        });
        if (esIndex.size() == 2) {
            sb.append("/");
            sb.append(UUID.randomUUID().toString());
        }
        return sb.toString();
    }

    String walkTree(JsonNode node, String[] name) {
        try {
            if (node == null)
                return null;


            for (int i = 0; i < name.length; i++) {
                String key = name[i];
                if (!(key.charAt(0) >= '0' && key.charAt(0) <= '9') && key.charAt(0) != '*') {

                    if (node instanceof ObjectNode == false) {
                        throw new RuntimeException("" + name[i] + " is not an object");
                    }

                    node = node.get(key);
                    if (node == null)
                        return null;

                } else {
                    if (node instanceof ArrayNode) {
                        var an = (ArrayNode) node;
                        node = an.get(Integer.parseInt(key));
                    } else
                        throw new RuntimeException("" + name[i] + " is not an array");
                }
            }
            return node.asText();
        } catch (Exception error) {
            System.err.println("Warning error in walkTree: " + name);
            return null;
        }
    }

    /**
     * Run the ZeroMQ logger in a loop.
     */
    public void runJmqLogger() {
        Object msg = null;
        while (!me.isInterrupted()) {
            try {
                while ((msg = queue.poll()) != null) {
                    if (ping != null)
                        ping.cancelPing();
                    logger.publish(msg);
                }
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
                // return;
            }
        }
    }

    /**
     * Add a message to the messages queue.
     *
     * @param s . String. JSON formatted message.
     */
    public void add(Object s) {
        if (fileName != null || http != null) {

            if (s instanceof Ping)    // don't ping files or http
                return;

            if (errored)
                return;

            String contents = null;
            try {
                contents = serialize(s);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                if (lockA.tryLock(10, TimeUnit.SECONDS)) {
                    sb.append(contents);
                    sb.append("\n");
                }
            } catch (Exception error) {
                error.printStackTrace();
            } finally {
                if (lockA.isHeldByCurrentThread()) lockA.unlock();
            }
        } else
            queue.add(s);
    }

    /**
     * Add a String to the messages queue without JSON'izing it.
     *
     * @param contents String. The string message to add.
     */
    public void addString(String contents) {
        if (producer != null) {
            ProducerRecord record = new ProducerRecord<String, String>(topic, "key", contents);
            producer.send(record, this);
            return;
        }

        if (fileName != null || http != null) {
            try {
                if (lockA.tryLock(10, TimeUnit.SECONDS)) {
                    sb.append(contents);
                    sb.append("\n");
                } else {
                    clogger.error("Error, can't obtain lock for appending data to {}", fileName);
                }
            } catch (Exception error) {
                error.printStackTrace();
            } finally {
                if (lockA.isHeldByCurrentThread()) lockA.unlock();
            }
        } else
            queue.add(contents);
    }

    /**
     * Kafka callback
     *
     * @param recordMetadata RecordMetaData. Information about the record that was sent
     * @param e              Exception. If an exception was thrown, this will be non-null.
     */

    @Override
    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
        if (e != null) {
            clogger.error("Error while producing message to topic '{}', refer: {}: {}", topic, address, e.toString());
            if (e instanceof org.apache.kafka.common.errors.TimeoutException) {
                clogger.error("Restarting to try to recover");
                System.exit(1);
            }
        }
    }

}

class PipeWriter implements Runnable {

    Thread me = null;
    String fileName;
    volatile PrintWriter pipe = null;

    public PipeWriter(String piper) throws Exception {
        fileName = piper;
        me = new Thread(this);
        me.start();
    }

    public void run() {
        while (!me.isInterrupted()) {
            try {
                if (pipe == null) {
                    FileOutputStream fos = new FileOutputStream(fileName);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    pipe = new PrintWriter(bos);
                } else {
                    Thread.sleep(1);
                }
            } catch (Exception error) {
                error.printStackTrace();
                me.interrupt();
            }
        }
    }

    public void write(String str) throws Exception {
        if (pipe == null)
            return;
        pipe.println(str);
        pipe.flush();
    }
}

class Pinger implements Runnable {
    Thread me = null;
    ZPublisher parent;
    volatile boolean cancel = true;

    public Pinger(ZPublisher parent) {
        this.parent = parent;
        me = new Thread(this);
        me.start();
    }

    public void run() {
        Ping ping = new Ping();
        while (!me.isInterrupted()) {
            try {
                if (!cancel)
                    parent.add(ping);
                cancel = false;
                Thread.sleep(60000);
            } catch (Exception error) {
                error.printStackTrace();
            }

        }
    }

    public void cancelPing() {
        cancel = true;
    }
   
}

class Macro {
    String name;
    String[] parts;
    boolean isMacro;

    public Macro(String name) {
        if (name.startsWith("$$")) {
            isMacro = true;
            name = name.substring(2);
        }
        this.name = name;
        if (isMacro)
            parts = name.split("\\\\.");
    }

    public String getName() {
        return name;
    }

    public String[] getParts() {
        return parts;
    }

    public boolean isMacro() {
        return isMacro;
    }
}
