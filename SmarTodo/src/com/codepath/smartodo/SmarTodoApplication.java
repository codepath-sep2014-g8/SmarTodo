package com.codepath.smartodo;

import android.app.Application;

import com.codepath.smartodo.model.ParseDbTest;

public class SmarTodoApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    // Required - Initialize the Parse SDK
	ParseConfig.init(this);
	
	// TODO For testing purposes only, DO NOT COMMIT when uncommented!
    ParseDbTest.test();
  }
}