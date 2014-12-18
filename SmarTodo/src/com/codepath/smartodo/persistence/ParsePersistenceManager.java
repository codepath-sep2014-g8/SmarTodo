package com.codepath.smartodo.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.model.User;
import com.codepath.smartodo.notifications.NotificationsSender;
import com.codepath.smartodo.services.ModelManagerService;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ParsePersistenceManager {
	
	public static List<TodoList> getLists() {
		return ModelManagerService.lists;
	}
	
	public static String saveList(final TodoList todoList, final SaveCallback callback) {
		Log.i("info", "Saving list");
	
		// Batch save
		List<ParseObject> allObjects = new ArrayList<ParseObject>();
		allObjects.add(todoList);
		allObjects.addAll(todoList.getItems());
		
		try {
			ParseObject.pinAll(allObjects);
		} catch (ParseException ex) {
			Log.e("error", "Pin error: " + ex.getMessage(), ex);
		}
		
		ParseObject.saveAllInBackground(allObjects, new SaveCallback() {
			@Override
			public void done(ParseException ex) {
				Log.i("info", "Save complete");
				
				if(ex != null) {
					Log.e("error", "Save error: " + ex.getMessage(), ex);
				} else {
					try {
						todoList.pin();
					} catch (ParseException ex2) {
						Log.e("error", "Pin error: " + ex2.getMessage(), ex2);
					}
					
					for(User sharedWith : todoList.getSharing()) {
						Log.i("info", "Sending push message to " + sharedWith.getUsername());
						NotificationsSender.shareTodoList(todoList, sharedWith.getParseUser());	
					}
					
					try {
						callback.done(null);
					} catch(Throwable th) {
						Log.e("error", "Error while executing callback", th);
					}
				}
			}
		});
		
		Log.i("info", "List save started in background");
		
		return todoList.getObjectId();
	}
	
	public static void refreshFromUser(Context context, User user) throws ParseException {
		Log.i("info", "Refreshing data from current user " + user.getUsername());
		ModelManagerService.user = user;
		ModelManagerService.lists = findAllLists(context, user);
		Log.i("info", "DONE. Loaded " + ModelManagerService.lists.size() + " lists for user.");
		
		for(TodoList list : ModelManagerService.lists) {
			if(list == null) {
				// TODO Why is the list null?!
				Log.w("warning", "Encountered a null list. Skipping it.");
				continue;
			}
			ModelManagerService.processListNotifications(list);
		}
	}
	
	public static List<TodoList> findAllLists(Context context, User user) throws ParseException {
		ParseQuery<TodoList> itemQuery = LocalParseQuery.getQuery(TodoList.class, context);
		itemQuery.whereEqualTo(TodoList.OWNER_KEY, user.getParseUser());
		List<TodoList> lists = itemQuery.find();
		
		itemQuery = LocalParseQuery.getQuery(TodoList.class, context);
		itemQuery.whereContainsAll(TodoList.SHARING_KEY, Arrays.asList(new ParseUser[]{user.getParseUser()}));
		lists.addAll(itemQuery.find());
		
		ParseObject.pinAll(lists);
		
		return lists;
	}
	
	public static Collection<User> findAll(Context context) {
		return findAllLike(context, null, false);
	}

	public static Collection<User> findAllLike(Context context, String substring) {
		return findAllLike(context, substring, true);
	}

	// TODO Merge the implementation with findTodoListByName
	public static TodoList findTodoListByNameAndUser(Context context, String listName, User user) throws ParseException {
		ParseQuery<TodoList> itemQuery = LocalParseQuery.getQuery(TodoList.class, context);
		itemQuery.whereEqualTo(TodoList.NAME_KEY, listName);
		itemQuery.whereNotEqualTo(TodoList.OWNER_KEY, user.getParseUser());
		
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

	public static TodoList findTodoListByName(Context context, String listName) throws ParseException {
		ParseQuery<TodoList> itemQuery = LocalParseQuery.getQuery(TodoList.class, context);
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

	public static TodoList findTodoListByObjectId(Context context, String objectId) throws ParseException {
		ParseQuery<TodoList> itemQuery = LocalParseQuery.getQuery(TodoList.class, context);
		itemQuery.whereEqualTo("objectId", objectId);
		
		List<TodoList> list = itemQuery.find();
		
		if(list.isEmpty()) {
			return null;
		} else { 
			if(list.size() > 1) {
				Log.w("warning", "Found " + list.size() + " lists with the same name: " + objectId + ". Returning only the first one");
			}
			
			return list.get(0);
		}
	}
	
	public static int findExistingListIdxByObjectId(String objectId) {
		for(int i=0;i< ParsePersistenceManager.getLists().size();i++) {
			TodoList list = ParsePersistenceManager.getLists().get(i);
			if(list.getObjectId().equals(objectId)) {
				return i;
			}
		}
		
		return -1;
	}

	/**
	 * Supports only substring based search in the realname, email and username fields. Note that the search is case SENSITIVE.
	 * 
	 * @param substring
	 * @return
	 */
	public static Collection<User> findAllLike(Context context, String substring, boolean doFilter) {
		// TODO For some reason whereContains("*asdf*") does not work at all and returns 0 matches
		
		String generousPattern = substring;//"*" + pattern + "*";
		List<ParseUser> users = new ArrayList<ParseUser>();
		
		ParseQuery<ParseUser> itemQuery;
		try {
			itemQuery = LocalParseQuery.getQuery(ParseUser.class, context);
			if(doFilter) {
				itemQuery.whereContains(User.REALNAME_KEY, generousPattern);
				itemQuery.orderByAscending("objectId");
			}
			users.addAll(itemQuery.find());
		} catch (ParseException e) {
			Log.e("error", e.getMessage(), e);
		}
		
		try {
			itemQuery = LocalParseQuery.getQuery(ParseUser.class, context);
			if(doFilter) {
				itemQuery.whereContains("email", generousPattern);
				itemQuery.orderByAscending("objectId");
			}
			users.addAll(itemQuery.find());
		} catch (ParseException e) {
			Log.e("error", e.getMessage(), e);
		}
		
		try {
			itemQuery = LocalParseQuery.getQuery(ParseUser.class, context);
			if(doFilter) {
				itemQuery.whereContains("username", generousPattern);
				itemQuery.orderByAscending("objectId");
			}
			users.addAll(itemQuery.find());
		} catch (ParseException e) {
			Log.e("error", e.getMessage(), e);
		}
		
		Map<String, User> uniqueUsers = new HashMap<String, User>();
		
		for(ParseUser tmpUser : users) {
			uniqueUsers.put(tmpUser.getUsername(), new User(tmpUser));
		}
		
		List<User> tmpList = new ArrayList<User>(uniqueUsers.values());
		try {
			ParseObject.pinAll(User.extractParseUsers(tmpList));
		} catch (ParseException e) {
			Log.e("error", e.getMessage(), e);
		}
		
		return tmpList;
	}

}
