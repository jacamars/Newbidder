package test.java;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import com.jacamars.dsp.rtb.bidder.DeadmanSwitch;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.shared.BidCachePool;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests whether the bidders will stop if the accounting deadman switch is deleted in Redis
 * @author Ben M. Faul
 *
 */
public class TestDeadmanSwitch {


    @BeforeClass
    public static void setup() {
        try {
            Config.setup();
            System.out.println("******************  TestDeamanSwitch");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Shut the RTB server down.
     */
    @AfterClass
    public static void testCleanup() {
        Config.teardown();
        System.out.println("We are done!");
    }
	@Test 
	public void testSwitch() throws Exception {

			DeadmanSwitch.testmode = true;
			
			BidCachePool.getInstance(RTBServer.getSharedInstance()).del("deadmanswitch");
			
			DeadmanSwitch d = new DeadmanSwitch("deadmanswitch");
			Thread.sleep(1000);
			assertFalse(d.canRun());
			BidCachePool.getInstance(RTBServer.getSharedInstance()).set("deadmanswitch", "ready",5);
			
			assertTrue(d.canRun());
			Thread.sleep(10000);
			assertFalse(d.canRun());
		}
	
}
