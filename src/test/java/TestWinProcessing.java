package test.java;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.InputSource;

import com.hazelcast.core.HazelcastInstance;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.HttpPostGet;
import com.jacamars.dsp.rtb.common.RecordedBid;
import com.jacamars.dsp.rtb.pojo.Bid;
import com.jacamars.dsp.rtb.pojo.BidResponse;
import com.jacamars.dsp.rtb.pojo.WinObject;

import com.jacamars.dsp.rtb.shared.BidCachePool;


/**
 * A class to test all aspects of the win processing.
 * @author Ben M. Faul
 *
 */
public class TestWinProcessing  {
	/**
	 * Setup the RTB server for the test
	 */

	static String WIN_PRICE = ".0001";
	
	static String password;
	
	static HazelcastInstance hz;
	@BeforeClass
	public static void setup() {
		try {
			Config.setup();
			hz =  RTBServer.getSharedInstance();
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

	/**
	 * Test the basic win processing system of the RTB
	 * @throws Exception on networking problems.
	 */
	@Test
	public void testWinProcessingNexage() throws Exception  {
		HttpPostGet http = new HttpPostGet();

		BidCachePool.getInstance(hz).deleteBid("35c22289-06e2-48e9-a0cd-94aeb79fab43");
		// Make the bid
		
		String s = Charset
				.defaultCharset()
				.decode(ByteBuffer.wrap(Files.readAllBytes(Paths
						.get("./SampleBids/nexage.txt")))).toString();
		/**
		 * Send the bid
		 */
		try {
			s = http.sendPost("http://" + Config.testHost + "/rtb/bids/nexage", s, 3000000, 3000000);
		} catch (Exception error) {
			fail("Can't connect to test host: " + Config.testHost);
		}
		int code = http.getResponseCode();
		assertTrue(code==200);
		Bid bid = null;
		System.out.println(s);
		int x = s.indexOf("{bid_id");
		assertTrue(x == -1);
		x = s.indexOf("%7Bbid_id");
		assertTrue(x == -1);
		
		try {
			bid = new Bid(s);
		} catch (Exception error) {
			error.printStackTrace();
			fail();
		}
		
		// Now retrieve the bid information from the cache
		RecordedBid m = BidCachePool.getInstance(hz).getBid(bid.id);
		assertNotNull(m);
		String price = m.getPrice();
		assertNotNull(price);
		assertTrue(!price.equals("0.0"));
		
		/**
		 * Send the win notification
		 */
		try {

			String repl = bid.nurl.replaceAll("\\$", "");
			bid.nurl = repl.replace("{AUCTION_PRICE}", WIN_PRICE);
			
			s = http.sendPost(bid.nurl, "");
		} catch (Exception error) {
			error.printStackTrace();
			fail();
		}

		assertNotNull(s);
		
		/*
		 * Make sure the returned adm is not crap html 
		 */
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(s));

	//	Document doc = db.parse(is);
		
		// Check to see the bid was removed from the cache
		m = BidCachePool.getInstance(hz).getBid(bid.id);
		assertNull(m);
		
	}
	
	/**
	 * Test the basic win processing system of the RTB
	 * @throws Exception on networking problems.
	 */
	@Test
	public void testWinProcessingSmartyAds() throws Exception  {
		HttpPostGet http = new HttpPostGet();
		BidCachePool.getInstance(hz).deleteBid("35c22289-06e2-48e9-a0cd-94aeb79fab43");
		// Make the bid
		
		String s = Charset
				.defaultCharset()
				.decode(ByteBuffer.wrap(Files.readAllBytes(Paths
						.get("./SampleBids/nexage.txt")))).toString();
		/**
		 * Send the bid
		 */
		try {
			s = http.sendPost("http://" + Config.testHost + "/rtb/bids/smartyads", s, 3000000, 3000000);
		} catch (Exception error) {
			fail("Can't connect to test host: " + Config.testHost);
		}
		int code = http.getResponseCode();
		assertTrue(code==200);
		Bid bid = null;
		System.out.println(s);
		int x = s.indexOf("{bid_id");
		assertTrue(x == -1);
		x = s.indexOf("%7Bbid_id");
		assertTrue(x == -1);
		
		try {
			bid = new Bid(s);
		} catch (Exception error) {
			error.printStackTrace();
			fail();
		}
		
		// Now retrieve the bid information from the cache
		RecordedBid m = BidCachePool.getInstance(hz).getBid(bid.id);
		assertNotNull(m);
		String price = m.getPrice();
		assertTrue(!price.equals("0.0"));
		
		/**
		 * Send the win notification
		 */
		try {

			String repl = bid.nurl.replaceAll("\\$", "");
			bid.nurl = repl.replace("{AUCTION_PRICE}", WIN_PRICE);
			
			s = http.sendPost(bid.nurl, "");
		} catch (Exception error) {
			error.printStackTrace();
			fail();
		}

		System.out.println(s);
		
		x = s.indexOf("{creative_");
		assertTrue(x == -1);
		
		/*
		 * Make sure the returned adm is not crap html 
		 */
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(s));

	//	Document doc = db.parse(is);
		
		// Check to see the bid was removed from the cache
		m =  BidCachePool.getInstance(hz).getBid(bid.id);
		assertNull(m);
		
	}
	
