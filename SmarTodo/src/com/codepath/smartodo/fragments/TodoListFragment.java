package com.codepath.smartodo.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.codepath.smartodo.model.ReminderLocation;
import com.codepath.smartodo.model.TodoItem;
import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.model.User;
import com.codepath.smartodo.services.ModelManagerService;
import com.google.android.gms.location.Geofence;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

public class TodoListFragment extends DialogFragment implements OnTouchListener {

	private static final String TAG = TodoListFragment.class.getSimpleName();

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
	
	//UI elements
	private EditText etTitle;
	private Button btnAdd;
	private EditText etNewItem;
	private ListView lvItems;
	private LinearLayout llActions;
	private ImageView ivColorPicker;
	private ImageView ivSave;
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
	// private HashMap<String, String> locationsMap;
	private List<ReminderLocation> reminderLocations;
	
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
//		getDialog().getWindow()
//	    .getAttributes().windowAnimations = animationStyle;
		
		listener = (TouchActionsListener)getActivity();
	}
	
	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		
//		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow()
	    .getAttributes().windowAnimations = animationStyle;
		
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
		ivColorPicker = (ImageView)view.findViewById(R.id.ivColorPicker_ftdl);
		ivSave = (ImageView)view.findViewById(R.id.ivSave_ftdl);
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
        
        reminderLocations = new ArrayList<ReminderLocation>();
        reminderLocations.add(new ReminderLocation(HOME_LOCATION_NAME, HOME_ADDR, HOME_IMAGE_URL, R.drawable.ic_home));
        reminderLocations.add(new ReminderLocation(BOFA_MTNVIEW_LOCATION_NAME, BOFA_MTNVIEW_ADDR, BOFA_MTNVIEW_IMAGE_URL, R.drawable.ic_dollar));
        reminderLocations.add(new ReminderLocation(YAHOO_BUILDING_E_LOCATION_NAME, YAHOO_BUILDING_E_ADDR, YAHOO_BUILDING_E_IMAGE_URL, R.drawable.ic_yahoo_logo));
        reminderLocations.add(new ReminderLocation(YAHOO_BUILDING_F_LOCATION_NAME, YAHOO_BUILDING_F_ADDR, YAHOO_BUILDING_F_IMAGE_URL, R.drawable.ic_yahoo_logo));
        reminderLocations.add(new ReminderLocation(SAFEWAY_STEVENSCREEK_LOCATION_NAME, SAFEWAY_STEVENSCREEK_ADDR, SAFEWAY_STEVENSCREEK_IMAGE_URL, R.drawable.ic_shopping_cart));
        reminderLocations.add(new ReminderLocation(RIGHT_STUFF_LOCATION_NAME, RIGHT_STUFF_ADDR, RIGHT_STUFF_IMAGE_URL, R.drawable.ic_gym));
        
		// Sort on location names
		Collections.sort(reminderLocations, new Comparator<ReminderLocation>() {
			public int compare(ReminderLocation o1, ReminderLocation o2) {
				if (o1.getName() == null || o2.getName() == null)
					return 0;
				return o1.getName().compareTo(o2.getName());
			}
		});
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
		private TodoList todoList;
		private List<ReminderLocation> reminderLocations;
		private ReminderLocation currentReminderLocation;
		private Address currentAddress;		
		private String currentLocation;
		private ListView lvLocationChooser;
		private int selectedColor;
		private View lastSelectedView = null;

		public LocationDialogFragment(TodoList todoList, List<ReminderLocation> reminderLocations) {
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
	    
	    private void setCustomtyle(TextView view) {
	    	view.setBackgroundColor(selectedColor);
	    	view.setAlpha(0.8f);
	    	view.setTextColor(getResources().getColor(R.color.white));
	    	view.setTextSize(16);
	    	view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
	    	view.setGravity(Gravity.CENTER_HORIZONTAL);
	    }

		private TextView getCustomTitle() {
	    	TextView title = new TextView(getActivity());
	    	setCustomtyle(title);
	    	title.setText(getString(R.string.title_location_reminding_dialog, todoList.getName()));    	    		    	
	    	return title;
	    }
		
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	if (todoList == null || Utils.isNullOrEmpty(reminderLocations)) {
	    		return null;
	    	}
	    	selectedColor = getActivity().getResources().getColor(colorId);  // must be set in the beginning
			// Create and initialize an adapter
	    	
	    	ReminderLocationsAdapter dataAdapter = new ReminderLocationsAdapter(getActivity(), reminderLocations);
	    	
	    	// View view = LayoutInflater.from(getActivity()).inflate(R.layout.location_chooser, null); 
	    	View view = getActivity().getLayoutInflater().inflate(R.layout.location_chooser, null);
			lvLocationChooser = (ListView) view.findViewById(R.id.lvLocationChooser);
			
			//Log.d(TAG, "In LocationDialogFragment:onCreateDialog: choicemode is " + lvLocationChooser.getChoiceMode());
			//Log.d(TAG, "In LocationDialogFragment:onCreateDialog: ListView.CHOICE_MODE_SINGLE is " + ListView.CHOICE_MODE_SINGLE);	
					 
			lvLocationChooser.setAdapter(dataAdapter);
			
			// lvLocationChooser.setSelector(getActivity().getResources().getColor(colorId));  // TODO: Check why this is not working
			
			lvLocationChooser.setSelector(colorId); 
			final int defaultDrawingCacheBackgroundColor = lvLocationChooser.getDrawingCacheBackgroundColor();
			int[] colors = {selectedColor, selectedColor}; 
			lvLocationChooser.setDivider(new GradientDrawable(Orientation.RIGHT_LEFT, colors));
			lvLocationChooser.setDividerHeight(3);
			lvLocationChooser.setOnItemClickListener(new OnItemClickListener() {

	            @Override
	            public void onItemClick(AdapterView<?> parent, View view,
	                            int position, long id) {
	            	 // Log.d(TAG, "In onItemClick, position is " + position);
	                 if (position != -1) {            
	                   lvLocationChooser.setItemChecked(position, true);
	                   view.setSelected(true); 
	                   if (lastSelectedView != null && lastSelectedView != view) {
	                	   lastSelectedView.setBackgroundColor(defaultDrawingCacheBackgroundColor);
	                   }
	                   view.setBackgroundColor(selectedColor);
	                   lastSelectedView = view;
	                 }
	            }
	          });
				
			// Create an AlertDialog and associate the listview for location names
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
			builder.setCustomTitle(getCustomTitle());		 			
			builder.setView(lvLocationChooser); 
			builder.setPositiveButton("Done", this);
			AlertDialog alertDialog = builder.create();

			alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
				@Override
				public void onShow(DialogInterface dialog) {
					AlertDialog alertDialog = (AlertDialog) dialog;
					Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
					if (button != null) {
						setCustomtyle(button);;
					}
					button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
					if (button != null) {
						setCustomtyle(button);
					}
				}
			});	
			
			alertDialog.show(); // Maybe needed for highlighting a row below by a programmatic click operation
			
			if (currentReminderLocation != null) {
				//set the default choice according to the current value
				int position = reminderLocations.indexOf(currentReminderLocation);
				if (position != -1) {
					// Log.d(TAG, "About to perform click for position " + position);
					lvLocationChooser.setItemChecked(position, true);	
					lvLocationChooser.setSelection(position);
					// lvLocationChooser.getAdapter().getView(position, null, null).setBackgroundColor(selectedColor);
					// lvLocationChooser.getAdapter().getView(position, null, null).performClick();
					
					lvLocationChooser.performItemClick(lvLocationChooser.getAdapter().getView(position, null, null), position, position);
				}
			}
			
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
					&& (selectedReminderLocation.getName().equalsIgnoreCase(currentLocation))) {
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
			
			HandleGeofencingAddress(selectedReminderLocation.getName(), streetAddress);

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
	
	public class LocationDialogFragmentWithSpinner extends DialogFragment implements android.content.DialogInterface.OnClickListener {	
		private Spinner locationSpinner;
		private TodoList todoList;
		private HashMap<String, String> locationsMap;
		private List<String> locationList;
		private Address currentAddress;
		private String currentLocation;
		private ArrayAdapter<String> dataAdapter;

		public LocationDialogFragmentWithSpinner(TodoList todoList, HashMap<String, String> locationsMap) {
			super();
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
	    	title.setBackgroundColor(getActivity().getResources().getColor(colorId));
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
	
	private class ReminderLocationsAdapter extends ArrayAdapter<ReminderLocation> {

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
