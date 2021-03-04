package com.jacamars.dsp.rtb.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jacamars.dsp.rtb.common.HttpPostGet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GetAds implements Runnable {

	protected static final Logger logger = LoggerFactory.getLogger(GetAds.class);
	private static volatile Map<String,String> adsystem = new ConcurrentHashMap();
	private String pub;
	private String url;
	Thread me;

	static volatile Set<GetAds> completed = ConcurrentHashMap.newKeySet();

	public GetAds(String url, String expire) throws Exception {
		this.url = url;
		pub = removeLastDot(url);
		me = new Thread(this);
	}

	String removeLastDot(String str) {
		int i = str.length() - 1;
		while (i > 0) {
			String s = str.substring(i, i + 1);
			if (s.equals(".")) {
				str = str.substring(0, i);
				return str;
			}
			i--;
		}
		return str;
	}

	public static GetAds getCompleted() {
		if (completed.isEmpty())
			return null;
		GetAds a = completed.iterator().next();
		completed.remove(a);
		return a;
	}
	
	public static void preload() throws Exception {
		var connection = Ads.getInstance().getConnection();
		var st = connection.prepareStatement("SELECT * FROM adsystem_domain");
		var rs = st.executeQuery();
		while(rs.next()) {
			var domain = rs.getString("DOMAIN");
			adsystem.put(domain,domain);
		}
	}

	public static boolean pending(String url) {
		var it = completed.iterator();
		while (it.hasNext()) {
			if (it.next().getUrl().equals(url))
				return true;
		}
		return false;
	}

	void getAds() throws Exception {
		String data = null;
		HttpPostGet http = new HttpPostGet();
		data = http.sendGet("http://" + url + "/ads.txt");
		if (data != null)
			parse(data, "ads");
	}

	void getAppAds() throws Exception {
		HttpPostGet http = new HttpPostGet();
		String data = http.sendGet("http://" + url + "/app-ads.txt");
		if (data != null)
			parse(data, "app-ads");
	}

	public String getUrl() {
		return url;
	}

	void parse(String data, String type) throws Exception {
		String[] lines = data.split("\n");
		PreparedStatement st = null;
		String sql = "DELETE FROM adstxt where APP_ADS=? AND SITE_DOMAIN=?";
		var connection = Ads.getInstance().getConnection();
		
		if (lines.length < 2)
			return;
			
		st = connection.prepareStatement(sql);
		st.setString(1, type);
		st.setString(2, url);
		st.execute();
		
		sql = "DELETE FROM adstxt_contentdistributor where APP_ADS=? AND SITE_DOMAIN=?";
		st = connection.prepareStatement(sql);
		st.setString(1, type);
		st.setString(2, url);
		st.execute();

		sql = "INSERT INTO adstxt_contentdistributor (APP_ADS,SITE_DOMAIN) VALUES (?,?)";
		st = connection.prepareStatement(sql);
		st.setString(1, type);
		st.setString(2, url);
		// TBD Need to add expires.
		st.execute();
		
		sql = "DELETE FROM adstxt WHERE APP_ADS=? AND SITE_DOMAIN=?";
		st = connection.prepareStatement(sql);
		st.setString(1, type);
		st.setString(2, url);
		// TBD Need to add expires.
		st.execute();
		
		String field1 = null;
		String field2 = null;
		String field3 = null;
		String field4 = "";
		String comment = "";
		
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			line = line.trim();
			if (!(line.startsWith("#") || line.length() == 0)) {
				field1 = null;
				field2 = null;
				field3 = null;
				field4 = "";
				comment = "";
				if (line.contains("#")) {
					int j = line.indexOf("#");
					comment = line.substring(j + 1).trim();
					line = line.substring(0, j);
				}
				String[] parts = line.trim().split(",");
				sql = "INSERT INTO adstxt (APP_ADS,SITE_DOMAIN,EXCHANGE_DOMAIN,SELLER_ACCOUNT_ID,ACCOUNT_TYPE,TAG_ID,ENTRY_COMMENT) VALUES(?,?,?,?,?,?,?);";
				switch (parts.length) {
				case 1:
					field1 = parts[0].trim();
					sql = null;
					break;
				case 2:
					field1 = parts[0].trim();
					field2 = parts[1].trim();
					sql = null;
					
					break;
				case 3:
					field1 = parts[0].trim();
					field2 = parts[1].trim();
					field3 = parts[2].trim();

					break;
				case 4:
					field1 = parts[0].trim();
					field2 = parts[1].trim();
					field3 = parts[2].trim();
					field4 = parts[3].trim();

					break;
				default:
					break;
				}
				try {
					if (sql != null) {
						st = connection.prepareStatement(sql);
						st.setString(1, type);
						st.setString(2, url);
						st.setString(3, field1);
						st.setString(4, field2);
						st.setString(5, field3);
						st.setString(6, field4);
						st.setString(7, comment);
						st.execute();

					}
				} catch (Exception error) {
					System.out.println("@1="+field1);
					System.out.println("@2="+field2);
					System.out.println("@3="+field3);
					System.out.println("@4="+field4);
					error.printStackTrace();
				}
			}
		}
	}

	String checkComment(String field) {
		int i = field.indexOf("#");
		if (i < 0)
			return null;
		return field.substring(i).trim();
	}

	String splitComment(String field) {
		int i = field.indexOf("#");
		if (i < 0)
			return field;
		return field.substring(0, i).trim();
	}

	public void start() {
		me.start();
	}

	public void run() {
		try {
			getAds();
			getAppAds();
		} catch (Exception error) {
			error.printStackTrace();
		}
		completed.add(this);
	}
}
