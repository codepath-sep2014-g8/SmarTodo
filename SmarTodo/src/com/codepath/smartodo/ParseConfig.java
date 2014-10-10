package com.codepath.smartodo;

import android.content.Context;

import com.codepath.smartodo.model.Address;
import com.codepath.smartodo.model.TodoItem;
import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.model.User;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseConfig {
	public static final String APPLICATION_ID = "q1Xy6yMRQSGUHU581wZ91YxspkdLwYbRb1CFy1fH";
	public static final String CLIENT_KEY = "fWs7vfkXICxUmcaKuXdKLi9ZMYNGDzhQDDTuQoHB";

	public static void init(Context context) {
        ParseObject.registerSubclass(Address.class);
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(TodoList.class);
        ParseObject.registerSubclass(TodoItem.class);
        Parse.initialize(context, ParseConfig.APPLICATION_ID, ParseConfig.CLIENT_KEY);
	}
}
