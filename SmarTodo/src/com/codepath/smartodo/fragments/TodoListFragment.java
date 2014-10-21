package com.codepath.smartodo.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.codepath.smartodo.R;
import com.codepath.smartodo.activities.ShareActivity;
import com.codepath.smartodo.adapters.TodoItemsAdapter;
import com.codepath.smartodo.dialogs.ColorPickerDialog;
import com.codepath.smartodo.dialogs.NotificationSelectorDialog;
import com.codepath.smartodo.enums.TodoListDisplayMode;
import com.codepath.smartodo.geofence.GeofenceUtils;
import com.codepath.smartodo.helpers.AppConstants;
import com.codepath.smartodo.model.Address;
import com.codepath.smartodo.model.TodoItem;
import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.model.User;
import com.codepath.smartodo.services.ModelManagerService;
import com.google.android.gms.location.Geofence;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

public class TodoListFragment extends Fragment {

	private static final String TAG = TodoListFragment.class.getSimpleName();
	
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
	
	private TodoListDisplayMode mode = TodoListDisplayMode.UPDATE;
	
	public static TodoListFragment newInstance(String todoListName)
    {
		TodoListFragment fragment = new TodoListFragment();

        Bundle arguments = new Bundle();
        arguments.putString(AppConstants.OBJECTID_EXTRA, todoListName);
        fragment.setArguments(arguments);

        return fragment;
    }

	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_todo_list, container, false);
		
		initializeViews(view);
		populateData();
		setupListeners();
		
		return view;
	}
		
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initialize();
	}


	private void initialize(){
		
		initializeTodoList();
		
		adapter = new TodoItemsAdapter(getActivity(), todoItemsList);	
	}
	
	private void initializeTodoList(){
		
		
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
			
			Address address = todoList.getAddress().fetchIfNeeded();
			if (null == address) {
				String streetAddress = "1350 North Mathilda Avenue, Sunnyvale, CA";			
				address = new Address();
				address.setLocation(new ParseGeoPoint(37.4151756, -122.0244941));  // need not do this; street address is sufficient
				address.setStreetAddress(streetAddress);
				address.setName("Yahoo Sunnyvale Building F");
				address.setUser(parseUser);
				todoList.setAddress(address);
			} else {
				address = address.fetchIfNeeded();
			}
			
			String location = address.getName();
			
			sb.append("Remind me at location: ").append(location);
		    sb.append("\r\n");
		    
		    int radius = 50; // meters
			GeofenceUtils.setupTestGeofences(getActivity(), parseUser.getObjectId(), address.getStreetAddress(), radius,
					Geofence.GEOFENCE_TRANSITION_ENTER, ("Close to " + address.getName()), todoList.getName(), "All Todo items");
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
			
			@Override
			public void onClick(View v) {
				FragmentManager manager = getActivity().getFragmentManager();
				
				NotificationSelectorDialog dialog = NotificationSelectorDialog.newInstance();
				dialog.show(manager, "fragment_notification_selector");
				
				
			}
		});
		
		
		ivShare.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Show Share activity
				Intent intent = new Intent(getActivity(), ShareActivity.class);
				intent.putExtra(AppConstants.KEY_TODOLIST, listObjectId);
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
				// if owner of the list then only allow deletion
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
		
		try {
			String objectId = ModelManagerService.saveList(todoList, todoItemsList);
			Toast.makeText(getActivity(), "List saved", Toast.LENGTH_SHORT).show();
			
			return objectId;
		} catch (ParseException e) {
			Log.e("error", e.getMessage(), e);
			Toast.makeText(getActivity(), "Error saving list. Try again.", Toast.LENGTH_SHORT).show();
			return null;
		}
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
		}
		
		private void initCurrentDate() {
			Date notificationTime = todoList.getNotificationTime();
			Log.d(TAG, "In initCurrentDate, notificationTime is null");
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
			
			Log.d(TAG, "In onTimeSet, hourOfDay is " + hourOfDay + ", minute is " + minute);
			c.set(Calendar.HOUR_OF_DAY, hourOfDay);
			c.set(Calendar.MINUTE, minute);
			todoList.setNotificationTime(c.getTime());	
			tvReminder.setText(getReminderDisplay()); // refresh
			// Save the todoList now?
		};
	}
	
}
