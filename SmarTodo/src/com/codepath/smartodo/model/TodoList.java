package com.codepath.smartodo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("TodoList")
public class TodoList extends ParseObject {
	public static final String NAME_KEY = "name";
	private static final String COMPLETED_KEY = "completed";
	private static final String ADDRESS_KEY = "address";
	private static final String NOTIFICATIONTIME_KEY = "notificationtime";
	private static final String SHARING_KEY = "sharing";
	public static final String OWNER_KEY = "owner";
	private static final String COLOR_KEY = "color";

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
		super.addAll(SHARING_KEY, extractParseUsers(users));
	}

	public List<ParseUser> extractParseUsers(List<User> users) {
		List<ParseUser> tmpList = new ArrayList<ParseUser>(users.size());
		for(User u:users) {
			tmpList.add(u.getParseUser());
		}
		return tmpList;
	}
	
	/**
	 * <p>Appends the user to the current list.
	 * <p>Note that it is not currently possible to atomically add and remove items from an array in the same save. You will have to call save in between every different kind of array operation.
	 * 
	 * @param user
	 */
	public void addToSharing(User user) {
		super.add(SHARING_KEY, user.getParseUser());
	}
	
	/**
	 * <p>Remove the users from the existing list.
	 * <p>Note that it is not currently possible to atomically add and remove items from an array in the same save. You will have to call save in between every different kind of array operation.
	 * 
	 * @param users
	 */
	public void removeAllFromSharing(List<User> users) {
		super.removeAll(SHARING_KEY, extractParseUsers(users));
	}
	
	@SuppressWarnings("unchecked")
	public List<User> getSharing() {
		List<ParseUser> parseUsers = (List<ParseUser>) super.get(SHARING_KEY);
		
		if(parseUsers == null) {
			return new ArrayList<User>();
		} else {
			return convertToUsers(parseUsers);
		}
	}

	public List<User> convertToUsers(List<ParseUser> parseUsers) {
		List<User> tmpList = new ArrayList<User>(parseUsers.size());
		for(ParseUser u:parseUsers) {
			tmpList.add(new User(u));
		}
		
		return tmpList;
	}
	
	public User getOwner() {
		return new User(super.getParseUser(OWNER_KEY));
	}
	
	public void setOwner(User value) {
		super.put(OWNER_KEY, value.getParseUser());
	}
	
	public int getColor() {
		Number number = super.getNumber(COLOR_KEY);
		if(number != null) {
			return number.intValue();
		} else {
			return 0;
		}
	}
	
	public void setColor(int value) {
		super.put(COLOR_KEY, value);
	}
	
	/**
	 * @return an unmodifiable list of items that belong to this list. Do not add items to it! Instead create a new TodoItem and call setItem().
	 * @throws ParseException
	 */
	public List<TodoItem> getAllItems() throws ParseException {
		ParseQuery<TodoItem> itemQuery = ParseQuery.getQuery(TodoItem.class);
		itemQuery.whereEqualTo(TodoItem.LIST_KEY, this);
		return itemQuery.find();
	}
	
	public static TodoList findTodoListByName(String listName) throws ParseException {
		ParseQuery<TodoList> itemQuery = ParseQuery.getQuery(TodoList.class);
		itemQuery.whereEqualTo(TodoList.NAME_KEY, listName);
		
		List<TodoList> list = itemQuery.find();
		
		if(list.isEmpty()) {
			return null;
		} else { 
			if(list.size() > 1) {
				Log.w("warning", "Found " + list.size() + " lists with the same name: " + listName + ". Returning only the first one");
			}
			
			return list.get(0);
		}
	}

	public int getUniqueId() {
		String objectId = getObjectId();
		
		if(objectId != null) {
		return objectId.hashCode();
		} else {
			Log.w("warning", "ObjectId is null for " + getName() + ". Object unsaved?");
			return -1;
		}
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof TodoList) {
			return getObjectId().equals(((TodoList)o).getObjectId());
		} else {
			return false;
		}
	}
	
	
}
