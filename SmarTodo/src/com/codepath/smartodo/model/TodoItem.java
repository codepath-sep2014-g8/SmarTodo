package com.codepath.smartodo.model;

import java.util.Date;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

@ParseClassName("TodoItem")
public class TodoItem extends ParseObject {
	public static final String TEXT_KEY = "text";
	private static final String COMPLETED_KEY = "completed";
	private static final String ADDRESS_KEY = "address";
	private static final String NOTIFICATIONTIME_KEY = "notificationtime";

	public boolean isCompleted() {
		try {
			return fetchIfNeeded().getBoolean(COMPLETED_KEY);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void setCompleted(boolean value) {
		super.put(COMPLETED_KEY, value);
	}
	
	public String getText() {
		try {
			return super.fetchIfNeeded().getString(TEXT_KEY);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void setText(String value) {
		super.put(TEXT_KEY, value);
	}
	
	public Address getAddress() {
		try {
			return (Address) super.fetchIfNeeded().getParseObject(ADDRESS_KEY);
		} catch (ParseException e) {
			Log.e("error", e.getMessage(), e);
			return null;
		}
	}
	
	public void setAddress(Address value) {
		super.put(ADDRESS_KEY, value);
	}
	
	public void setNotificationTime(Date datetime) {
		super.put(NOTIFICATIONTIME_KEY, datetime);
	}
	
	public Date getNotificationTime() {
		return super.getDate(NOTIFICATIONTIME_KEY);
	}

	@Override
	public String toString() {
		return "TodoItem [isCompleted()=" + isCompleted() + ", getText()="
				+ getText() + ", getAddress()=" + getAddress()
				+ ", getNotificationTime()=" + getNotificationTime() + "]";
	}
	
	
}
