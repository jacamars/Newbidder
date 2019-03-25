package com.jacamars.dsp.crosstalk.api;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jacamars.dsp.crosstalk.budget.AccountingCampaign;
import com.jacamars.dsp.crosstalk.budget.Configuration;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;

/**
 * Web API access to add  a campaign to the bidders.
 * @author Ben M. Faul
 *
 */
public class AddCampaignCmd extends ApiCommand {
	public static Map<Integer, JsonNode> globalRtbSpecification;
	public static ArrayNode campaignRtbStd;
	public static ArrayNode bannerRtbStd;
	public static ArrayNode videoRtbStd;
	public static ArrayNode exchangeAttributes;

	public static final String RTB_STD = "rtb_standards";
	public static final String CAMP_RTB_STD = "campaigns_rtb_standards";
	public static final String BANNER_RTB_STD = "banners_rtb_standards";
	public static final String VIDEO_RTB_STD = "banner_videos_rtb_standards";
	
	
	
	ResultSet rs = null;
	/** The returned results from the campaign. */
	public String updated;

	/**
	 * Default constructor
	 */
	public AddCampaignCmd() {

	}

	/**
	 * Deletes a campaign from the bidders.
	 *
	 * @param username
	 *            String. User authorization for command.
	 * @param password
	 *            String. Password authorization for command.
	 */
	public AddCampaignCmd(String username, String password) {
		super(username, password);
		type = Add;
	}

	/**
	 * Targeted form of command. starts a specific bidder.
	 *
	 * @param username
	 *            String. User authorizatiom.
	 * @param password
	 *            String. Password authorization.
	 * @param target
	 *            String. The bidder to start.
	 */
	public AddCampaignCmd(String username, String password, String target) {
		super(username, password);
		campaign = target;
		type = Add;
	}

	/**
	 * Convert to JSON
	 */
	public String toJson() throws Exception {
		return WebAccess.mapper.writeValueAsString(this);
	}

	/**
	 * Execute the command, masrshall the results.
	 */
	@Override
		public void execute() {
			super.execute();
			try {
				campaign = campaign.trim();
				if (!campaign.startsWith("{")) {
					ArrayNode node = createJson(campaign);
					AccountingCampaign c =  new AccountingCampaign(node.get(0));
					campaign = c.toJson();
				}
				updated = Crosstalk.getInstance().add(campaign);
				return;
			} catch (Exception err) {
				error = true;
				message = err.toString();
			}
		}
	
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		public ArrayNode createJson() throws Exception {
			Date now = new Date();
			Timestamp update = new Timestamp(now.getTime());

			Configuration config = Configuration.getInstance();
			//String select = "select * from campaigns where  status = 'runnable' and activate_time <= ? and expire_time > ?";
			String select = "select * from campaigns where status='runnable'";

			PreparedStatement prep = config.getConnection().prepareStatement(select);
			ResultSet rs = prep.executeQuery();
			ArrayNode nodes = ResultSetToJSON.convert(rs);
			handleNodes(nodes);

			return nodes;
		}

		public ArrayNode createJson(String id) throws Exception {
			Date now = new Date();
			Timestamp update = new Timestamp(now.getTime());

			Configuration config = Configuration.getInstance();

			String select = "select * from campaigns where id = " + id;

			PreparedStatement prep = config.getConnection().prepareStatement(select);
			ResultSet rs = prep.executeQuery();

			ArrayNode nodes = ResultSetToJSON.convert(rs);
			handleNodes(nodes);

			return nodes;
		}

		/**
		 * Convert SQL tables for campaigns, creatives, target and rtb_standard into JSON object
		 * @param nodes JSON array to hold the results.
		 * @throws Exception on JSON or SQL errors.
		 */
		public static void handleNodes(ArrayNode nodes) throws Exception {

			ResultSet rs;
			Configuration config = Configuration.getInstance();
			Statement stmt = config.getStatement();
			List<Integer> list = new ArrayList<Integer>();

			for (int i = 0; i < nodes.size(); i++) {
				ObjectNode x = (ObjectNode) nodes.get(i);
				int campaignid = x.get("id").asInt();
				String regions = x.get("regions").asText();
				regions = regions.toLowerCase();
				if (regions.contains(config.getRegion().toLowerCase())) {
					int targetid = x.get("target_id").asInt();
					rs = stmt.executeQuery("select * from targets where id = " + targetid);
					ArrayNode inner = ResultSetToJSON.convert(rs);
					ObjectNode y = (ObjectNode) inner.get(0);
					x.set("targetting", y);
				} else {
					list.add(i);
				}
			}

			//
			// Remove in reverse all that don't belong to my region.
			//
			while (list.size() > 0) {
				Integer x = list.get(list.size() - 1);
				nodes.remove(x);
				list.remove(x);
			}

			if (nodes.size() == 0)
				return;

			// /////////////////////////// GLOBAL rtb_spec
			globalRtbSpecification = new HashMap<Integer, JsonNode>();
			rs = stmt.executeQuery("select * from " + RTB_STD);
			ArrayNode std = ResultSetToJSON.convert(rs);
			Iterator<JsonNode> it = std.iterator();
			while (it.hasNext()) {
				JsonNode child = it.next();
				globalRtbSpecification.put(child.get("id").asInt(), child);
			}

			campaignRtbStd = ResultSetToJSON.factory.arrayNode();
			rs = stmt.executeQuery("select * from " + CAMP_RTB_STD);
			std = ResultSetToJSON.convert(rs);
			it = std.iterator();
			while (it.hasNext()) {
				JsonNode child = it.next();
				campaignRtbStd.add(child);
			}

			bannerRtbStd = ResultSetToJSON.factory.arrayNode();
			rs = stmt.executeQuery("select * from " + BANNER_RTB_STD);
			std = ResultSetToJSON.convert(rs);
			it = std.iterator();
			while (it.hasNext()) {
				JsonNode child = it.next();
				bannerRtbStd.add(child);
			}

			videoRtbStd = ResultSetToJSON.factory.arrayNode();
			rs = stmt.executeQuery("select * from " + VIDEO_RTB_STD);
			std = ResultSetToJSON.convert(rs);
			it = std.iterator();
			while (it.hasNext()) {
				JsonNode child = it.next();
				videoRtbStd.add(child);
			}

			exchangeAttributes = ResultSetToJSON.factory.arrayNode();
			rs = stmt.executeQuery("select * from exchange_attributes");
			std = ResultSetToJSON.convert(rs);
			it = std.iterator();
			while (it.hasNext()) {
				JsonNode child = it.next();
				exchangeAttributes.add(child);
			}
			// ////////////////////////////////////////////////////////////////////////////
			// Banner
			for (int i = 0; i < nodes.size(); i++) {
				ObjectNode x = (ObjectNode) nodes.get(i);
				int campaignid = x.get("id").asInt();
				rs = stmt.executeQuery("select * from banners where campaign_id = " + campaignid);
				ArrayNode inner = ResultSetToJSON.convert(rs);
				x.set("banner", inner);
			}

			// Video
			for (int i = 0; i < nodes.size(); i++) {
				ObjectNode x = (ObjectNode) nodes.get(i);
				int campaignid = x.get("id").asInt(); ///////// CHECK
				rs = stmt.executeQuery("select * from banner_videos where campaign_id = " + campaignid);
				ArrayNode inner = ResultSetToJSON.convert(rs);
				x.set("banner_video", inner);
			}
		}

}
