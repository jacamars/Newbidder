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

import com.jacamars.dsp.crosstalk.budget.CrosstalkConfig;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.tools.JdbcTools;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;

/**
 * Web API access to add  a campaign to the bidders. This is not the CreateCampaign command.
 * @author Ben M. Faul
 *
 */
public class AddCampaignCmd extends ApiCommand {
	
	ResultSet rs = null;
	/** The returned results from the campaign. */
	public String updated;

	/**
	 * Default constructor
	 */
	public AddCampaignCmd() {

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
					Campaign c =  new Campaign(node.get(0));
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

			CrosstalkConfig config = CrosstalkConfig.getInstance();
			//String select = "select * from campaigns where  status = 'runnable' and activate_time <= ? and expire_time > ?";
			String select = "select * from campaigns where status='runnable'";

			PreparedStatement prep = config.getConnection().prepareStatement(select);
			ResultSet rs = prep.executeQuery();
			ArrayNode nodes = JdbcTools.convertToJson(rs);
			handleNodes(nodes);

			return nodes;
		}

		public ArrayNode createJson(String id) throws Exception {
			Date now = new Date();
			Timestamp update = new Timestamp(now.getTime());

			CrosstalkConfig config = CrosstalkConfig.getInstance();

			String select = "select * from campaigns where id = " + id;

			PreparedStatement prep = config.getConnection().prepareStatement(select);
			ResultSet rs = prep.executeQuery();

			ArrayNode nodes = JdbcTools.convertToJson(rs);
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
			CrosstalkConfig config = CrosstalkConfig.getInstance();
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
					ArrayNode inner = JdbcTools.convertToJson(rs);
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

			// ////////////////////////////////////////////////////////////////////////////
			// Banner
			for (int i = 0; i < nodes.size(); i++) {
				ObjectNode x = (ObjectNode) nodes.get(i);
				int campaignid = x.get("id").asInt();
				rs = stmt.executeQuery("select * from banners where campaign_id = " + campaignid);
				ArrayNode inner = JdbcTools.convertToJson(rs);
				x.set("banner", inner);
			}

			// Video
			for (int i = 0; i < nodes.size(); i++) {
				ObjectNode x = (ObjectNode) nodes.get(i);
				int campaignid = x.get("id").asInt(); ///////// CHECK
				rs = stmt.executeQuery("select * from banner_videos where campaign_id = " + campaignid);
				ArrayNode inner = JdbcTools.convertToJson(rs);
				x.set("banner_video", inner);
			}
			
			// audio
			for (int i = 0; i < nodes.size(); i++) {
				ObjectNode x = (ObjectNode) nodes.get(i);
				int campaignid = x.get("id").asInt(); ///////// CHECK
				rs = stmt.executeQuery("select * from banner_audios where campaign_id = " + campaignid);
				ArrayNode inner = JdbcTools.convertToJson(rs);
				x.set("banner_audios", inner);
			}
	
			// audio
			for (int i = 0; i < nodes.size(); i++) {
				ObjectNode x = (ObjectNode) nodes.get(i);
				int campaignid = x.get("id").asInt(); ///////// CHECK
				rs = stmt.executeQuery("select * from banner_natives where campaign_id = " + campaignid);
				ArrayNode inner = JdbcTools.convertToJson(rs);
				x.set("banner_natives", inner);
			}
		}

}
