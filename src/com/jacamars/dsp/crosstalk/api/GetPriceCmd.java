package com.jacamars.dsp.crosstalk.api;

import java.sql.ResultSet;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.common.Deal;

/**
 * Get the assigned price of a Campaign/creative
 * 
 * @author Ben M. Faul
 *
 */
public class GetPriceCmd extends ApiCommand {

	public double price;
	public List<Map<String, Double>> deals;

	/**
	 * Default constructor for the object.
	 */

	public GetPriceCmd() {

	}

	/**
	 * Basic form of the command.
	 * 
	 * @param username String. The username to use for authorization.
	 * @param password String. The password to use for authorization.
	 */
	public GetPriceCmd(String username, String password) {
		super(username, password);
		type = GetPrice;

	}

	/**
	 * Targeted form of the command.
	 * 
	 * @param username String. The user authorization.
	 * @param password String. THe password authorization.
	 * @param campaign String. The target campaign.
	 * @param creative String. The target creative.
	 */
	public GetPriceCmd(String username, String password, String campaign, String creative) {
		super(username, password);
		this.campaign = campaign;
		this.creative = creative;
		type = GetPrice;
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
		try {
			price = 0;

			Campaign c = Crosstalk.getInstance().getKnownCampaign(campaign);
			if (c == null) {
				error = true;
				message = "No campaign defined: " + campaign;
				return;
			}

			Creative cr = c.getCreative(this.creative);
			if (cr == null) {
				error = true;
				message = "No creative defined: " + creative + " in " + campaign;
				return;
			}

			price = cr.price;
			if (cr.deals != null) {
				deals = new ArrayList();
				for (Deal d : cr.deals) {
					Map<String, Double> m = new HashMap();
					m.put(d.id, d.price);
					deals.add(m);
				}
			}
		} catch (Exception e) {
			error = true;
			message = e.getMessage();
			return;
		}
	}
}
