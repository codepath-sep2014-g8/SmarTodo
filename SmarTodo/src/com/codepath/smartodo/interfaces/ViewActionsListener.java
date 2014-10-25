package com.codepath.smartodo.interfaces;

import com.codepath.smartodo.model.TodoItem;
import com.codepath.smartodo.model.TodoList;

public interface ViewActionsListener {

	public void onSave(TodoList todoList);
	public void onDelete(TodoList todoList);
	public void onUpdate(TodoList todoList);
	
	public void onSave(TodoItem todoItem);
	public void onUpdate(TodoItem todoItem);
	
	public void onShareActivityRequested(TodoList todoList);
	public void onColotPickerRequested(TodoList todoList);
}
