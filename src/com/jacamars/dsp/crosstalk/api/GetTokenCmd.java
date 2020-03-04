package com.jacamars.dsp.crosstalk.api;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.hazelcast.core.HazelcastInstance;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.crosstalk.budget.CrosstalkConfig;
import com.jacamars.dsp.crosstalk.budget.Targeting;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.shared.BidCachePool;
import com.jacamars.dsp.rtb.shared.CampaignCache;
import com.jacamars.dsp.rtb.shared.TokenData;

/**
 * Web API to list all campaigns known by crosstalk
 * @author Ben M. Faul
 *
 */
public class GetTokenCmd extends ApiCommand {

	/** Get an API token */
	public String customer;
	public String username;
	public String password;
	public String token;


	/**
	 * Default constructor
	 */
	public GetTokenCmd() {

	}

	/**
	 * Convert to JSON
	 */
	public String toJson() throws Exception {
		return WebAccess.mapper.writeValueAsString(this);
	}

	/**
	 * Execute the command, msrshal the results.
	 */
	@Override
		public void execute() {
			super.execute();
			try {
				String sql = "select * from users where customer_id='"+customer+"' AND username='"+username+"' AND password='"+password+"'";
				System.out.println(sql);
				ResultSet rs = CrosstalkConfig.getInstance().getStatement().executeQuery(sql);
				if (rs.next()) {
					token = UUID.randomUUID().toString();
					TokenData t = new TokenData(customer,username, rs.getString("sub_id"));
					BidCachePool.getInstance().setToken(token, t);
					customer = null;
					username = null;
					password = null;
				} else {
					customer = null;
					username = null;
					password = null;
					error = true;
					message = "No token";
				}
				rs.close();
				return;
			} catch (Exception err) {
				error = true;
				message = err.toString();
			}
		}
}
