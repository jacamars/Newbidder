package com.jacamars.dsp.crosstalk.budget;

import java.sql.Connection;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jacamars.dsp.rtb.common.Node;
import com.jacamars.dsp.rtb.shared.TokenData;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.tools.JdbcTools;


/**
 * The target object used by the AccountingCampaign object/.t
 * @author Ben M. Faul
 *
 */
public class Targeting {
	
	static public final Map<String,Integer> deviceTypes = new HashMap<>();
	static {
		deviceTypes.put("mobile", 1);
		deviceTypes.put("desktop", 2);
		deviceTypes.put("smarttv", 3);
		deviceTypes.put("phone", 4);
		deviceTypes.put("tablet", 5);
		deviceTypes.put("mobile-not(phone or tablet)", 6);
		deviceTypes.put("mobile", 7);
	}

	public static final String NONE = "NONE";
	public static final String BLACKLIST = "BLACKLIST";
	public static final String WHITELIST = "WHITELIST";

	public int id;
	public String name;
	
	/**
	 * Its either a listofdomains or a symbol that is al ist.
	 */
	
	public List<String> listofdomains = new ArrayList<String>();
	public String listofdomainsSYMBOL = null;

	public String carrier = "";
	public String connectionType = "";

	public String os = "";
	public String make = "";
	public String model = "";
	public String os_version = "";
	public String browser = "";
	public String country = "";


	public List<Double> geo = new ArrayList<>();

	public List<Node> nodes = new ArrayList<Node>();

	public String iab_category = "";
	public String iab_category_blklist = "";
	
	public String customer_id;

	List<String> listofpages = new ArrayList<String>();
	List<String> transparency = new ArrayList<String>();
	List<String> devicetypes = new ArrayList<String>();
	List<Integer> connectionTypes = new ArrayList<>();

	protected ObjectNode myNode;

	public String list_of_domains; // converts to listofdomains
	public String domain_targetting; 
	public String devicetypes_str;

	protected String LIST_OF_PAGES = "listofpages";
	protected String TRANSPARENCY = "pagetransparency";
	protected String RTB_STANDARD = "rtb_standard";

	public static Targeting getInstance(int id, TokenData td) throws Exception {
		String select = "select * from targets where id="+id;
		var conn = CrosstalkConfig.getInstance().getConnection();
		var stmt = conn.createStatement();
		var prep = conn.prepareStatement(select);
		ResultSet rs = prep.executeQuery();
		
		ArrayNode inner = JdbcTools.convertToJson(rs);
		ObjectNode y = (ObjectNode) inner.get(0);
		
		if (y == null)
			throw new Exception("Missing target id: " + id + " in database");
		var t = new Targeting(y);
		if (td == null || td.isAuthorized(t.customer_id))
			return t;
		return null;
	}
	public Targeting() {

	}

