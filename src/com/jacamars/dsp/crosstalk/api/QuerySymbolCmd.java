package com.jacamars.dsp.crosstalk.api;


import java.util.List;

import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.rtb.blocks.LookingGlass;

/**
 * Deletes a campaign
 * @author Ben M. Faul
 *
 */
public class QuerySymbolCmd extends ApiCommand {

	/** The list of deletions/updates */
	public String symbol;
	public String value;
	public String reply;
	public long nano;

	/**
	 * Default constructor
	 */
	public QuerySymbolCmd() {

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
				
				// TBD needs rewrite for multi tenant
				LookingGlass q = (LookingGlass)LookingGlass.symbols.get(symbol);
				if (q == null) {
					error = true;
					message = "No such symbol: " + symbol;
				}
				nano = System.nanoTime();
				Object x = q.query(value);
				nano = System.nanoTime() -nano;
				if (x instanceof String)
					reply = (String)x;
				else
					reply = mapper.writeValueAsString(x);
				return;
			} catch (Exception err) {
				error = true;
				message = err.toString();
			}
			message = "Timed out";
		}
}
