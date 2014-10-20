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

import com.codepath.smartodo.activities.GeofenceActivity;
import com.codepath.smartodo.model.TodoGeofence;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Storage for geofence values, implemented in SharedPreferences.
 * For a production app, use a content provider that's synced to the
 * web or loads geofence data based on current location.
 */
public class TodoGeofenceStore {
	
	private String TAG = "TodoGeofenceStore";

    // The SharedPreferences object in which geofences are stored
    private final SharedPreferences mPrefs;

    // The name of the resulting SharedPreferences
    private static final String SHARED_PREFERENCE_NAME =
    		GeofenceActivity.class.getSimpleName();

    // Create the SharedPreferences storage with private access only
    public TodoGeofenceStore(Context context) {
        mPrefs =
                context.getSharedPreferences(
                        SHARED_PREFERENCE_NAME,
                        Context.MODE_PRIVATE);
    }

    /**
     * Returns a stored geofence by its id, or returns {@code null}
     * if it's not found.
     *
     * @param id The ID of a stored geofence
     * @return A geofence defined by its center and radius. See
     * {@link TodoGeofence}
     */
    public TodoGeofence getGeofence(String id) {

        /*
         * Get the latitude for the geofence identified by id, or GeofenceUtils.INVALID_VALUE
         * if it doesn't exist
         */
        double lat = mPrefs.getFloat(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_LATITUDE),
                GeofenceUtils.INVALID_FLOAT_VALUE);

