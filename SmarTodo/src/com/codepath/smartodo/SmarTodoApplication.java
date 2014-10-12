package com.codepath.smartodo;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;

import android.app.Application;

public class SmarTodoApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    // Required - Initialize the Parse SDK
    Parse.initialize(this, getString(R.string.parse_app_id),
        getString(R.string.parse_client_key));

    Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

    // Optional - If you don't want to allow Facebook login, you can
    // remove this line (and other related ParseFacebookUtils calls)
    // ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));

    // Optional - If you don't want to allow Twitter login, you can
    // remove this line (and other related ParseTwitterUtils calls)
    //ParseTwitterUtils.initialize(getString(R.string.twitter_consumer_key),
    //   getString(R.string.twitter_consumer_secret));
  }
}