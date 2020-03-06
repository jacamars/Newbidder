package com.jacamars.dsp.crosstalk.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.crosstalk.budget.CrosstalkConfig;
import com.jacamars.dsp.rtb.shared.TokenData;

public class UserRecord {

	public int id;
	public String customer_id;
	public String sub_id;
	public String username;
	public String password;
	public String company;
	public String email;
	public String telephone;
	public String firstname;
	public String lastname;
	public String address;
	public String citystate;
	public String country;
	public String postalcode;
	public String about;
	public String picture;
	public String title;
	public String description;

	
	public UserRecord() {
		
	}
	
	public UserRecord(TokenData td)  throws Exception {
		Connection conn =  CrosstalkConfig.getInstance().getConnection();
		String sql = "select * from users where username=? and customer_id=?";
		PreparedStatement p = conn.prepareStatement(sql);
		p.setString(1, td.username);
		p.setString(2,  td.customer);
		ResultSet rs = p.executeQuery();
		
		if (!rs.next()) {
			throw new Exception("No such user record");
		}
		id = rs.getInt("id");
		customer_id = rs.getString("customer_id");
		sub_id = rs.getString("sub_id");
		username = rs.getString("username");
		password = rs.getString("password");
		company = rs.getString("company");
		email = rs.getString("email");
		telephone = rs.getString("telephone");
		firstname = rs.getString("firstname");
		lastname = rs.getString("lastname");
		address = rs.getString("address");
		citystate = rs.getString("citystate");
		country = rs.getString("country");
		postalcode = rs.getString("postalcode");
		about = rs.getString("about");
		picture = rs.getString("picture");
		title = rs.getString("title");
		description = rs.getString("description");
		
	}
	
	public void toSql() throws Exception {
		if (id == 0)
			doNew();
		else
			doUpdate();
	}
	
	void doNew() throws Exception {
		Connection conn =  CrosstalkConfig.getInstance().getConnection();
		String sql = "insert into users (customer_id,"
				+"sub_id,"
				+"username,"
				+"password,"
				+"company,"
				+"email,"
				+"telephone,"
				+"firstname,"
				+"lastname,"
				+"address,"
				+"citystate,"
				+"country,"
				+"postalcode,"
				+"about,"
				+"title,"
				+"picture,"
				+"description"
				+") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		PreparedStatement p = conn.prepareStatement(sql);
		int k = 1;
		k = insert(k,p,customer_id);
		k = insert(k,p,sub_id);
		k = insert(k,p,password);
		k = insert(k,p,username);
		k = insert(k,p,company);
		k = insert(k,p,email);
		k = insert(k,p,telephone);
		k = insert(k,p,firstname);
		k = insert(k,p,lastname);
		k = insert(k,p,address);
		k = insert(k,p,citystate);
		k = insert(k,p,country);
		k = insert(k,p,postalcode);
		k = insert(k,p,about);
		k = insert(k,p,title);
		k = insert(k,p,picture);
		k = insert(k,p,description);
		
		ResultSet rs = p.executeQuery();
		id = rs.getInt("id");
	}
	
	void doUpdate() throws Exception {
		Connection conn =  CrosstalkConfig.getInstance().getConnection();
		String sql = "update  users set customer_id=?,"
				+"sub_id=?,"
				+"username=?,"
				+"password=?,"
				+"company=?,"
				+"email=?,"
				+"telephone=?,"
				+"firstname=?,"
				+"lastname=?,"
				+"address=?,"
				+"citystate=?,"
				+"country=?,"
				+"postalcode=?,"
				+"about=?,"
				+"title=?,"
				+"picture=?,"
				+"description=? where id=?";
		PreparedStatement p = conn.prepareStatement(sql);
		int k = 1;
		k = insert(k,p,customer_id);
		k = insert(k,p,sub_id);
		k = insert(k,p,username);
		k = insert(k,p,password);
		k = insert(k,p,company);
		k = insert(k,p,email);
		k = insert(k,p,telephone);
		k = insert(k,p,firstname);
		k = insert(k,p,lastname);
		k = insert(k,p,address);
		k = insert(k,p,citystate);
		k = insert(k,p,country);
		k = insert(k,p,postalcode);
		k = insert(k,p,about);
		k = insert(k,p,title);
		k = insert(k,p,picture);
		k = insert(k,p,description);
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
}
