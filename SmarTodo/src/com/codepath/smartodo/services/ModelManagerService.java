package com.codepath.smartodo.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.codepath.smartodo.R;
import com.codepath.smartodo.activities.ListsViewerActivity;
import com.codepath.smartodo.helpers.Utils;
import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.model.User;
import com.codepath.smartodo.notifications.NotificationsSender;
import com.codepath.smartodo.persistence.PersistenceManager.ACCESS_LOCATION;
import com.codepath.smartodo.persistence.PersistenceManager;
import com.codepath.smartodo.persistence.PersistenceManagerFactory;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ModelManagerService extends Service {
	private static ModelManagerService INSTANCE;
	public static ModelManagerService getInstance() { return INSTANCE; }

	private static final int NOTIFICATION_ID = 1;
	
	public static User user;
	public static List<TodoList> cachedTodoLists;
	private static boolean cacheRefreshed = false; 
	private static Handler handler;

	public ModelManagerService() {
		INSTANCE = this;
		cachedTodoLists = new ArrayList<TodoList>();
		handler = new ListMessageHandler(this);
	}
	
	private static class ListMessageHandler extends Handler {
		private ModelManagerService service;
		
		public ListMessageHandler(ModelManagerService service) {
			this.service = service;
		}

		@Override public void handleMessage(Message msg) {
			TodoList i = (TodoList) msg.obj;
			Log.i("alarm", "Alarm activated for item " + i.getName() + " for time " + i.getNotificationTime());
			
			service.displayNotification("SmarTodo alarm for " + i.getName(), "Your list is due now!");
		}
	}
	
	@Override
	public void onCreate() {
		running = true;
		
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

	public static void registerInstallation() {
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
					Log.i("info", "Parse installation set up correctly");
					// For testing
//					sendTestTodoList();
				} else {
					Log.e("error", ex.getMessage(), ex);
				}
			}
		});
	}
	
	/**
	 * Call this whenever a user logs in.
	 * 
	 * @param user
	 * @throws ParseException 
	 */

	public static void processListNotifications(TodoList list) {
		// Clean up any previous alarms
		if (handler != null) {
		    handler.removeMessages(list.getUniqueId());
		}
		
//			Date notificationTime = new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30)); // TODO For testing purposes only
		
		Date notificationTime = list.getNotificationTime();
		
		if(notificationTime != null) {
			if(handler == null) {
				return;
			}
			
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
	
	public static void setUser(User user) {
		ModelManagerService.user = user;
	}

	public void displayNotification(String title, String text) {
		// Create a notification area notification so the user 
		// can get back to the client UI		
		final Intent notificationIntent = new Intent(getApplicationContext(), ListsViewerActivity.class);
		final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		final Notification notification = new NotificationCompat.Builder(
				getApplicationContext())
				.setSmallIcon(R.drawable.ic_stat_todolistalarm)
				.setAutoCancel(true)
				.setContentTitle(title)
				.setContentText(text)
				.setContentIntent(pendingIntent)
				.build();

		// Put this Service in a foreground state, so it won't 
		// readily be killed by the system  
		startForeground(NOTIFICATION_ID, notification);
	}
	
	// Refreshes the cache of TodoLists only if it has never been refreshed before in this session.
	// Otherwise, the cache should contain most up-to-date information anyway.
	public static void softRefreshCachedTodoLists(ACCESS_LOCATION accessLocation) throws ParseException {
		Log.i("info", "In softRefreshCachedTodoLists, cacheRefreshed=" + cacheRefreshed);
		if (cacheRefreshed) {
			return;
		}
		refreshCachedTodoLists(accessLocation);
	}
	
	public static void refreshCachedTodoLists(ACCESS_LOCATION accessLocation) throws ParseException {
		List<TodoList> refreshedTodoLists = null;
		try {
			refreshedTodoLists = PersistenceManagerFactory.getInstance().refreshTodoListsForUser(INSTANCE, getUser(), accessLocation);
			setCachedTodoLists(refreshedTodoLists);
			cacheRefreshed = true;
		} catch (ParseException e) {
			e.printStackTrace();
			setCachedTodoLists(null);
			throw e;
		}
		
		if (accessLocation == ACCESS_LOCATION.CLOUD || (accessLocation == ACCESS_LOCATION.CLOUD_ELSE_LOCAL && Utils.isNetworkAvailable(INSTANCE))) {
			processListsNotifications();	
		}		
	}
	
	private static void processListsNotifications() {
		for(TodoList todoList : cachedTodoLists) {
			if(todoList == null) {
				// TODO Why is the list null?!
				Log.w("warning", "Encountered a null list. Skipping it.");
				continue;
			}
			processListNotifications(todoList);
		}			
	}	

	public static List<TodoList> getCachedTodoLists() {
		return cachedTodoLists;
	}

	public static void setCachedTodoLists(List<TodoList> todoLists) {
		cachedTodoLists = todoLists;
		if (cachedTodoLists == null) {
			cachedTodoLists = new ArrayList<TodoList>();
		}
	}
	
	public static void setCachedTodoList(int location, TodoList todoList) {
		if (cachedTodoLists == null) {
			return;
		}
		cachedTodoLists.set(location, todoList);
	}
	
	public static void addToCachedTodoLists(TodoList todoList) {		
		if (cachedTodoLists == null) {
			cachedTodoLists = new ArrayList<TodoList>();
		}
		cachedTodoLists.add(todoList);
	}
	
	public static TodoList findCachedTodoListByObjectId(String objectId) {
		if (cachedTodoLists == null) {
			return null;
		}
		for(int i = 0; i < cachedTodoLists.size(); i++) {
			TodoList todoList = cachedTodoLists.get(i);
			if(todoList.getObjectId().equals(objectId)) {
				return todoList;
			}
		}
		
		return null;
	}
	
	public static int findCachedIndexForATodoListByObjectId(String objectId) {
		if (cachedTodoLists == null) {
			return -1;
		}
		for(int i = 0; i < cachedTodoLists.size(); i++) {
			TodoList todoList = cachedTodoLists.get(i);
			if (todoList.getObjectId().equals(objectId)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public synchronized static void removeFromCachedTodoLists(TodoList todoList) {
		if (cachedTodoLists == null) {
			return;
		}
		cachedTodoLists.remove(todoList);
	}
	
	public synchronized static void removeFromCachedTodoLists(String todoListId) {
		if (cachedTodoLists == null) {
			return;
		}
		for (int i = 0; i < cachedTodoLists.size(); i++) {
			TodoList todoList = cachedTodoLists.get(i);
			if (todoList.getObjectId().equals(todoListId)) {
				removeFromCachedTodoLists(todoList);
				return;
			}
		}
	}	
	

}
