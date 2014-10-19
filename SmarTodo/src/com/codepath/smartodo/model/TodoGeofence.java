package com.codepath.smartodo.model;

import java.io.Serializable;

import com.google.android.gms.location.Geofence;

/**
 * A single Geofence object, defined by its center (latitude and longitude
 * position) and radius.
 * 
Home 
1274 Colleen Way, Campbell, CA 95008
Latitude : 37.288007
Longitude: -121.97236700000002

Neighbor
1234 Colleen Way, Campbell, CA 95008
Latitude : 37.28801199999999
Longitude: -121.97173700000002

Yahoo Building
1350 North Mathilda Avenue, Sunnyvale, CA
Latitude : 37.4151756
Longitude: -122.02449409999997
 */
public class TodoGeofence implements Serializable {
	// Instance variables
	private String geofenceId;
	private double latitude; // between -90 and +90 inclusive
	private double longitude; // between -180 and +180 inclusive
	private float radius;  // in meters
	private long expirationDuration;
	private int transitionType;
	private String alertMessage;
	private String userId;
	private String todoListId;
	private String todoItemId;
	
	/**
	 * @param geofenceId
	 *            The Geofence's request ID
	 * @param latitude
	 *            Latitude of the Geofence's center. The value is not checked
	 *            for validity.
	 * @param longitude
	 *            Longitude of the Geofence's center. The value is not checked
	 *            for validity.
	 * @param radius
	 *            Radius of the geofence circle. The value is not checked for
	 *            validity
	 * @param expiration
	 *            Geofence expiration duration in milliseconds The value is not
	 *            checked for validity.
	 * @param transition
	 *            Type of Geofence transition. The value is not checked for
	 *            validity.
	 * @param todoItemId 
	 * @param todoListId 
	 * @param userId 
	 * @param alertMessage 
	 */
	public TodoGeofence(String geofenceId, double latitude,
			double longitude, float radius, long expiration, int transition, 
			String alertMessage, String userId, String todoListId, String todoItemId) {
		// Set the instance fields from the constructor

		// An identifier for the geofence
		this.geofenceId = geofenceId;

		// Center of the geofence
		this.latitude = latitude;
		this.longitude = longitude;

		// Radius of the geofence, in meters
		this.radius = radius;

		// Expiration time in milliseconds
		this.expirationDuration = expiration;

		// Transition type
		this.transitionType = transition;
		
		this.alertMessage = alertMessage;
		this.userId = userId;
		this.todoListId = todoListId;
		this.todoItemId = todoItemId;
	}

	public String getGeofenceId() {
		return geofenceId;
	}

	public void setGeofenceId(String geofenceId) {
		this.geofenceId = geofenceId;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public float getRadius() {
		return radius;
	}
	
	public void setRadius(float radius) {
		this.radius = radius;
	}

	public long getExpirationDuration() {
		return expirationDuration;
	}
	
	public void setExpirationDuration(long expirationDuration) {
		this.expirationDuration = expirationDuration;
	}
	
	public int getTransitionType() {
		return transitionType;
	}
	
	public void setTransitionType(int transitionType) {
		this.transitionType = transitionType;
	}

	public String getAlertMessage() {
		return alertMessage;
	}

	public void setAlertMessage(String alertMessage) {
		this.alertMessage = alertMessage;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTodoListId() {
		return todoListId;
	}

	public void setTodoListId(String todoListId) {
		this.todoListId = todoListId;
	}

	public String getTodoItemId() {
		return todoItemId;
	}

	public void setTodoItemId(String todoItemId) {
		this.todoItemId = todoItemId;
	}

	/**
	 * Creates a Location Services Geofence object from a SimpleGeofence.
	 * 
	 * @return A Geofence object
	 */
	public Geofence toGeofence() {
		// Build a new Geofence object
		return new Geofence.Builder().setRequestId(getGeofenceId())
				.setTransitionTypes(transitionType)
				.setCircularRegion(getLatitude(), getLongitude(), getRadius())
				.setExpirationDuration(expirationDuration).build();
	}
	
	@Override
	public String toString() {
		return ("geofenceId=" + geofenceId
				+ ", transitionType=" + transitionType 
				+ ", alertMessage=" + alertMessage 
				+ ", todoListId=" + todoListId 
				+ ", todoItemId=" + todoItemId);
	}
}

