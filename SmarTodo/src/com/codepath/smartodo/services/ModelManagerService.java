package com.codepath.smartodo.services;

import java.util.Date;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.codepath.smartodo.model.TodoItem;
import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.model.User;
import com.codepath.smartodo.notifications.NotificationsSender;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ModelManagerService extends Service {
//	private static ModelManagerService INSTANCE;
//	public static ModelManagerService getInstance() { return INSTANCE; }

	private static User user;
	private static List<TodoList> lists;
	private static Handler handler;

	private static class ListMessageHandler extends Handler {
		@Override public void handleMessage(Message msg) {
			TodoList i = (TodoList) msg.obj;
			Log.i("alarm", "Alarm activated for item " + i.getName() + " for time " + i.getNotificationTime());
		}
	}
	
	@Override
	public void onCreate() {
//		INSTANCE = this;
		running = true;
		handler = new ListMessageHandler();
		
		Log.i("info", "Started " + getClass().getSimpleName());
		
	 	ParseInstallation.getCurrentInstallation().saveInBackground();
	    
	 	ParsePush.subscribeInBackground("", new SaveCallback() {
	  	  @Override
	  	  public void done(ParseException e) {
	  	    if (e == null) {
	  	      Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
	  	    } else {
	  	      Log.e("com.parse.push", "failed to subscribe for push", e);
	  	    }
	  	  }
	  	});
	}
	
	@Override
	public void onDestroy() {
		Log.i("info", "Stopping " + getClass().getSimpleName());
		running = false;
	}

	/**
	 * Call this whenever a user logs in.
	 * 
	 * @param user
	 * @throws ParseException 
	 */
	public static void refreshFromUser(User user) throws ParseException {
		Log.i("info", "Refreshing data from current user " + user.getUsername());
		ModelManagerService.user = user;
		ModelManagerService.lists = user.findAllLists();
		Log.i("info", "DONE. Loaded " + ModelManagerService.lists.size() + " lists for user.");
		
		for(TodoList list : ModelManagerService.lists) {
			// Clean up any previous alarms
			handler.removeMessages(list.getUniqueId());
			
			Date notificationTime = list.getNotificationTime();
			
			if(notificationTime != null) {
				Log.i("info", "Registering alarm for list " + list.getName() + " at " + notificationTime);
				
		        long timeAtBoot = System.currentTimeMillis() - SystemClock.uptimeMillis();
		        long reminderTime = notificationTime.getTime() - timeAtBoot;
		        
		        boolean posted = handler.sendMessageAtTime(Message.obtain(handler, list.hashCode(), list), reminderTime);
		        
		        if(!posted) {
		        	Log.w("warning", "The alarm could not be set up!");
		        } else {
		        	Log.i("info", "Alarm set up successfully");
		        }
			}
		}
		
		// Log.d("DEBUG", "In LoginActivity.lauchMainApp");	
		// Register with ParseInstallation the current user under SHARED_USER_KEY 
		// so that push notifications can be received on behalf of the current user 
		// when they are sent by other users of the app.
		ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		installation.put(NotificationsSender.SHAREDWITH_USER_KEY, ParseUser.getCurrentUser());
		installation.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException ex) {
				if(ex == null) {
					// For testing
					sendTestTodoList();
				} else {
					Log.e("error", ex.getMessage(), ex);
				}
			}
		});
	}
	
	// This is just for testing purpose. Notice that we are sharing a newly created 
	// Todo list with the current user itself.
	private static void sendTestTodoList() {
		TodoList todoList = new TodoList();
		todoList.setName(ParseUser.getCurrentUser().getUsername() + "'s TodoList");
		try {
			todoList.save();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NotificationsSender.shareTodoList(todoList, ParseUser.getCurrentUser());		
	}
	
	/**
	 * Call this when the user logs out.
	 */
	public void unregisterUser() {
		// TODO
	}
	
	public static void saveList(String name, User owner, final List<TodoItem> todoItemsList) {
		Log.i("info", "Saving list");
		final TodoList todoList = new TodoList();
		todoList.setName(name);
		todoList.setOwner(owner);
		
		// TODO Add more properties
		
		todoList.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException arg0) {
				for(User sharedWith : todoList.getSharing()) {
					Log.i("info", "Sending push message to " + sharedWith.getUsername());
					NotificationsSender.shareTodoList(todoList, sharedWith.getParseUser());	
				}
				
				Log.i("info", "Saving " + todoItemsList.size() + " list items");
				for(TodoItem item : todoItemsList) {
					item.setList(todoList);
					
					try {
						item.save();
					} catch (ParseException e) {
						Log.e("error", e.getMessage(), e);
					}
				}
				
				Log.i("info", "Items saved");
			}
		});
		
		Log.i("info", "List save started in background");
	}
	
	private static boolean running = false;
	
	// https://groups.google.com/forum/#!topic/android-developers/jEvXMWgbgzE
	public static boolean isRunning() {
		return running;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null; // Don't let anybody bind to this service
	}

	public static User getUser() {
		return user;
	}

	public static List<TodoList> getLists() {
		return lists;
	}

}
