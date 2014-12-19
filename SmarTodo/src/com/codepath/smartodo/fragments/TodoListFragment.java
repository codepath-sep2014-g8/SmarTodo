package com.codepath.smartodo.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.smartodo.R;
import com.codepath.smartodo.adapters.SharedWithAdapter;
import com.codepath.smartodo.adapters.TodoItemsAdapter;
import com.codepath.smartodo.dialogs.ColorPickerDialog;
import com.codepath.smartodo.enums.TodoListDisplayMode;
import com.codepath.smartodo.helpers.AppConstants;
import com.codepath.smartodo.interfaces.TouchActionsListener;
import com.codepath.smartodo.model.Address;
import com.codepath.smartodo.model.ReminderLocation;
import com.codepath.smartodo.model.TodoItem;
import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.model.User;
import com.codepath.smartodo.persistence.PersistenceManager;
import com.codepath.smartodo.persistence.PersistenceManagerFactory;
import com.codepath.smartodo.services.ModelManagerService;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

public class TodoListFragment extends DialogFragment implements OnTouchListener {

	static final String TAG = TodoListFragment.class.getSimpleName();

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
	private ListView lvItems;
	private LinearLayout llActions;
	private ImageView ivColorPicker;
	private ImageView ivSave;
	private LinearLayout llFooter;
	private ImageView ivFooterReminder;
	TextView tvReminder;
	
	private TextView tvSharedWithList;
	
	private TodoItemsAdapter adapter;
	private List<TodoItem> todoItemsList;
	private TodoList todoList = null;
	private String listObjectId = null;
	
	private int animationStyle = R.style.DialogFromLeftAnimation;
	int colorId;
	private TodoListDisplayMode mode = TodoListDisplayMode.UPDATE;
	
	private TouchActionsListener listener = null;
	// private HashMap<String, String> locationsMap;
	private List<ReminderLocation> reminderLocations;

	private GridView gvViewSharedWith;

	public SharedWithAdapter sharedWithListAdapter;
	private PersistenceManager persistenceManager = PersistenceManagerFactory.getInstance();
	
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
			todoList = persistenceManager.findTodoListByObjectId(getActivity(), listObjectId);
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
		lvItems = (ListView)view.findViewById(R.id.lvToDoItemsList_ftdl);
		llActions = (LinearLayout)view.findViewById(R.id.llAction_ftdl);
		ivColorPicker = (ImageView)view.findViewById(R.id.ivColorPicker_ftdl);
		ivSave = (ImageView)view.findViewById(R.id.ivSave_ftdl);
		llFooter = (LinearLayout)view.findViewById(R.id.llfooter_ftdl);
		ivFooterReminder = (ImageView)view.findViewById(R.id.ivFooterReminder_ftdl);
		tvReminder = (TextView)view.findViewById(R.id.tvReminder_ftdl);
		
		tvSharedWithList = (TextView)view.findViewById(R.id.tvSharedWith_ftdl);
		
		gvViewSharedWith = (GridView)view.findViewById(R.id.gvViewSharedWith);
		
		lvItems.setAdapter(adapter);
		sharedWithListAdapter = new SharedWithAdapter(getActivity(), todoList.getSharing(), true);
		gvViewSharedWith.setAdapter(sharedWithListAdapter);
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
	
	String getReminderDisplay(){
		
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
		
		
//		ivSave.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				String objectId = updateTodoList();
//				
//				if(objectId != null) {
//					Intent i = new Intent();
//					i.putExtra(AppConstants.OBJECTID_EXTRA, objectId);
//					TodoListFragment.this.getActivity().setResult(Activity.RESULT_OK, i);
//				}
//			}
//		});
		
		ivFooterReminder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new TimePickerFragment(TodoListFragment.this, todoList);
			    newFragment.show(getFragmentManager(), "timePicker");	
			}
		});
	}
	
	@Override
	public void onStop() {
		saveTodoList();
		super.onStop();
	}
	
	private void saveTodoList(){
		String objectId = updateTodoList();
		
		if(objectId != null) {
			Intent i = new Intent();
			i.putExtra(AppConstants.OBJECTID_EXTRA, objectId);
			TodoListFragment.this.getActivity().setResult(Activity.RESULT_OK, i);
		}
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
		
		List<TodoItem> listWithoutDummy = new ArrayList<TodoItem>();
		listWithoutDummy.addAll(todoItemsList.subList(0, todoItemsList.size() - 1));
		todoList.setItems(listWithoutDummy);
		
		String objectId = persistenceManager.saveTodoList(todoList, new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
//				if(e == null) {
//					Toast.makeText(getActivity(), "List saved", Toast.LENGTH_SHORT).show();
//				} else {
//					Log.e("error", e.getMessage(), e);
//					Toast.makeText(getActivity(), "Error saving list. Try again.", Toast.LENGTH_SHORT).show();
//				}
				
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
			if(mode == TodoListDisplayMode.CREATE && persistenceManager.findTodoListByNameAndUser(this.getActivity(), title, ModelManagerService.getUser()) != null) {
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
	
	class ReminderLocationsAdapter extends ArrayAdapter<ReminderLocation> {

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
