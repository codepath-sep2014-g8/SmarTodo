package com.codepath.smartodo.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.codepath.smartodo.adapters.TodoItemsAdapter;
import com.codepath.smartodo.model.TodoItem;
import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.services.ModelManagerService;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class TodoListFragment extends Fragment {

	private static final String TAG = TodoListFragment.class.getSimpleName();
	
	//UI elements
	private EditText etTitle;
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
	
	private TodoItemsAdapter adapter;
	private List<TodoItem> todoItemsList;

	private Button btnSave;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_todo_list, container, false);
		
		initializeViews(view);
		setupListeners();
		
		return view;
	}
		
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initialize();
	}


	private void initialize(){
		todoItemsList = new ArrayList<TodoItem>();
		adapter = new TodoItemsAdapter(getActivity(), todoItemsList);	
	}

	private void initializeViews(View view){
		
		etTitle = (EditText)view.findViewById(R.id.etTitle_ftdl);
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
		btnSave = (Button)view.findViewById(R.id.btnSave);
		
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("info", "Saving list");
				final TodoList todoList = new TodoList();
				todoList.setName(etTitle.getText().toString());
				todoList.setOwner(ModelManagerService.getUser());
				
				// TODO Add more properties
				
				todoList.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException arg0) {
						Log.i("info", "Saving " + todoItemsList.size() + " list items");
						for(TodoItem item : todoItemsList) {
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
				
				Log.i("info", "Initial list save complete");
			}
		});
		
		lvItems.setAdapter(adapter);
	}
	
	
	private void setupListeners(){
		
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
				// Validate and save the list
				
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
		
	}
	
	
	public void setList(List<TodoItem> todoItems){
		this.todoItemsList = todoItems;
		adapter.clear();
		adapter.addAll(this.todoItemsList);
		adapter.add(new TodoItem());
	}
	
}
