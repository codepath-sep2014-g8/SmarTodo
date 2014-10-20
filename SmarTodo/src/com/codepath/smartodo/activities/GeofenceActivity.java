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

package com.codepath.smartodo.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.smartodo.R;
import com.codepath.smartodo.geofence.GeofenceRemover;
import com.codepath.smartodo.geofence.GeofenceRequester;
import com.codepath.smartodo.geofence.GeofenceUtils;
import com.codepath.smartodo.geofence.GeofenceUtils.REMOVE_TYPE;
import com.codepath.smartodo.geofence.GeofenceUtils.REQUEST_TYPE;
import com.codepath.smartodo.geofence.TodoGeofenceStore;
import com.codepath.smartodo.model.TodoGeofence;
import com.codepath.smartodo.notifications.GeofenceReceiver;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;

/**
 * Shell activity to register Geofences
 */
public class GeofenceActivity extends FragmentActivity {
    /*
     * Use to set an expiration time for a geofence. After this amount
     * of time Location Services will stop tracking the geofence.
     * Remember to unregister a geofence when you're finished with it.
     * Otherwise, your app will use up battery. To continue monitoring
     * a geofence indefinitely, set the expiration time to
     * Geofence#NEVER_EXPIRE.
     */
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * DateUtils.HOUR_IN_MILLIS;
	static final String TODO_GEOFENCE_KEY = "Todo_Geofence";
	public static final String TODO_GEOFENCES_KEY = "Todo_Geofences";

    // Store the current request
    private REQUEST_TYPE mRequestType;

    // Store the current type of removal
    private REMOVE_TYPE mRemoveType;

    // Persistent storage for geofences
    private TodoGeofenceStore mPrefs;

    // Store a list of geofences to add
    List<Geofence> mCurrentGeofences;

    // Add geofences handler
    private GeofenceRequester mGeofenceRequester;
    
    // Remove geofences handler
    private GeofenceRemover mGeofenceRemover;

    /*
     * An instance of an inner class that receives broadcasts from listeners and from the
     * IntentService that receives geofence transition events
     */
    private GeofenceReceiver mBroadcastReceiver;

    // An intent filter for the broadcast receiver
    private IntentFilter mIntentFilter;

    // Store the list of geofences to remove
    private List<String> mGeofenceIdsToRemove;
    
    private List<TodoGeofence> mTodoGeoFences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle("GeofenceActivity");
   
        // Get the geofencing parameters passed in the intent.
        mTodoGeoFences = (List<TodoGeofence>) getIntent().getSerializableExtra(GeofenceActivity.TODO_GEOFENCES_KEY);
        if (mTodoGeoFences == null) {
        	 Toast.makeText(this, "Error: A null todoGeoFences object is passed in the intent for GeofenceActivity",
                     Toast.LENGTH_LONG).show();
        	 finish();	
        }
        
        doGeofencingSetup();
        
        initTodoGeofences();
        
        // sampleNotification();
        
