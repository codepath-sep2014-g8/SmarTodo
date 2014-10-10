package com.codepath.smartodo.model;

import java.util.Date;
import java.util.List;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("TodoList")
public class TodoList extends ParseObject {
	private static final String NAME_KEY = "name";
	private static final String COMPLETED_KEY = "completed";
	private static final String ADDRESS_KEY = "address";
	private static final String NOTIFICATIONTIME_KEY = "notificationtime";
	private static final String SHARING_KEY = "sharing";

	public boolean isCompleted() {
		return getBoolean(COMPLETED_KEY);
	}
	
	public void setCompleted(boolean value) {
		super.put(COMPLETED_KEY, value);
	}
	
	public String getName() {
		return super.getString(NAME_KEY);
	}
	
	public void setName(String value) {
		super.put(NAME_KEY, value);
	}
	
	public Address getAddress() {
		return (Address) super.getParseObject(ADDRESS_KEY);
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
	
	/**
	 * <p>Appends the users to the current list.
	 * <p>Note that it is not currently possible to atomically add and remove items from an array in the same save. You will have to call save in between every different kind of array operation.
	 * 
	 * @param users
	 */
	public void addToSharing(List<User> users) {
		super.addAll(SHARING_KEY, users);
	}
	
	/**
	 * <p>Appends the user to the current list.
	 * <p>Note that it is not currently possible to atomically add and remove items from an array in the same save. You will have to call save in between every different kind of array operation.
	 * 
	 * @param user
	 */
	public void addToSharing(User user) {
		super.add(SHARING_KEY, user);
	}
	
	/**
	 * <p>Remove the users from the existing list.
	 * <p>Note that it is not currently possible to atomically add and remove items from an array in the same save. You will have to call save in between every different kind of array operation.
	 * 
	 * @param users
	 */
	public void removeAllFromSharing(List<User> users) {
		super.removeAll(SHARING_KEY, users);
	}
	
	@SuppressWarnings("unchecked")
	public List<User> getSharing() {
		return (List<User>) super.getParseObject(SHARING_KEY);
	}
}
