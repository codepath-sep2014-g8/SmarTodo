package com.codepath.smartodo.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.smartodo.R;
import com.codepath.smartodo.adapters.TodoListAdapter;
import com.codepath.smartodo.fragments.TodoListFragment;
import com.codepath.smartodo.helpers.AppConstants;
import com.codepath.smartodo.helpers.Utils;
import com.codepath.smartodo.interfaces.TouchActionsListener;
import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.services.ModelManagerService;
import com.etsy.android.grid.StaggeredGridView;
import com.parse.ParseException;

public class ListsViewerActivity extends FragmentActivity implements TouchActionsListener{
	public static final int REQUEST_CODE_NEW_LIST = 333;
	protected static final int REQUEST_CODE_EDIT_LIST = 334;
	private StaggeredGridView staggeredGridView;
	private TodoListAdapter adapter;

	private String editedObjectId;
	private int currentListIndex = -1;
	private SwipeRefreshLayout swipeContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lists_viewer);

		initialize();
		setupListeners();
	}

	private void initialize() {

		initializeActionBar();

		swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
            	AsyncTask task = new AsyncTask() {

					@Override
					protected Object doInBackground(Object... params) {
						try {
							ModelManagerService.refreshFromUser(ModelManagerService.getUser());
						} catch (ParseException e) {
							Log.e("error", e.getMessage(), e);
						}
						return null;
					}
            		
					@Override
					protected void onPostExecute(Object result) {
						adapter.clear();
						adapter.addAll(ModelManagerService.getLists());
						adapter.notifyDataSetChanged();
						swipeContainer.setRefreshing(false);
					}
            	};
            	
            	task.execute();
            } 
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, 
                android.R.color.holo_green_light, 
                android.R.color.holo_orange_light, 
                android.R.color.holo_red_light);
		
		staggeredGridView = (StaggeredGridView) findViewById(R.id.grid_view);
		// list = new ArrayList<TodoList>();
		// populateTestData();
		// list = ModelManagerService.getLists();
		// if(list == null){
		// list = new ArrayList<TodoList>();
		// }

		adapter = new TodoListAdapter(getBaseContext(),
				ModelManagerService.getLists());

		staggeredGridView.setAdapter(adapter);
	}

	private void initializeActionBar() {
		ActionBar actionBar = getActionBar();
		View view = getLayoutInflater().inflate(R.layout.action_bar_grid_view,
				null);


		TextView tvTitle_home = (TextView) view.findViewById(R.id.tvTitle_home);
		tvTitle_home.setText(Utils.buildTitleText());

		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				ActionBar.LayoutParams.MATCH_PARENT,
				ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);

		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);

		actionBar.setCustomView(view, params);
	}

	private void setupListeners() {

		staggeredGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TodoList todoList = adapter.getItem(position);
//				editedObjectId = todoList.getObjectId();
//
//				Intent i = new Intent(ListsViewerActivity.this,
//						ItemsViewerActivity.class);
//				i.putExtra(AppConstants.OBJECTID_EXTRA, todoList.getObjectId());
//				startActivityForResult(i, REQUEST_CODE_EDIT_LIST);
				
				
				showTodoListDialog(todoList.getObjectId(), 
						(position % 2 == 0 ) ? R.style.DialogFromLeftAnimation : R.style.DialogFromRightAnimation,
							com.codepath.smartodo.helpers.Utils.getColor(position % 6)	);
			}

		});

	}

	public void onNewTodoRequested(View view) {
		showCreateListActivity();
	}

	private void showCreateListActivity() {
//		Intent intent = new Intent(ListsViewerActivity.this,
//				ItemsViewerActivity.class);
//
//		editedObjectId = null;
//		startActivityForResult(intent, REQUEST_CODE_NEW_LIST);
		
		showTodoListDialog(null, R.style.DialogFromBottomAnimation, R.color.todo_list_backcolor);
		
		
	}
	
	private void showTodoListDialog(String objectID, int animationStyle, int colorId){
		FragmentManager manager = getSupportFragmentManager();
		TodoListFragment dialog = TodoListFragment.newInstance(objectID, animationStyle, colorId);
		dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		dialog.show(manager, "TAG");
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lists_viewer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_EDIT_LIST:
		case REQUEST_CODE_NEW_LIST:
			try {
				if (data != null) {
					String objectId = data
							.getStringExtra(AppConstants.OBJECTID_EXTRA);

					if (objectId != null) {
						// Refresh the model with only the modified/added list
						// without triggering a full refresh
						TodoList newList = TodoList
								.findTodoListByObjectId(objectId);
						int existingListIdx = ModelManagerService
								.findExistingListIdxByObjectId(objectId);

						if (existingListIdx == -1) {
							ModelManagerService.getLists().add(newList);
						} else {
							ModelManagerService.getLists().set(existingListIdx,
									newList);
						}

						// adapter.clear();
						// adapter.addAll(ModelManagerService.getLists());
						adapter.notifyDataSetChanged();
					}
				}
			} catch (ParseException e) {
				Log.e("error", e.getMessage(), e);
				Toast.makeText(this, "Failed refresh", Toast.LENGTH_LONG)
						.show();
			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onPreviousListRequested() {
		System.out.println("onPreviousListRequested: " + currentListIndex);
		if(currentListIndex == -1 || currentListIndex == 0){
			currentListIndex = adapter.getCount() - 1;
		}
		else{
			currentListIndex --;
		}
		TodoList todoList = adapter.getItem(currentListIndex);
		showTodoListDialog(todoList.getObjectId(), 
				(currentListIndex % 2 == 0 ) ? R.style.DialogFromLeftAnimation : R.style.DialogFromRightAnimation,
					com.codepath.smartodo.helpers.Utils.getColor(currentListIndex % 6)	);
		
	}

	@Override
	public void onNextListRequested() {
		
		System.out.println("onNextListRequested: " + currentListIndex);
		
		if(currentListIndex == adapter.getCount() -1 || currentListIndex == -1){
			currentListIndex = 0;
		}
		else{
			currentListIndex ++;
		}
		
		TodoList todoList = adapter.getItem(currentListIndex);
		showTodoListDialog(todoList.getObjectId(), 
				(currentListIndex % 2 == 0 ) ? R.style.DialogFromLeftAnimation : R.style.DialogFromRightAnimation,
					com.codepath.smartodo.helpers.Utils.getColor(currentListIndex % 6)	);
	}
	
	
}
