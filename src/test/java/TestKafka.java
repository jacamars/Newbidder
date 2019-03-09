package test.java;

import com.jacamars.dsp.rtb.bidder.RTBServer;

import com.jacamars.dsp.rtb.jmq.ZPublisher;


import org.junit.Test;


import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.CountDownLatch;


/**
 * A class for testing that the bid has the right parameters
 * 
 * @author Ben M. Faul
 *
 */
public class TestKafka  {


	/**
	 * Test a valid bid response.
	 * 
	 * @throws Exception
	 *             on networking errors.
	 */
	@Test
	public void testPublisherAndSubscriber() throws Exception {
		String address = "kafka://[localhost:9092]&topic=junk";

		ZPublisher z = new ZPublisher(RTBServer.getSharedInstance(),address);
        CountDownLatch latch = new CountDownLatch(2);

        List list = new ArrayList();

        com.jacamars.dsp.rtb.jmq.RTopic channel = new com.jacamars.dsp.rtb.jmq.RTopic("kafka://[localhost:9092]&topic=junk");
        channel.addListener(new com.jacamars.dsp.rtb.jmq.MessageListener<String>() {
            @Override
            public void onMessage(String channel, String data) {
                System.out.println("<<<<<<<<<<<<<<<<<" + data);
                list.add(data);
             //   latch.countDown();
            }
        });

        com.jacamars.dsp.rtb.jmq.RTopic channel2= new com.jacamars.dsp.rtb.jmq.RTopic("kafka://[localhost:9092]&topic=junk");
        channel2.addListener(new com.jacamars.dsp.rtb.jmq.MessageListener<String>() {
            @Override
            public void onMessage(String channel, String data) {
                System.out.println("================" + data);
                list.add(data);

            }
        });


        z.add("Hello world");
        z.add("Another Hello world");

        Thread.sleep(15000);
	}
}
