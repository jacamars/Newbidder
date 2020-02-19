package com.jacamars.dsp.crosstalk.api;


import java.util.ArrayList;

import java.util.List;

import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.rtb.common.Campaign;

/**
 * Web API to return the reason a campaign is not loaded.
 * @author Ben M. Faul
 *
 */
public class GetReasonCmd extends ApiCommand {


	/** If only 1 reason, its here */
	public String reason;
	
	/** If there are more than one reason, they are here */
	public List<String> reasons;
	
	/**
	 * Default constructor
	 */
	public GetReasonCmd() {

	}

	/**
	 * Basic form of the command, starts all bidders.
	 * 
	 * @param username
	 *            String. User authorization for command.
	 * @param password
	 *            String. Password authorization for command.
	 */
	public GetReasonCmd(String username, String password) {
		super(username, password);
		type = GetReason;
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
	public GetReasonCmd(String username, String password, String target) {
		super(username, password);
		campaign = target;
		type = GetReason;
	}

	/**
	 * Convert to JSON
	 */
	public String toJson() throws Exception {
		return WebAccess.mapper.writeValueAsString(this);
	}

	/**
	 * Execute the command, msrshall the results.
	 */
	@Override
	public void execute() {
			super.execute();
			reasons = new ArrayList<>();
			try {
				if (campaign == null) {
					reasons = new ArrayList();
					
					Crosstalk.getInstance().campaigns.entrySet().forEach(e->{
						var camp = e.getValue();
						try {
							reasons.add(camp.name + ": " + camp.report());
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					});
					
					Crosstalk.getInstance().deletedCampaigns.entrySet().forEach(e->{
						var camp = e.getValue();
						try {
							reasons.add(camp.name + ": " + camp.report());
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					});
													
					return;
				}
				
				reason = "Unknown campaign";
				var id = Integer.parseInt(campaign);
				Crosstalk.getInstance().campaigns.entrySet().forEach(e->{
					if (e.getKey().equals("campaign")) {
						var camp = e.getValue();
						try {
							reason = camp.report();
							return;
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						return;
					}
				});
				
				Crosstalk.getInstance().deletedCampaigns.entrySet().forEach(e->{
					var camp = e.getValue();
					if (camp.id == id)
					try {
						reasons.add(camp.name + ": " + camp.report());
						return;
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});
				
				Campaign c = Campaign.getInstance(id);
				reasons.add(c.report());
				return;
			} catch (Exception err) {
				error = true;
				message = err.toString();
			}
		}
}
