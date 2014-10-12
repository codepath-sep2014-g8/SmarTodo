package com.codepath.smartodo;

import android.app.Application;

public class SmarTodoApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    // Required - Initialize the Parse SDK
	ParseConfig.init(this);
  }
}