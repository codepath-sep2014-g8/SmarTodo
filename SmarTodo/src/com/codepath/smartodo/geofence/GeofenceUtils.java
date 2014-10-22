/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codepath.smartodo.geofence;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.codepath.smartodo.activities.GeofenceActivity;
import com.codepath.smartodo.model.TodoGeofence;

/**
 * This class defines constants used by location sample apps.
 */
public final class GeofenceUtils {

    // Used to track what type of geofence removal request was made.
    public enum REMOVE_TYPE {INTENT, LIST}

    // Used to track what type of request is in process
    public enum REQUEST_TYPE {ADD, REMOVE}

    /*
     * A log tag for the application
     */
    public static final String APPTAG = "Geofence Detection";

    // Intent actions
    public static final String ACTION_CONNECTION_ERROR =
            "com.codepath.smartodo.geofence.ACTION_CONNECTION_ERROR";

    public static final String ACTION_CONNECTION_SUCCESS =
            "com.codepath.smartodo.geofence.ACTION_CONNECTION_SUCCESS";

    public static final String ACTION_GEOFENCES_ADDED =
            "com.codepath.smartodo.geofence.ACTION_GEOFENCES_ADDED";

    public static final String ACTION_GEOFENCES_REMOVED =
            "com.codepath.smartodo.geofence.ACTION_GEOFENCES_DELETED";

    public static final String ACTION_GEOFENCE_ERROR =
            "com.codepath.smartodo.geofence.ACTION_GEOFENCES_ERROR";

    public static final String ACTION_GEOFENCE_TRANSITION =
            "com.codepath.smartodo.geofence.ACTION_GEOFENCE_TRANSITION";

    public static final String ACTION_GEOFENCE_TRANSITION_ERROR =
                    "com.codepath.smartodo.geofence.ACTION_GEOFENCE_TRANSITION_ERROR";

    // The Intent category used by all Location Services sample apps
    public static final String CATEGORY_LOCATION_SERVICES =
                    "com.codepath.smartodo.geofence.CATEGORY_LOCATION_SERVICES";

    // Keys for extended data in Intents
    public static final String EXTRA_CONNECTION_CODE =
                    "com.codepath.smartodo.EXTRA_CONNECTION_CODE";

    public static final String EXTRA_CONNECTION_ERROR_CODE =
            "com.codepath.smartodo.geofence.EXTRA_CONNECTION_ERROR_CODE";

    public static final String EXTRA_CONNECTION_ERROR_MESSAGE =
            "com.codepath.smartodo.geofence.EXTRA_CONNECTION_ERROR_MESSAGE";

    public static final String EXTRA_GEOFENCE_STATUS =
            "com.codepath.smartodo.geofence.EXTRA_GEOFENCE_STATUS";

    /*
     * Keys for flattened geofences stored in SharedPreferences
     */
    public static final String KEY_LATITUDE = "com.codepath.smartodo.geofence.KEY_LATITUDE";

    public static final String KEY_LONGITUDE = "com.codepath.smartodo.geofence.KEY_LONGITUDE";

    public static final String KEY_RADIUS = "com.codepath.smartodo.geofence.KEY_RADIUS";

    public static final String KEY_EXPIRATION_DURATION =
            "com.codepath.smartodo.geofence.KEY_EXPIRATION_DURATION";

    public static final String KEY_TRANSITION_TYPE =
            "com.codepath.smartodo.geofence.KEY_TRANSITION_TYPE";
    
    public static final String KEY_ALERT_MESSAGE_TYPE =
            "com.codepath.smartodo.geofence.KEY_ALERT_MESSAGE_TYPE";
    
    public static final String KEY_USER_ID_TYPE =
            "com.codepath.smartodo.geofence.KEY_USER_ID_TYPE";
    
    public static final String KEY_TODO_LIST_NAME_TYPE =
            "com.codepath.smartodo.geofence.KEY_TODO_LIST_NAME_TYPE";
    
    public static final String KEY_TODO_ITEM_NAME_TYPE =
            "com.codepath.smartodo.geofence.KEY_TODO_ITEM_NAME_TYPE";

    // The prefix for flattened geofence keys
    public static final String KEY_PREFIX =
            "com.codepath.smartodo.geofence.KEY";

    // Invalid values, used to test geofence storage when retrieving geofences
    public static final long INVALID_LONG_VALUE = -999l;

    public static final float INVALID_FLOAT_VALUE = -999.0f;

    public static final int INVALID_INT_VALUE = -999;
    
    public static final String INVALID_STRING_VALUE = "INVALID_VALUE";

    /*
     * Constants used in verifying the correctness of input values
     */
    public static final double MAX_LATITUDE = 90.d;

    public static final double MIN_LATITUDE = -90.d;

    public static final double MAX_LONGITUDE = 180.d;

    public static final double MIN_LONGITUDE = -180.d;

    public static final float MIN_RADIUS = 1f;

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // A string of length 0, used to clear out input fields
    public static final String EMPTY_STRING = new String();

    public static final CharSequence GEOFENCE_ID_DELIMITER = ",";
    
    public static class GeoPoint {
    	double latitude;
    	double longitude;
		public GeoPoint(double latitude, double longitude) {
			super();
			this.latitude = latitude;
			this.longitude = longitude;
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
    }
     
    public static GeoPoint getGeoPointFromSreetAddress(Context context, String streetAddress) {
    	Geocoder geocoder = new Geocoder(context);
    	List<Address> addresses;
        try {
    	    addresses = geocoder.getFromLocationName(streetAddress, 1);
    	    if (addresses == null || addresses.size() == 0) {
    	        return null;
    	    }
    	    Address location = addresses.get(0);

    	    return new GeoPoint(location.getLatitude(), location.getLongitude());	                  

    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        return null;
    }
      
    // Creates a Geofence using the passed parameters and installs that geofence using
    // the GeofenceActivity class.
    //
    // Todo => Move GeofenceActivity functionality to a service.
    public static void setupTestGeofences(Context context, String userId, String streetAddress, 
			int radius, int transition, String alertMessage, String todoListName, String todoItemName) {
		ArrayList<TodoGeofence> todoGeofences = new ArrayList<TodoGeofence>();
		double latitude, longitude;	
		
		GeoPoint geoPoint = getGeoPointFromSreetAddress(context, streetAddress);
		if (geoPoint != null) {
			latitude = geoPoint.getLatitude();
			longitude = geoPoint.getLongitude();
			// Override for testing
			// latitude = 37.288028; // using Accurate GPS: 37.2880556; // using
			// precision GPS: 37.288028
			// longitude = -121.972359; // using Accurate GPS: -121.9722823; //
			// using precision GPS: -121.972359
			Log.d("Debug", "For " + streetAddress + ", latitude=" + latitude
					+ ", longitude=" + longitude);

			TodoGeofence todoGeofence = new TodoGeofence(null, latitude,
					longitude, radius,
					GeofenceActivity.GEOFENCE_EXPIRATION_IN_MILLISECONDS,
					transition, alertMessage, userId, todoListName,
					todoItemName);
			todoGeofences.add(todoGeofence);

			Intent intent = new Intent(context, GeofenceActivity.class);
			intent.putExtra(GeofenceActivity.TODO_GEOFENCES_KEY, todoGeofences);
			context.startActivity(intent);
		} else {
			Log.e("Error", "No Geopoint found for " + streetAddress);
		}
		
	}

}
