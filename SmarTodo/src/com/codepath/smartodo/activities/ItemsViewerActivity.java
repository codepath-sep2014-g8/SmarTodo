package com.codepath.smartodo.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.TimePicker;

import com.codepath.smartodo.R;
import com.codepath.smartodo.fragments.TodoListFragment;
import com.codepath.smartodo.geofence.GeofenceUtils;
import com.codepath.smartodo.helpers.AppConstants;
import com.codepath.smartodo.helpers.Utils;
import com.codepath.smartodo.interfaces.TouchActionsListener;
import com.codepath.smartodo.model.Address;
import com.codepath.smartodo.model.ReminderLocation;
import com.codepath.smartodo.model.TodoList;
import com.google.android.gms.location.Geofence;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;


public class ItemsViewerActivity extends FragmentActivity implements TouchActionsListener {
	
	private static final String TAG = ItemsViewerActivity.class.getSimpleName();
	
	// Some dummy addresses for demo. Todo: They should eventually come from some Settings/Preferences
	private static final String HOME_LOCATION_NAME = "Home";
	private static final String HOME_ADDR = "1274 Colleen Way, Campbell, CA 95008";
	private static final String HOME_IMAGE_URL = "someUrl";
	
	private static final String BOFA_MTNVIEW_LOCATION_NAME = "Bank of America, Mtn View";
	private static final String BOFA_MTNVIEW_ADDR = " 444 Castro St, Mountain View, CA 94041";
	private static final String BOFA_MTNVIEW_IMAGE_URL = "someUrl";
	
	private static final String YAHOO_BUILDING_E_LOCATION_NAME = "Yahoo Building E";
	private static final String YAHOO_BUILDING_E_ADDR = "700 First Ave, Sunnyvale, CA 94089";
	private static final String YAHOO_BUILDING_E_IMAGE_URL = "someUrl";
	
	private static final String YAHOO_BUILDING_F_LOCATION_NAME = "Yahoo Building F";
	private static final String YAHOO_BUILDING_F_ADDR = "1350 North Mathilda Avenue, Sunnyvale, CA 94089";
	private static final String YAHOO_BUILDING_F_IMAGE_URL = "someUrl";
	
	private static final String SAFEWAY_STEVENSCREEK_LOCATION_NAME = "Safeway Stevens Creek";
	private static final String SAFEWAY_STEVENSCREEK_ADDR = "5146 Stevens Creek Blvd, San Jose, CA";
	private static final String SAFEWAY_STEVENSCREEK_IMAGE_URL = "someUrl";
	
	private static final String RIGHT_STUFF_LOCATION_NAME = "Gym";
	private static final String RIGHT_STUFF_ADDR = "1730 W Campbell Ave, Campbell, CA 95008";
	private static final String RIGHT_STUFF_IMAGE_URL = "someUrl";
	
	public static final int RESULT_SHARE = 678;
	
	private ImageView ivBack;
	private ImageView ivShare;
	private ImageView ivNotifications;
	private ImageView ivMoreOptions;
	private ImageView ivDelete;
	private ImageView ivLocationReminder;
	
	String objectId = null;
	private int animationStyle = R.style.DialogFromLeftAnimation;
	private int colorId = R.color.todo_list_backcolor;
	private TodoList todoList = null;
	private List<ReminderLocation> reminderLocations;

