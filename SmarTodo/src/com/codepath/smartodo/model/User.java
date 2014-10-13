package com.codepath.smartodo.model;

import com.parse.ParseClassName;
import com.parse.ParseUser;

@ParseClassName("_User")
public class User extends ParseUser {
	private static final String PHONENUMBER_KEY = "phonenumber";
	private static final String REALNAME_KEY = "realname";

	public void setPhoneNumber(String value) {
		put(PHONENUMBER_KEY, value);
	}
	
	public String getPhoneNumber() {
		return getString(PHONENUMBER_KEY);
	}

	public void setRealName(String value) {
		put(REALNAME_KEY, value);
	}
	
	public String getRealName() {
		return getString(REALNAME_KEY);
	}
}
