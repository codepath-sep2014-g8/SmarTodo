package com.codepath.smartodo.geofence;

import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.codepath.smartodo.R;
import com.codepath.smartodo.activities.GeofenceActivity;
import com.codepath.smartodo.activities.ShowGeoNotificationActivity;
import com.codepath.smartodo.model.TodoGeofence;
import com.codepath.smartodo.services.ModelManagerService;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

/**
 * This class receives geofence transition events from Location Services, in the
 * form of an Intent containing the transition type and geofence id(s) that triggered
 * the event.
 */
public class ReceiveTransitionsIntentService extends IntentService {
	private String TAG = "ReceiveTransitionsIntentService";

    /**
     * Sets an identifier for this class' background thread
     */
    public ReceiveTransitionsIntentService() {
        super("ReceiveTransitionsIntentService");
        // Log.d("Debug", "In ReceiveTransitionsIntentService()");
    }

    /**
     * Handles incoming intents
     * @param intent The Intent sent by Location Services. This Intent is provided
     * to Location Services (inside a PendingIntent) when you call addGeofences()
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        // Create a local broadcast Intent
        Intent broadcastIntent = new Intent();

        // Give it the category for all intents sent by the Intent Service
        broadcastIntent.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);

        // First check for errors
        if (LocationClient.hasError(intent)) {

            // Get the error code
            int errorCode = LocationClient.getErrorCode(intent);

            // Get the error message
            String errorMessage = LocationServiceErrorMessages.getErrorString(this, errorCode);

            // Log the error
            Log.e(GeofenceUtils.APPTAG,
                    getString(R.string.geofence_transition_error_detail, errorMessage));

            // Set the action and error message for the broadcast intent
            broadcastIntent.setAction(GeofenceUtils.ACTION_GEOFENCE_ERROR)
                           .putExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS, errorMessage);

            // Broadcast the error *locally* to other components in this app
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

        // If there's no error, get the transition type and create a notification
        } else {

            // Get the type of transition (entry or exit)
            int transition = LocationClient.getGeofenceTransition(intent);
            Log.d(TAG, "In ReceiveTransitionsIntentService:onHandleIntent, transition is " + transition);

            // Test that a valid transition was reported
            if (
                    (transition == Geofence.GEOFENCE_TRANSITION_ENTER)
                    ||
                    (transition == Geofence.GEOFENCE_TRANSITION_EXIT)
                    ||
                    (transition == Geofence.GEOFENCE_TRANSITION_DWELL)
               ) {

                // Post a notification
                List<Geofence> geofences = LocationClient.getTriggeringGeofences(intent);
                String[] geofenceIds = new String[geofences.size()];
                for (int index = 0; index < geofences.size() ; index++) {
                    geofenceIds[index] = geofences.get(index).getRequestId();
                }
                String ids = TextUtils.join(GeofenceUtils.GEOFENCE_ID_DELIMITER,geofenceIds);
                String transitionType = getTransitionString(transition);

                sendNotification(transitionType, ids);

                // Log the transition type and a message
                Log.d(GeofenceUtils.APPTAG,
                        getString(
                                R.string.geofence_transition_notification_title,
                                transitionType,
                                ids));
                Log.d(GeofenceUtils.APPTAG,
                        getString(R.string.geofence_transition_notification_text));

            // An invalid transition was reported
            } else {
                // Always log as an error
                Log.e(GeofenceUtils.APPTAG,
                        getString(R.string.geofence_transition_invalid_type, transition, intent.getAction()));
            }
        }
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the main Activity (GeofenceActivity).
     * @param transitionType The type of transition that occurred.
     *
     */
    private void sendNotification(String transitionType, String ids) {

    	Log.d(TAG, "In sendNotification: ids are " + ids.toString());
    	TodoGeofenceStore mPrefs = new TodoGeofenceStore(this);
    	String[] idArray = TextUtils.split(ids, GeofenceUtils.GEOFENCE_ID_DELIMITER.toString());
    	
    	ArrayList<TodoGeofence> todoGeofences = new ArrayList<TodoGeofence>();
    	for (int i = 0; i < idArray.length; i++) {
    		String geofenceId = idArray[i];
        	TodoGeofence todoGeofence = mPrefs.getGeofence(geofenceId);
        	if (todoGeofence != null) {
        	    Log.d(TAG, "In sendNotification: geofenceId is " + geofenceId + ", message is " + todoGeofence.getAlertMessage());
            	todoGeofences.add(todoGeofence);
        	    Log.d("Debug", "In sendNotification: todoGeofence retrieved from preferences is " + todoGeofence.toString());
        	}
        	// Toast.makeText(this, todoGeofence.getAlertMessage(), Toast.LENGTH_LONG).show();
    	}
    		
       /* // Create an explicit content Intent that starts the ShowGeoNotificationActivity Activity
        Intent notificationIntent =
                new Intent(getApplicationContext(), ShowGeoNotificationActivity.class);
        notificationIntent.putExtra(GeofenceActivity.TODO_GEOFENCES_KEY, todoGeofences);		

        // Construct a task stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the ShowGeoNotificationActivity Activity to the task stack as the parent
        stackBuilder.addParentStack(ShowGeoNotificationActivity.class);

        // Push the content Intent onto the stack
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);*/

        // Set the notification contents
/*        builder.setSmallIcon(R.drawable.ic_notification)
               .setContentTitle(
                       getString(R.string.geofence_transition_notification_title,
                               transitionType, ids))
               .setContentText(getString(R.string.geofence_transition_notification_text))
               .setContentIntent(notificationPendingIntent)  // Todo: remove this comment after understanding why it may cause problem.
               ;*/
        
        String title =  getString(R.string.geofence_transition_alert_title);
        String contentText = getString(R.string.geofence_transition_alert_text);
        
        int numGeoFencesHit = todoGeofences.size();
        if (numGeoFencesHit > 0) {
        	StringBuffer titleBuffer = new StringBuffer();
        	StringBuffer contentTextBuffer  = new StringBuffer();
        	int i = 0;
        	for (TodoGeofence todoGeofence : todoGeofences) {
        		titleBuffer.append(todoGeofence.getAlertMessage());
        		contentTextBuffer.append(todoGeofence.getTodoListName());
        		i++;
        		if (i < numGeoFencesHit) {			
        			titleBuffer.append(", ");
        			contentTextBuffer.append(", ");
        		}
        	}
        	title = titleBuffer.toString();
        	contentText = contentTextBuffer.toString();
        	ModelManagerService.getInstance().displayNotification(title, contentText);
        }
        
/*		builder.setSmallIcon(R.drawable.ic_notification)
			.setContentTitle(title)
			.setContentText(contentText);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());*/
        
        
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     * @param transitionType A transition type constant defined in Geofence
     * @return A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {

            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
                
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return getString(R.string.geofence_transition_dwell);

            default:
                return getString(R.string.geofence_transition_unknown);
        }
    }
}
