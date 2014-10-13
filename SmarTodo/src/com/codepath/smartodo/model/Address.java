package com.codepath.smartodo.model;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Address")
public class Address extends ParseObject {
	private static final String USER_KEY = "user";
	public static final String NAME_KEY = "name";
	private static final String LOCATION_KEY = "location";

	public String getName() {
		return getString(NAME_KEY);
	}
	
	public void setName(String value) {
		super.put(NAME_KEY, value);
	}
	
	public ParseUser getUser() {
		return super.getParseUser(USER_KEY);
	}
	
	public void setUser(ParseUser value) {
		super.put(USER_KEY, value);
	}
	
	public ParseGeoPoint getLocation() {
		return super.getParseGeoPoint(LOCATION_KEY);
	}
	
	public void setLocation(ParseGeoPoint value) {
		super.put(LOCATION_KEY, value);
	}
}
