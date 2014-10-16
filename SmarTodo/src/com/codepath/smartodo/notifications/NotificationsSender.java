package com.codepath.smartodo.notifications;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.model.User;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SendCallback;

public class NotificationsSender {
	
	// Using the following constants for push notifications. 
	public static final String SHAREDWITH_USER_KEY = "sharedwith_user"; 
	public static final String SHAREDBY_USER_KEY = "sharedby_user";
	public static final String SHARING_LISTNAME_KEY = "sharing_listname";
	public static final String TODOLIST_ID_KEY = "todolist_id";
	public static final String ACTION_SHARE_TODOLIST_VALUE = "com.codepath.smartodo.notifications.SHARE_TODOLIST";
	public static final String SHAREDBY_OBJECTID_KEY = "sharedby_objectid";
	public static final String SHAREDWITH_OBJECTID_KEY = "sharedwith_objectid";

	public NotificationsSender() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Sends a push notification to a target user about sharing a TodoList.
	 * 
	 * @param todoList The TodoList to be shared
	 * @param targetUser The user with whom the TodoList should be shared
	 */
	public static void shareTodoList(TodoList todoList, ParseUser targetUser) {
		
		ParseUser currentUser = ParseUser.getCurrentUser();
		
		// Create our Installation query
		ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
		pushQuery.whereEqualTo(SHAREDWITH_USER_KEY, targetUser); 
		 
		// Create and send a push notification to query
		ParsePush push = new ParsePush();
		// Set the message for the notification layout
		push.setMessage("Sharing a SmarTodo list " + todoList.getName()); 
		push.setQuery(pushQuery); // Set our Installation query
		
		// Setup the data to be pushed
		final JSONObject data = new JSONObject();
		try {
			// Set the title for the notification layout
			data.put("title", currentUser.getUsername()); 
			data.put("action", ACTION_SHARE_TODOLIST_VALUE);
			
			data.put(SHARING_LISTNAME_KEY, todoList.getName());
			data.put(TODOLIST_ID_KEY, todoList.getObjectId());
			
			data.put(SHAREDBY_USER_KEY, currentUser.getUsername());
			data.put(SHAREDWITH_USER_KEY, targetUser.getUsername());
			
			data.put(SHAREDBY_OBJECTID_KEY, currentUser.getObjectId());
			data.put(SHAREDWITH_OBJECTID_KEY, targetUser.getObjectId());
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		// Finally, send the push notification across	
		ParsePush.sendDataInBackground(data, pushQuery, new SendCallback() {
			
			@Override
			public void done(ParseException e) {
				if (e != null) {
					e.printStackTrace();
				} else {
					Log.d("com.codepath.smartodo", "Sent: " + data.toString());
				}
			}
		});		
	}
}