	@Test
	public void testWinProcessingCappture() throws Exception  {
		HttpPostGet http = new HttpPostGet();
		BidCachePool.getInstance(hz).deleteBid("35c22289-06e2-48e9-a0cd-94aeb79fab43");
		// Make the bid
		
		String s = Charset
				.defaultCharset()
				.decode(ByteBuffer.wrap(Files.readAllBytes(Paths
						.get("./SampleBids/nexage.txt")))).toString();
		/**
		 * Send the bid
		 */
		try {
			s = http.sendPost("http://" + Config.testHost + "/rtb/bids/cappture", s, 3000000, 3000000);
		} catch (Exception error) {
			error.printStackTrace();
			fail("Can't connect to test host: " + Config.testHost);
		}
		int code = http.getResponseCode();
		assertTrue(code==200);
		Bid bid = null;
		System.out.println(s);
		int x = s.indexOf("{bid_id");
		assertTrue(x == -1);
		x = s.indexOf("%7Bbid_id");
		assertTrue(x == -1);
		
		try {
			bid = new Bid(s);
		} catch (Exception error) {
			error.printStackTrace();
			fail();
		}
		
		// Now retrieve the bid information from the cache
		RecordedBid m = BidCachePool.getInstance(hz).getBid(bid.id);
		assertNotNull(m);
		String price = m.getPrice();
		assertNotNull(price);
		assertTrue(!price.equals("0"));
		
		/**
		 * Send the win notification
		 */
		try {

			String repl = bid.nurl.replaceAll("\\$", "");
			bid.nurl = repl.replace("{AUCTION_PRICE}", WIN_PRICE);
			
			s = http.sendPost(bid.nurl, "");
		} catch (Exception error) {
			error.printStackTrace();
			fail();
		}

		System.out.println(s);
		
		x = s.indexOf("{creative_");
		assertTrue(x == -1);
		
		/*
		 * Make sure the returned adm is not crap html 
		 */
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(s));

	//	Document doc = db.parse(is);
		
