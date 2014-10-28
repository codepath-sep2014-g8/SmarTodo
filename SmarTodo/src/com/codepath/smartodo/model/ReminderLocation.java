package com.codepath.smartodo.model;

public class ReminderLocation {	
	private String name;
	private String streetAddress;
	private String imageUrl;
	private int imageResourceId;  // An easy alternative for showing images

	public ReminderLocation() {
		
	}
	
	public ReminderLocation(String name, String streetAddress, String imageUrl, int imageResourceId) {
		super();
		this.name = name;
		this.streetAddress = streetAddress;
		this.imageUrl = imageUrl;
		this.imageResourceId = imageResourceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getImageResourceId() {
		return imageResourceId;
	}

	public void setImageResourceId(int imageResourceId) {
		this.imageResourceId = imageResourceId;
	}

}
