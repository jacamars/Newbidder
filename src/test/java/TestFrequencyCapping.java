package test.java;

import com.hazelcast.core.HazelcastInstance;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.*;
import com.jacamars.dsp.rtb.pojo.Bid;
import com.jacamars.dsp.rtb.shared.BidCachePool;
import com.jacamars.dsp.rtb.shared.FrequencyGoverner;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;


import static org.junit.Assert.*;


/**
 * A class to test all aspects of the win processing.
 * @author Ben M. Faul
 *
 */
public class TestFrequencyCapping {
	/**
	 * Setup the RTB server for the test
	 */

	static String WIN_PRICE = ".0001";

	
	static String password;

	static String CAP_KEY = "capped_block-test166.137.138.18";
	
	static HazelcastInstance inst;

	@BeforeClass
	public static void setup() {
		try {

			Config.setup();
			System.out.println("******************  TestFrequencyCapping");
			password = Configuration.getInstance().password;
			inst = RTBServer.getSharedInstance();
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
	}

	  /**
	   * Test a valid bid response with no bid, the campaign doesn't match width or height of the bid request
	   * @throws Exception on network errors.
	   */
	  @Test 
	  public void testCappingTimes3() throws Exception {
			BidCachePool.getInstance().del(CAP_KEY);
			BidCachePool.getInstance().deleteBid("123");
			
			long x = BidCachePool.getInstance().getAsNumber(CAP_KEY);
			System.out.println("=====>" + x);
			assertTrue(x == 0);
			
			for (Campaign c : Configuration.getInstance().getCampaignsListReal()) {
				if (c.adId.equals("block-test")) {
					c.frequencyCap.capFrequency = 3;
					break;
				}
			}
			
			HttpPostGet http = new HttpPostGet();
			String bid = Charset
					.defaultCharset()
					.decode(ByteBuffer.wrap(Files.readAllBytes(Paths
							.get("./SampleBids/nexage50x50.txt")))).toString();
			
			// Get 3 times is ok, but 4th is a no bid
			String s = http.sendPost("http://" + Config.testHost + "/rtb/bids/index", bid, 100000, 100000);
			assertNotNull(s);
			int rc = http.getResponseCode();
			assertTrue(rc==200);
			boolean b = BidCachePool.getInstance().getBid("123") != null;
			assertTrue(b);
			
			x = BidCachePool.getInstance(inst).getAsNumber(CAP_KEY);
			assertTrue(x == 0);
			
			
			Bid win = new Bid(s);
			String repl = win.nurl.replaceAll("\\$", "");
			win.nurl = repl.replace("{AUCTION_PRICE}", WIN_PRICE);
			s = http.sendPost(win.nurl, "",100000,100000);
			x = BidCachePool.getInstance(inst).getAsNumber(CAP_KEY);
			
			System.out.println("--------->X: " + x);
			assertTrue(x==1);
			
			
			s = http.sendPost("http://" + Config.testHost + "/rtb/bids/index", bid, 100000, 100000);
			assertNotNull(s);
			rc = http.getResponseCode();
			assertTrue(rc==200);
			s = http.sendPost(win.nurl, "",100000,100000);
			
			x = BidCachePool.getInstance(inst).getAsNumber(CAP_KEY);
			assertTrue(x==2);
			
			s = http.sendPost("http://" + Config.testHost + "/rtb/bids/index", bid, 100000, 100000);
			assertNotNull(s);
			rc = http.getResponseCode();
			assertTrue(rc==200);


			s = http.sendPost(win.nurl, "",100000,100000);

			Thread.sleep(1000);
			x = (Long)BidCachePool.getInstance(inst).get(CAP_KEY);
			assertTrue(x==3);
			
			// better no bid.
			s = http.sendPost("http://" + Config.testHost + "/rtb/bids/index", bid, 100000, 100000);
			rc = http.getResponseCode();
			assertTrue(rc==204);
			assertNull(s);
			rc = http.getResponseCode();
			
		    x = (Long)BidCachePool.getInstance(inst).get(CAP_KEY);
			assertTrue(x==3);

			System.out.println("DONE!");
		}

	/**
	 * Test a valid bid response with no bid, the campaign doesn't match width or height of the bid request
	 * @throws Exception on network errors.
	 */
	@Test
	public void testFrequencyGovernance() throws Exception {

		// Delete the frequency CAP first
		BidCachePool.getInstance(inst).del(CAP_KEY);


		// Make sure cap is > 3
		for (Campaign c : Configuration.getInstance().getCampaignsListReal()) {
			if (c.adId.equals("block-test")) {
				c.frequencyCap.capFrequency = 3;
				break;
			}
		}

		// Wait for the keys to expire before turning it on.
		Thread.sleep(5000);
		FrequencyGoverner.silent = true;
		// Make a bid
		HttpPostGet http = new HttpPostGet();
		String bid = Charset
				.defaultCharset()
				.decode(ByteBuffer.wrap(Files.readAllBytes(Paths
						.get("./SampleBids/nexage50x50.txt")))).toString();

		// Bid once using the bid request.
		String s = http.sendPost("http://" + Config.testHost + "/rtb/bids/index", bid, 100000, 100000);
		assertNotNull(s);

		// This second requestone should return no bid if the governer works.
		s = http.sendPost("http://" + Config.testHost + "/rtb/bids/index", bid, 100000, 100000);
		assertNull(s);

		// Wait for the Governer to clear.
		Thread.sleep(1000);

		/////////////////////// NOW TEST THAT IT WILL BID ////////////////////////////
		BidCachePool.getInstance(inst).del(CAP_KEY);
		s = http.sendPost("http://" + Config.testHost + "/rtb/bids/index", bid, 100000, 100000);
		assertNotNull(s);

		// Wait for it to clear out
		Thread.sleep(1000);

		// better bid, it timed out by now.
		s = http.sendPost("http://" + Config.testHost + "/rtb/bids/index", bid, 100000, 100000);
		assertNotNull(s);

		FrequencyGoverner.silent = true;
	}

	@Test
	  public void testCappingTimes1() throws Exception {
		BidCachePool.getInstance(inst).del(CAP_KEY);
			
			
			for (Campaign c : Configuration.getInstance().getCampaignsListReal()) {
				if (c.adId.equals("block-test")) {
					c.frequencyCap.capFrequency = 1;
					break;
				}
			}
			
			
			HttpPostGet http = new HttpPostGet();
			String bid = Charset
					.defaultCharset()
					.decode(ByteBuffer.wrap(Files.readAllBytes(Paths
							.get("./SampleBids/nexage50x50.txt")))).toString();
			
			// Get 1 time is ok, but 2d time is a no bid
			String s = http.sendPost("http://" + Config.testHost + "/rtb/bids/index", bid, 1000000, 1000000);
			assertNotNull(s);
			int rc = http.getResponseCode();
			assertTrue(rc==200);
			boolean b = BidCachePool.getInstance().getBid("123") != null;
			assertTrue(b);

			Bid win = new Bid(s);
			String repl = win.nurl.replaceAll("\\$", "");
			win.nurl = repl.replace("{AUCTION_PRICE}", WIN_PRICE);
			
			System.out.println(win.nurl);
			s = http.sendPost(win.nurl, "",300000,300000);
			long x = (Long)BidCachePool.getInstance(inst).get(CAP_KEY);
			assertTrue(x==1);
			
			// better no bid.
			s = http.sendPost("http://" + Config.testHost + "/rtb/bids/index", bid, 100000, 100000);
			rc = http.getResponseCode();
			assertTrue(rc==204);
			assertNull(s);
			rc = http.getResponseCode();
			
		    x = (Long)BidCachePool.getInstance(inst).get(CAP_KEY);
			assertTrue(x==1);

		}

	@Test
		public void testDates() throws Exception {
	  		FrequencyCap.returnTimeout("2020-01-01 12:00");
	  		try {
				FrequencyCap.returnTimeout("garbage");
				fail("This should not have worked");
			} catch (Exception error) {

			}
		}

}

