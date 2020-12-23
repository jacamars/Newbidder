package com.jacamars.dsp.crosstalk.api;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.jacamars.dsp.crosstalk.budget.CrosstalkConfig;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.nativeads.creative.NativeCreative;
import com.jacamars.dsp.rtb.tools.JdbcTools;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;

/**
 * Returns an empty campaign object.
 * @author Ben M. Faul
 *
 */
public class SQLGetNewCreativeCmd extends ApiCommand {
	
	ResultSet rs = null;
	/** The returned results from the campaign. */
	public String name;
	public Creative data;
	public String ctype;;

	/**
	 * Default constructor
	 */
	public SQLGetNewCreativeCmd() {

	}

	/**
	 * Convert to JSON
	 */
	public String toJson() throws Exception {
		return WebAccess.mapper.writeValueAsString(this);
	}

	/**
	 * Execute the command, masrshall the results.
	 */
	@Override
		public void execute() {
			super.execute();
			try {
				data =  new Creative();
				switch(ctype.toLowerCase()) {
				case "banner":
					data.isBanner = true; break;
				case "video":
					data.isVideo = true; break;
				case "audio":
					data.isAudio = true; break;
				case "native": 
					data.isNative = true; 
					data.nativead = new NativeCreative();
					break;
				}

				data.name = name;
				return;
			} catch (Exception err) {
				error = true;
				message = err.toString();
			}
		}
	
}
