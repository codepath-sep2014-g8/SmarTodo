package com.codepath.smartodo.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.codepath.smartodo.R;
import com.codepath.smartodo.activities.ShareActivity;
import com.codepath.smartodo.adapters.TodoItemsAdapter;
import com.codepath.smartodo.dialogs.ColorPickerDialog;
import com.codepath.smartodo.enums.TodoListDisplayMode;
import com.codepath.smartodo.geofence.GeofenceUtils;
import com.codepath.smartodo.helpers.AppConstants;
import com.codepath.smartodo.helpers.Utils;
import com.codepath.smartodo.interfaces.TouchActionsListener;
import com.codepath.smartodo.model.Address;
import com.codepath.smartodo.model.TodoItem;
import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.model.User;
import com.codepath.smartodo.services.ModelManagerService;
import com.google.android.gms.location.Geofence;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class TodoListFragment extends DialogFragment implements OnTouchListener {

	private static final String TAG = TodoListFragment.class.getSimpleName();

	// Some dummy addresses for demo. Todo: They should eventually come from some Settings/Preferences
	private static final String HOME_ADDR = "1274 Colleen Way, Campbell, CA 95008";
	private static final String YAHOO_BUILDING_E_ADDR = "700 First Ave, Sunnyvale, CA 94089";
	private static final String YAHOO_BUILDING_F_ADDR = "1350 North Mathilda Avenue, Sunnyvale, CA 94089";
	private static final String SAFEWAY_STEVENSCREEK_ADDR = "5146 Stevens Creek Blvd, San Jose, CA";
	private static final String SAFEWAY_CAMPBELL_ADDR = "950 W Hamilton Ave, Campbell, CA";
	private static final String RIGHT_STUFF_ADDR = "1730 W Campbell Ave, Campbell, CA 95008";
	
	//UI elements
	private EditText etTitle;
	private Button btnAdd;
	private EditText etNewItem;
	private ListView lvItems;
	private LinearLayout llActions;
	private ImageView ivNotifications;
	private ImageView ivShare;
	private ImageView ivColorPicker;
	private ImageView ivSave;
	private ImageView ivDelete;
	private LinearLayout llFooter;
	private ImageView ivFooterReminder;
	private TextView tvReminder;
	
	private TextView tvSharedWithList;
	
	private TodoItemsAdapter adapter;
	private List<TodoItem> todoItemsList;
	private TodoList todoList = null;
	private String listObjectId = null;
	
	private int animationStyle = R.style.DialogFromLeftAnimation;
	private int colorId;
	private TodoListDisplayMode mode = TodoListDisplayMode.UPDATE;
	
	private TouchActionsListener listener = null;
	private HashMap<String, String> locationsMap;
	
	public static TodoListFragment newInstance(String todoListName, int animationStyle, int colorId)
    {
		TodoListFragment fragment = new TodoListFragment();

        Bundle arguments = new Bundle();
        arguments.putString(AppConstants.OBJECTID_EXTRA, todoListName);
        arguments.putInt(AppConstants.KEY_ANIMATION_STYLE, animationStyle);
        arguments.putInt(AppConstants.KEY_COLOR_ID, colorId);
        fragment.setArguments(arguments);
        

        return fragment;
    }

	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		System.out.println("Hello");
		return false;
	}


	private void setViewColor(View view){
		LayerDrawable sld = (LayerDrawable)view.getBackground();
		GradientDrawable shape = (GradientDrawable) (sld.findDrawableByLayerId(R.id.drop_shadow_backcolor));
        shape.setColor(getActivity().getResources().getColor(colorId));
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_todo_list, container, false);
		
		if(getArguments() != null && getArguments().containsKey(AppConstants.KEY_COLOR_ID)){
			colorId = getArguments().getInt(AppConstants.KEY_COLOR_ID);
		}
		else{
			colorId = R.color.todo_list_backcolor;
		}
		
		
		
		initializeViews(view);
		populateData();
		setupListeners();
		
		Drawable drawable = llActions.getBackground();
		
		setViewColor(llActions);
		//llActions.setBackgroundColor(getResources().getColor(colorId));
