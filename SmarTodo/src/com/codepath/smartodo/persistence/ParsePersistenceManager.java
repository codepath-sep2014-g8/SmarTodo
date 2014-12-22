package com.codepath.smartodo.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.codepath.smartodo.helpers.Utils;
import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.model.User;
import com.codepath.smartodo.notifications.NotificationsSender;
import com.codepath.smartodo.services.ModelManagerService;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * This class provide the persistence functionality for the app using the Parse 
 * package for both the local and the cloud storage.
 *
 */
public class ParsePersistenceManager implements PersistenceManager {
	
	public List<TodoList> getTodoLists() {
		return ModelManagerService.lists;
	}
	
	public String saveTodoList(final TodoList todoList, final SaveCallback callback) {
		Log.i("info", "Saving list");
	
		// Batch save
		final List<ParseObject> allObjects = new ArrayList<ParseObject>();
		allObjects.add(todoList);
		allObjects.addAll(todoList.getItems());
		Log.i("info", "In saveTodoList " + allObjects.size() + " objects being saved for TodoList " + todoList.getName());	
		
		// First save in the cloud and then save locally.
		ParseObject.saveAllInBackground(allObjects, new SaveCallback() {
			@Override
			public void done(ParseException ex) {
				Log.i("info", "Cloud save complete");
				
				if(ex != null) {
					Log.e("error", "Save error: " + ex.getMessage(), ex);
					// TODO: Save locally anyway?
				} else {
					try {
						ParseObject.pinAll(allObjects); // save locally now as the objectIds would have been initialized.
						Log.i("info", "Local save complete");
					} catch (ParseException ex2) {
						Log.e("error", "Pin error: " + ex2.getMessage(), ex2);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					for(User sharedWith : todoList.getSharing()) {
						Log.i("info", "Sending push message to " + sharedWith.getUsername());
						NotificationsSender.shareTodoList(todoList, sharedWith.getParseUser());	
					}
					
					try {
						// TODO: The following callback can send the original todoList which can be updated on the screen "Saving..." -> "" 
						callback.done(null);  
					} catch(Throwable th) {
						Log.e("error", "Error while executing callback", th);
					}
				}
			}
		});
		
		Log.i("info", "List save started in background");
		
		return todoList.getObjectId();  // This could be null if the TodoList is saved for the first time and the cloud operation has not yet finished.
	}
	
	public void refreshTodoListsForUser(Context context, User user) throws ParseException {
		refreshTodoListsForUser(context, user, ACCESS_LOCATION.CLOUD_ELSE_LOCAL);
	}
	
	public void refreshTodoListsForUser(Context context, User user, ACCESS_LOCATION accessLocation) throws ParseException {
		Log.i("info", "Refreshing data for current user " + user.getUsername() + ", accessTarget=" + accessLocation);
		ModelManagerService.user = user;
		ModelManagerService.lists = findAllTodoLists(context, user, accessLocation);
		Log.i("info", "DONE. Loaded " + ModelManagerService.lists.size() + " lists for user.");	
		
		if (accessLocation == PersistenceManager.ACCESS_LOCATION.CLOUD) {
			for(TodoList list : ModelManagerService.lists) {
				if(list == null) {
					// TODO Why is the list null?!
					Log.w("warning", "Encountered a null list. Skipping it.");
					continue;
				}
				ModelManagerService.processListNotifications(list);
			}		
		}
	}
	
	public List<TodoList> findAllTodoLists(Context context, User user, ACCESS_LOCATION accessLocation) throws ParseException {
		ParseQuery<TodoList> itemQuery1 = ParseQueryFactory.getQuery(TodoList.class, context, accessLocation);
		itemQuery1.whereEqualTo(TodoList.OWNER_KEY, user.getParseUser());
		
		ParseQuery<TodoList> itemQuery2 = ParseQueryFactory.getQuery(TodoList.class, context, accessLocation);
		itemQuery2.whereContainsAll(TodoList.SHARING_KEY, Arrays.asList(new ParseUser[]{user.getParseUser()}));
		
		List<ParseQuery<TodoList>> itemQueries = new ArrayList<ParseQuery<TodoList>>();
		itemQueries.add(itemQuery1);
		itemQueries.add(itemQuery2);
		
		ParseQuery<TodoList> itemQuery = ParseQuery.or(itemQueries);
		List<TodoList> todoLists = itemQuery.find();
		
		if (accessLocation == ACCESS_LOCATION.CLOUD || (accessLocation == ACCESS_LOCATION.CLOUD_ELSE_LOCAL && Utils.isNetworkAvailable(context))) {
			// Data is obtained from the cloud
			ParseObject.pinAll(todoLists);
		}
		
		return todoLists;
	}
	
	public TodoList findTodoListByName(Context context, String listName) throws ParseException {
		ParseQuery<TodoList> itemQuery = ParseQueryFactory.getQuery(TodoList.class, context, ACCESS_LOCATION.LOCAL);
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

	// TODO Merge the implementation with findTodoListByName
	public TodoList findTodoListByNameAndUser(Context context, String listName, User user) throws ParseException {
		ParseQuery<TodoList> itemQuery = ParseQueryFactory.getQuery(TodoList.class, context, ACCESS_LOCATION.LOCAL);
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

	public TodoList findTodoListByObjectId(Context context, String objectId) throws ParseException {
		ParseQuery<TodoList> itemQuery = ParseQueryFactory.getQuery(TodoList.class, context, ACCESS_LOCATION.LOCAL);
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
	
	public int findExistingTodoListIdxByObjectId(String objectId) {
		for(int i=0;i< getTodoLists().size();i++) {
			TodoList list = getTodoLists().get(i);
			if(list.getObjectId().equals(objectId)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public Collection<User> findAllUsers(Context context) {
		return findAllUsersLike(context, null, false);
	}

	public Collection<User> findAllUsersLike(Context context, String substring) {
		return findAllUsersLike(context, substring, true);
	}

	/**
	 * Supports only substring based search in the realname, email and username fields. Note that the search is case SENSITIVE.
	 * 
	 * @param substring
	 * @return
	 */
	public Collection<User> findAllUsersLike(Context context, String substring, boolean doFilter) {
		// TODO For some reason whereContains("*asdf*") does not work at all and returns 0 matches

		Log.i("info", "Entering ParsePersistenceManger.findAllUsersLike ");
		
		String generousPattern = substring;//"*" + pattern + "*";
		List<ParseUser> parseUsers = new ArrayList<ParseUser>();
		ACCESS_LOCATION accessLocation = ACCESS_LOCATION.CLOUD_ELSE_LOCAL;  // maybe passed as a parameter
				
		try {
			ParseQuery<ParseUser> itemQuery1 = ParseQueryFactory.getQuery(ParseUser.class, context, accessLocation);
			if(doFilter) {
				itemQuery1.whereContains(User.REALNAME_KEY, generousPattern);
				itemQuery1.orderByAscending("objectId");
			}
	
			ParseQuery<ParseUser> itemQuery2 = ParseQueryFactory.getQuery(ParseUser.class, context, accessLocation);
			if(doFilter) {
				itemQuery2.whereContains("email", generousPattern);
				itemQuery2.orderByAscending("objectId");
			}
		
			ParseQuery<ParseUser> itemQuery3 = ParseQueryFactory.getQuery(ParseUser.class, context, accessLocation);
			if(doFilter) {
				itemQuery3.whereContains("username", generousPattern);
				itemQuery3.orderByAscending("objectId");
			}
			
			List<ParseQuery<ParseUser>> itemQueries = new ArrayList<ParseQuery<ParseUser>>();
			itemQueries.add(itemQuery1);
			itemQueries.add(itemQuery2);
			itemQueries.add(itemQuery3);
			
			ParseQuery<ParseUser> itemQuery = ParseQuery.or(itemQueries);	
			
			parseUsers.addAll(itemQuery.find());			
			
		} catch (ParseException e) {
			Log.e("error", e.getMessage(), e);
			return new ArrayList<User>();
		}
		
		if (accessLocation == ACCESS_LOCATION.CLOUD || (accessLocation == ACCESS_LOCATION.CLOUD_ELSE_LOCAL && Utils.isNetworkAvailable(context))) {
			// Data is obtained from the cloud
			try {
				ParseObject.pinAll(parseUsers); 
			} catch (ParseException e) {
				Log.e("error", e.getMessage(), e);
			}
		}	
		
		List<User> users = new ArrayList<User>();		
		for(ParseUser parseUser : parseUsers) {
			users.add(new User(parseUser));
		}
		Log.i("info", "Exiting ParsePersistenceManger.findAllUsersLike ");
		
		return users;
	}

}
