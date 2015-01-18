package com.codepath.smartodo.persistence;

import com.codepath.smartodo.model.TodoList;

/**
 * Defines methods to be called by a PersistenceManager after completion of 
 * certain persistence operations.
 * 
 * @author Damodar Periwal
 *
 */
public interface PersistenceCallback {
	
	public void added(Exception exception, TodoList todoList);
	
	public void updated(Exception exception, TodoList todoList);
	
	public void deleted(Exception exception, TodoList todoList);
}
