package com.jacamars.dsp.crosstalk.api;

import java.util.HashMap;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;

/**
 * Web API access for telling the bidders to update a campaign.
 * @author Ben M. Faul
 *
 */
public class UpdateCmd extends ApiCommand {

	/**
	 * Default constructor
	 */
	
	public List<String> updated;

	public UpdateCmd() {

	}

	/**
	 * Basic form of the command, update a named campaign.
	 * 
	 * @param username
	 *            String. User authorization for command.
	 * @param password
	 *            String. Password authorization for command.
	 */
	public UpdateCmd(String username, String password) {
		super(username, password);
		type = Update;
	}

	/**
	 * Targeted form of command. Updates a specific campaign.
	 * 
	 * @param username
	 *            String. User authorizatiom.
	 * @param password
	 *            String. Password authorization.
	 * @param target
	 *            String. The bidder to start.
	 */
	public UpdateCmd(String username, String password, String target) {
		super(username, password);
		campaign = target;
		type = Update;
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
				ArrayNode nodes = Crosstalk.getInstance().createJson(campaign);
				if (nodes.size()==0)
					throw new Exception("No such campaign in db: " + campaign);
				var c = Crosstalk.getInstance().makeNewCampaign((ObjectNode)nodes.get(0));
				message = Crosstalk.getInstance().update(c,true);
			} catch (Exception err) {
				error = true;
				logger.error("Update command issued an error: " + err.toString());
				message = err.toString();
				err.printStackTrace();
			}
		}
}
