package test.java;

import static org.junit.Assert.*;



import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.hash.BloomFilter;
import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.blocks.Bloom;
import com.jacamars.dsp.rtb.blocks.LookingGlass;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.common.Node;
import com.jacamars.dsp.rtb.pojo.BidRequest;

/**
 * Test Geo fencing
 * @author Ben M. Faul
 *
 */

public class TestLiveRamp {
	/**
	 * Test a single geo fence region in an isolated node.
	 * @throws Exception on I/O errors.
	 */
	@Test
	public void testGeoInBidRequest() throws Exception {
	    Bloom f = new Bloom("@AUDIENCE1","data/audience.txt",6);
	    LookingGlass.symbols.put("@AUDIENCE1",f);
		
		InputStream is = Configuration.getInputStream("SampleBids/liveramp.txt");
		BidRequest br = new BidRequest(is);

		Node node = new Node("IDL","user.ext.eids", Node.IDL, "@AUDIENCE1");
     	assertTrue(node.test(br,null));
     	
	}

}
