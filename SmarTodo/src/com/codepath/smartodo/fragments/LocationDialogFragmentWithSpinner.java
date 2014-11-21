package com.codepath.smartodo.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.codepath.smartodo.R;
import com.codepath.smartodo.geofence.GeofenceUtils;
import com.codepath.smartodo.helpers.Utils;
import com.codepath.smartodo.model.Address;
import com.codepath.smartodo.model.TodoList;
import com.google.android.gms.location.Geofence;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

public class LocationDialogFragmentWithSpinner extends DialogFragment implements android.content.DialogInterface.OnClickListener {	
	/**
	 * 
	 */
	private final TodoListFragment todoListFragment;
	private Spinner locationSpinner;
	private TodoList todoList;
	private HashMap<String, String> locationsMap;
	private List<String> locationList;
	private Address currentAddress;
	private String currentLocation;
	private ArrayAdapter<String> dataAdapter;

	public LocationDialogFragmentWithSpinner(TodoListFragment todoListFragment, TodoList todoList, HashMap<String, String> locationsMap) {
		super();
		this.todoListFragment = todoListFragment;
		this.todoList = todoList;
		this.locationsMap = locationsMap;
		initCurrentLocation();
	}
	
    private void initCurrentLocation() {    
    	if (todoList == null || locationsMap == null) {
    		return;
    	}
	    currentAddress = todoList.getAddress();
    	if (currentAddress != null) {
    	    currentLocation = currentAddress.getName();    	        	       
    	} else {
    		currentLocation = null;
    	}
    	// Create a list of location names
    	locationList = new ArrayList<String>();		
    	locationList.addAll(locationsMap.keySet());
    	Collections.sort(locationList, String.CASE_INSENSITIVE_ORDER);
    	// Log.d(TAG, "In LocationDialogFragment: total location keys are " + locationList.size());
	}
    
    private TextView getCustomTitle() {
    	TextView title = new TextView(getActivity());
    	title.setText(getString(R.string.title_location_reminding_dialog, todoList.getName()));
    	title.setGravity(Gravity.CENTER_HORIZONTAL);
    	title.setTextSize(16);
    	title.setBackgroundColor(getActivity().getResources().getColor(this.todoListFragment.colorId));
    	title.setAlpha(0.8f);
    	title.setTextColor(getResources().getColor(R.color.white));
    	return title;
    }
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	if (todoList == null || locationsMap == null) {
    		return null;
    	}
		// Create and initialize an adapter
		dataAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item, locationList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Create and initialize a spinner
		locationSpinner = new Spinner(getActivity());
		// locationSpinner.setBackgroundResource(R.drawable.drop_shadow);
		locationSpinner.setAdapter(dataAdapter);
		if (currentLocation != null) {
			//set the default choice according to the current value
			int spinnerPosition = dataAdapter.getPosition(currentLocation);			
			locationSpinner.setSelection(spinnerPosition);		
		}

		// Create an AlertDialog and associate the spinner for location names
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
		builder.setCustomTitle(getCustomTitle());		 			
		builder.setView(locationSpinner); 
		builder.setPositiveButton("OK", this).setNegativeButton("CANCEL", null);	
		return builder.create();
	}
    
	@Override
	public void onClick(DialogInterface dialog, int which) {
		String locationKey = (String) locationSpinner.getSelectedItem();
				
		// Log.d(TAG, "In onClick, locationKey is:" + locationKey);
		if (Utils.isNullOrEmpty(locationKey)) {
			dialog.dismiss();
			return;
		}

		if (!Utils.isNullOrEmpty(currentLocation)
				&& (locationKey.equalsIgnoreCase(currentLocation))) {
			dialog.dismiss();
			return; // Nothing much to do; same old location has been chosen.
		}

		String streetAddress = locationsMap.get(locationKey);
		// Log.d(TAG, "In onClick, streetAddress is:" + streetAddress);
		// The following should not happen because a street address
		// would always be associated with a location name
		if (Utils.isNullOrEmpty(streetAddress)) { 
			dialog.dismiss();
			return;
		}
		
		HandleGeofencingAddress(locationKey, streetAddress);

		dialog.dismiss();;
		return;	
	}
   		    
	void HandleGeofencingAddress(String locationKey, String newStreetAddress) {		
		Log.d(TodoListFragment.TAG, "In HandleGeofencingAddress, newStreetAddress is:" + newStreetAddress);
		ParseUser parseUser = ParseUser.getCurrentUser();
		Address newAddress;
		if (null == currentAddress) {
			newAddress = new Address();
			newAddress.setUser(parseUser);

		} else {
			newAddress = currentAddress;
		}

		newAddress.setName(locationKey);
		newAddress.setStreetAddress(newStreetAddress);
		newAddress.setLocation(new ParseGeoPoint(10, 10)); // need not do this; streetaddress is sufficient
		todoList.setAddress(newAddress);
		
		// Now set up a geofence around the new street address
		int radius = 50; // meters
	    GeofenceUtils.setupTestGeofences(getActivity(), parseUser.getObjectId(), newAddress.getStreetAddress(), radius,
					Geofence.GEOFENCE_TRANSITION_ENTER, ("Close to " + newAddress.getName()), todoList.getName(), "All Todo items");
		
		this.todoListFragment.tvReminder.setText(this.todoListFragment.getReminderDisplay()); // refresh              			
	}
}