		// Check to see the bid was removed from the cache
		m = BidCachePool.getInstance(hz).getBid(bid.id);
		assertNull(m);
		
	}
	
	@Test
	public void testWinProcessingEpom() throws Exception  {
		HttpPostGet http = new HttpPostGet();
		BidCachePool.getInstance(hz).deleteBid("35c22289-06e2-48e9-a0cd-94aeb79fab43");
		// Make the bid
		
		String s = Charset
				.defaultCharset()
				.decode(ByteBuffer.wrap(Files.readAllBytes(Paths
						.get("./SampleBids/nexage.txt")))).toString();
		/**
		 * Send the bid
		 */
		try {
			s = http.sendPost("http://" + Config.testHost + "/rtb/bids/epomx", s, 3000000, 3000000);
		} catch (Exception error) {
			fail("Can't connect to test host: " + Config.testHost);
		}
		int code = http.getResponseCode();
		assertTrue(code==200);
		Bid bid = null;
		System.out.println(s);
		int x = s.indexOf("{bid_id");
		assertTrue(x == -1);
		x = s.indexOf("%7Bbid_id");
		assertTrue(x == -1);
		
		try {
			bid = new Bid(s);
		} catch (Exception error) {
			error.printStackTrace();
			fail();
		}
		
		// Now retrieve the bid information from the cache
		RecordedBid m = BidCachePool.getInstance(hz).getBid(bid.id);
		assertNotNull(m);
		String price = m.getPrice();
		System.out.println("PRICE: " + price);
		assertNotNull(price);
		assertTrue(!price.equals("0.0"));
		
		/**
		 * Send the win notification
		 */
		try {

			String repl = bid.nurl.replaceAll("\\$", "");
			bid.nurl = repl.replace("{AUCTION_PRICE}", WIN_PRICE);
			
			s = http.sendPost(bid.nurl, "");
		} catch (Exception error) {
			error.printStackTrace();
			fail();
		}

		System.out.println(s);
		
		x = s.indexOf("{creative_");
		assertTrue(x == -1);
		
		/*
		 * Make sure the returned adm is not crap html 
		 */
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(s));

	//	Document doc = db.parse(is);
		
		// Check to see the bid was removed from the cache
		m = BidCachePool.getInstance(hz).getBid(bid.id);
		assertNull(m);
		
	}
	
	@Test
	public void testWinProcessingAtomx() throws Exception  {
		HttpPostGet http = new HttpPostGet();
		BidCachePool.getInstance(hz).deleteBid("35c22289-06e2-48e9-a0cd-94aeb79fab43");
		// Make the bid
		
		String s = Charset
				.defaultCharset()
				.decode(ByteBuffer.wrap(Files.readAllBytes(Paths
						.get("./SampleBids/nexage.txt")))).toString();
		/**
		 * Send the bid
		 */
		try {
			s = http.sendPost("http://" + Config.testHost + "/rtb/bids/atomx", s, 3000000, 3000000);
		} catch (Exception error) {
			fail("Can't connect to test host: " + Config.testHost);
		}
		int code = http.getResponseCode();
		assertTrue(code==200);
		Bid bid = null;
		System.out.println(s);
		int x = s.indexOf("{bid_id");
		assertTrue(x == -1);
		x = s.indexOf("%7Bbid_id");
		assertTrue(x == -1);
		
		try {
			bid = new Bid(s);
		} catch (Exception error) {
			error.printStackTrace();
			fail();
		}
		
		// Now retrieve the bid information from the cache
		RecordedBid m = BidCachePool.getInstance(hz).getBid(bid.id);
		assertNotNull(m);
		String price = m.getPrice();
		assertNotNull(price);
		assertTrue(!price.equals("0.0"));		
		/**
		 * Send the win notification
		 */
		try {

			String repl = bid.nurl.replaceAll("\\$", "");
			bid.nurl = repl.replace("{AUCTION_PRICE}", WIN_PRICE);
			
			s = http.sendPost(bid.nurl, "");
		} catch (Exception error) {
			error.printStackTrace();
			fail();
		}

		System.out.println(s);
		
		x = s.indexOf("{creative_");
		assertTrue(x == -1);
		
		/*
		 * Make sure the returned adm is not crap html 
		 */
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(s));

	//	Document doc = db.parse(is);
		
		// Check to see the bid was removed from the cache
		m =  BidCachePool.getInstance(hz).getBid(bid.id);
		assertNull(m);
		
	}

	  @Test
		public void testWinProcessingInvalidHttp() throws Exception  {
			HttpPostGet http = new HttpPostGet();
			// Make the bid
			
			String s = Charset
					.defaultCharset()
					.decode(ByteBuffer.wrap(Files.readAllBytes(Paths
							.get("./SampleBids/nexage.txt")))).toString();
			
			s = s.replaceAll("35c22289-06e2-48e9-a0cd-94aeb79fab4", "ADM#ssp#1023#56425#1490316943.792#68738174.223.128.39-1490316943883-130-0-0-6808053509727162318");
			if (s.indexOf("ADM#") == -1) {
				s = s.replaceAll("123", "ADM#ssp#1023#56425#1490316943.792#68738174.223.128.39-1490316943883-130-0-0-6808053509727162318");
			}
			/**
			 * Send the bid
			 */
			try {
				s = http.sendPost("http://" + Config.testHost + "/rtb/bids/nexage", s, 3000000, 3000000);
			} catch (Exception error) {
				fail("Can't connect to test host: " + Config.testHost);
			}
			int code = http.getResponseCode();
			assertTrue(code==200);
			Bid bid = null;
			System.out.println(s);
			try {
				bid = new Bid(s);
			} catch (Exception error) {
				error.printStackTrace();
				fail();
			}
				
		
			/**
			 * Send the win notification
			 */
			try {

				String repl = bid.nurl.replaceAll("\\$", "");
				bid.nurl = repl.replace("{AUCTION_PRICE}", WIN_PRICE);
				
				s = http.sendPost(bid.nurl, "",300000,300000);
			} catch (Exception error) {
				error.printStackTrace();
				fail();
			}
			System.out.println("---->" + s);;
			assertTrue(s.length() > 10);
		}
	  
	  /**
	   * Test that the piggy backed pixels will turn into win notifications too. Note, C1X uses piggy backed wins
	   * so we will send a pixel that way.
	   * @throws Exception on network errors.
	   */
	  String price;
	  String adId = "";
	  String creativeId = "";
	  String bidId = "";
	  
	  @Test
		public void testPiggyBackedNoNurl() throws Exception {  
			HttpPostGet http = new HttpPostGet();
			final CountDownLatch latch = new CountDownLatch(1);
			
			String s = Charset
					.defaultCharset()
					.decode(ByteBuffer.wrap(Files.readAllBytes(Paths
							.get("./SampleBids/nexage.txt")))).toString();
			
			price = null; 
			
			/**
			 * Send the bid request, make sure there is no win url.
			 */
			try {
				s = http.sendPost("http://" + Config.testHost + "/rtb/bids/c1x", s, 3000000, 3000000);
			} catch (Exception error) {
				fail("Can't connect to test host: " + Config.testHost);
			}
			System.out.println(s);
			int code = http.getResponseCode();
			assertTrue(code==200);
			Bid bid = new Bid(s);
			assertNull(bid.nurl);
		
		}
}
