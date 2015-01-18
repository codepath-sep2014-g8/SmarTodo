package com.codepath.smartodo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("TodoList")
public class TodoList extends ParseObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7768423733381722921L;
	public static final String TODOLIST_KEY = "todolist";
	public static final String NAME_KEY = "name";
	public static final String OPERATION_KEY = "operation";
	private static final String COMPLETED_KEY = "completed";
	private static final String ADDRESS_KEY = "address";
	private static final String NOTIFICATIONTIME_KEY = "notificationtime";
	public static final String SHARING_KEY = "sharing";
	public static final String OWNER_KEY = "owner";
	private static final String COLOR_KEY = "color";
	private static final String ITEMS_KEY = "items";

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
	
	/**
	 * <p>Appends the users to the current list.
	 * <p>Note that it is not currently possible to atomically add and remove items from an array in the same save. You will have to call save in between every different kind of array operation.
	 * 
	 * @param users
	 */
	public void addToSharing(List<User> users) {
		super.addAll(SHARING_KEY, User.extractParseUsers(users));
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
		super.removeAll(SHARING_KEY, User.extractParseUsers(users));
	}
	
	@SuppressWarnings("unchecked")
	public List<User> getSharing() {
		Object result = super.get(SHARING_KEY);

		// TODO working around inconsistent parse.com behavior (returning JSONObject on occasion)
		if(result instanceof List) {
			List<ParseUser> parseUsers = (List<ParseUser>) result;
			
			return convertToUsers(parseUsers);
		} else {
			Log.w("warning", "Unexpected return type: " + result);
			return new ArrayList<User>();
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
	 * <p>Appends the items to the current list.
	 * <p>Note that it is not currently possible to atomically add and remove items from an array in the same save. You will have to call save in between every different kind of array operation.
	 * 
	 * @param users
	 */
	public void addToItems(List<TodoItem> items) {
		super.addAll(ITEMS_KEY, items);
	}

	public void addItem(TodoItem item) {
		super.add(ITEMS_KEY, item);
	}
	
	public void setItems(List<TodoItem> todoItemsList) {
		remove(ITEMS_KEY);
		addToItems(todoItemsList);
	}

	
	/**
	 * <p>Appends the item to the current list.
	 * <p>Note that it is not currently possible to atomically add and remove items from an array in the same save. You will have to call save in between every different kind of array operation.
	 * 
	 * @param user
	 */
	public void addToSharing(TodoItem item) {
		super.add(ITEMS_KEY, item);
	}
	
	/**
	 * <p>Remove the items from the existing list.
	 * <p>Note that it is not currently possible to atomically add and remove items from an array in the same save. You will have to call save in between every different kind of array operation.
	 * 
	 * @param users
	 */
	public void removeAllFromItems(List<TodoItem> items) {
		super.removeAll(ITEMS_KEY, items);
	}
	
	@SuppressWarnings("unchecked")
	public List<TodoItem> getItems() {
		Object result = super.get(ITEMS_KEY);

		// TODO working around inconsistent parse.com behavior (returning JSONObject on occasion)
		if(result instanceof List) {
			return (List<TodoItem>) result;
		} else {
			Log.w("warning", "Unexpected return type: " + result);
			return new ArrayList<TodoItem>();
		}
	}
	
	/**
	 * @return an unmodifiable list of items that belong to this list. Do not add items to it! Instead create a new TodoItem and call setItem().
	 * @throws ParseException
	 */
	public List<TodoItem> getAllItems() throws ParseException {
		return getItems();
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
		if(o instanceof TodoList && getObjectId() != null) {
			return getObjectId().equals(((TodoList)o).getObjectId());
		} else {
			return false;
		}
	}

	public void setSharing(List<User> users) {
		remove(SHARING_KEY);
		addToSharing(users); // add new
	}
}
