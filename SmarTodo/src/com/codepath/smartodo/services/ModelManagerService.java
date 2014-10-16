package com.codepath.smartodo.services;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.model.User;
import com.parse.ParseException;

public class ModelManagerService extends Service {
//	private static ModelManagerService INSTANCE;
//	public static ModelManagerService getInstance() { return INSTANCE; }

	private static User user;
	private static List<TodoList> lists;

	@Override
	public void onCreate() {
//		INSTANCE = this;
		running = true;
		Log.i("info", "Started " + getClass().getSimpleName());
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

	public static List<TodoList> getLists() {
		return lists;
	}

}
