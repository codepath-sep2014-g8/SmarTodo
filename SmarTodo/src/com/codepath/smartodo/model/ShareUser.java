package com.codepath.smartodo.model;

/*
 * 
 * Rename later
 */
public class ShareUser {
	private boolean selected;
	private User user;
	
	public ShareUser(User user) {
		selected = false;
		this.user = user;
	}
	
	public ShareUser() {
		selected = false;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
	
}
