package com.jacamars.dsp.crosstalk.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.tools.JdbcTools;
import com.jacamars.dsp.crosstalk.budget.CampaignBuilderWorker;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;

/**
 * Returns an empty campaign object.
 * @author Ben M. Faul
 *
 */
public class SQLAddNewCampaignCmd extends ApiCommand {
	
	ResultSet rs = null;
	
	public int id;

	/**
	 * Default constructor
	 */
	public SQLAddNewCampaignCmd() {

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
				ObjectNode node = mapper.readValue(campaign,ObjectNode.class);
				
				node.put("customer_id", tokenData.customer);
				
				Campaign x = Crosstalk.getInstance().shadow.get(node.get("id").asText());
				
				Campaign c =  new Campaign(node);   // patch running totals back in
				if (x != null && x.budget != null) {
					c.budget.totalCost = x.budget.totalCost;
					c.budget.hourlyCost = x.budget.hourlyCost;
					c.budget.dailyCost = x.budget.dailyCost;
				} else {
					c.reloadBudgetFromDb();
				}
	
				PreparedStatement st = Campaign.toSql(c, CrosstalkConfig.getInstance().getConnection());
				st.execute();
				if (c.id == 0) {
					ResultSet updated = st.getResultSet();
					if (updated.next()) {
						id = updated.getInt("id");
					}
				} else
					id = c.id;
				st.close();
				
				int updates = 0;
				updates += update(c.customer_id,id,c.banners,"banner");
				updates += update(c.customer_id,id,c.videos,"video");
				updates += update(c.customer_id,id,c.audios,"audio");
				updates += update(c.customer_id,id,c.natives,"native");
				
			//	Crosstalk.getInstance().scan();
				if (c.id != 0 && updates > 0) {
					c.saveToDatabase();
				}	
				c.encoded = false;
								
				Connection conn = CrosstalkConfig.getInstance().getConnection();
						
				CampaignBuilderWorker w = new CampaignBuilderWorker(c);
				w.run();
				
				findOrphans(node);
				
				System.out.println(c.toJson());
				
				return;
			} catch (Exception err) {
				err.printStackTrace();
				error = true;
				message = err.toString();
			}
		}
	
	int update(String customer_id, int campaign_id, List<Integer> ids, String type) throws Exception {
		List<Integer> dels = new ArrayList<>();
		List<Integer> updates = new ArrayList();
		if (ids!= null) {
			ids.forEach(id->{
				Creative c = Creative.getInstance(id, type, customer_id);
				if (c == null)
					dels.add(id);
				else {
					if (c.campaign_id != campaign_id || c.campaign_id == 0) {
						c.campaign_id = campaign_id;
						try {
							c.campaign_id = campaign_id;
							updates.add(id);
							c.saveToDatabase();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
		}
		dels.forEach(id->{
			ids.remove(id);
			updates.add(id);
		});
		
		return updates.size();
	}
	
	
	void findOrphans(ObjectNode node) throws Exception {
		Connection conn = CrosstalkConfig.getInstance().getConnection();
		get(node.get("banners_delete")).forEach(n->{
			String s = "UPDATE banners set campaign_id=0 WHERE id="+n;
			try {
				PreparedStatement st = conn.prepareStatement(s);
				st.execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		get(node.get("videos_delete")).forEach(n->{
			String s = "UPDATE banner_videos set campaign_id=0 WHERE id="+n;
			try {
				PreparedStatement st = conn.prepareStatement(s);
				st.execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		get(node.get("audios_delete")).forEach(n->{
			String s = "UPDATE banner_audios set campaign_id=0 WHERE id="+n;
			try {
				PreparedStatement st = conn.prepareStatement(s);
				st.execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		get(node.get("natives_delete")).forEach(n->{
			String s = "UPDATE  banner_natives set campaign_id=0 WHERE id="+n;
			try {
				PreparedStatement st = conn.prepareStatement(s);
				st.execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	List<Integer> get(JsonNode a) {
		List<Integer> list = new ArrayList();
		if (a != null) {
			ArrayNode an = (ArrayNode)a;
			for(int i=0;i<an.size();i++) {
				list.add(an.get(i).asInt());
			}
		}
		return list;
	}
}
