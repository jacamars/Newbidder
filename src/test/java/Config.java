package test.java;

import static org.junit.Assert.fail;

import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.shared.FrequencyGoverner;
import com.jacamars.dsp.rtb.tools.Commands;
import com.jacamars.dsp.rtb.tools.DbTools;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Map;


/**
 * The JUNIT common configuration is done here. Start the server if not running. If it is running, then
 * reload the campaigns from REDIS as tests can monmkey with them.
 * 
 * @author Ben M. Faul
 *
 */
public class Config {
	/** The hostname the test programs will use for the RTB bidder */
	public static final String testHost = "localhost:8080";
	static String redisHost;
	static {
		try {
			String content = new String(Files.readAllBytes(Paths.get("Campaigns/payday.json")), StandardCharsets.UTF_8);
			Map test =  DbTools.mapper.readValue(content, Map.class);
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
	static DbTools tools; 
	/** The RTBServer object used in the tests. */
	static RTBServer server;

    public static void setupNoFg() throws Exception {
        try {
            FrequencyGoverner.silent = true;
			// Load database.json
            if (server == null) {
                server = new RTBServer("./Campaigns/payday.json");
                int wait = 0;
                while(!server.isReady() && wait < 10) {
                    Thread.sleep(1000);
                    wait++;
                }
                if (wait == 10) {
                    fail("Server never started");
                }

                Thread.sleep(1000);
            }
            tools.loadDatabase("database.json");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

	public static void setup() throws Exception {
		try {
			FrequencyGoverner.silent = true;
			if (server == null) {	
				server = new RTBServer("./Campaigns/payday.json");
				int wait = 0;
				while(!server.isReady() && wait < 10) {
					Thread.sleep(1000);
					wait++;
				}
				if (wait == 10) {
					fail("Server never started");
				}

				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

    public static void setupBucket(String exchanges) throws Exception {
        try {

            if (server == null) {
                server = new RTBServer("./Campaigns/payday.json",  "",exchanges);
                int wait = 0;
                while(!server.isReady() && wait < 10) {
                    Thread.sleep(1000);
                    wait++;
                }if (wait == 10) {
                    fail("Server never started");
                }

                Thread.sleep(1000);
            }
            tools.loadDatabase("database.json");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }
	
	public static void setup(String configFile) throws Exception {
		try {
            FrequencyGoverner.silent = true;
			if (server == null) {	
				server = new RTBServer(configFile);
				int wait = 0;
				while(!server.isReady() && wait < 10) {
					Thread.sleep(1000);
					wait++;
				}
				if (wait == 10) {
					fail("Server never started");
				}
				Thread.sleep(1000);
			}
            tools.loadDatabase("database.json");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	/** 
	 * JUNIT Test configuration for shards.
	 * 
	 */
	public static void setup(String shard, int port) throws Exception {
		try {
			if (server == null) {
           
				server = new RTBServer("./Campaigns/payday.json", shard, null);
				int wait = 0;
				while(!server.isReady() && wait < 10) {
					Thread.sleep(1000);
					wait++;
				}
				if (wait == 10) {
					fail("Server never started");
				}
	            tools.loadDatabase("database.json");
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	public static void teardown() {

	}
}
