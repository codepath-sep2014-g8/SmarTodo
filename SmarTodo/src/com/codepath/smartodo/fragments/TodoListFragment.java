package com.codepath.smartodo.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.codepath.smartodo.R;
import com.codepath.smartodo.activities.ShareActivity;
import com.codepath.smartodo.adapters.TodoItemsAdapter;
import com.codepath.smartodo.enums.TodoListDisplayMode;
import com.codepath.smartodo.helpers.AppConstants;
import com.codepath.smartodo.model.TodoItem;
import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.model.User;
import com.codepath.smartodo.services.ModelManagerService;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
	private String listName = null;
	
	private TodoListDisplayMode mode = TodoListDisplayMode.UPDATE;
	
	public static TodoListFragment newInstance(String todoListName)
    {
		TodoListFragment fragment = new TodoListFragment();

        Bundle arguments = new Bundle();
        arguments.putString(AppConstants.KEY_TODOLIST, todoListName);
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
		
		
		if(getArguments() != null && getArguments().containsKey(AppConstants.KEY_TODOLIST)){
			listName = getArguments().getString(AppConstants.KEY_TODOLIST);
		}
		
		if(listName == null || listName.isEmpty()){	
			todoList = new TodoList();
			todoItemsList = new ArrayList<TodoItem>();
			mode = TodoListDisplayMode.CREATE;
			return;
		}
		
		try {
			todoList = TodoList.findTodoListByName(listName);
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
		
	}
	
	private String getDisplaySharedWithList(){
		StringBuilder sb = new StringBuilder();
		
		List<User> users = todoList.getSharing();
	
		if(users == null){
			return sb.toString();
		}
		try{
		for(User user : users){
			sb.append(user.getRealName());
			sb.append(",");
		}
		}
		catch(Exception ex){
			
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
				// Show notification creation window	
			}
		});
		
		
		ivShare.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Show Share activity
				Intent intent = new Intent(getActivity(), ShareActivity.class);
				intent.putExtra(AppConstants.KEY_TODOLIST, listName);
				startActivityForResult(intent, 200);				
			}
		});
		
		
		ivColorPicker.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Show color picker	
			}
		});
		
		
		ivSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateTodoList();
				
			}
		});
		
		ivDelete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// if owner of the list then only allow deletion
			}
		});
	}
	
	
	/*
	 * Populate the TodoList object
	 */
	private void updateTodoList(){
		
		if(isValidInput() == false){
			//Show toast and dont update
		}
		//Create todoList if new list being created
		if(todoList == null){
			todoList = new TodoList();
		}
		
		//Should be used when creating new list
		//ModelManagerService.saveList(etTitle.getText().toString(), ModelManagerService.getUser(), todoItemsList);
		
		Log.i("info", "Saving TODO List " + listName);
		
		todoList.setName(etTitle.getText().toString());
		todoList.setOwner(ModelManagerService.getUser());
		
		todoList.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException arg0) {
				Log.i("info", "Saving " + todoItemsList.size() + " list items");
				for(TodoItem item : todoItemsList) {
					Log.i("info", "Saving " + item.getText() + "   ");
					item.setList(todoList);
					
					try {
						item.save();
					} catch (ParseException e) {
						Log.e("error", e.getMessage(), e);
					}
				}
				
				Log.i("info", "Items saved");
			}
		});
	}
	
	private boolean isValidInput(){
		boolean isValid = false;
		
		String title = etTitle.getText().toString();
		
		if(title == null || title.isEmpty()){
			return isValid;
		}
		
		//Validate items
		for(int i = 0; i < adapter.getCount(); i++){
			
		}
		
		return true;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode == AppConstants.REQUEST_CODE_SHARE_ACTIVITY && resultCode == Activity.RESULT_OK){
			
			
		}
	}
	
}
