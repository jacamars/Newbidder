package com.jacamars.dsp.rtb.bidder;

import java.io.File;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.eclipse.jetty.server.Request;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacamars.dsp.rtb.commands.AddCampaign;
import com.jacamars.dsp.rtb.commands.DeleteCampaign;
import com.jacamars.dsp.rtb.commands.Echo;
import com.jacamars.dsp.rtb.commands.LogLevel;
import com.jacamars.dsp.rtb.commands.NobidReason;
import com.jacamars.dsp.rtb.commands.StartBidder;
import com.jacamars.dsp.rtb.commands.StopBidder;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.common.HttpPostGet;

import com.jacamars.dsp.rtb.shared.BidCachePool;
import com.jacamars.dsp.rtb.shared.CampaignCache;
import com.jacamars.dsp.rtb.tools.DbTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Singleton class that handles all the campaigns.html actions. Basically it
 * serves up JSON data about campaigns, and can also load and unload campaigns.
 * 
 * @author Ben M. Faul
 *
 */
public class WebCampaign {
	/** The instance vatiable */
	static WebCampaign instance;

	/** The log object */
	static final Logger logger = LoggerFactory.getLogger(WebCampaign.class);

	private WebCampaign() {

	}

	/**
	 * Returns the singleton instance of the web campaign selector.
	 * 
	 * @return WebCampaign. The object that selects campaigns
	 */
	public static WebCampaign getInstance() {
		if (instance == null) {
			synchronized (WebCampaign.class) {
				if (instance == null) {
					instance = new WebCampaign();
				}
			}
		}
		return instance;
	}

	/**
	 * Handles the request from the HTTP handler in RTBServer.java
	 * 
	 * @param request
	 *            HttpServlet. The request used to get to this handler.
	 * @param in
	 *            InputStream. The POST body.
	 * @return String. The JSON return (A map) in String form.
	 * @throws Exception
	 *             on JSON errors.
	 */
	public String handler(HttpServletRequest request, InputStream in) throws Exception {
		/**
		 * Use jackson to read the content, then use gson to turn it into a map.
		 */
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(in);
		String data = rootNode.toString();

		Map r = new HashMap();
		Map m = mapper.readValue(data, Map.class);
		String cmd = (String) m.get("command");
		if (cmd == null) {
			m.put("error", "No command given");
			m.put("original", data);
			return getString(cmd);
		}

		if (cmd.equals("login")) {
			return doLogin(request, m);
		}

		if (cmd.equals("loginAdmin")) {
			return getAdmin(m);
		}

		if (cmd.equals("showCreative")) {
			return showCreative(m);
		}

		if (cmd.equals("stub")) {
			return doNewCampaign(m);
		}

		if (cmd.equals("deletecampaign")) {
			return doDeleteCampaign(m);
		}
		if (cmd.equals("startcampaign")) {
			return startCampaign(m);
		}
		if (cmd.equals("stopcampaign")) {
			return stopCampaign(m);
		}
		if (cmd.equals("updatecampaign")) {
			return updateCampaign(m);
		}

		if (cmd.equalsIgnoreCase("executeCommand")) {
			return doExecute(m);
		}

		if (cmd.equalsIgnoreCase("dumpFile")) {
			return dumpFile(m);
		}


		if (cmd.equalsIgnoreCase("reloadBidders")) {
			return reloadBidders(m);
		}
		
		if (cmd.equalsIgnoreCase("writeDeletedCampaigns")) {
			return writeDeletedCampaigns(m);
		}
		
		if (cmd.equalsIgnoreCase("getstatus")) {
			return getStatusCmd();
		}

		m.put("error", true);
		m.put("message", "No such command: " + cmd);
		m.put("original", data);
		return getString(cmd);
	}

