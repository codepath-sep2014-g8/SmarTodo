package com.codepath.smartodo.activities;

import java.util.List;

import com.codepath.smartodo.R;
import com.codepath.smartodo.R.id;
import com.codepath.smartodo.R.layout;
import com.codepath.smartodo.R.menu;
import com.codepath.smartodo.adapters.TodoItemsAdapter;
import com.codepath.smartodo.fragments.ListPropertiesDialogFragment;
import com.codepath.smartodo.helpers.AppConstants;
import com.codepath.smartodo.model.TodoItem;
import com.codepath.smartodo.model.TodoList;
import com.parse.ParseException;
import com.parse.ParseQuery;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

public class ItemsViewerActivity extends FragmentActivity {
	
	private ListView lvToDoItems;
	private TodoItemsAdapter adapter;
	private List<TodoItem> itemsList;
	private TodoList todoList;
	private ImageView ivAdd;
	private ImageView ivBack;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_items_viewer);
		
		initialize();
	}

	private void initialize(){
		initializeActionBar();
		
		String name = (String)getIntent().getStringExtra(AppConstants.KEY_TODOLIST);
		setTitle(name);
		ParseQuery<TodoList> itemQuery = ParseQuery.getQuery(TodoList.class);
		itemQuery.whereEqualTo(TodoList.NAME_KEY, name);
		//??? should be redone - to user TodoList directly when passed from parent activity
		
		try {
			List<TodoList> list = itemQuery.find();
			for(TodoList tdl : list){
				itemsList = tdl.getAllItems();
				if(itemsList.size() > 0){
					break;
				}
			}
			todoList = list.get(0);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		lvToDoItems = (ListView)findViewById(R.id.lvToDoItemsList);
		
		
		adapter = new TodoItemsAdapter(getBaseContext(), itemsList);
		
		lvToDoItems.setAdapter(adapter);
	}
	
	
	private void initializeActionBar(){

        ActionBar actionBar = getActionBar();

        View view = getLayoutInflater().inflate(R.layout.action_bar_grid_view, null);
        
        ivAdd = (ImageView)view.findViewById(R.id.ivAdd_todolist);
        ivBack = (ImageView)view.findViewById(R.id.ivBackButton_grid_view);
        ivBack.setVisibility(View.VISIBLE);
        
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.items_viewer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_items_properties) {
			Log.d("debug", "here");
			ListPropertiesDialogFragment.newInstance(null).show(getSupportFragmentManager(), "dummytag");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
