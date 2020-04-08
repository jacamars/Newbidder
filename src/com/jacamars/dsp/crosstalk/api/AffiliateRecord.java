package com.jacamars.dsp.crosstalk.api;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.crosstalk.budget.CrosstalkConfig;
import com.jacamars.dsp.rtb.shared.TokenData;

public class AffiliateRecord {

	public int id;
	public String customer_id;
	public String customer_name;
	public String email;
	public String telephone;
	public String firstname;
	public String lastname;
	public String address;
	public String citystate;
	public String country;
	public String postalcode;
	public BigDecimal budget;

	
	public AffiliateRecord() {
		
	}
	
	public AffiliateRecord(TokenData td)  throws Exception {
		Connection conn =  CrosstalkConfig.getInstance().getConnection();
		String sql = "select * from companies where customer_id=?";
		PreparedStatement p = conn.prepareStatement(sql);
		ResultSet rs = p.executeQuery();
		
		if (!rs.next()) {
			throw new Exception("No such affiliate record");
		}
		id = rs.getInt("id");
		customer_id = rs.getString("customer_id");
		customer_name = rs.getString("customer_name");
		email = rs.getString("email");
		telephone = rs.getString("telephone");
		firstname = rs.getString("firstname");
		lastname = rs.getString("lastname");
		address = rs.getString("address");
		citystate = rs.getString("citystate");
		country = rs.getString("country");
		postalcode = rs.getString("postalcode");
		budget = rs.getBigDecimal("budget");
		
	}
	
	public void toSql() throws Exception {
		if (id == 0)
			doNew();
		else
			doUpdate();
	}
	
	void doNew() throws Exception {
		Connection conn =  CrosstalkConfig.getInstance().getConnection();
		String sql = "insert into companies (customer_id,"
				+"customer_name,"
				+"email,"
				+"telephone,"
				+"firstname,"
				+"lastname,"
				+"address,"
				+"citystate,"
				+"country,"
				+"postalcode,"
				+"budget"
				+") values(?,?,?,?,?,?,?,?,?,?,?);";
		PreparedStatement p = conn.prepareStatement(sql);
		int k = 1;
		k = insert(k,p,customer_id);
		k = insert(k,p,customer_name);
		k = insert(k,p,email);
		k = insert(k,p,telephone);
		k = insert(k,p,firstname);
		k = insert(k,p,lastname);
		k = insert(k,p,address);
		k = insert(k,p,citystate);
		k = insert(k,p,country);
		k = insert(k,p,postalcode);
		k = insert(k,p,budget);
		
		id = p.executeUpdate();
	}
	
	void doUpdate() throws Exception {
		Connection conn =  CrosstalkConfig.getInstance().getConnection();
		String sql = "update  companies set customer_id=?,"
				+"customer_name=?"
				+"email=?,"
				+"telephone=?,"
				+"firstname=?,"
				+"lastname=?,"
				+"address=?,"
				+"citystate=?,"
				+"country=?,"
				+"postalcode=?,"
				+"budget=?"
				+" where id=?";
		PreparedStatement p = conn.prepareStatement(sql);
		int k = 1;
		k = insert(k,p,customer_id);
		k = insert(k,p,customer_name);
		k = insert(k,p,email);
		k = insert(k,p,telephone);
		k = insert(k,p,firstname);
		k = insert(k,p,lastname);
		k = insert(k,p,address);
		k = insert(k,p,citystate);
		k = insert(k,p,country);
		k = insert(k,p,postalcode);
		k = insert(k,p,budget);
		k = insert(k,p,id);
		
		p.execute();
	}
	
	int insert(int k,PreparedStatement p, String value) throws Exception {
		if (value == null)
			p.setNull(k, Types.VARCHAR);
		else
			p.setString(k,  value);
		return ++k;
	}
	
	int insert(int k,PreparedStatement p, int value) throws Exception {
		p.setInt(k,  value);
		return ++k;
	}
	
	int insert(int k, PreparedStatement p, BigDecimal value) throws Exception {
		p.setBigDecimal(k, value);
		return ++k;
	}
}