        /*
         * Get the longitude for the geofence identified by id, or
         * -999 if it doesn't exist
         */
        double lng = mPrefs.getFloat(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_LONGITUDE),
                GeofenceUtils.INVALID_FLOAT_VALUE);

        /*
         * Get the radius for the geofence identified by id, or GeofenceUtils.INVALID_VALUE
         * if it doesn't exist
         */
        float radius = mPrefs.getFloat(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_RADIUS),
                GeofenceUtils.INVALID_FLOAT_VALUE);

        /*
         * Get the expiration duration for the geofence identified by
         * id, or GeofenceUtils.INVALID_VALUE if it doesn't exist
         */
        long expirationDuration = mPrefs.getLong(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_EXPIRATION_DURATION),
                GeofenceUtils.INVALID_LONG_VALUE);

        /*
         * Get the transition type for the geofence identified by
         * id, or GeofenceUtils.INVALID_VALUE if it doesn't exist
         */
        int transitionType = mPrefs.getInt(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_TRANSITION_TYPE),
                GeofenceUtils.INVALID_INT_VALUE);
        
        /*
         * Get the alert message for the geofence identified by
         * id, or GeofenceUtils.INVALID_VALUE if it doesn't exist
         */
        String alertMessage = mPrefs.getString(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_ALERT_MESSAGE_TYPE),
                GeofenceUtils.INVALID_STRING_VALUE);
        
        /*
         * Get the user id for the geofence identified by
         * id, or GeofenceUtils.INVALID_VALUE if it doesn't exist
         */
        String userId = mPrefs.getString(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_USER_ID_TYPE),
                GeofenceUtils.INVALID_STRING_VALUE);
        
        /*
         * Get the TodoList id for the geofence identified by
         * id, or GeofenceUtils.INVALID_VALUE if it doesn't exist
         */
        String todoListName = mPrefs.getString(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_TODO_LIST_NAME_TYPE),
                GeofenceUtils.INVALID_STRING_VALUE);
        
        /*
         * Get the TodoItem id for the geofence identified by
         * id, or GeofenceUtils.INVALID_VALUE if it doesn't exist
         */
        String todoItemName = mPrefs.getString(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_TODO_ITEM_NAME_TYPE),
                GeofenceUtils.INVALID_STRING_VALUE);

        // If none of the values is incorrect, return the object
        if (
            lat != GeofenceUtils.INVALID_FLOAT_VALUE &&
            
            lng != GeofenceUtils.INVALID_FLOAT_VALUE &&
            radius != GeofenceUtils.INVALID_FLOAT_VALUE &&
            expirationDuration != GeofenceUtils.INVALID_LONG_VALUE &&
            transitionType != GeofenceUtils.INVALID_INT_VALUE &&
            !alertMessage.equals(GeofenceUtils.INVALID_STRING_VALUE) &&
            !userId.equals(GeofenceUtils.INVALID_STRING_VALUE) &&
            !todoListName.equals(GeofenceUtils.INVALID_STRING_VALUE) &&
            !todoItemName.equals(GeofenceUtils.INVALID_STRING_VALUE)       
            ) {
        	    // Log.d(TAG, "Returning a good object from getGeofence for id=" + id);

                // Return a good Geofence object
                return new TodoGeofence(id, lat, lng, radius, expirationDuration, transitionType,
            		alertMessage, userId, todoListName, todoItemName);
        // Otherwise, return null.
        } else {
        	// Log.d(TAG, "Returning null from getGeofence for id=" + id);
            return null;
        }
    }

    /**
     * Save a geofence.

     * @param geofence The {@link TodoGeofence} containing the
     * values you want to save in SharedPreferences
     */
    public void setGeofence(String id, TodoGeofence geofence) {

        /*
         * Get a SharedPreferences editor instance. Among other
         * things, SharedPreferences ensures that updates are atomic
         * and non-concurrent
         */
        Editor editor = mPrefs.edit();
        
        // Log.d(TAG, "In setGeofence:, TodoGeofence is: " + geofence.toString());

        // Write the Geofence values to SharedPreferences
        editor.putFloat(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_LATITUDE),
                (float) geofence.getLatitude());

        editor.putFloat(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_LONGITUDE),
                (float) geofence.getLongitude());

        editor.putFloat(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_RADIUS),
                geofence.getRadius());

        editor.putLong(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_EXPIRATION_DURATION),
                geofence.getExpirationDuration());

        editor.putInt(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_TRANSITION_TYPE),
                geofence.getTransitionType());
        
        editor.putString(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_ALERT_MESSAGE_TYPE),
                geofence.getAlertMessage());
        
        editor.putString(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_USER_ID_TYPE),
                geofence.getUserId());
        
        editor.putString(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_TODO_LIST_NAME_TYPE),
                geofence.getTodoListName());
        
        editor.putString(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_TODO_ITEM_NAME_TYPE),
                geofence.getTodoItemName());

        // Commit the changes
        editor.commit();
        
        // Test weather the item was stored properly.
        /* TodoGeofence newTodoGeofence = getGeofence(id);
        if (newTodoGeofence != null) {
        	Log.d(TAG, "getGeofence returned a good object in setGeofence for id=" + id);
        } else {
        	Log.d(TAG, "getGeofence returned null in setGeofence for id=" + id);
        }*/
        
    }

    public void clearGeofence(String id) {

        // Remove a flattened geofence object from storage by removing all of its keys
        Editor editor = mPrefs.edit();
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_LATITUDE));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_LONGITUDE));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_RADIUS));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_EXPIRATION_DURATION));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_TRANSITION_TYPE));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_ALERT_MESSAGE_TYPE));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_USER_ID_TYPE));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_TODO_LIST_NAME_TYPE));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_TODO_ITEM_NAME_TYPE));
        editor.commit();
    }

    /**
     * Given a Geofence object's ID and the name of a field
     * (for example, GeofenceUtils.KEY_LATITUDE), return the key name of the
     * object's values in SharedPreferences.
     *
     * @param id The ID of a Geofence object
     * @param fieldName The field represented by the key
     * @return The full key name of a value in SharedPreferences
     */
    private String getGeofenceFieldKey(String id, String fieldName) {

        return
                GeofenceUtils.KEY_PREFIX +
                id +
                "_" +
                fieldName;
    }
}