        registerGeofence(mTodoGeoFences);   
    }
    
    private void sampleNotification() {
    	
    	 // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
       
        // Set the notification contents
        builder.setSmallIcon(R.drawable.ic_notification)
               .setContentTitle(getString(R.string.geofence_transition_alert_title))
               .setContentText(getString(R.string.geofence_transition_alert_text));

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());	
	}

	private void initTodoGeofences() {
    	for (TodoGeofence todoGeoFence : mTodoGeoFences) {
    		if (todoGeoFence.getGeofenceId() == null) {
            	todoGeoFence.setGeofenceId(UUID.randomUUID().toString());
            }		
    	} 	
    }

    private void doGeofencingSetup() {
    	// Create a new broadcast receiver to receive updates from the listeners and service
        mBroadcastReceiver = new GeofenceReceiver();

        // Create an intent filter for the broadcast receiver
        mIntentFilter = new IntentFilter();

        // Action for broadcast Intents that report successful addition of geofences
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_ADDED);

        // Action for broadcast Intents that report successful removal of geofences
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_REMOVED);

        // Action for broadcast Intents containing various types of geofencing errors
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCE_ERROR);

        // All Location Services sample apps use this category
        mIntentFilter.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);

        // Instantiate a new geofence storage area
        mPrefs = new TodoGeofenceStore(this);

        // Instantiate the current List of geofences
        mCurrentGeofences = new ArrayList<Geofence>();

        // Instantiate a Geofence requester
        mGeofenceRequester = new GeofenceRequester(this);

        // Instantiate a Geofence remover
        mGeofenceRemover = new GeofenceRemover(this);	
	}

	/*
     * Handle results returned to this Activity by other Activities started with
     * startActivityForResult(). In particular, the method onConnectionFailed() in
     * GeofenceRemover and GeofenceRequester may call startResolutionForResult() to
     * start an Activity that handles Google Play services problems. The result of this
     * call returns here, to onActivityResult.
     * calls
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Choose what to do based on the request code
    	
    	Log.d(GeofenceUtils.APPTAG, "In GeofenceActivity:onActivityResult, got request code " + requestCode);
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        // If the request was to add geofences
                        if (GeofenceUtils.REQUEST_TYPE.ADD == mRequestType) {

                            // Toggle the request flag and send a new request
                            mGeofenceRequester.setInProgressFlag(false);

                            // Restart the process of adding the current geofences
                            mGeofenceRequester.addGeofences(mCurrentGeofences);

                        // If the request was to remove geofences
                        } else if (GeofenceUtils.REQUEST_TYPE.REMOVE == mRequestType ){

                            // Toggle the removal flag and send a new removal request
                            mGeofenceRemover.setInProgressFlag(false);

                            // If the removal was by Intent
                            if (GeofenceUtils.REMOVE_TYPE.INTENT == mRemoveType) {

                                // Restart the removal of all geofences for the PendingIntent
                                mGeofenceRemover.removeGeofencesByIntent(
                                    mGeofenceRequester.getRequestPendingIntent());

                            // If the removal was by a List of geofence IDs
                            } else {

                                // Restart the removal of the geofence list
                                mGeofenceRemover.removeGeofencesById(mGeofenceIdsToRemove);
                            }
                        }
                    break;

                    // If any other result was returned by Google Play services
                    default:

                        // Report that Google Play services was unable to resolve the problem.
                        Log.d(GeofenceUtils.APPTAG, getString(R.string.no_resolution));
                }

            // If any other request code was received
            default:
               // Report that this Activity received an unknown requestCode
               Log.d(GeofenceUtils.APPTAG,
                       getString(R.string.unknown_activity_request_code, requestCode));

               break;
        }
    }

    /*
     * Whenever the Activity resumes, reconnect the client to Location
     * Services and reload the last geofences that were set
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Register the broadcast receiver to receive status updates
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    /*
     * Inflate the app menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        return true;

    }
    /*
     * Respond to menu item selections
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /*
     * Save the current geofence settings in SharedPreferences.
     */
    @Override
    protected void onPause() {
        super.onPause();
        for (TodoGeofence todoGeoFence : mTodoGeoFences) {
        	mPrefs.setGeofence(todoGeoFence.getGeofenceId(), todoGeoFence);
    	} 
    }

    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {

            // In debug mode, log the status
            Log.d(GeofenceUtils.APPTAG, getString(R.string.play_services_available));

            // Continue
            return true;

        // Google Play services was not available for some reason
        } else {

            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getSupportFragmentManager(), GeofenceUtils.APPTAG);
            }
            return false;
        }
    }

    /**
     * Called when the user clicks the "Remove geofences" button
     *
     * @param view The view that triggered this callback
     */
    public void onUnregisterByPendingIntentClicked(View view) {
        /*
         * Remove all geofences set by this app. To do this, get the
         * PendingIntent that was added when the geofences were added
         * and use it as an argument to removeGeofences(). The removal
         * happens asynchronously; Location Services calls
         * onRemoveGeofencesByPendingIntentResult() (implemented in
         * the current Activity) when the removal is done
         */

        /*
         * Record the removal as remove by Intent. If a connection error occurs,
         * the app can automatically restart the removal if Google Play services
         * can fix the error
         */
        // Record the type of removal
        mRemoveType = GeofenceUtils.REMOVE_TYPE.INTENT;

        /*
         * Check for Google Play services. Do this after
         * setting the request type. If connecting to Google Play services
         * fails, onActivityResult is eventually called, and it needs to
         * know what type of request was in progress.
         */
        if (!servicesConnected()) {

            return;
        }

        // Try to make a removal request
        try {
        /*
         * Remove the geofences represented by the currently-active PendingIntent. If the
         * PendingIntent was removed for some reason, re-create it; since it's always
         * created with FLAG_UPDATE_CURRENT, an identical PendingIntent is always created.
         */
        mGeofenceRemover.removeGeofencesByIntent(mGeofenceRequester.getRequestPendingIntent());

        } catch (UnsupportedOperationException e) {
            // Notify user that previous request hasn't finished.
            Toast.makeText(this, R.string.remove_geofences_already_requested_error,
                        Toast.LENGTH_LONG).show();
        }
    }

    private void registerGeofence(List<TodoGeofence> todoGeoFences) {
        mRequestType = GeofenceUtils.REQUEST_TYPE.ADD;

        /*
         * Check for Google Play services. Do this after
         * setting the request type. If connecting to Google Play services
         * fails, onActivityResult is eventually called, and it needs to
         * know what type of request was in progress.
         */
        if (!servicesConnected()) {
            return;
        }

        /*
         * Check that the input fields have values and that the values are with the
         * permitted range
         */
        
        for (TodoGeofence todoGeoFence : mTodoGeoFences) {
        	 if (!checkGeofenceParameters(todoGeoFence.getLatitude(), todoGeoFence.getLongitude(),
        			 todoGeoFence.getRadius())) {
             	Toast.makeText(this, "One of the lang,  lat, or radius values is invalid", Toast.LENGTH_SHORT).show();
                return;
             }
    	} 
       

        // Store this flat version in SharedPreferences
        for (TodoGeofence todoGeoFence : mTodoGeoFences) {
        	mPrefs.setGeofence(todoGeoFence.getGeofenceId(), todoGeoFence);
    	} 

        /*
         * Add Geofence objects to a List. toGeofence()
         * creates a Location Services Geofence object from a
         * flat object
         */
        
        for (TodoGeofence todoGeoFence : mTodoGeoFences) {
        	mCurrentGeofences.add(todoGeoFence.toGeofence());
    	} 

        // Start the request. Fail if there's already a request in progress
        try {
            // Try to add geofences
            mGeofenceRequester.addGeofences(mCurrentGeofences);
        } catch (UnsupportedOperationException e) {
            // Notify user that previous request hasn't finished.
            Toast.makeText(this, R.string.add_geofences_already_requested_error,
                        Toast.LENGTH_LONG).show();
        }
    }
    
	/**
	 * Check all the geofencing parameter values for validity.
	 * 
	 * @return true if all the parameter values are valid; otherwise false
	 */
	private boolean checkGeofenceParameters(double lat, double lang, float radius) {
		boolean parametersOK = true;

		/*
		 * Test to ensure that the values are within the acceptable range. 
		 * Test latitude and longitude for minimum and maximum values. Highlight
		 * incorrect values and set a Toast in the UI.
		 */

		if (lat > GeofenceUtils.MAX_LATITUDE
				|| lat < GeofenceUtils.MIN_LATITUDE) {
			Toast.makeText(this,
					R.string.geofence_input_error_latitude_invalid,
					Toast.LENGTH_LONG).show();

			// Set the validity to "invalid" (false)
			parametersOK = false;
		}

		if ((lang > GeofenceUtils.MAX_LONGITUDE)
				|| (lang < GeofenceUtils.MIN_LONGITUDE)) {
			Toast.makeText(this,
					R.string.geofence_input_error_longitude_invalid,
					Toast.LENGTH_LONG).show();

			// Set the validity to "invalid" (false)
			parametersOK = false;
		}

		if (radius < GeofenceUtils.MIN_RADIUS) {
			Toast.makeText(this, R.string.geofence_input_error_radius_invalid,
					Toast.LENGTH_LONG).show();

			// Set the validity to "invalid" (false)
			parametersOK = false;
		}

		return parametersOK;
	}

    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
}
