package com.codepath.smartodo.activities;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.codepath.smartodo.R;
import com.codepath.smartodo.geofence.GeofenceUtils;
import com.codepath.smartodo.helpers.Utils;
import com.codepath.smartodo.model.Address;
import com.codepath.smartodo.model.ReminderLocation;
import com.codepath.smartodo.model.TodoList;
import com.google.android.gms.location.Geofence;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

public class LocationDialogFragment extends DialogFragment implements android.content.DialogInterface.OnClickListener {
		/**
		 * 
		 */
		private final ItemsViewerActivity itemsViewerActivity;
		private TodoList todoList;
		private List<ReminderLocation> reminderLocations;
		private ReminderLocation currentReminderLocation;
		private Address currentAddress;
		private String currentLocation;
		private ListView lvLocationChooser;
		private int selectedColor;
		private View lastSelectedView = null;

		public LocationDialogFragment(ItemsViewerActivity itemsViewerActivity, TodoList todoList,
				List<ReminderLocation> reminderLocations) {
			super();
			this.itemsViewerActivity = itemsViewerActivity;
			this.todoList = todoList;
			this.reminderLocations = reminderLocations;
			initCurrentLocation();
		}

		private void initCurrentLocation() {
			if (todoList == null || Utils.isNullOrEmpty(reminderLocations)) {
				return;
			}
			// Log.d(TAG, "In LocationDialogFragment:initCurrentLocation: total location keys are " + reminderLocations.size());

			currentAddress = todoList.getAddress();
			if (currentAddress != null) {
				currentLocation = currentAddress.getName();
				currentReminderLocation = findCurrentReminderLocation(reminderLocations, currentLocation);
			} else {
				currentLocation = null;
				currentReminderLocation = null;
			}
			Log.d(ItemsViewerActivity.TAG, "In LocationDialogFragment:initCurrentLocation: currentLocation is " + currentLocation);
		}

		private ReminderLocation findCurrentReminderLocation(
				List<ReminderLocation> remLocations, String currLocation) {
			for (ReminderLocation remLocation : remLocations) {
				if (currLocation.equalsIgnoreCase(remLocation.getName())) {
					return remLocation;
				}
			}
			return null;
		}

		private void setCustomStyle(TextView view) {
			view.setBackgroundColor(selectedColor);
			view.setAlpha(0.8f);
			view.setTextColor(getResources().getColor(R.color.white));
			view.setTextSize(16);
			view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
			view.setGravity(Gravity.CENTER_HORIZONTAL);
		}

