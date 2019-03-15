package com.jacamars.dsp.rtb.tools;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.commands.*;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.common.RecordedBid;
import com.jacamars.dsp.rtb.jmq.MessageListener;
import com.jacamars.dsp.rtb.jmq.RTopic;
import com.jacamars.dsp.rtb.jmq.ZPublisher;
import com.jacamars.dsp.rtb.shared.BidCachePool;
import com.jacamars.dsp.rtb.shared.CampaignCache;
import com.jacamars.dsp.rtb.shared.FreqSetCache;
import com.jacamars.dsp.rtb.shared.FrequencyGoverner;

/**
 * A simple class that sends and receives commands from RTB4FREE bidders.
 *
 * @author Ben M. Faul
 */

public class Commands {
	static CountDownLatch latch;

	static String uuid = "crosstalk:commands:" + UUID.randomUUID().toString();
	static ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/**
	 * Main entry point, see description for usage. -starbidder -n
	 * localhost:2000,2001 -all or -id number -stopbidder -all or -id numer -loaddb
	 * [filename] -loadcamps -all or -id num -cid cid or 'cid, cid, cid' -printdb
	 * [filename] -listbidders -listcamps -all or -id num -echo -all or -id num
	 *
	 * @param args String[]. The array of arguments.
	 */
	public static void main(String[] args) throws Exception {
		String cluster[] = null;
		String command = "list-bidders";
		String key = null;
		String bid = null;
		String uname = null;
		String passwd = null;

		if (args.length == 0) {
			// System.out.println(usage());
			// return;
		}
		int i = 0;
		while (i < args.length) {
			switch (args[i]) {
			case "-cluster":
				cluster = args[i + 1].split(",");
				i += 2;
				break;
			case "-command":
				command = args[i + 1];
				i += 2;
				switch (command) {

				case "getkey":
					key = args[i];
					i++;
					break;

				case "getbid":
					bid = args[i];
					i++;
					break;

				case "delete-campaign":
					key = args[i];
					i++;
					break;

				case "delete-campaigns":
					i++;
					break;

				case "add-campaigns":
				case "add-campaign":
					key = args[i];
					i++;
					break;

				case "list-campaigns":
					i++;
					break;

				case "get-fq":
					key = args[i];
					i++;
					break;

				case "clear-fq":
					key = args[i];
					i++;
					break;

				case "clear-fqs":
					i++;
					break;
				}

			default:
				i++;
			}
		}

		if (cluster == null) {
			cluster = new String[2];
			cluster[0] = "localhost:5701";
			cluster[1] = "localhost:5702";

		}

		ClientConfig clientConfig = new ClientConfig();
		clientConfig.getSerializationConfig()
			.addPortableFactory(PortableEchoFactory.FACTORY_ID, new PortableEchoFactory());
		//clientConfig.setProperty("hazelcast.logging.type", "slf4j");

		clientConfig.addAddress(cluster);
		HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);

		String str = null;
		switch (command) {
		case "list-bidders":
			List<String> list = BidCachePool.getClientInstance(client).getMembersNames();
			System.out.println("Members = " + list);
			for (String member : list) {
				Echo m = BidCachePool.getClientInstance(client).getMemberStatus(member);
				str = member + ":\n" + DbTools.mapper.writer().withDefaultPrettyPrinter().writeValueAsString(m)
						+ "\n**************";
				System.out.println(str);
			}
			break;

		case "getkey":
			Object x = BidCachePool.getClientInstance(client).get(key);
			if (x == null)
				str = key + " = null";
			else
				str = key + " = " + DbTools.mapper.writer().withDefaultPrettyPrinter().writeValueAsString(x);
			System.out.println(str);
			;
			break;

		case "getbid":
			RecordedBid rb = BidCachePool.getClientInstance(client).getBid(key);
			if (rb == null)
				str = key + " = null";
			str = key + " = " + DbTools.mapper.writer().withDefaultPrettyPrinter().writeValueAsString(rb);
			System.out.println(str);
			break;

		case "getvideo":
			String rc = BidCachePool.getClientInstance(client).getVideo(key);
			if (rc == null)
				str = key + " = null";
			str = key + " = " + rc;
			System.out.println(str);
			break;

		case "list-campaigns":
			List<Campaign> clist = CampaignCache.getClientInstance(client).getCampaigns();
			List<String> names = new ArrayList();
			for (Campaign cm : clist) {
				names.add(cm.adId);
			}
			System.out.println(names);
			break;

		case "show-campaigns":
			clist = CampaignCache.getClientInstance(client).getCampaigns();
			str = key + " = " + DbTools.mapper.writer().withDefaultPrettyPrinter().writeValueAsString(clist);
			System.out.println(str);
			break;

		case "add-campaign":
			CampaignCache.getClientInstance(client).loadDatabase(key);
			break;

		case "add-campaigns":
			CampaignCache.getClientInstance(client).loadDatabase(key);
			break;

		case "delete-campaigns":
			CampaignCache.getClientInstance(client).deleteCampaigns();
			break;

		case "delete-campaign":
			CampaignCache.getClientInstance(client).deleteCampaign(key);
			break;

		case "clear-fqs":
			FreqSetCache.getClientInstance(client).clear();
			break;

		case "clear-fq":
			FreqSetCache.getClientInstance(client).remove(key);
			break;

		case "show-fq":
			FreqSetCache.getClientInstance(client).get(key);
			break;

		default:

		}

		client.shutdown();

		System.exit(1);

	}
}
