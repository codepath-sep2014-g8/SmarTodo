package com.codepath.smartodo;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.codepath.smartodo.services.ModelManagerService;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

public class SmarTodoApplication extends Application {
	private Intent modelManagerServiceIntent;
	
  @Override
  public void onCreate() {
    super.onCreate();
    
 // Required - Initialize the Parse SDK
 	ParseConfig.init(this);
    
    modelManagerServiceIntent = new Intent(this, ModelManagerService.class);
    startService(modelManagerServiceIntent);
	
	// TODO For testing purposes only, DO NOT COMMIT when uncommented!
//    ParseDbTest.test();
  }
  
}