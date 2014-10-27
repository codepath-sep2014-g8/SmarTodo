package com.codepath.smartodo.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RefreshCallback;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

/**
 * Convenience wrapper around a ParseUser enabling access to the extra columns we added.
 * @author renyedi
 */
public class User {
	private static final String PHONENUMBER_KEY = "phonenumber";
	public static final String REALNAME_KEY = "realname";
	private ParseUser parseUser;

	public User(ParseUser parseUser) {
		this.parseUser = parseUser;
	}
	
	public void setPhoneNumber(String value) {
		parseUser.put(PHONENUMBER_KEY, value);
	}
	
	public String getPhoneNumber() throws ParseException {
		return parseUser.fetchIfNeeded().getString(PHONENUMBER_KEY);
	}

	public void setRealName(String value) {
		parseUser.put(REALNAME_KEY, value);
	}
	
	public String getRealName() throws ParseException {
		return parseUser.fetchIfNeeded().getString(REALNAME_KEY);
	}
	
	public ParseUser getParseUser() {
		return parseUser;
	}

	public boolean isAuthenticated() {
		return parseUser.isAuthenticated();
	}

	public List<TodoList> findAllLists() throws ParseException {
		ParseQuery<TodoList> itemQuery = ParseQuery.getQuery(TodoList.class);
		itemQuery.whereEqualTo(TodoList.OWNER_KEY, this.parseUser);
		List<TodoList> lists = itemQuery.find();
		
		itemQuery = ParseQuery.getQuery(TodoList.class);
		itemQuery.whereContainsAll(TodoList.SHARING_KEY, Arrays.asList(new ParseUser[]{this.parseUser}));
		lists.addAll(itemQuery.find());
		
		ParseObject.pinAll(lists);
		
		return lists;
	}
	
	/**
	 * Supports only substring based search in the realname, email and username fields. Note that the search is case SENSITIVE.
	 * 
	 * @param substring
	 * @return
	 */
	public static Collection<User> findAllLike(String substring) {
		// TODO For some reason whereContains("*asdf*") does not work at all and returns 0 matches
		
		String generousPattern = substring;//"*" + pattern + "*";
		List<ParseUser> users = new ArrayList<ParseUser>();
		
		ParseQuery<ParseUser> itemQuery;
		try {
			itemQuery = LocalParseQuery.getQuery(ParseUser.class);
			itemQuery.whereContains(REALNAME_KEY, generousPattern);
			users.addAll(itemQuery.find());
		} catch (ParseException e) {
			Log.e("error", e.getMessage(), e);
		}
		
		try {
			itemQuery = LocalParseQuery.getQuery(ParseUser.class);
			itemQuery.whereContains("email", generousPattern);
			users.addAll(itemQuery.find());
		} catch (ParseException e) {
			Log.e("error", e.getMessage(), e);
		}
		
		try {
			itemQuery = LocalParseQuery.getQuery(ParseUser.class);
			itemQuery.whereContains("username", generousPattern);
			users.addAll(itemQuery.find());
		} catch (ParseException e) {
			Log.e("error", e.getMessage(), e);
		}
		
		Map<String, User> uniqueUsers = new HashMap<String, User>();
		
		for(ParseUser tmpUser : users) {
			uniqueUsers.put(tmpUser.getUsername(), new User(tmpUser));
		}
		
		List<User> tmpList = new ArrayList<User>(uniqueUsers.values());
		try {
			ParseObject.pinAll(extractParseUsers(tmpList));
		} catch (ParseException e) {
			Log.e("error", e.getMessage(), e);
		}
		
		return tmpList;
	}
	
	public boolean equals(Object o) {
		return parseUser.equals(o);
	}

	public void setUsername(String username) {
		parseUser.setUsername(username);
	}

	public String getUsername() {
		return parseUser.getUsername();
	}

	public void setPassword(String password) {
		parseUser.setPassword(password);
	}

	public void setEmail(String email) {
		parseUser.setEmail(email);
	}

	public String getEmail() {
		return parseUser.getEmail();
	}

