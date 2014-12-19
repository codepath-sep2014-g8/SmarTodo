package com.codepath.smartodo.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.codepath.smartodo.persistence.LocalParseQuery;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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
	
	public List<TodoList> findAllLists(Context context) throws ParseException { // TODO: rename findAllRelevantTodoLists
		ParseQuery<TodoList> todoListsQuery = LocalParseQuery.getQuery(TodoList.class, context);
		todoListsQuery.whereEqualTo(TodoList.OWNER_KEY, this);
		List<TodoList> relevantTodoLists = todoListsQuery.find();
		
		ParseQuery<TodoList> sharedTodoListsQuery = LocalParseQuery.getQuery(TodoList.class, context);
		sharedTodoListsQuery.whereContainsAll(TodoList.SHARING_KEY, Arrays.asList(new SmarTodoUser[]{this})); // TODO: check this
		relevantTodoLists.addAll(sharedTodoListsQuery.find());
		
		ParseObject.pinAll(relevantTodoLists);
		
		return relevantTodoLists;
	}
	
	public static Collection<SmarTodoUser> findAll(Context context) {
		return findAllLike(context, null, false);
		
	}
	
	public static Collection<SmarTodoUser> findAllLike(Context context, String substring) {
		return findAllLike(context, substring, true);
	}
	
	/**
	 * Supports only substring based search in the realname, email and username fields. Note that the search is case SENSITIVE.
	 * 
	 * @param substring
	 * @return
	 */
	public static Collection<SmarTodoUser> findAllLike(Context context, String substring, boolean doFilter) {
		// TODO For some reason whereContains("*asdf*") does not work at all and returns 0 matches
		// It may work now because of subclassing from the ParseUser class.
		
		String generousPattern = substring;//"*" + pattern + "*";
		List<SmarTodoUser> users = new ArrayList<SmarTodoUser>();	
		
		ParseQuery<SmarTodoUser> smarTodoUserQuery;
		try {
			smarTodoUserQuery = LocalParseQuery.getQuery(SmarTodoUser.class, context);
			if(doFilter) {
				smarTodoUserQuery.whereContains(REALNAME_KEY, generousPattern);
				smarTodoUserQuery.orderByAscending(OBJECTID_KEY);
			}
			users.addAll(smarTodoUserQuery.find());
		} catch (ParseException e) {
			Log.e("error", e.getMessage(), e);
		}
		
		try {
			smarTodoUserQuery = LocalParseQuery.getQuery(SmarTodoUser.class, context);
			if(doFilter) {
				smarTodoUserQuery.whereContains(EMAIL_KEY, generousPattern);
				smarTodoUserQuery.orderByAscending(OBJECTID_KEY);
			}
			users.addAll(smarTodoUserQuery.find());
		} catch (ParseException e) {
			Log.e("error", e.getMessage(), e);
		}
		
		try {
			smarTodoUserQuery = LocalParseQuery.getQuery(SmarTodoUser.class, context);
			if(doFilter) {
				smarTodoUserQuery.whereContains(USERNAME_KEY, generousPattern);
				smarTodoUserQuery.orderByAscending(OBJECTID_KEY);
			}
			users.addAll(smarTodoUserQuery.find());
		} catch (ParseException e) {
			Log.e("error", e.getMessage(), e);
		}
		
		Map<String, SmarTodoUser> uniqueUsers = new HashMap<String, SmarTodoUser>();
		
		for(SmarTodoUser tmpUser : users) {
			uniqueUsers.put(tmpUser.getUsername(), tmpUser);
		}
		
		List<SmarTodoUser> filteredUsers = new ArrayList<SmarTodoUser>(uniqueUsers.values());
		
		try {
			ParseObject.pinAll(filteredUsers);  // Doing this for local query later on? Where is that happening?
		} catch (ParseException e) {
			Log.e("error", e.getMessage(), e);
		}
		
		return filteredUsers;
	}
	
	public boolean equals(Object o) {
		if(o instanceof SmarTodoUser) {
			SmarTodoUser op = (SmarTodoUser) o;
			return op.getEmail().equals(getEmail());
		}
		
		return false;
	}
}
