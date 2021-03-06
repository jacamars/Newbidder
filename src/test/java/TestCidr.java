package test.java;

import static org.junit.Assert.*;



import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jacamars.dsp.rtb.bidder.Controller;
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
}