	public Date getUpdatedAt() {
		return parseUser.getUpdatedAt();
	}

	public Date getCreatedAt() {
		return parseUser.getCreatedAt();
	}

	public ParseUser fetch() throws ParseException {
		return parseUser.fetch();
	}

	public void signUp() throws ParseException {
		parseUser.signUp();
	}

	public void signUpInBackground(SignUpCallback callback) {
		parseUser.signUpInBackground(callback);
	}

	public boolean isDirty() {
		return parseUser.isDirty();
	}

	public boolean isDirty(String key) {
		return parseUser.isDirty(key);
	}

	public ParseUser fetchIfNeeded() throws ParseException {
		return parseUser.fetchIfNeeded();
	}

	public final void save() throws ParseException {
		parseUser.save();
	}

	public final void saveInBackground(SaveCallback callback) {
		parseUser.saveInBackground(callback);
	}

	public final void saveInBackground() {
		parseUser.saveInBackground();
	}

	public final void saveEventually() {
		parseUser.saveEventually();
	}

	public void saveEventually(SaveCallback callback) {
		parseUser.saveEventually(callback);
	}

	public final void deleteEventually() {
		parseUser.deleteEventually();
	}

	public boolean isNew() {
		return parseUser.isNew();
	}

	public final void deleteEventually(DeleteCallback callback) {
		parseUser.deleteEventually(callback);
	}

	public final void refresh() throws ParseException {
		parseUser.refresh();
	}

	public final void refreshInBackground(RefreshCallback callback) {
		parseUser.refreshInBackground(callback);
	}

	public final <T extends ParseObject> void fetchInBackground(
			GetCallback<T> callback) {
		parseUser.fetchInBackground(callback);
	}

	public final <T extends ParseObject> void fetchIfNeededInBackground(
			GetCallback<T> callback) {
		parseUser.fetchIfNeededInBackground(callback);
	}

	public final void delete() throws ParseException {
		parseUser.delete();
	}

	public final void deleteInBackground(DeleteCallback callback) {
		parseUser.deleteInBackground(callback);
	}

	public final void deleteInBackground() {
		parseUser.deleteInBackground();
	}

	public Date getDate(String key) {
		return parseUser.getDate(key);
	}

	public boolean isDataAvailable() {
		return parseUser.isDataAvailable();
	}

	public <T extends ParseObject> void fetchFromLocalDatastoreInBackground(
			GetCallback<T> callback) {
		parseUser.fetchFromLocalDatastoreInBackground(callback);
	}

	public void fetchFromLocalDatastore() throws ParseException {
		parseUser.fetchFromLocalDatastore();
	}

	public void pinInBackground(String name, SaveCallback callback) {
		parseUser.pinInBackground(name, callback);
	}

	public void pinInBackground(String name) {
		parseUser.pinInBackground(name);
	}

	public void pin(String name) throws ParseException {
		parseUser.pin(name);
	}

	public void pinInBackground(SaveCallback callback) {
		parseUser.pinInBackground(callback);
	}

	public void pinInBackground() {
		parseUser.pinInBackground();
	}

	public void pin() throws ParseException {
		parseUser.pin();
	}

	public void unpinInBackground(String name, DeleteCallback callback) {
		parseUser.unpinInBackground(name, callback);
	}

	public void unpinInBackground(String name) {
		parseUser.unpinInBackground(name);
	}

	public void unpin(String name) throws ParseException {
		parseUser.unpin(name);
	}

	public void unpinInBackground(DeleteCallback callback) {
		parseUser.unpinInBackground(callback);
	}

	public void unpinInBackground() {
		parseUser.unpinInBackground();
	}

	public void unpin() throws ParseException {
		parseUser.unpin();
	}

	public static List<ParseUser> extractParseUsers(List<User> users) {
		List<ParseUser> tmpList = new ArrayList<ParseUser>(users.size());
		for(User u:users) {
			tmpList.add(u.getParseUser());
		}
		return tmpList;
	}
}
