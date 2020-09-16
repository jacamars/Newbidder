package com.jacamars.dsp.crosstalk.api;

import com.github.mgunlogson.cuckoofilter4j.CuckooFilter;
import com.google.common.hash.BloomFilter;
import com.jacamars.dsp.rtb.blocks.LookingGlass;
import com.jacamars.dsp.rtb.blocks.Membership;
import com.jacamars.dsp.rtb.blocks.NavMap;
import com.jacamars.dsp.rtb.blocks.SimpleSet;
import com.jacamars.dsp.rtb.exchanges.adx.AdxGeoCodes;
import com.jacamars.dsp.rtb.shared.BidCachePool;
import com.jacamars.dsp.rtb.tools.IsoTwo2Iso3;

/**
 * Deletes a campaign
 * 
 * @author Ben M. Faul
 *
 */
public class QuerySymbolCmd extends ApiCommand {

	public String symbol;
	public String value;
	public String predicate;
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

			if (predicate != null) {
				String[] parts = predicate.split(" ");
				Object rets = null;
				if (parts.length == 1 && predicate.length() != 0) { // query by key
					rets = BidCachePool.getInstance().get(symbol, predicate);
				} else {
					rets = BidCachePool.getInstance().query(symbol, predicate);
				}
				if (rets != null) {
					if (rets instanceof String)
						reply = (String) rets;
					else
						reply = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rets);
				}
				return;

			} else {

				Object rets = null;
				Object x = LookingGlass.symbols.get(symbol);
				if (x == null) {
					error = true;
					message = "No such symbol: " + symbol;
					return;
				}

				nano = System.nanoTime();
				if (x instanceof BloomFilter) {
					BloomFilter f = (BloomFilter) x;
					rets = f.mightContain(value);
				} else if (x instanceof CuckooFilter) {
					CuckooFilter f = (CuckooFilter) x;
					rets = f.mightContain(value);
				} else if (x instanceof Membership) {
					Membership f = (Membership) x;
					if (f.get(value) == null)
						rets = false;
					else
						rets = true;
				} else if (x instanceof SimpleSet) {
					SimpleSet f = (SimpleSet) x;
					if (f.query(value) == null)
						rets = false;
					else
						rets = true;
				} else if (x instanceof IsoTwo2Iso3) {
					IsoTwo2Iso3 f = (IsoTwo2Iso3) x;
					rets = f.query(value);
				} else if (x instanceof AdxGeoCodes) {
					AdxGeoCodes f = (AdxGeoCodes) x;
					rets = f.query(value);
				} else {
					NavMap f = (NavMap) x;
					rets = f.query(value);
				}
				nano = System.nanoTime() - nano;

				if (rets instanceof String)
					reply = (String) rets;
				else
					reply = mapper.writeValueAsString(rets);
			}
			return;
		} catch (Exception err) {
			error = true;
			message = err.toString();
		}
		message = "Timed out";
	}
}