//		view.setOnTouchListener(this);
//		lvItems.setOnTouchListener(new OnSwipeTouchListener(getActivity().getApplicationContext()){
//
//			@Override
//			public void onSwipeRight() {
//				// TODO Auto-generated method stub
//				listener.onNextListRequested();
//				//dismiss();
//			}
//
//			@Override
//			public void onSwipeLeft() {
//				// TODO Auto-generated method stub
//				listener.onPreviousListRequested();
//				//dismiss();
//				
//			}
//
//			@Override
//			public void onSwipeUp() {
//				// TODO Auto-generated method stub
//				super.onSwipeUp();
//			}
//
//			@Override
//			public void onSwipeDown() {
//				// TODO Auto-generated method stub
//				super.onSwipeDown();
//			}
//			
//		});
//		getDialog().getWindow().setSoftInputMode(
//		WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		return view;
	}
		
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize();
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onActivityCreated(arg0);
		getDialog().getWindow()
	    .getAttributes().windowAnimations = animationStyle;
		
		listener = (TouchActionsListener)getActivity();
	}
	
	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		
//		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		return dialog;
	}

	private void initialize(){
		
		initializeTodoList();
		
		adapter = new TodoItemsAdapter(getActivity(), todoItemsList, mode);	
	}
	
	private void initializeTodoList(){
		
		if(getArguments() != null && getArguments().containsKey(AppConstants.KEY_ANIMATION_STYLE)){
			animationStyle = getArguments().getInt(AppConstants.KEY_ANIMATION_STYLE);
		}
		
		if(getArguments() != null && getArguments().containsKey(AppConstants.OBJECTID_EXTRA)){
			listObjectId = getArguments().getString(AppConstants.OBJECTID_EXTRA);
		}
		
		if(listObjectId == null || listObjectId.isEmpty()){	
			todoList = new TodoList();
			todoItemsList = new ArrayList<TodoItem>();
			mode = TodoListDisplayMode.CREATE;
			return;
		}
		
		try {
			todoList = TodoList.findTodoListByObjectId(listObjectId);
			todoItemsList = todoList.getAllItems();
		} catch (ParseException e1) {
			
			Log.d(TAG, "Excpetion while getting the todo list");
			e1.printStackTrace();
			
			todoList = new TodoList();
			todoItemsList = new ArrayList<TodoItem>();
			mode = TodoListDisplayMode.CREATE;
		}
		
		if(todoList == null){
			todoList = new TodoList();
			todoItemsList = new ArrayList<TodoItem>();
			mode = TodoListDisplayMode.CREATE;
		}
		
		if(todoItemsList == null){
			todoItemsList = new ArrayList<TodoItem>();
		}
	}

	private void initializeViews(View view){
		
		etTitle = (EditText)view.findViewById(R.id.etTitle_ftdl);
		etNewItem = (EditText)view.findViewById(R.id.etNewItem_ftdl);
		btnAdd = (Button)view.findViewById(R.id.btnAdd_ftdl);
		lvItems = (ListView)view.findViewById(R.id.lvToDoItemsList_ftdl);
		llActions = (LinearLayout)view.findViewById(R.id.llAction_ftdl);
		ivNotifications = (ImageView)view.findViewById(R.id.ivNotification_ftdl);
		ivShare = (ImageView)view.findViewById(R.id.ivShare_ftdl);
		ivColorPicker = (ImageView)view.findViewById(R.id.ivColorPicker_ftdl);
		ivSave = (ImageView)view.findViewById(R.id.ivSave_ftdl);
		ivDelete = (ImageView)view.findViewById(R.id.ivDelete_ftdl);
		llFooter = (LinearLayout)view.findViewById(R.id.llfooter_ftdl);
		ivFooterReminder = (ImageView)view.findViewById(R.id.ivFooterReminder_ftdl);
		tvReminder = (TextView)view.findViewById(R.id.tvReminder_ftdl);
		
		
		tvSharedWithList = (TextView)view.findViewById(R.id.tvSharedWith_ftdl);
		
		lvItems.setAdapter(adapter);
	}
	
	private void populateData(){
		
		if(mode == TodoListDisplayMode.CREATE){
			return;
		}
		
		etTitle.setText(todoList.getName());
		
		tvSharedWithList.setText("Shared with: " + getDisplaySharedWithList());
		
		tvReminder.setText(getReminderDisplay());
		
        locationsMap = new HashMap();
        locationsMap.put("Home", HOME_ADDR);
        locationsMap.put("Yahoo Building E", YAHOO_BUILDING_E_ADDR);
        locationsMap.put("Yahoo Building F", YAHOO_BUILDING_F_ADDR);
        locationsMap.put("Safeway Stevens Creek", SAFEWAY_STEVENSCREEK_ADDR);
        locationsMap.put("Safeway Campbell", SAFEWAY_CAMPBELL_ADDR);
        locationsMap.put("Gym", RIGHT_STUFF_ADDR);
	}
	
	private String getReminderDisplay(){
		
		StringBuilder sb = new StringBuilder();
		
		try{
			Date dt = todoList.getNotificationTime();
			if (dt == null) {
				Log.d(TAG, "In getReminderDisplay, notificationTime is null");
				dt = new Date();
			}
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
			 		    
		    String displayName = formatter.format(dt);
		    sb.append("Remind me at: ").append(displayName);
		    sb.append("\r\n");
		}
		catch(Exception ex){
			
		}
		
		try {		
			// Here is some test code to assign a street address to this Todolist for geofencing.
			ParseUser parseUser = ParseUser.getCurrentUser();
			
			Address address = todoList.getAddress();
			if (null == address) { // Create a temporary address object for display
				String streetAddress = "Not set yet";			
				address = new Address();
				address.setStreetAddress(streetAddress);
				address.setName(streetAddress);
			} else {
				address = address.fetchIfNeeded();
			}
			
			String location = address.getName(); 
			
			sb.append("Remind me at location: ").append(location);
		    sb.append("\r\n");
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		return sb.toString();
	}
	
	private String getDisplaySharedWithList(){
		StringBuilder sb = new StringBuilder();
		
		List<User> users = todoList.getSharing();
	
		if(users == null){
			return sb.toString();
		}
		
		for(int i=0;i<users.size();i++){
			User user = users.get(i);
			try {
				String realName = user.getRealName();
				if(!StringUtils.isEmpty(realName)) {
					sb.append(realName);
				} else {
					sb.append(user.getUsername());
				}
			} catch (ParseException e) {
				Log.e("error", e.getMessage(), e);
				sb.append(user.getUsername());
			}
			
			if(i<users.size() - 1) {
				sb.append(", "); // Don't end the string with a comma
			}
		}
		
		return sb.toString();
	}
	
	private void setupListeners(){
		
		etNewItem.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				String itemText = etNewItem.getText().toString();
				
				if(itemText == null || itemText.isEmpty() || itemText.trim().isEmpty()){
					btnAdd.setEnabled(false);
				}
				else{
					btnAdd.setEnabled(true);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		btnAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String itemText = etNewItem.getText().toString();
						
				if(itemText == null || itemText.isEmpty() || itemText.trim().isEmpty()){
					return;
				}
				
				TodoItem todoItem = new TodoItem();
				todoItem.setText(itemText);
				
				adapter.add(todoItem);
				
				etNewItem.setText("");
			}
		});
		
		
		ivNotifications.setOnClickListener(new View.OnClickListener() {
			
/*			@Override
			public void onClick(View v) {
				FragmentManager manager = getActivity().getFragmentManager();
				
				NotificationSelectorDialog dialog = NotificationSelectorDialog.newInstance();
				dialog.show(manager, "fragment_notification_selector");
				
				
			}*/
			
			@Override
			public void onClick(View v) {
				android.support.v4.app.FragmentManager manager = getActivity()
						.getSupportFragmentManager();
				LocationDialogFragment dialog = new LocationDialogFragment(todoList, locationsMap);
				dialog.show(manager, "fragment_notification_selector");
			}
		});
		
		ivShare.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Show Share activity
				Intent intent = new Intent(getActivity(), ShareActivity.class);
				intent.putExtra(AppConstants.OBJECTID_EXTRA, listObjectId);
				startActivityForResult(intent, 200);				
			}
		});
		
		
		ivColorPicker.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Show color picker
				new ColorPickerDialog(TodoListFragment.this.getActivity(), new ColorPickerDialog.OnColorChangedListener() {
					
					@Override
					public void colorChanged(int color) {
						Log.i("info", "Color changed: " + color);
						todoList.setColor(color);
					}
				}, todoList.getColor()).show();
			}
		});
		
		
		ivSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String objectId = updateTodoList();
				
				if(objectId != null) {
					Intent i = new Intent();
					i.putExtra(AppConstants.OBJECTID_EXTRA, objectId);
					TodoListFragment.this.getActivity().setResult(Activity.RESULT_OK, i);
				}
			}
		});
		
		ivDelete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				try {
					todoList.delete();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dismiss();
			}
		});
		
		ivFooterReminder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new TimePickerFragment(todoList);
			    newFragment.show(getFragmentManager(), "timePicker");	
			}
		});
	}
	
	
	/*
	 * Populate the TodoList object
	 */
	private String updateTodoList(){
		String validationString = validateInput();
		if(validateInput() != null){
			//Show toast and dont update
			Toast.makeText(getActivity(), validationString, Toast.LENGTH_LONG).show();
			return null;
		}
		//Create todoList if new list being created
		if(todoList == null){
			todoList = new TodoList();
		}
		
		//Should be used when creating new list
		//ModelManagerService.saveList(etTitle.getText().toString(), ModelManagerService.getUser(), todoItemsList);
		
		Log.i("info", "Saving TODO List " + listObjectId);
		
		todoList.setName(etTitle.getText().toString());
		todoList.setOwner(ModelManagerService.getUser());
		
		String objectId = ModelManagerService.saveList(todoList, todoItemsList, new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if(e == null) {
					Toast.makeText(getActivity(), "List saved", Toast.LENGTH_SHORT).show();
				} else {
					Log.e("error", e.getMessage(), e);
					Toast.makeText(getActivity(), "Error saving list. Try again.", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
		return objectId;
	}
	
	private String validateInput(){
		String title = etTitle.getText().toString();
		
		if(title == null || title.isEmpty()){
			return "Please specify a non-empty name";
		}
		
		//Validate items
		for(int i = 0; i < adapter.getCount(); i++){
			
		}
		
		try {
			if(mode == TodoListDisplayMode.CREATE && TodoList.findTodoListByNameAndUser(title, ModelManagerService.getUser()) != null) {
				return "The list name is not unique!";
			}
		} catch (ParseException e) {
			Log.e("error", e.getMessage(), e);
			return e.getMessage();
		}
		
		return null;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode == AppConstants.REQUEST_CODE_SHARE_ACTIVITY && resultCode == Activity.RESULT_OK){
			
			
		}
	}
	
	public class TimePickerFragment extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {	

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
			tvReminder.setText(getReminderDisplay()); // refresh
			// Save the todoList now?
		};
	}
	
	public class LocationDialogFragment extends DialogFragment implements android.content.DialogInterface.OnClickListener {	
		private Spinner locationSpinner;
		private TodoList todoList;
		private HashMap<String, String> locationsMap;
		private List<String> locationList;
		private Address currentAddress;
		private String currentLocation;
		ArrayAdapter<String> dataAdapter;

		public LocationDialogFragment(TodoList todoList, HashMap<String, String> locationsMap) {
			super();
			this.todoList = todoList;
			this.locationsMap = locationsMap;
			initCurrentLocation();
		}
		
	    private void initCurrentLocation() {    	
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
		
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Create and initialize an adapter
			dataAdapter = new ArrayAdapter<String>(
					getActivity(), android.R.layout.simple_spinner_item, locationList);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			// Create and initialize a spinner
			locationSpinner = new Spinner(getActivity());
			locationSpinner.setAdapter(dataAdapter);
			if (currentLocation != null) {
				//set the default choice according to the current value
				int spinnerPosition = dataAdapter.getPosition(currentLocation);			
				locationSpinner.setSelection(spinnerPosition);		
			}

			// Create an AlertDialog and associate the spinner for location names
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(getString(R.string.title_location_reminding_dialog, todoList.getName()));		 			
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
			Log.d(TAG, "In HandleGeofencingAddress, newStreetAddress is:" + newStreetAddress);
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
			
			tvReminder.setText(getReminderDisplay()); // refresh              
			
		}


	}
	
	public class LocationDialogFragmentOld extends DialogFragment implements android.content.DialogInterface.OnClickListener {

		private TodoList todoList;
		private Address currentAddress;
		private String currentStreetAddress;
		private EditText etStreetAddress;

		public LocationDialogFragmentOld(TodoList todoList) {
			super();
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
			
			tvReminder.setText(getReminderDisplay()); // refresh                
	        dialog.dismiss();
	    }
	}
}
