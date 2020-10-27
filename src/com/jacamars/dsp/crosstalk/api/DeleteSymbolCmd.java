package com.jacamars.dsp.crosstalk.api;


import java.util.List;

import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.rtb.blocks.LookingGlass;

/**
 * Deletes a campaign
 * @author Ben M. Faul
 *
 */
public class DeleteSymbolCmd extends ApiCommand {

	/** The list of deletions/updates */
	public String symbol;

	/**
	 * Default constructor
	 */
	public DeleteSymbolCmd() {

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
			try {
				LookingGlass.symbols.remove(symbol);
				return;
			} catch (Exception err) {
				error = true;
				message = err.toString();
			}
			message = "Timed out";
		}
}
