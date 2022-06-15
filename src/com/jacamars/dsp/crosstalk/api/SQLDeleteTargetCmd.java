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
 * Returns an empty campaign object.
 * @author Ben M. Faul
 *
 */
public class SQLDeleteTargetCmd extends ApiCommand {
	
	ResultSet rs = null;
	public int id;

	/**
	 * Default constructor
	 */
	public SQLDeleteTargetCmd() {

	}

	/**
	 * Convert to JSON
	 */
	public String toJson() throws Exception {
		return WebAccess.mapper.writeValueAsString(this);
	}

	/**
	 * Execute the command, marshal the results.
	 */
	@Override
		public void execute() {
			super.execute();
			try {
				PreparedStatement st;
				if (tokenData.isRtb4FreeSuperUser()) {
					st= CrosstalkConfig.getInstance().getConnection().
						prepareStatement("delete from targets where id=?");
					st.setInt(1, id);
					st.executeUpdate();
				} else {
					st = CrosstalkConfig.getInstance().getConnection().
							prepareStatement("delete from targets where id=?");
					st.setInt(1, id);
					st.executeUpdate();
				}
				st.close();
				Campaign.removeTargetFromCampaigns(id, tokenData);
				return;
			} catch (Exception err) {
				err.printStackTrace();
				error = true;
				message = err.toString();
			}
		}
	
}
