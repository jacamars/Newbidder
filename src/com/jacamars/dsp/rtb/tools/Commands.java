package com.jacamars.dsp.rtb.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;

import com.jacamars.dsp.rtb.commands.*;
import com.jacamars.dsp.rtb.common.Campaign;

import com.jacamars.dsp.rtb.common.RecordedBid;

import com.jacamars.dsp.rtb.shared.BidCachePool;
import com.jacamars.dsp.rtb.shared.CampaignCache;
import com.jacamars.dsp.rtb.shared.FreqSetCache;
import com.jacamars.dsp.rtb.shared.PortableJsonFactory;
import com.jacamars.dsp.rtb.shared.WatchInterface;

/**
 * A simple class that sends and receives commands from RTB4FREE bidders.
 *
 * @author Ben M. Faul
 */

public class Commands implements WatchInterface {
	HazelcastInstance client;
	CountDownLatch latch;
	String [] args;

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
		
		Commands c = new Commands(args);
		c.run();
	}
	
	public Commands(String[] args) throws Exception {
		this.args = args;
	}
	
	public void run() throws Exception {
		String cluster[] = null;
		String command = null; // "list-bidders";
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
			cluster = new String[3];
			cluster[0] = "localhost:5701";
			cluster[1] = "localhost:5702";
			cluster[2] = "localhost:5703";

		}

		ClientConfig clientConfig = new ClientConfig();
		clientConfig.getSerializationConfig().addPortableFactory(PortableJsonFactory.FACTORY_ID,
				new PortableJsonFactory());
		Echo.registerWithHazelCast(clientConfig);
		Campaign.registerWithHazelCast(clientConfig);
		// clientConfig.setProperty("hazelcast.logging.type", "slf4j");

		clientConfig.addAddress(cluster);

		client = HazelcastClient.newHazelcastClient(clientConfig);

		String str = null;
		int count = 0;
		BufferedReader obj = new BufferedReader(new InputStreamReader(System.in));   

		boolean running = true;
		String oCmd = command;
		
		BidCachePool bcp = BidCachePool.getClientInstance(client);
		
		while(running) {
			if (oCmd == null) {
				System.out.print("command>");
				command = obj.readLine().trim();
			}
			List<String> sub = null;
			if (command.contains(" ")) {
				sub = new ArrayList<String>();
				String q [] = command.split(" ");
				for (i=1;i<q.length;i++) {
					sub.add(q[i]);
				}
				command = q[0];
			}
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
				key = sub.get(0);
				Object x = BidCachePool.getClientInstance(client).get(key);
				if (x == null)
					str = key + " = null";
				else
					str = key + " = " + DbTools.mapper.writer().withDefaultPrettyPrinter().writeValueAsString(x);
				System.out.println(str);
				break;

			case "getbid":
				key = sub.get(0);
				RecordedBid rb = BidCachePool.getClientInstance(client).getBid(key);
				if (rb == null)
					str = key + " = null";
				str = key + " = " + DbTools.mapper.writer().withDefaultPrettyPrinter().writeValueAsString(rb);
				System.out.println(str);
				break;

			case "getvideo":
				key = sub.get(0);
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
				str = DbTools.mapper.writer().withDefaultPrettyPrinter().writeValueAsString(clist);
				System.out.println(str);
				break;
				
			case "get-campaign":
				key = sub.get(0);
				Campaign c = CampaignCache.getClientInstance(client).getCampaign(key);
				str = DbTools.mapper.writer().withDefaultPrettyPrinter().writeValueAsString(c);
				System.out.println(str);
				break;

			case "add-campaign":
				String fileName = sub.get(0);
				String contents = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
		    	c = mapper.readValue(contents, Campaign.class);
				CampaignCache.getClientInstance(client).addCampaign(c.adId,c);
				break;

			case "add-campaigns":
				key = sub.get(0);
				CampaignCache.getClientInstance(client).loadDatabase(key);
				break;

			case "delete-campaigns":
				CampaignCache.getClientInstance(client).deleteCampaigns();
				break;

			case "delete-campaign":
				key = sub.get(0);
				CampaignCache.getClientInstance(client).deleteCampaign(key);
				break;

			case "clear-fqs":
				FreqSetCache.getClientInstance(client).clear();
				break;

			case "clear-fq":
				key = sub.get(0);
				FreqSetCache.getClientInstance(client).remove(key);
				break;

			case "show-fq":
				key = sub.get(0);
				FreqSetCache.getClientInstance(client).get(key);
				break;
				
			case "show-events":
				if (sub == null) {
					list = BidCachePool.getClientInstance(client).getMembersNames();
					for (String member : list) {
						Echo m = BidCachePool.getClientInstance(client).getMemberStatus(member);
						str = member + ":\n" + DbTools.mapper.writer().withDefaultPrettyPrinter().writeValueAsString(m.events)
								+ "\n**************";
						System.out.println(str);
					}
				}
				break;
				
			case "get":
				Object mx = doGet(sub);
				if (mx == null)
					System.out.println("null");
				else
					System.out.println(DbTools.mapper.writer().withDefaultPrettyPrinter().writeValueAsString(mx));
				break;
			case "set":
				doSet(sub);
				break;
			
			case "watch":
				 BidCachePool.getClientInstance(client).addWatch(sub.get(0), sub.get(1), this);
				 break;

			case "exit":
				running = false;
				break;
				
			default:
				if (command.trim().length()!=0)				
					System.out.println("???");
			}
			command = null;
		}

		client.shutdown();

		System.exit(1);

	}
	
	public Object doGet(List<String> sub) {
		Object r = null;
		switch(sub.get(0)) {
		case "misc":
			r = BidCachePool.getClientInstance(client).get(sub.get(1));
			break;
		default:
			r = BidCachePool.getClientInstance(client).get(sub.get(0));
			break;
		}
		
		return r;
		
	}
	
	public void doSet(List<String> sub) throws Exception {
		switch(sub.get(0)) {
		case "misc":
			BidCachePool.getClientInstance(client).set(sub.get(1), sub.get(2), Long.parseLong(sub.get(3)));
			break;
		default:

			break;
		}
	}

	@Override
	public void callback(String which, String key) {
		System.out.println("WATCHER SHARED MAP: " + which + " has deleted: " + key);
		
	}
}
