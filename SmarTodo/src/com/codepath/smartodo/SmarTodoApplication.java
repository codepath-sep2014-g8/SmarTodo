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
    
    modelManagerServiceIntent = new Intent(this, ModelManagerService.class);
    startService(modelManagerServiceIntent);
	
    // Required - Initialize the Parse SDK
	ParseConfig.init(this);
	
	// TODO For testing purposes only, DO NOT COMMIT when uncommented!
//    ParseDbTest.test();
  }
  
}