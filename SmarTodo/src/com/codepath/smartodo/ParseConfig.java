package com.codepath.smartodo;

import android.content.Context;

import com.codepath.smartodo.model.Address;
import com.codepath.smartodo.model.TodoItem;
import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.model.User;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseConfig {
	public static void init(Context context) {
		Parse.initialize(context, context.getString(R.string.parse_app_id),
				context.getString(R.string.parse_client_key));
		
	    // Optional - If you don't want to allow Facebook login, you can
	    // remove this line (and other related ParseFacebookUtils calls)
	    // ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));

	    // Optional - If you don't want to allow Twitter login, you can
	    // remove this line (and other related ParseTwitterUtils calls)
	    //ParseTwitterUtils.initialize(getString(R.string.twitter_consumer_key),
	    //   getString(R.string.twitter_consumer_secret));

		Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
		ParseObject.registerSubclass(Address.class);
		ParseObject.registerSubclass(User.class);
		ParseObject.registerSubclass(TodoList.class);
		ParseObject.registerSubclass(TodoItem.class);
	}
}
