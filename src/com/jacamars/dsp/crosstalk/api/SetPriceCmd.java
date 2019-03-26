package com.jacamars.dsp.crosstalk.api;

import java.sql.ResultSet;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.jacamars.dsp.crosstalk.budget.AtomicBigDecimal;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.rtb.commands.BasicCommand;
import com.jacamars.dsp.rtb.commands.SetPrice;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.common.Deal;

/**
 * Wen API to set the price of a campaign/creative
 * 
 * @author Ben M. Faul
 *
 */
public class SetPriceCmd extends ApiCommand {

	/** THe price to set. */
	public double price;

	/** If setting a deal price, then indicate that here */
	public Deal deal;

	/**
	 * Default constructor for the object.
	 */
	public SetPriceCmd() {

	}

	/**
	 * Basic form of the command.
	 * 
	 * @param username String. The username to use for authorization.
	 * @param password String. The password to use for authorization.
	 */
	public SetPriceCmd(String username, String password) {
		super(username, password);
		type = SetPrice;

	}

	/**
	 * Targeted form of the command.
	 * 
	 * @param username String. The user authorization.
	 * @param password String. THe password authorization.
	 * @param campaign String. The target campaign.
	 * @param creative String. The target creative.
	 * @param price    double. The price to set.
	 */
	public SetPriceCmd(String username, String password, String campaign, String creative, double price) {
		super(username, password);
		this.campaign = campaign;
		this.creative = creative;
		this.price = price;
		type = SetPrice;
	}

	/**
	 * Convert to JSON
	 */
	public String toJson() throws Exception {
		return mapper.writeValueAsString(this);
	}

	/**
	 * Executes the command, marshalls the response.
	 */
	@Override
	public void execute() {
		super.execute();
		Campaign c = null;

		try {
			c = Crosstalk.getInstance().getKnownCampaign(campaign);
			if (c == null) {
				error = true;
				message = "No campaign defined: " + campaign;
				return;
			}
		} catch (Exception e) {
			error = true;
			message = e.getMessage();
			return;
		}

		Creative cr = c.getCreative(this.creative);
		if (cr == null) {
			error = true;
			message = "No creative defined: " + creative + " in " + campaign;
			return;
		}

		cr.price = price;

		if (deal != null) {
			for (int i = 0; i < cr.deals.size(); i++) {
				var d = cr.deals.get(i);
				if (d.id.equals(deal.id)) {
					d.price = deal.price;
				}
			}
		}

		try {
			Crosstalk.getInstance().update(c, true);
		} catch (Exception e) {
			error = true;
			message = e.getMessage();
		}
	}
}
