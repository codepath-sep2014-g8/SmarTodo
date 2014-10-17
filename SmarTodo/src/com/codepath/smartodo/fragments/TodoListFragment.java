package com.codepath.smartodo.fragments;

import java.util.ArrayList;
import java.util.List;

import com.codepath.smartodo.R;
import com.codepath.smartodo.adapters.TodoItemsAdapter;
import com.codepath.smartodo.model.TodoItem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
		
		lvItems.setAdapter(adapter);
	}
	
	
	private void setupListeners(){
		
	}
	
	
	public void setList(List<TodoItem> todoItems){
		this.todoItemsList = todoItems;
		adapter.clear();
		adapter.addAll(this.todoItemsList);
		adapter.add(new TodoItem());
	}
	
}
