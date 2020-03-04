package com.jacamars.dsp.crosstalk.api;


import java.util.ArrayList;

import java.util.List;


import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.commands.Echo;

import com.jacamars.dsp.rtb.shared.BidCachePool;


/**
 * Get all the bidders status'es and put into a list
 * @author Ben M. Faul
 *
 */
public class GetBiddersStatusCmd extends ApiCommand {

	/** the list of bidders */
	public List<Echo> entries;

	/**
	 * Default constructor
	 */
	public GetBiddersStatusCmd() {

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
			entries = new ArrayList<Echo>();
			try {
				var hz = RTBServer.getSharedInstance();
				List<String> list = BidCachePool.getClientInstance(hz).getMembersNames();
				for (String member : list) {
					Echo m = BidCachePool.getClientInstance(hz).getMemberStatus(member);
					m.from = member;
					entries.add(m);
				}
				return;
			} catch (Exception err) {
				error = true;
				message = err.toString();
			}
		}
}
