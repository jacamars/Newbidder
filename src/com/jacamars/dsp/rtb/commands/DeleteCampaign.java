package com.jacamars.dsp.rtb.commands;

import com.jacamars.dsp.rtb.bidder.Controller;

/**
 * A class that is used to delete a campaign (by the 'id')
 * @author Ben M. Faul
 *
 */
public class DeleteCampaign extends BasicCommand {
	/**
	 * Default constructor for GSON
	 */
	public DeleteCampaign() {
		super();
		cmd = Controller.DEL_CAMPAIGN;
		status = "ok";
		msg = "A campaign is being deleted from the system";
		name = "DeleteCampaign";
	}
	
	/**
	 * Delete a campaign from the database.
	 * @param to String. The bidder that will host the command..
	 * @param id String. The campaign adid to delete.
	 */
	public DeleteCampaign(String to, String id) {
		super(to);
		target = id;
		cmd = Controller.DEL_CAMPAIGN;
		status = "ok";
		msg = "A campaign is being deleted from the system: " + id;
		name = "DeleteCampaign";
	}
}