	/**
	 * Handle the login from a web page.
	 * 
	 * @param request
	 *            HttpServletRequest. The request object.
	 * @param m
	 *            Map. The parameters of the login.
	 * @return String. A JSON formatted string of the response to the login.
	 * @throws Exception
	 *             on JSON errors.
	 */
	private String doLogin(HttpServletRequest request, Map m) throws Exception {
		Map response = new HashMap();
		String message = null;
		String who = (String) m.get("username");
		String pass = (String) m.get("password");
		String subcommand = (String)m.get("subcommand");

		if (who.equals("root")) {

			if (Configuration.getInstance().password != null
					&& Configuration.getInstance().password.equals(pass) == false) {
				response.put("error", true);
				response.put("message", "No such login");
				logger.warn("WebAccess-Login", "Bad Campaign Admin root login attempted!");
				return getString(response);
			}

			response.put("campaigns", CampaignCache.getInstance().getCampaigns());
			response.put("running", Configuration.getInstance().getLoadedCampaignNames());

			logger.warn( "WebAccess-Login", "root user has logged in");
			return getString(response);
		} else {
			if (who.equalsIgnoreCase("demo") == true) {
				who = "root";
			}
		}

		response.put("campaigns", CampaignCache.getInstance().getCampaigns());
		response.put("running", Configuration.getInstance().getLoadedCampaignNames());

		logger.info( "WebAccess-Login", "Demo user has logged in");


		response.put("running", Configuration.getInstance().getLoadedCampaignNames());


		HttpSession session = request.getSession();

		if (message != null)
			response.put("message", message);

		return getString(response);

	}

	public String multiPart(Request baseRequest, HttpServletRequest request, MultipartConfigElement config)
			throws Exception {

		HttpSession session = request.getSession(false);
		String user = (String) session.getAttribute("user");

		baseRequest.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, config);
		Collection<Part> parts = request.getParts();
		for (Part part : parts) {
			System.out.println("" + part.getName());
		}

		Part filePart = request.getPart("file");

		InputStream imageStream = filePart.getInputStream();
		byte[] resultBuff = new byte[0];
		byte[] buff = new byte[1024];
		int k = -1;
		while ((k = imageStream.read(buff, 0, buff.length)) > -1) {
			byte[] tbuff = new byte[resultBuff.length + k]; // temp buffer size
															// = bytes already
															// read + bytes last
															// read
			System.arraycopy(resultBuff, 0, tbuff, 0, resultBuff.length); // copy
																			// previous
																			// bytes
			System.arraycopy(buff, 0, tbuff, resultBuff.length, k); // copy
																	// current
																	// lot
			resultBuff = tbuff; // call the temp buffer as your result buff
		}