	private TodoListFragment fragmentTodoList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_items_viewer);
		
		initialize();
		
		setupListeners();
	}

	private void initialize(){
		
		initializeTodoList();
		
		if (getIntent().hasExtra(AppConstants.KEY_ANIMATION_STYLE)) {			
			animationStyle = (int) getIntent().getIntExtra(AppConstants.KEY_ANIMATION_STYLE, 0);
		}
		
		if (getIntent().hasExtra(AppConstants.KEY_COLOR_ID)) {			
			colorId = getIntent().getIntExtra(AppConstants.KEY_COLOR_ID, 0);
		}
		
		initializeActionBar();

		fragmentTodoList = TodoListFragment.newInstance(objectId, R.style.DialogFromLeftAnimation, colorId);
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.fragmentContainer, fragmentTodoList);
		transaction.commit();
		
		initializeData();
	}
	
    private void initializeData() {
       reminderLocations = new ArrayList<ReminderLocation>();
       reminderLocations.add(new ReminderLocation(HOME_LOCATION_NAME, HOME_ADDR, HOME_IMAGE_URL, R.drawable.ic_home));
       reminderLocations.add(new ReminderLocation(BOFA_MTNVIEW_LOCATION_NAME, BOFA_MTNVIEW_ADDR, BOFA_MTNVIEW_IMAGE_URL, R.drawable.ic_dollar));
       reminderLocations.add(new ReminderLocation(YAHOO_BUILDING_E_LOCATION_NAME, YAHOO_BUILDING_E_ADDR, YAHOO_BUILDING_E_IMAGE_URL, R.drawable.ic_yahoo_logo));
       reminderLocations.add(new ReminderLocation(YAHOO_BUILDING_F_LOCATION_NAME, YAHOO_BUILDING_F_ADDR, YAHOO_BUILDING_F_IMAGE_URL, R.drawable.ic_yahoo_logo));
       reminderLocations.add(new ReminderLocation(SAFEWAY_STEVENSCREEK_LOCATION_NAME, SAFEWAY_STEVENSCREEK_ADDR, SAFEWAY_STEVENSCREEK_IMAGE_URL, R.drawable.ic_shopping_cart));
       reminderLocations.add(new ReminderLocation(RIGHT_STUFF_LOCATION_NAME, RIGHT_STUFF_ADDR, RIGHT_STUFF_IMAGE_URL, R.drawable.ic_gym));		
	}

    private void initializeTodoList(){
		
	    if (getIntent().hasExtra(AppConstants.OBJECTID_EXTRA)) {			
		    objectId = (String) getIntent().getStringExtra(AppConstants.OBJECTID_EXTRA);
	    }
		
		if(objectId == null || objectId.isEmpty()){	
			todoList = new TodoList();
			return;
		}
		
		try {
			todoList = TodoList.findTodoListByObjectId(objectId);
		} catch (ParseException e1) {
			
			Log.d(TAG, "Excpetion while getting the todo list");
			e1.printStackTrace();
			
			todoList = new TodoList();
		}
		
		if(todoList == null){
			todoList = new TodoList();
		}
		
	}
	
	
	private void initializeActionBar(){

        ActionBar actionBar = getActionBar();

        View view = getLayoutInflater().inflate(R.layout.action_bar_grid_view, null);
        
        view.setBackgroundColor(getResources().getColor(colorId));
        
        ivBack = (ImageView)view.findViewById(R.id.ivBackButton_grid_view);
        ivBack.setVisibility(View.VISIBLE);
        
        ivShare = (ImageView)view.findViewById(R.id.ivShare);
        ivShare.setVisibility(View.VISIBLE);
        
        ivNotifications = (ImageView)view.findViewById(R.id.ivNotifications);
        ivNotifications.setVisibility(View.VISIBLE);
        
        ivMoreOptions = (ImageView)view.findViewById(R.id.ivMoreOptions);
        ivMoreOptions.setVisibility(View.VISIBLE);
        
        ivDelete = (ImageView)view.findViewById(R.id.ivDelete);
        ivDelete.setVisibility(View.GONE);
        
        ivLocationReminder = (ImageView)view.findViewById(R.id.ivLocationReminder);
        ivLocationReminder.setVisibility(View.VISIBLE);
        
        TextView tvTitle_home = (TextView) view.findViewById(R.id.tvTitle_home);
        tvTitle_home.setText(Utils.buildTitleText());
        
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);


        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);
        
      //Hack to hide the home icon -- Otherwise the action bar was getting displayed on top of Tabs
        View homeIcon = findViewById(android.R.id.home);
        ((View) homeIcon.getParent()).setVisibility(View.GONE);
        
        actionBar.setCustomView(view, params);
    }
	
	@Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(AppConstants.OBJECTID_EXTRA, objectId);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
        overridePendingTransition (R.anim.slide_in_from_right, R.anim.slide_out_from_left);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == RESULT_SHARE) {
			Log.i("info", "Refreshing the shared view");
			refreshTodoList();
		}
	}
	
	private void refreshTodoList() {
		try {
			todoList = TodoList.findTodoListByObjectId(objectId);
			fragmentTodoList.sharedWithListAdapter.clear();
			fragmentTodoList.sharedWithListAdapter.addAll(todoList.getSharing());
		} catch (ParseException e) {
			Log.e("error", "Error refresshing todo item", e);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.items_viewer, menu);
		return true;
	}
	
	private void setupListeners(){
		
		ivMoreOptions.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				PopupMenu popupMenu = new PopupMenu(ItemsViewerActivity.this, ivMoreOptions);
				popupMenu.getMenuInflater().inflate(R.menu.poupup_menu, popupMenu.getMenu());
				
				popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						if(item.getItemId() == R.id.deleteMenu){
							deleteList();
						}
						return true;
					}
				});
				
				popupMenu.show();
			}
		});
		
		ivBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
				
			}
		});
		
		ivShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(ItemsViewerActivity.this, ShareActivity.class);
				intent.putExtra(AppConstants.OBJECTID_EXTRA, objectId);
				intent.putExtra(AppConstants.KEY_COLOR_ID, colorId);
				startActivityForResult(intent, RESULT_SHARE);	
				overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_from_left);
			}
		});
		
		ivDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				try {
					todoList.deleteEventually();
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally{
					onBackPressed();
				}
			}
		});
		
		ivNotifications.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogFragment dialog = new TimePickerFragment(todoList);
				if (dialog != null) {
					dialog.show(getSupportFragmentManager(), "timePicker");	
				}
			}
		});
		
		ivLocationReminder.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LocationDialogFragment dialog = new LocationDialogFragment(todoList, reminderLocations);
				if (dialog != null) {
					dialog.show(getSupportFragmentManager(), "fragment_notification_selector");
				}
			}
		});
	}
	
	private void deleteList(){

		try {
			todoList.deleteEventually();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			onBackPressed();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_items_properties) {
			Log.d("debug", "here");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onPreviousListRequested() {
//		System.out.println("onPreviousListRequested: " + currentListIndex);
//		if(currentListIndex == -1 || currentListIndex == 0){
//			currentListIndex = adapter.getCount() - 1;
//		}
//		else{
//			currentListIndex --;
//		}
//		TodoList todoList = adapter.getItem(currentListIndex);
//		showTodoListDialog(todoList.getObjectId(), 
//				(currentListIndex % 2 == 0 ) ? R.style.DialogFromLeftAnimation : R.style.DialogFromRightAnimation,
//					com.codepath.smartodo.helpers.Utils.getColor(currentListIndex % 6)	);
		
	}

	@Override
	public void onNextListRequested() {
		
//		System.out.println("onNextListRequested: " + currentListIndex);
//		
//		if(currentListIndex == adapter.getCount() -1 || currentListIndex == -1){
//			currentListIndex = 0;
//		}
//		else{
//			currentListIndex ++;
//		}
//		
//		TodoList todoList = adapter.getItem(currentListIndex);
//		showTodoListDialog(todoList.getObjectId(), 
//				(currentListIndex % 2 == 0 ) ? R.style.DialogFromLeftAnimation : R.style.DialogFromRightAnimation,
//					com.codepath.smartodo.helpers.Utils.getColor(currentListIndex % 6)	);
	}


	public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
		private TodoList todoList;
		final Calendar c = Calendar.getInstance();

		public TimePickerFragment(TodoList todoList) {
			super();
			this.todoList = todoList;
			initCurrentDate();
		}

		private void initCurrentDate() {
			Date notificationTime = todoList.getNotificationTime();
			if (notificationTime == null) {
				notificationTime = new Date();
			}
			c.setTime(notificationTime);
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			c.set(Calendar.HOUR_OF_DAY, hourOfDay);
			c.set(Calendar.MINUTE, minute);
			todoList.setNotificationTime(c.getTime());
			// tvReminder.setText(getReminderDisplay()); // refresh
			// Save the todoList now?
		};
	}

	public class LocationDialogFragment extends DialogFragment implements android.content.DialogInterface.OnClickListener {
		private TodoList todoList;
		private List<ReminderLocation> reminderLocations;
		private ReminderLocation currentReminderLocation;
		private Address currentAddress;
		private String currentLocation;
		private ListView lvLocationChooser;
		private int selectedColor;
		private View lastSelectedView = null;

		public LocationDialogFragment(TodoList todoList,
				List<ReminderLocation> reminderLocations) {
			super();
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
			Log.d(TAG, "In LocationDialogFragment:initCurrentLocation: currentLocation is " + currentLocation);
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
			selectedColor = getActivity().getResources().getColor(colorId); 
					
			View locationChooserView = getActivity().getLayoutInflater().inflate(R.layout.location_chooser, null);
			locationChooserView.setBackgroundColor(getResources().getColor(R.color.white));
			
			lvLocationChooser = (ListView) locationChooserView.findViewById(R.id.lvLocationChooser);		
			lvLocationChooser.setSelector(colorId);
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
			alertDialog.getWindow().setBackgroundDrawableResource(colorId);

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
					Log.d(TAG, "In onClick, locationKey is:" + selectedReminderLocation.getName());

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
			Log.d(TAG, "In onClick, locationKey is:" + selectedReminderLocation.getName());

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
			Log.d(TAG, "In HandleGeofencingAddress, newStreetAddress is:"
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
	
	private  class ReminderLocationsAdapter extends ArrayAdapter<ReminderLocation> {

		public ReminderLocationsAdapter(Context context, List<ReminderLocation> reminderLocations) {
			super(context, R.layout.todo_location, reminderLocations);
			// TODO Auto-generated constructor stub
		}
		
		// View lookup cache
		private class ViewHolder {
			ImageView ivLocationImage;
			TextView tvLocName;		
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Get the data item for this position
			ReminderLocation reminderLocation = getItem(position);
			// Check if an existing view is being reused, otherwise inflate the view
			ViewHolder viewHolder; // view lookup cache stored in tag
			if (convertView == null) {
				viewHolder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater.from(getContext());
				convertView = inflater.inflate(R.layout.todo_location, parent, false);
				viewHolder.ivLocationImage = (ImageView) convertView.findViewById(R.id.ivLocationImage);
				viewHolder.tvLocName = (TextView) convertView.findViewById(R.id.tvLocName);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
				// reset the image from the recycled view
				viewHolder.ivLocationImage.setImageResource(0);
				viewHolder.tvLocName.setText("");
			}
			// Remotely download the image data in the background (with Picasso)
			Picasso.with(getContext()).load(reminderLocation.getImageResourceId()).placeholder(R.drawable.ic_launcher).into(viewHolder.ivLocationImage);
			viewHolder.tvLocName.setText(reminderLocation.getName());
			
			// Return the completed view to be displayed
			return convertView;
		}
	}
}

