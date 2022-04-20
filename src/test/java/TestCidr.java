package test.java;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.blocks.CIDRUtils;
import com.jacamars.dsp.rtb.blocks.LookingGlass;
import com.jacamars.dsp.rtb.blocks.NavMap;

/**
 * A class for testing that the bid has the right parameters
 * @author Ben M. Faul
 *
 */
public class TestCidr  {
	static Controller c;
	public static String test = "";
	
	@BeforeClass
	  public static void testSetup() {		
		
	  }

	  @AfterClass
	  public static void testCleanup() {
		
	  }
	  
	  /**
	   * Test a valid bid response.
	   * @throws Exception on networking errors.
	   */
	  @Test 
	  public void testCidr() throws Exception {
		  
		  
		    NavMap sr = new NavMap("CIDR", "data/METHBOT.txt", "cidr");
		    boolean p = sr.contains("45.33.224.0");
		    assertTrue(p);
		    
		    p = sr.contains("45.33.239.255");
		    assertTrue(p);
		    
		    p = sr.contains("44.33.224.0");
		    assertFalse(p);
		    
		    p = sr.contains("165.52.0.0");
		    assertTrue(p);
		    
		    p = sr.contains("165.55.255.255");
		    assertTrue(p);
		    
		    p = sr.contains("166.55.255.255");
		    assertFalse(p);

	  }
	  
	
	  @Test
	  public void testForNegative() throws Exception {
		  var cidr = new CIDRUtils("191.255.255.254/32");
		  
		  long a = cidr.getStartAddress() ;
		  long b = cidr.getEndAddress();
		  
		  assertTrue(a>0);
		  assertTrue(b>0);
		  assertTrue(a==b);
	  }
	  
	  @Test
	  public void testInRange() throws Exception {
		  List<String> addrs = new ArrayList<>();
		  
		  addrs.add("45.33.224.0/20");
		  
		  var cidr = new CIDRUtils("45.33.224.0/20");
		  boolean p = cidr.isInRange("45.33.224.0");
		  
		  
		  cidr = new CIDRUtils(addrs);
		  p = cidr.isInRange("45.33.224.0");
		  assertTrue(p);
	  }
}