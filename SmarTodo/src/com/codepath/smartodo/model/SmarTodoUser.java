package com.codepath.smartodo.model;

import com.parse.ParseClassName;
import com.parse.ParseUser;

@ParseClassName("_User")
public class SmarTodoUser extends ParseUser {
	private static final String GITHUB_USERNAME_KEY = "githubUsername";
	private static final String PHONENUMBER_KEY = "phonenumber";
	public static final String REALNAME_KEY = "realname";
	public static final String OBJECTID_KEY = "objectId";
	public static final String USERNAME_KEY = "username";
	public static final String EMAIL_KEY = "email";

	public SmarTodoUser() {
		super();
	}
		
	public boolean equals(Object o) {
		if(o instanceof SmarTodoUser) {
			SmarTodoUser op = (SmarTodoUser) o;
			return op.getEmail().equals(getEmail());
		}
		
		return false;
	}
}
