package com.codepath.smartodo;

import android.app.Application;
import android.content.Intent;

import com.codepath.smartodo.geofence.ReceiveTransitionsIntentService;
import com.codepath.smartodo.model.ParseDbTest;
import com.codepath.smartodo.services.ModelManagerService;

public class SmarTodoApplication extends Application {
	private Intent modelManagerServiceIntent;
	
  @Override
  public void onCreate() {
    super.onCreate();
    
 // Required - Initialize the Parse SDK
 	ParseConfig.init(this);
 	
    modelManagerServiceIntent = new Intent(this, ModelManagerService.class);
    startService(modelManagerServiceIntent);
    
    startService(new Intent(this, ReceiveTransitionsIntentService.class));
	
	// TODO For testing purposes only, DO NOT COMMIT when uncommented!
    ParseDbTest.test();
  }
  
}