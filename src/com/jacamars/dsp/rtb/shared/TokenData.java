package com.jacamars.dsp.rtb.shared;

import java.io.Serializable;

public class TokenData implements Serializable {

	public String customer;
	public String username;
	public String role;
	
	public TokenData() {
		
	}
	
	public TokenData(String customer, String username, String role) {
		this.customer = customer;
		this.username = username;
		this.role = role;
	}
	
	public String getShadowCustomer() {
		return customer;
	}
	
	public static String getInstance(TokenData d) {
		// Using customer, username, password, login 
		return null;
	}
	
	public boolean isRtb4FreeSuperUser() {
		if (customer.equals("rtb4free") && role.equals("superuser"))
			return true;
		return false;
	}
	
	public boolean isAuthorized(String cid) {
		if (isRtb4FreeSuperUser())
			return true;
		if (cid.equals(customer))
			return true;
		return false;
	}
	
}
