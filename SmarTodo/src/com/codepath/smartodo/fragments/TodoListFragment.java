package com.codepath.smartodo.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.smartodo.R;
import com.codepath.smartodo.activities.ListsViewerActivity;
import com.codepath.smartodo.adapters.SharedWithAdapter;
import com.codepath.smartodo.adapters.TodoItemsAdapter;
import com.codepath.smartodo.dialogs.ColorPickerDialog;
import com.codepath.smartodo.enums.TodoListDisplayMode;
import com.codepath.smartodo.helpers.AppConstants;
import com.codepath.smartodo.interfaces.TouchActionsListener;
import com.codepath.smartodo.model.Address;
import com.codepath.smartodo.model.TodoItem;
import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.model.User;
import com.codepath.smartodo.persistence.PersistenceManager;
import com.codepath.smartodo.persistence.PersistenceManager.ACCESS_LOCATION;
import com.codepath.smartodo.persistence.PersistenceManager.PERSISTENCE_OPERATION;
import com.codepath.smartodo.persistence.PersistenceManagerFactory;
import com.codepath.smartodo.services.ModelManagerService;
import com.parse.ParseException;
import com.parse.ParseUser;

public class TodoListFragment extends DialogFragment implements OnTouchListener {

	static final String TAG = TodoListFragment.class.getSimpleName();

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

	private GridView gvViewSharedWith;

	public SharedWithAdapter sharedWithListAdapter;
	private PersistenceManager persistenceManager = PersistenceManagerFactory.getInstance();
	
	public static TodoListFragment newInstance(String todoListId, int animationStyle, int colorId)
    {
		TodoListFragment fragment = new TodoListFragment();

        Bundle arguments = new Bundle();
        arguments.putString(AppConstants.OBJECTID_EXTRA, todoListId);
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
		getDialog().getWindow().getAttributes().windowAnimations = animationStyle;
		
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
			todoList = persistenceManager.findTodoListByObjectId(getActivity(), listObjectId, ACCESS_LOCATION.CLOUD_ELSE_LOCAL); // TODO: ACCESS_LOCATION.CLOUD_ELSE_LOCAL ok?
			if (todoList != null) {
			    todoItemsList = todoList.getAllItems();
			}
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
		
		if(mode == TodoListDisplayMode.CREATE) {
			return;
		}
		
		etTitle.setText(todoList.getName());
		
		tvSharedWithList.setText("Shared with: " + getDisplaySharedWithList());
		
		tvReminder.setText(getReminderDisplay());
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
		
		for(int i=0; i < users.size(); i++){
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
			
			if (i < users.size() - 1) {
				sb.append(", "); // Don't end the string with a comma
			}
		}
		
		return sb.toString();
	}
	
	private void setupListeners() {
		
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
		// saveTodoList();
		super.onStop();
	}
	
	public TodoList saveTodoList() {
		return updateTodoList();

/*		// Is the following needed? For a new TodoList, objectId will be null anyway.
		if(objectId != null) {
			Intent i = new Intent();
			Log.i("info", "In TodoListFragment:saveTodoList, putting TodoList " + todoList.getName());
			i.putExtra(TodoList.TODOLIST_KEY, todoList);
			i.putExtra(AppConstants.OBJECTID_EXTRA, objectId);
			TodoListFragment.this.getActivity().setResult(Activity.RESULT_OK, i);
		}*/
	}
		
	/*
	 * Populate the TodoList object
	 */
	private TodoList updateTodoList() {
		if (!validateInput()) {
			return null;
		}
		
		PERSISTENCE_OPERATION operation = PERSISTENCE_OPERATION.UPDATE;
		if (mode == TodoListDisplayMode.CREATE) {
			operation = PERSISTENCE_OPERATION.ADD;
		}
		
		// Create todoList if new list being created
		if(todoList == null) {
			todoList = new TodoList();			
		}
		
		todoList.setName(etTitle.getText().toString());
		todoList.setOwner(ModelManagerService.getUser());
		
		List<TodoItem> listWithoutDummy = new ArrayList<TodoItem>();
		listWithoutDummy.addAll(todoItemsList.subList(0, todoItemsList.size() - 1));
		todoList.setItems(listWithoutDummy);
		
		Log.i("info", "Calling PeristenceManager to save the TodoList " + todoList.getName());
		String objectId = persistenceManager.saveTodoList(todoList, operation, ListsViewerActivity.getInstance()); 
		
		return todoList;
	}
	
	private boolean validateInput() {
		String title = etTitle.getText().toString();
		
		if(title == null || title.isEmpty()) {
			Toast.makeText(getActivity(), "Please specify a non-empty name", Toast.LENGTH_LONG).show();
			return false;
		}
		
		// TODO: Validate items
		for(int i = 0; i < adapter.getCount(); i++){
			
		}
		
		try {
			if (mode == TodoListDisplayMode.CREATE && 
					persistenceManager.findTodoListByNameAndUser(this.getActivity(), title, ModelManagerService.getUser(), ACCESS_LOCATION.LOCAL) != null) { // TODO: ACCESS_LOCATION.LOCAL ok?
				Toast.makeText(getActivity(), "The list name is not unique!", Toast.LENGTH_LONG).show();
				return false;
			}
		} catch (ParseException e) {
			Log.e("error", e.getMessage(), e);
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
			return false;
		}
		
		return true;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == AppConstants.REQUEST_CODE_SHARE_ACTIVITY && resultCode == Activity.RESULT_OK) {			
			
		}
	}	
	
}
