package com.jacamars.dsp.rtb.commands;

import com.jacamars.dsp.rtb.bidder.Controller;

/**
 * A class that is used to get the price in a creative.
 * @author Ben M. Faul
 *
 */

public class GetPrice extends BasicCommand {
	
	/**
	 * Default constructor.
	 */
	public GetPrice() {
		super();
		cmd = Controller.GET_PRICE;
		msg = "Get Price issued";
	}
	
	/**
	 * A command to query the price of a campaign.
	 * @param to String. The bidder that will host this command.
	 * @param campaign String. The campaignid in question.
	 * @param creative String. The creative impid to retrieve the price from.
	 */
	public GetPrice(String to, String campaign, String creative) {
		super(to);
		this.name = campaign;
		this.target = creative;
		cmd = Controller.GET_PRICE;
		msg = "Get Price Issued";
	}
}