	public Targeting(ObjectNode myNode)
			throws Exception {
		if (myNode  == null)
			throw new Exception("Missing target!");
		this.myNode = myNode;
		process();
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////
	public void process() throws Exception {
		customer_id = myNode.get("customer_id").asText();
		if (myNode.get("name") != null)
			name = myNode.get("name").asText();
		
		if (myNode.get("id") != null) {
			var x = myNode.get("id").asInt();
			if (x != 0)
				id = x;
		}
		
		if (myNode.get("list_of_domains") != null) {
			String str = myNode.get("list_of_domains").asText(null);
			if (str != null && str.trim().length()>0) {
				
				if (str.startsWith("$")) {
					listofdomainsSYMBOL = str;
				} else
					getList(listofdomains, str);
			}
			list_of_domains = str;
		}

		if (myNode.get("domain_targetting") != null)
			domain_targetting = myNode.get("domain_targetting").asText().trim();
		
		if (myNode.get("carrier") != null)
			carrier = myNode.get("carrier").asText().trim();
		if (myNode.get("country") != null)
			country = myNode.get("country").asText().trim();
		if (myNode.get("os") != null)
			os = myNode.get("os").asText().trim();

		if (myNode.get(LIST_OF_PAGES) != null) {
			String test = myNode.get(LIST_OF_PAGES).asText(null);
			if (test != null) {
				if (!test.equals("X") && test.length() != 0) {
					getList(listofpages, test);
					test = myNode.get(TRANSPARENCY).asText(null);
					if (test != null)
						getList(transparency, test);
				}
			}
		}
		
		if (myNode.get("devicetype") != null) {
			String test = myNode.get("devicetype").asText(null);
			if (test != null && test.length() != 0) {
				getList(devicetypes, test);
				devicetypes_str = test;
			}
		}

		if (myNode.get("make") != null)
			make = myNode.get("make").asText().trim();
		if (myNode.get("model") != null)
			model = myNode.get("model").asText().trim();
		if (myNode.get("geo") != null) {
			ArrayNode an = (ArrayNode)myNode.get("geo");
			if (an.size() != 0) {
				geo = new ArrayList<>();
				for (int i=0; i<an.size();i+=3) {
					if (an.get(i).doubleValue() != 0) {
						geo.add(an.get(i).doubleValue());
						geo.add(an.get(i+1).doubleValue());
						geo.add(an.get(i+2).doubleValue());
					}
				}
			}
		}
	
		// connectionTYpe is needed here

		// td = Test.getValue(geo_latitude,"geo_range",rs);
		// geo_range = td.value; delta |= ts.delta;

		if (myNode.get("iab_category") != null)
			iab_category = myNode.get("iab_category").asText();
		if (myNode.get("iab_category_blklist") != null)
			iab_category_blklist = myNode.get("iab_category_blklist").asText();	

		if (myNode.get("connectiontype") != null && myNode.get("connectiontype") instanceof MissingNode == false) {
			String connections = myNode.get("connectiontype").asText();
			if (connections.equals("null")==false && connections.length()>0)
				getIntegerList(connectionTypes,connections);
		} else {
			connectionTypes.clear();
		}
		
		nodes = compile();
	}

	// /////////////////////////////////////////////////////////////////////////////////////////

	public static boolean getList(List<String> list, String text) {
		boolean delta = false;

		text = text.trim();
		if (text.contains(",")) {
			if (text.startsWith("["))
				text = text.substring(1);
			if (text.endsWith("]"))
				text = text.substring(0,text.length()-1);
		}
		List<String> temp = new ArrayList<String>();
		temp.addAll(list);
		
		/**
		 * Ok, I presume the user doesn't add dups, though the bidder doesn't care it will turn it into a Set anyway.
		 */
		if (text != null) {
			String[] parts = text.split(",");
			for (String part : parts) {
				part = part.trim();
				part = part.replaceAll("\t","");
				part = part.replaceAll("\n", "");
				part = part.replaceAll("\r", "");
				list.add(part);
			}
		}

		delta |= list.size() != temp.size();
		return delta;
	}
	
	public static boolean getIntegerList(List<Integer> list, String text) {
		boolean delta = false;
		
		if (text == null || text.equals("null"))
			return true;

		text = text.trim();
		if (text.contains(",")) {
			if (text.startsWith("["))
				text = text.substring(1);
			if (text.endsWith("]"))
				text = text.substring(0,text.length()-1);
		}
		
		if (text != null) {
			String[] parts = text.split(",");
			for (String part : parts) {
				part = part.trim();
				part = part.replaceAll("\t","");
				part = part.replaceAll("\n", "");
				part = part.replaceAll("\r", "");
				Integer test = Integer.parseInt(part);
				if (!list.contains(test)) {
					delta = true;
					list.add(test);
				}
			}
		}

		return delta;
	}

	public List<Node> compile() throws Exception {
		nodes = new ArrayList<Node>();
		Node n = null;

		//
		// Domain blacklist/whitelist Can be an actual list or a symbol
		//
		if (listofdomains != null && listofdomains.size() != 0) {
			boolean bl = true;
			if ((bl=domain_targetting.equalsIgnoreCase("BLACKLIST"))==true)
				n = new Node("blacklist", "domain", Node.NOT_MEMBER,
						listofdomains);
			else if (domain_targetting.equalsIgnoreCase("WHITELIST"))
				n = new Node("whitelist", "domain", Node.MEMBER,
						listofdomains);

			if (n != null) {
				if (bl)
					n.notPresentOk = true;
				else
					n.notPresentOk = false;
				nodes.add(n);

				if (listofpages != null && listofpages.size() != 0) {
					boolean notPresentOk = false;
					int op = Node.STRINGIN;
					if (transparency.contains("N"))
						op = Node.NOT_STRINGIN;
					if (transparency.contains("NOTREQUIRED"))
						notPresentOk = true;

					n = new Node("pagetest", "site.page", op, listofpages);
					n.notPresentOk = notPresentOk;
					nodes.add(n);
				}
			}
		} else {
			if (listofdomainsSYMBOL != null) {
				Node n1 = null;
				boolean bl = true;
				if ((bl=domain_targetting.equalsIgnoreCase("BLACKLIST"))==true) {
					n = new Node("blacklist", "domain", Node.NOT_MEMBER,
							listofdomainsSYMBOL);
					//n1 = new Node("blacklist", "app.domain", Node.NOT_MEMBER,
					//		listofdomainsSYMBOL);
				}
				else if (domain_targetting.equalsIgnoreCase("WHITELIST")) {
					n = new Node("whitelist", "domain", Node.MEMBER,
							listofdomainsSYMBOL);
					//n1 = new Node("whitelist", "app.domain", Node.MEMBER,
					//		listofdomainsSYMBOL);
					
				}

				if (bl)
					n.notPresentOk = true;
				else
					n.notPresentOk = false;
				//n1.notPresentOk = true;

				nodes.add(n);
				//nodes.add(n1);
			}
		}
		
		if (devicetypes != null && devicetypes.size() != 0) {
			List<Integer> list = new ArrayList<Integer>();
			for (String s : devicetypes) {
				Integer dt = deviceTypes.get(s);
				if (dt != null) {
					list.add(dt);
				}
			}
			if (list.size() > 0) {
				n = new Node("devicetypes", "device.devicetype", Node.INTERSECTS, list);
				n.notPresentOk = false;
				nodes.add(n);
			}
		}

		if (carrier.length() > 0 && !carrier.equals("null")) {
			String[] parts = carrier.split(",");
			n = new Node("carrier", "device.carrier", Node.MEMBER, parts);
			n.notPresentOk = false;
			nodes.add(n);
		}

		if (os.length() > 0 && !os.equals("null")) {
			List<String> list = new ArrayList<String>();
			getList(list, os);
			n = new Node("os", "device.os", Node.MEMBER, list);
			n.notPresentOk = false;
			nodes.add(n);

			if (os_version.length() > 0 && !os_version.equals("null")) {
				list = new ArrayList<String>();
				getList(list, os_version);
				n = new Node("os_version", "device.os.version", Node.MEMBER,
						list);
				n.notPresentOk = false;
				nodes.add(n);
			}
		}

		if (browser.length() > 0 & !browser.equals("null")) {
			List<String> list = new ArrayList<String>();
			getList(list, browser);
			n = new Node("browser", "device.browser", Node.MEMBER, list);
			n.notPresentOk = false;
			nodes.add(n);
		}

		if (country.length() > 0 && !country.equals("null")) {
			List<String> countries = setCountries(country);
			n = new Node("country", "device.geo.country", Node.MEMBER,
					countries);
			n.notPresentOk = false;
			nodes.add(n);
		}

		if (carrier.length() > 0 && !carrier.equals("null")) {
			String[] parts = carrier.split(",");
			n = new Node("carriers", "device.carrier", Node.MEMBER, parts);
			n.notPresentOk = false;
			nodes.add(n);
		}

		if (connectionType.length() > 0
				&& !connectionType.equals("null")) {
			String[] parts = connectionType.split(",");
			List<Integer> list = new ArrayList<Integer>();
			for (String part : parts) {
				list.add(Integer.parseInt(part));
			}
			n = new Node("", "device.connectiontype", Node.MEMBER, list);
			n.notPresentOk = false;
			nodes.add(n);
		}

		/////////////////////////////////////
		//
		// IAB Categories
		//
		if (iab_category.length() > 0 && !iab_category.equals("null")) {
			String[] parts = iab_category.split(",");
			for (int i = 0; i < parts.length; i++) {
				String s = parts[i];
				s = s.replaceAll("\"", "");
				parts[i] = s;
			}
			n = new Node("matching-categories", "site.cat", Node.INTERSECTS,
					parts);
			n.notPresentOk = false;
			Node n1 = new Node("matching-categories", "app.cat", Node.INTERSECTS,
					parts);
			n1.notPresentOk = false;
			List<Node> orList = new ArrayList<Node>();
			orList.add(n);
			orList.add(n1);

			Node ornode = new Node("ortest", null, Node.OR,orList);
			nodes.add(ornode);
		}
		if (iab_category_blklist.length() > 0 && !iab_category_blklist.equals("null")) {
			String[] parts = iab_category_blklist.split(",");
			for (int i = 0; i < parts.length; i++) {
				String s = parts[i];
				s = s.replaceAll("\"", "");
				parts[i] = s;
			}
			n = new Node("nonmatching-categories", "site.cat", Node.NOT_INTERSECTS,
					parts);
			n.notPresentOk = true;
			Node n1 = new Node("nonmatching-categories", "app.cat", Node.NOT_INTERSECTS,
					parts);
			n1.notPresentOk = true;
			List<Node> orList = new ArrayList<Node>(Arrays.asList(n, n1));;

			Node ornode = new Node("ortest", null, Node.OR,orList);
			nodes.add(ornode);
		}
		////////////////////////////////////////////////////////////////////////////////////////////////////////

		if (make.length() > 0 && !make.equals("null")) {
			String[] parts = make.split(",");
			n = new Node("make", "device.make", Node.MEMBER, parts);
			n.notPresentOk = false;
			nodes.add(n);
		}

		if (model.length() > 0 && !model.equals("null")) {
			String[] parts = model.split(",");
			n = new Node("model", "device.model", Node.MEMBER, parts);
			n.notPresentOk = false;
			nodes.add(n);
		}

		if (geo.size() > 0) {
			n = new Node("LATLONLIST", "device.geo", Node.INRANGE, geo);
			nodes.add(n);
		}

		/**
		 * Connection types
		 */
		if (connectionTypes.size() > 0) {
			n = new Node("connectiontypes", "device.connectiontype", Node.MEMBER, connectionTypes);
			n.notPresentOk = false;
			nodes.add(n);
		}

		return nodes;

	}

	/**
	 * Set the country, but fix US to USA. Strip off garbage characters like ' and "
	 * @param test String. The list of string comma separated.
	 * @return List. Returns a list of countries.
	 * @throws Exception on regex errors.
	 */
	public List<String> setCountries(String test) throws Exception {
		List<String> countries = new ArrayList<String>();
		if (test.length() > 0 && !test.equals("null")) {
			String parts[] = test.split(",");
			for (int i = 0; i < parts.length; i++) {
				String value = parts[i];
				value = value.replaceAll("\"", "");
				if (value.equals("US"))
					value = "USA";
				countries.add(value);
			}
		}
		return countries;
	}
	
	public void updateAttachedCampaigns() throws Exception {
		String sql = "SELECT id FROM campaigns where target_id=" + this.id;
		var conn = CrosstalkConfig.getInstance().getConnection();
		var stmt = conn.createStatement();
		var prep = conn.prepareStatement(sql);
		ResultSet rs = prep.executeQuery();
		while(rs.next()) {
			int id = rs.getInt("id");
			Campaign c = Campaign.getInstance(id,null);
			c.updated_at = System.currentTimeMillis();
			Campaign.toSql(c, conn);
			CampaignBuilderWorker w = new CampaignBuilderWorker(c);
			w.run();
		}
	}
	
	public static PreparedStatement toSql(Targeting c, Connection conn) throws Exception {
		if (c.id == 0) 
			return doNew(c, conn);
		return doUpdate(c, conn);
	}
	
	static PreparedStatement doNew(Targeting c, Connection conn) throws Exception {
		PreparedStatement p = null;
		String sql = "INSERT INTO targets (" 
				+"list_of_domains,"
				+"domain_targetting,"
				+"geo,"
				+"country,"
				+"carrier,"
				+"os,"
				+"make,"
				+"model,"
				+"devicetype,"
				+"iab_category,"
				+"iab_category_blklist,"
				+"created_at,"
				+"customer_id,"
				+"name) VALUES ("
				+"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
		
		p = conn.prepareStatement(sql);
		
		if (c.listofdomainsSYMBOL != null)
			p.setString(1, c.listofdomainsSYMBOL);
		else
			p.setString(1, asStringList(c.listofdomains));
		
		if (c.domain_targetting == null)
			p.setNull(2,Types.VARCHAR);
		p.setString(2, c.domain_targetting);
		if (c.geo == null || c.geo.size() == 0) 
			p.setNull(3,Types.ARRAY);
		else
			p.setArray(3, conn.createArrayOf("decimal", c.geo.toArray()));	
		if (c.country == null)
			p.setNull(4, Types.VARCHAR);
		else
			p.setString(4,c.country);
		if (c.carrier == null)
			p.setNull(5, Types.VARCHAR);
		else
			p.setString(5,c.carrier);
		if (c.os == null)
			p.setNull(6, Types.VARCHAR);
		else		
			p.setString(6,c.os);
		if (c.make == null)
			p.setNull(7, Types.VARCHAR);
		else
			p.setString(7,c.make);
		if (c.model == null)
			p.setNull(8, Types.VARCHAR);
		else
			p.setString(8,c.model);
		if (c.devicetypes.size() == 0)
			p.setNull(9, Types.VARCHAR);
		else
			p.setString(9, c.devicetypes_str);
		if (c.iab_category == null)
			p.setNull(10, Types.VARCHAR);
		else
			p.setString(10,c.iab_category);
		if (c.iab_category_blklist == null)
			p.setNull(11, Types.VARCHAR);
		else
			p.setString(11,c.iab_category_blklist);
		p.setTimestamp(12,new Timestamp(System.currentTimeMillis()));
		p.setString(13, c.customer_id);
		p.setString(14, c.name);
		
		return p;
	}
	
	static PreparedStatement doUpdate(Targeting c, Connection conn) throws Exception {
		PreparedStatement p = null;
		String sql = "UPDATE targets SET " 
		  +"list_of_domains=?,"
		  +"domain_targetting=?,"
		  +"geo=?,"
		  +"country=?,"
		  +"carrier=?,"
		  +"os=?,"
		  +"make=?,"
		  +"model=?,"
		  +"devicetype=?,"
		  +"iab_category=?,"
		  +"iab_category_blklist=?,"
		  +"updated_at=?,"
		  +"name=? WHERE id=?";
		
		p = conn.prepareStatement(sql);
		
		if (c.listofdomainsSYMBOL != null)
			p.setString(1, c.listofdomainsSYMBOL);
		else
			p.setString(1, c.list_of_domains);
		
		if (c.domain_targetting == null)
			p.setNull(2,Types.VARCHAR);
		p.setString(2, c.domain_targetting);
		if (c.geo == null || c.geo.size() == 0)
			p.setNull(3,Types.ARRAY);
		else
			p.setArray(3, conn.createArrayOf("decimal", c.geo.toArray()));		
		if (c.country == null)
			p.setNull(4, Types.VARCHAR);
		else
			p.setString(4,c.country);
		if (c.carrier == null)
			p.setNull(5, Types.VARCHAR);
		else
			p.setString(5,c.carrier);
		if (c.os == null)
			p.setNull(6, Types.VARCHAR);
		else		
			p.setString(6,c.os);
		if (c.make == null)
			p.setNull(7, Types.VARCHAR);
		else
			p.setString(7,c.make);
		if (c.model == null)
			p.setNull(8, Types.VARCHAR);
		else
			p.setString(8,c.model);
		if (c.devicetypes.size() == 0)
			p.setNull(9, Types.VARCHAR);
		else
			p.setString(9,c.devicetypes_str);
		if (c.iab_category == null)
			p.setNull(10, Types.VARCHAR);
		else
			p.setString(10,c.iab_category);
		if (c.iab_category_blklist == null)
			p.setNull(11, Types.VARCHAR);
		else
			p.setString(11,c.iab_category_blklist);
		p.setTimestamp(12,new Timestamp(System.currentTimeMillis()));
		p.setString(13, c.name);
		p.setInt(14, c.id);
		
		return p;
	}
	
	public static String asStringList(List<String> s) {
		String str = "";
		for (int i=0;i<s.size();i++) {
			str += s.get(i);
			if (i+1<s.size()) str += ",";
		}
		return str;
	}
}
