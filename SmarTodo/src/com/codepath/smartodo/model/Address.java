package com.codepath.smartodo.model;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Address")
public class Address extends ParseObject {
	private static final String USER_KEY = "user";
	public static final String NAME_KEY = "name";
	public static final String STREET_ADDRESS_KEY = "street_address";
	private static final String LOCATION_KEY = "location";

	public String getName() {
		try {
			return fetchIfNeeded().getString(NAME_KEY);
		} catch (ParseException e) {
			Log.e("error", e.getMessage(), e);
			return null;
		}
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
	
	public String getStreetAddress() {
		return getString(STREET_ADDRESS_KEY);
	}
	
	public void setStreetAddress(String value) {
		super.put(STREET_ADDRESS_KEY, value);
	}
	
	public ParseGeoPoint getLocation() {
		return super.getParseGeoPoint(LOCATION_KEY);
	}
	
	public void setLocation(ParseGeoPoint value) {
		super.put(LOCATION_KEY, value);
	}
}
