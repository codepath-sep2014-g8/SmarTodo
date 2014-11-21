package com.codepath.smartodo.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.widget.EditText;

import com.codepath.smartodo.geofence.GeofenceUtils;
import com.codepath.smartodo.helpers.Utils;
import com.codepath.smartodo.model.Address;
import com.codepath.smartodo.model.TodoList;
import com.google.android.gms.location.Geofence;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

public class LocationDialogFragmentOld extends DialogFragment implements android.content.DialogInterface.OnClickListener {

	/**
	 * 
	 */
	private final TodoListFragment todoListFragment;
	private TodoList todoList;
	private Address currentAddress;
	private String currentStreetAddress;
	private EditText etStreetAddress;

	public LocationDialogFragmentOld(TodoListFragment todoListFragment, TodoList todoList) {
		super();
		this.todoListFragment = todoListFragment;
		this.todoList = todoList;	
		initCurrentLocation();
	}
	
    private void initCurrentLocation() {    	
	    currentAddress = todoList.getAddress();
    	if (currentAddress != null) {
    	    currentStreetAddress = currentAddress.getStreetAddress();    	        	       
    	}
	}
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	etStreetAddress = new EditText(getActivity());
	    etStreetAddress.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
	    if (!Utils.isNullOrEmpty(currentStreetAddress)) {
		    etStreetAddress.setText(currentStreetAddress);
	    } 
        return new AlertDialog.Builder(getActivity()).setTitle("Please specify an address for " + todoList.getName())
        		.setMessage("Please enter an address for reminder")
                .setPositiveButton("OK", this).setNegativeButton("CANCEL", null).setView(etStreetAddress).create();
    }

    @Override
    public void onClick(DialogInterface dialog, int position) {

        String newStreetAddress = etStreetAddress.getText().toString();
        if (Utils.isNullOrEmpty(newStreetAddress)) {
        	return; // Nothing much to do
        }
        if (!Utils.isNullOrEmpty(currentStreetAddress) &&
            (newStreetAddress.equalsIgnoreCase(currentStreetAddress))) {
        	currentAddress.setStreetAddress(newStreetAddress);	 // Update it anyway
        	return; // Nothing much to do
        }
        
        // Now we have a new street address specified
        ParseUser parseUser = ParseUser.getCurrentUser();
        Address newAddress;        
        if (null == currentAddress) {
        	newAddress = new Address();	 
        	newAddress.setUser(parseUser);	    
        	newAddress.setName(newStreetAddress);  // Defaulting to the address itself instead of (Home, Office, Safeway, etc.). Todo: Get this also in a text view        	    	
		} else {
		    newAddress = currentAddress;
		}
        						
		newAddress.setStreetAddress(newStreetAddress);
		newAddress.setLocation(new ParseGeoPoint(10, 10));  // need not do this; street address is sufficient
		todoList.setAddress(newAddress);
		
		// Now set up a geofence around the new street address
		int radius = 50; // meters
	    GeofenceUtils.setupTestGeofences(getActivity(), parseUser.getObjectId(), newAddress.getStreetAddress(), radius,
					Geofence.GEOFENCE_TRANSITION_ENTER, ("Close to " + newAddress.getName()), todoList.getName(), "All Todo items");
		
		this.todoListFragment.tvReminder.setText(this.todoListFragment.getReminderDisplay()); // refresh                
        dialog.dismiss();
    }
}