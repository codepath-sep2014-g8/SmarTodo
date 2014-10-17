package com.codepath.smartodo;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.codepath.smartodo.services.ModelManagerService;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;

public class SmarTodoApplication extends Application {
	private Intent modelManagerServiceIntent;
	
  @Override
  public void onCreate() {
    super.onCreate();
    
 // Required - Initialize the Parse SDK
 	ParseConfig.init(this);
 	
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
 	
    modelManagerServiceIntent = new Intent(this, ModelManagerService.class);
    startService(modelManagerServiceIntent);
	
	// TODO For testing purposes only, DO NOT COMMIT when uncommented!
//    ParseDbTest.test();
  }
  
}