		private TextView getCustomTitle() {
			TextView title = new TextView(getActivity());
			setCustomStyle(title);
			title.setText(getString(R.string.title_location_reminding_dialog /*, todoList.getName() */));
			return title;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			if (todoList == null || Utils.isNullOrEmpty(reminderLocations)) {
				return null;
			}
			// must be set in the beginning
			selectedColor = getActivity().getResources().getColor(this.itemsViewerActivity.colorId); 
					
			View locationChooserView = getActivity().getLayoutInflater().inflate(R.layout.location_chooser, null);
			locationChooserView.setBackgroundColor(getResources().getColor(R.color.white));
			
			lvLocationChooser = (ListView) locationChooserView.findViewById(R.id.lvLocationChooser);		
			lvLocationChooser.setSelector(this.itemsViewerActivity.colorId);
			final int defaultDrawingCacheBackgroundColor = lvLocationChooser.getDrawingCacheBackgroundColor();
			int[] colors = { selectedColor, selectedColor };
			lvLocationChooser.setDivider(new GradientDrawable(Orientation.RIGHT_LEFT, colors));
			
			LinearLayout llHeaderBand = (LinearLayout) locationChooserView.findViewById(R.id.llHeaderBand);
			llHeaderBand.setBackgroundColor(selectedColor);
			LinearLayout llFooterBand = (LinearLayout) locationChooserView.findViewById(R.id.llFooterBand);
			llFooterBand.setBackgroundColor(selectedColor);
			lvLocationChooser.setDividerHeight(3);
			
			
/*			View listViewBorder = new View(getActivity());
			listViewBorder.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 3));
			listViewBorder.setBackgroundColor(selectedColor);
			lvLocationChooser.addHeaderView(listViewBorder);
			lvLocationChooser.addFooterView(listViewBorder);*/
			
			// Create and initialize an adapter
			ReminderLocationsAdapter dataAdapter = new ReminderLocationsAdapter(getActivity(), reminderLocations);
			lvLocationChooser.setAdapter(dataAdapter);		
			
			lvLocationChooser.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// Log.d(TAG, "In onItemClick, position is " + position);
					if (position != -1) {
						lvLocationChooser.setItemChecked(position, true);
						view.setSelected(true);
						if (lastSelectedView != null
								&& lastSelectedView != view) {
							lastSelectedView.setBackgroundColor(defaultDrawingCacheBackgroundColor);
						}
						view.setBackgroundColor(selectedColor);
						lastSelectedView = view;
					}
				}
			});
			
			TextView tvLocationChooserTitle = (TextView) locationChooserView.findViewById(R.id.tvLocationChooserTitle);
			setCustomStyle(tvLocationChooserTitle);
			
			Button btnDone = (Button) locationChooserView.findViewById(R.id.btnDone);
			setCustomStyle(btnDone);
			
			// Create an AlertDialog and associate the view for location names
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
			// builder.setCustomTitle(getCustomTitle());
			builder.setView(locationChooserView);
			// builder.setPositiveButton("Done", this);
			final AlertDialog alertDialog = builder.create();
			alertDialog.getWindow().setBackgroundDrawableResource(this.itemsViewerActivity.colorId);

			alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
				@Override
				public void onShow(DialogInterface dialog) {
					AlertDialog alertDialog = (AlertDialog) dialog;
					Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
					if (button != null) {
						setCustomStyle(button);
					}
					button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
					if (button != null) {
						setCustomStyle(button);
					}
				}
			});
			
			// alertDialog.getWindow().setLayout(600, ViewGroup.LayoutParams.WRAP_CONTENT);

			alertDialog.show(); // Maybe needed for highlighting a row below by a programmatic click operation

			if (currentReminderLocation != null) {
				// set the default choice according to the current value
				int position = reminderLocations.indexOf(currentReminderLocation);
				if (position != -1) {
					// Log.d(TAG, "About to perform click for position " + position);
					lvLocationChooser.setItemChecked(position, true);
					lvLocationChooser.setSelection(position);
					// lvLocationChooser.getAdapter().getView(position, null, null).setBackgroundColor(selectedColor);
					// lvLocationChooser.getAdapter().getView(position, null, null).performClick();
					lvLocationChooser.performItemClick(lvLocationChooser.getAdapter()
							.getView(position, null, null), position, position);
				}
			}
			
			btnDone.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int selectedPosition = lvLocationChooser.getCheckedItemPosition();
					if (selectedPosition < 0) {
						// Log.d(TAG, "In onClick, selectedPosition is " + selectedPosition);
						alertDialog.dismiss();
						return;
					}
					ReminderLocation selectedReminderLocation = reminderLocations.get(selectedPosition);

					if (null == selectedReminderLocation) {
						// Log.d(TAG, "In onClick, selectedReminderLocation is null");
						alertDialog.dismiss();
						return;
					}
					Log.d(ItemsViewerActivity.TAG, "In onClick, locationKey is:" + selectedReminderLocation.getName());

					if (!Utils.isNullOrEmpty(currentLocation)
							&& (selectedReminderLocation.getName()
									.equalsIgnoreCase(currentLocation))) {
						alertDialog.dismiss();
						return; // Nothing much to do; same old location has been chosen.
					}

					String streetAddress = selectedReminderLocation.getStreetAddress();
					// Log.d(TAG, "In onClick, streetAddress is:" + streetAddress);
					// The following should not happen because a street address
					// would always be associated with a location name
					if (Utils.isNullOrEmpty(streetAddress)) {
						alertDialog.dismiss();
						return;
					}

					HandleGeofencingAddress(selectedReminderLocation.getName(),
							streetAddress);

					alertDialog.dismiss();
					return;
					
				}
			});
			
			return alertDialog;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {

			int selectedPosition = lvLocationChooser.getCheckedItemPosition();
			if (selectedPosition < 0) {
				// Log.d(TAG, "In onClick, selectedPosition is " + selectedPosition);
				dialog.dismiss();
				return;
			}
			ReminderLocation selectedReminderLocation = reminderLocations.get(selectedPosition);

			if (null == selectedReminderLocation) {
				// Log.d(TAG, "In onClick, selectedReminderLocation is null");
				dialog.dismiss();
				return;
			}
			Log.d(ItemsViewerActivity.TAG, "In onClick, locationKey is:" + selectedReminderLocation.getName());

			if (!Utils.isNullOrEmpty(currentLocation)
					&& (selectedReminderLocation.getName()
							.equalsIgnoreCase(currentLocation))) {
				dialog.dismiss();
				return; // Nothing much to do; same old location has been chosen.
			}

			String streetAddress = selectedReminderLocation.getStreetAddress();
			// Log.d(TAG, "In onClick, streetAddress is:" + streetAddress);
			// The following should not happen because a street address
			// would always be associated with a location name
			if (Utils.isNullOrEmpty(streetAddress)) {
				dialog.dismiss();
				return;
			}

			HandleGeofencingAddress(selectedReminderLocation.getName(),
					streetAddress);

			dialog.dismiss();
			;
			return;
		}

		void HandleGeofencingAddress(String locationKey, String newStreetAddress) {
			Log.d(ItemsViewerActivity.TAG, "In HandleGeofencingAddress, newStreetAddress is:"
					+ newStreetAddress);
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
			GeofenceUtils.setupTestGeofences(getActivity(),
					parseUser.getObjectId(), newAddress.getStreetAddress(),
					radius, Geofence.GEOFENCE_TRANSITION_ENTER,
					("Close to " + newAddress.getName()), todoList.getName(),
					"All Todo items");

			// tvReminder.setText(getReminderDisplay()); // refresh
		}
	}