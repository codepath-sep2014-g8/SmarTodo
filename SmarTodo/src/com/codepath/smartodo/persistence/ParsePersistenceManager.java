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
	
	@Override
	public String saveTodoList(final TodoList todoList, final PERSISTENCE_OPERATION operation, final PersistenceCallback callback) {
		Log.i("info", "Saving list, operation=" + operation);
	
		// Batch save
		final List<ParseObject> allObjects = new ArrayList<ParseObject>();
		allObjects.add(todoList);
		allObjects.addAll(todoList.getItems());
		Log.i("info", "In saveTodoList " + allObjects.size() + " objects being saved for TodoList " + todoList.getName());	
		
		// First save in the cloud and then save locally.
		ParseObject.saveAllInBackground(allObjects, new SaveCallback() {
			@Override
			public void done(ParseException ex) {
						
				if(ex != null) {
					Log.e("error", "Cloud save error: " + ex.getMessage(), ex);
					performCallback(ex, todoList, operation, callback);
				} else {
					Log.i("info", "Cloud save ok");
					try {
						ParseObject.pinAll(allObjects); // save locally now as the objectIds would have been initialized.
						Log.i("info", "Local save complete");
					} catch (ParseException ex2) {
						Log.e("error", "Pin error: " + ex2.getMessage(), ex2);
						performCallback(ex2, todoList, operation, callback);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						performCallback(e, todoList, operation, callback);
					}
					
					for(User sharedWith : todoList.getSharing()) {
						// TODO: it will be good move the notifications out of PersistenceManager
						Log.i("info", "Sending push message to " + sharedWith.getUsername());
						NotificationsSender.shareTodoList(todoList, sharedWith.getParseUser());	
					}
					
					try {
						performCallback(null, todoList, operation, callback);						 
					} catch (Throwable th) {
						Log.e("error", "Error while executing callback", th);
					}
				}
			}
		});
		
		Log.i("info", "List save started in background");
		
		return todoList.getObjectId();  // This could be null if the TodoList is saved for the first time and the cloud operation has not yet finished.
	}
	
	private void performCallback(Exception exception, TodoList todoList, PERSISTENCE_OPERATION operation, PersistenceCallback callback) {
		if (callback == null) {
			return;
		}
		if (operation == PERSISTENCE_OPERATION.ADD) {
			callback.added(exception, todoList);
		} else if (operation == PERSISTENCE_OPERATION.UPDATE) {
			callback.updated(exception, todoList);
		} else if (operation == PERSISTENCE_OPERATION.DELETE) {
			callback.deleted(exception, todoList);
		}
	}
	
	@Override
	public void deleteTodoList(TodoList todoList, PersistenceCallback callback) {
		try {
			deleteObject(todoList);
			performCallback(null, todoList, PERSISTENCE_OPERATION.DELETE, callback);
		} catch (ParseException ex) {
			Log.e("error", "Delete error: " + ex.getMessage(), ex);
			performCallback(ex, todoList, PERSISTENCE_OPERATION.DELETE, callback);
		} 		
	}
	
	@Override
	public void deleteObject(Object object) throws ParseException {
		if (object instanceof ParseObject) {
			((ParseObject) object).unpin();
			((ParseObject) object).deleteEventually();
		}		
	}
	
	@Override
	public List<TodoList> refreshTodoListsForUser(Context context, User user, ACCESS_LOCATION accessLocation) throws ParseException {
		Log.i("info", "Refreshing data for current user " + user.getUsername() + ", accessTarget=" + accessLocation);
		List<TodoList> todoListsForUser = findAllTodoLists(context, user, accessLocation);
		Log.i("info", "DONE. Loaded " + todoListsForUser.size() + " lists for user.");	
		return todoListsForUser;
	}
	
	@Override
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
	
	@Override
	public TodoList findTodoListByName(Context context, String listName, ACCESS_LOCATION accessLocation) throws ParseException {
		ParseQuery<TodoList> itemQuery = ParseQueryFactory.getQuery(TodoList.class, context, accessLocation);
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
	@Override
	public TodoList findTodoListByNameAndUser(Context context, String listName, User user, ACCESS_LOCATION accessLocation) throws ParseException {
		ParseQuery<TodoList> itemQuery = ParseQueryFactory.getQuery(TodoList.class, context, accessLocation);
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

	
	@Override
	public TodoList findTodoListByObjectId(Context context, String objectId, ACCESS_LOCATION accessLocation) throws ParseException {
		ParseQuery<TodoList> itemQuery = ParseQueryFactory.getQuery(TodoList.class, context, accessLocation);
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
	@Override
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
			}
	
			ParseQuery<ParseUser> itemQuery2 = ParseQueryFactory.getQuery(ParseUser.class, context, accessLocation);
			if(doFilter) {
				itemQuery2.whereContains("email", generousPattern);
			}
		
			ParseQuery<ParseUser> itemQuery3 = ParseQueryFactory.getQuery(ParseUser.class, context, accessLocation);
			if(doFilter) {
				itemQuery3.whereContains("username", generousPattern);
			}
			
			List<ParseQuery<ParseUser>> itemQueries = new ArrayList<ParseQuery<ParseUser>>();
			itemQueries.add(itemQuery1);
			itemQueries.add(itemQuery2);
			itemQueries.add(itemQuery3);
			
			ParseQuery<ParseUser> itemQuery = ParseQuery.or(itemQueries);	
			itemQuery.orderByAscending("objectId");
			
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
