package com.codepath.smartodo.persistence;

import java.util.Collection;
import java.util.List;

import android.content.Context;

import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.model.User;
import com.parse.ParseException;
import com.parse.SaveCallback;

/**
 * A persistence manager for this app should implement this interface.
 *
 * @author Damodar
 *
 */
public interface PersistenceManager {

	//TODO: Add javadocs for the methods
	
	public enum ACCESS_LOCATION {
	    CLOUD, 
	    CLOUD_ELSE_LOCAL, 
	    LOCAL 
	}

	public List<TodoList> getTodoLists();
	
	public String saveTodoList(final TodoList todoList, final SaveCallback callback);
	
	public void deleteObject(Object object) throws ParseException;
	
	public void refreshTodoListsForUser(Context context, User user) throws ParseException;
	
	public void refreshTodoListsForUser(Context context, User user, ACCESS_LOCATION accessLocation) throws ParseException;
	
	public List<TodoList> findAllTodoLists(Context context, User user, ACCESS_LOCATION accessLocation) throws ParseException;
	
	public TodoList findTodoListByName(Context context, String listName) throws ParseException;
	
	public TodoList findTodoListByNameAndUser(Context context, String listName, User user) throws ParseException;

	public TodoList findTodoListByObjectId(Context context, String objectId) throws ParseException;
	
	public int findExistingTodoListIdxByObjectId(String objectId);
	
	public Collection<User> findAllUsers(Context context);

	public Collection<User> findAllUsersLike(Context context, String substring);
	
	/**
	 * Supports only substring based search in the realname, email and username fields. Note that the search is case SENSITIVE.
	 * 
	 * @param substring
	 * @return
	 */
	public Collection<User> findAllUsersLike(Context context, String substring, boolean doFilter);

	

}