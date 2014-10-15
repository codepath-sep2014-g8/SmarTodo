package com.codepath.smartodo.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ModelManagerService extends Service {

	@Override
	public void onCreate() {
		Log.i("info", "Started " + getClass().getSimpleName());
	}
	
	@Override
	public void onDestroy() {
		Log.i("info", "Stopping " + getClass().getSimpleName());
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

}