		Map response = new HashMap();
		return getString(response);

	}

	/**
	 * Reload the bidder from the cache/Aerorpike.
	 * 
	 * @param cmd
	 *            Map. The command parameters.
	 * @return String. The JSON encoded results of the command.
	 * @throws Exception
	 *             on JSON or cache errors.
	 */
	private String reloadBidders(Map cmd) throws Exception {
		Map r = new HashMap();
		r.put("error", false);

		try {
			List<String> list = Configuration.getInstance().initialLoadlist;
			for (String camp : list) {
				Configuration.getInstance().addCampaign(camp);
			}
		} catch (Exception error) {
			r.put("error", true);
			r.put("message", error.toString());

		}

		return getString(r);
	}


	/**
	 * Adds a new campaign to the cache/aerospike.
	 * 
	 * @param m
	 *            Map. The command parameters.
	 * @return JSON. The results of the add.
	 * @throws Exception
	 *             on JSON or cache errors.
	 */
	private String doNewCampaign(Map m) throws Exception {
		Map response = new HashMap();
		String who = (String) m.get("username");
		String name = (String) m.get("username");
		String id = (String) m.get("campaign");

		logger.info("WebAccess-New-Campaign {}  added a new campaign: {}", who, id);

		try {
			if (CampaignCache.getInstance().getCampaign(id) != null) {
				response.put("error", true);
				response.put("message", "Error, campaign by that name is already defined");
				return getString(response);
			}
			Campaign c = CampaignCache.getInstance().createStub(name, id);
			CampaignCache.getInstance().editCampaign(c);
			response.put("campaign", c);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.put("error", true);
			response.put("message", "Error creating campaign: " + e.toString());
		}
		return getString(response);
	}

	/**
	 * Deletes a campaign from aerospike/cache.
	 * 
	 * @param m
	 *            Map. The command parameters.
	 * @return JSON. The JSON encoded return values of the command.
	 * @throws Exception
	 *             on JSON or cache errors.
	 */
	private String doDeleteCampaign(Map m) throws Exception {
		Map response = new HashMap();
		String who = (String) m.get("username");
		String id = (String) m.get("campaign");

		logger.info("WebAccess-Delete-Campaign {} deleted campaign {}", who, id);

		Controller.getInstance().deleteCampaign(who, id); // delete from bidder
		response.put("campaigns", CampaignCache.getInstance().getCampaigns()); // delete from
																// database
		return getString(response);
	}

	/**
	 * Starts the campaign from the web portal
	 * 
	 * @param cmd
	 *            Map. The JSON command structure from the web user.
	 * @return String. The JSON string of all the running campaigns in this
	 *         bidder.
	 */
	public String startCampaign(Map cmd) throws Exception {
		Map response = new HashMap();
		try {
			String id = getString(cmd.get("id"));
			String name = (String) cmd.get("username");

			id = id.replaceAll("\"", "");

			Campaign c = CampaignCache.getInstance().getCampaign(id);
			Controller.getInstance().addCampaign(c);
			response.put("error", false);

			AddCampaign command = new AddCampaign(null, id);
			command.to = "*";
			command.from = Configuration.getInstance().instanceName;

			logger.info("WebAccess-Start-Campaign {}", id);

		} catch (Exception error) {
			response.put("message", "failed: " + error.toString());
			response.put("error", true);
		}
		response.put("running", Configuration.getInstance().getLoadedCampaignNames());
		return getString(response);
	}
	

	/**
	 * Dump the current redis database to database.json
	 * 
	 * @param cmd
	 *            Map. The command mapping.
	 * @return String. JSON formatted results of the write.
	 */
	public String dumpFile(Map cmd) throws Exception {
		Map response = new HashMap();
		String version = "";
		try {

			for (int i = 0; i < 1000; i++) {
				version = "" + i;
				while (version.length() != 3) {
					version = "0" + version;
				}
				if (new File(CampaignCache.DB_NAME + "." + version).exists() == false)
					break;
			}

			File f = new File(CampaignCache.DB_NAME + "." + version);

			File oldfile = new File(CampaignCache.DB_NAME);
			File newfile = new File(CampaignCache.DB_NAME + "." + version);

			if (oldfile.renameTo(newfile) == false) {
				response.put("error", true);
				response.put("message", "Can't rename old database file");
				return getString(response);
			}

			CampaignCache.getInstance().write();
			response.put("message", "Dumped file ok on system: " + Configuration.getInstance().instanceName
					+ "\nPrevious = " + CampaignCache.DB_NAME + "." + version);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("error", true);
			response.put("message", e.toString());
		}
		return getString(response);
	}

	/**
	 * Delete the campaigns and then write the new file
	 * @param cmd
	 */
	public String writeDeletedCampaigns(Map cmd) throws Exception {
		Map response = new HashMap();
		try {
			List<String> deletions =( List<String>)cmd.get("deletions");
			for (String s : deletions) {
				CampaignCache.getInstance().deleteCampaign(s);
			}
			
			dumpFile(cmd);

			logger.info("WebAccess-Update- deleted campaigns {}", deletions);

		} catch (Exception error) {
			response.put("message", "failed: " + error.toString());
			response.put("error", true);
		}
		response.put("running", Configuration.getInstance().getLoadedCampaignNames());
		return getString(response);
	}
	
	/**
	 * Updates a command in the database (NOT in the currently running list)
	 * 
	 * @param cmd
	 *            Map. The web user command map.
	 * @return String. JSON representation of the running campaigns.
	 */
	public String updateCampaign(Map cmd) throws Exception {
		Map response = new HashMap();
		try {
			String name = (String) cmd.get("username");
			String id = getString(cmd.get("id"));

			id = id.replaceAll("\"", "");
			String data = (String) cmd.get("campaign");

			Campaign c = new Campaign(data);

			System.out.println(data);

			CampaignCache.getInstance().editCampaign(c);
		
			List<String> deletions =( List<String>)cmd.get("deletions");
			for (String s : deletions) {
				CampaignCache.getInstance().deleteCampaign(s);
			}
			
			dumpFile(cmd);

			
			if (Configuration.getInstance().isRunning(name, id)) {
				AddCampaign command = new AddCampaign(null, id);
				command.to = "*";
				command.from = Configuration.getInstance().instanceName;
			}

			c = CampaignCache.getInstance().getCampaign(id);
			Controller.getInstance().addCampaign(c);
			
			logger.info("WebAccess-Update {} modified campaign: {}", name, id);

		} catch (Exception error) {
			error.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			error.printStackTrace(pw);
			response.put("message", "Incomplete construction, failed: " + sw.toString());
			response.put("error", true);
		}
		response.put("running", Configuration.getInstance().getLoadedCampaignNames());
		return getString(response);
	}

	/**
	 * Delete a campaign
	 * 
	 * @param cmd
	 *            Map. The delete command map from the web user.
	 * @return String. The list of campaigns running.
	 */
	public String stopCampaign(Map cmd) throws Exception {
		String name = (String) cmd.get("username");
		String adId = (String) cmd.get("id");
		Map response = new HashMap();
		try {
			Controller.getInstance().deleteCampaign(name, adId);
			response.put("error", false);
			DeleteCampaign command = new DeleteCampaign("", adId);
			command.to = "*";
			command.from = Configuration.getInstance().instanceName;

			logger.info("WebAccess-New-Campaign {} stopped campaign {}",name, adId);
		} catch (Exception error) {
			error.printStackTrace();
			response.put("message", "failed: " + error.toString());
			response.put("error", true);
		}
		response.put("running", Configuration.getInstance().getLoadedCampaignNames());
		return getString(response);
	}

	/**
	 * Return a map off all the campaigns in the database for the specified
	 * user.
	 * 
	 * @param who
	 *            String. The user name.
	 * @return Map. A response map containing campaigns.
	 */
	private Map getCampaigns(String who) throws Exception {

		Map response = new HashMap();
		List camps = CampaignCache.getInstance().getCampaigns();

		response.put("campaigns", camps);
		return response;
	}

	/**
	 * Get admin data based on the login.
	 * 
	 * @param cmd
	 *            Map. The command parameters.
	 * @return String. The JSON encoded return from the command.
	 * @throws Exception
	 *             on cache/aerorpike or JSON errors.
	 */
	public String getAdmin(Map cmd) throws Exception {
		String who = (String) cmd.get("username");
		String pass = (String) cmd.get("password");
		String subcommand = (String)cmd.get("subcommand");
		Map response = new HashMap();


	    long time = System.currentTimeMillis();

		Map m = new HashMap();
		try {
			//List<String> userList = db.getUserList();
			//List<User> users = new ArrayList();
			//for (String s : userList) {
			//	u = db.getUser(s);
//
			//	if (u.name.equals(who) || who.equals("root"))
			//		users.add(u);
			//}

			m.put("fileName", Configuration.getInstance().fileName);
			m.put("initials", Configuration.getInstance().initialLoadlist);
			m.put("seats", Configuration.getInstance().seatsList);
			m.put("lists", Configuration.getInstance().filesList);
			m.put("users", new ArrayList());
			m.put("status", getStatus());
			m.put("summaries", getSummary());
			Map x = new HashMap();

			x.put("winchannel", Configuration.getInstance().WINS_CHANNEL);
			x.put("bidchannel", Configuration.getInstance().BIDS_CHANNEL);
			//x.put("responses", Configuration.getInstance().RESPONSES);
			x.put("nobid", Configuration.getInstance().NOBIDS_CHANNEL);
			x.put("request", Configuration.getInstance().REQUEST_CHANNEL);
			x.put("clicks", Configuration.getInstance().CLICKS_CHANNEL);
			x.put("forensiq", Configuration.getInstance().FORENSIQ_CHANNEL);
			x.put("subscriber_hosts", Configuration.getInstance().commandAddresses);
			x.put("commands", Configuration.getInstance().commandsPort);
			x.put("requeststrategy", Configuration.getInstance().requstLogStrategyAsString());
			x.put("status", Configuration.getInstance().PERF_CHANNEL);

			if (Configuration.getInstance().ssl != null) {
				x = new HashMap();
				x.put("setKeyStorePath", Configuration.getInstance().ssl.setKeyStorePath);
				x.put("setKeyStorePassword", Configuration.getInstance().ssl.setKeyStorePassword);
				x.put("setKeyManagerPassword", Configuration.getInstance().ssl.setKeyManagerPassword);
				m.put("ssl", x);
			}

			x = new HashMap();
			x.put("threads", RTBServer.threads);
			x.put("deadmanswitch", Configuration.getInstance().deadmanSwitch);
			x.put("winurl", Configuration.getInstance().winUrl);
			x.put("pixel-tracking-url", Configuration.getInstance().pixelTrackingUrl);
			x.put("redirect-url", Configuration.getInstance().redirectUrl);
			x.put("adminPort", Configuration.getInstance().adminPort);
			x.put("adminSSL", Configuration.getInstance().adminSSL);
			x.put("ttl", Configuration.getInstance().ttl);
			x.put("stopped", Configuration.getInstance().pauseOnStart);
			x.put("password", Configuration.getInstance().password);
			m.put("app", x);

			m.put("verbosity", Configuration.getInstance().verbosity);
			m.put("geotags", Configuration.getInstance().geotags);

			m.put("forensiq", Configuration.forensiq);
			m.put("template", Configuration.getInstance().template);
		} catch (Exception error) {
			m.put("error", true);
			m.put("message", error.toString());
			error.printStackTrace();
		}
		m.put("sparklines", RTBServer.getSummary());
		m.put("running", Configuration.getInstance().getLoadedCampaignNames());
		return getString(m);
	}

	/**
	 * Creates a sample output from a campaign creative.
	 * 
	 * @param m
	 *            Map. The command parameters.
	 * @return String. The JSON encoded return from the command.
	 * @throws Exception
	 *             on aerorpike/cache2k errors.
	 */
	public String showCreative(Map m) throws Exception {
		Map rets = new HashMap();
		rets.put("creative", "YOU ARE HERE");
		String adid = (String) m.get("adid");
		String crid = (String) m.get("impid");
		String user = (String) m.get("name");
		for (Campaign campaign : Configuration.getInstance().getCampaignsList()) {
			if ( campaign.adId.equals(adid)) {
				for (Creative c : campaign.creatives) {
					if (c.impid.equals(crid)) {
						rets.put("creative", c.createSample(campaign));
						return getString(rets);
					}
				}
			}
		}
		rets.put("creative", "No such creative");
		return getString(rets);
	}

	/**
	 * Used to stop, start, nobbideason and change log level of a bidder
	 * instance.
	 * 
	 * @param m
	 *            Map. The command parameters.
	 * @return Sting. The JSON encoded return of the command.
	 * @throws Exception
	 *             on cache/aerospike or JSON errors.
	 */
	public String doExecute(Map m) throws Exception {
		String action = (String) m.get("action");
		String who = (String) m.get("who");
		String username = (String) m.get("username");
		switch (action) {
		case "start":
			StartBidder start = new StartBidder();
			start.from = username;
			start.to = who;
			Controller.getInstance().startBidder(start);
			break;
		case "stop":
			StopBidder stop = new StopBidder();
			stop.from = username;
			stop.to = who;
			Controller.getInstance().stopBidder(stop);
			break;
		case "loglevel":
			LogLevel level = new LogLevel();
			level.from = username;
			level.to = who;
			String valu = (String) m.get("level");
			level.target = valu;
			Controller.getInstance().setLogLevel(level);
			break;
		case "nobidreason":
			NobidReason nbr = new NobidReason();
			nbr.from = username;
			nbr.to = who;
			valu = (String) m.get("level");
			nbr.target = valu;
			Controller.getInstance().setNoBidReason(nbr);
			break;
		case "reload":
			break;
		default:
			break;
		}

		Map x = new HashMap();
		x.put("message", "Command sent");
		return getString(x);
	}

	/**
	 * Return summary statistics of all the bidders known to the
	 * aerorpike/cache.
	 * 
	 * @return list. A list of maps that hold the summary stats info of all the
	 *         bidders.
	 * @throws Exception
	 *             On cache2k data.
	 */
	private List getSummary() throws Exception {
		String data = null;
		List core = new ArrayList();

		List<String> members = getMembers();
		
		System.out.println(members);
		for (String member : members) {
			Map entry = new HashMap();
			HttpPostGet http = new HttpPostGet();
			Map values = new HashMap();
			if (member.equals(Configuration.getInstance().instanceName)) {
				values.put("stopped", RTBServer.stopped);
				values.put("ncampaigns", Configuration.getInstance().getCampaignsList().size());
				values.put("loglevel", Configuration.getInstance().logLevel);
				values.put("nobidreason", Configuration.getInstance().printNoBidReason);
				values.put("events", RTBServer.events);
			} else {
				Echo info = Controller.getInstance().getMemberStatus(member);
				values.put("stopped", info.stopped);
				values.put("ncampaigns", info.ncampaigns);
				values.put("loglevel", info.loglevel);
				values.put("nobidreason", info.nobidreason);
				values.put("events", info.events);
			}
			entry.put("name", member);
			entry.put("values", values);
			core.add(entry);

		}
		return core;
	}

	/**
	 * Return a list of member bidders if using aerospike, or just this instance
	 * if using only cache2k
	 * 
	 * @return List. The membership list of 'bidders'
	 * @throws Exception
	 *             on hazelcast errors.
	 */
	List<String> getMembers() throws Exception {
		return BidCachePool.getInstance(RTBServer.getSharedInstance()).getMembersNames();
	}
	
	public String getStatusCmd() throws Exception{
		Map outer = null;
		var list = new ArrayList();
		var members = getMembers();
		try {
			for (var member : members) {
				outer = new HashMap();
				outer.put("name", member);
				outer.put("values",Controller.getInstance().getMemberStatus(member));
				list.add(outer);
			}
			return getString(list);
		} catch (Exception error) {
			outer = new HashMap();
			error.printStackTrace();
			outer.put("message", "failed: " + error.toString());
			outer.put("error", true);
			return getString(outer);
		}
	}

	/*
	 * Returns a status of all the members in
	 */
	private List getStatus() throws Exception {
		String data = null;
		List core = new ArrayList();

		List<String> cmembers = getMembers();    // avoid concurrent modifications error
		List<String> members = new ArrayList();
		for (int i=0;i<cmembers.size();i++) {
		    members.add(cmembers.get(i));
        }

		// Sort the list
		Collections.sort(members);
		for (String member : members) {
			Map entry = new HashMap();
			HttpPostGet http = new HttpPostGet();
			Echo values = Controller.getInstance().getMemberStatus(member);
			
			entry.put("name", member);
			entry.put("values", values);
			core.add(entry);
		}

		return core;

	}

	/**
	 * Encodes an object into a JSON string.
	 * 
	 * @param o
	 *            Object. The object to turn into JSON.
	 * @return String. The returned JSON encoded string.
	 * @throws Exception
	 *             on JSON encoding errors.
	 */
	private String getString(Object o) throws Exception {
		return DbTools.mapper.writer().writeValueAsString(o);
	}
}
