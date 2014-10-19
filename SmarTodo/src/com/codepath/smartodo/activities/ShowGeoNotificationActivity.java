package com.codepath.smartodo.activities;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.smartodo.R;
import com.codepath.smartodo.model.TodoGeofence;

public class ShowGeoNotificationActivity extends Activity {
	List<TodoGeofence> mTodoGeofences;
	TodoGeofence mTodoGeofence;
	TextView tvLocationName;
	TextView tvTodoListName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_geo_notification);
		
		// Get the geofencing parameters passed in the intent.
        mTodoGeofences = (List<TodoGeofence>) getIntent().getSerializableExtra(GeofenceActivity.TODO_GEOFENCES_KEY);
        if (mTodoGeofences == null) {
        	 Toast.makeText(this, "Error: A null todoGeoFences object is passed in the intent for GeofenceActivity",
                     Toast.LENGTH_LONG).show();
        	 finish();	
        }
        
        setupViews();
        
        // Just show the information about the first TodoGeofence, for now.
        if (mTodoGeofences.size() > 0) {    	
        	mTodoGeofence = mTodoGeofences.get(0);
        	tvLocationName.setText(mTodoGeofence.getAlertMessage());  
        	tvTodoListName.setText(mTodoGeofence.getTodoListId());  // TODO: fetch the todo list and get its name
        }
        else {
        	tvLocationName.setText("Here is a nice location.");
        	tvLocationName.setText("Remember something?");
        }	
	}
	
	private void setupViews() {
		tvLocationName = (TextView) findViewById(R.id.tvLocationName);
		tvTodoListName = (TextView) findViewById(R.id.tvTodoListName);	
	}

	public void onAcknowlegement(View view) {
		finish();		
	}
}
