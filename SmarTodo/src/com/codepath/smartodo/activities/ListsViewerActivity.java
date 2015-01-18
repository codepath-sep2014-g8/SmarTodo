package com.codepath.smartodo.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import com.codepath.smartodo.helpers.AppConstants;
import com.codepath.smartodo.helpers.Utils;
import com.codepath.smartodo.interfaces.TouchActionsListener;
import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.persistence.PersistenceCallback;
import com.codepath.smartodo.persistence.PersistenceManager;
import com.codepath.smartodo.persistence.PersistenceManager.ACCESS_LOCATION;
import com.codepath.smartodo.persistence.PersistenceManager.PERSISTENCE_OPERATION;
import com.codepath.smartodo.persistence.PersistenceManagerFactory;
import com.codepath.smartodo.services.ModelManagerService;
import com.etsy.android.grid.StaggeredGridView;
import com.parse.ParseException;

public class ListsViewerActivity extends FragmentActivity implements TouchActionsListener, PersistenceCallback {
	public static final int REQUEST_CODE_NEW_LIST = 333;
	protected static final int REQUEST_CODE_EDIT_LIST = 334;
	private StaggeredGridView staggeredGridView;
	private TodoListAdapter adapter;
	private int currentListIndex = -1;
	private SwipeRefreshLayout swipeContainer;
	private static ListsViewerActivity instance;
	
	public static ListsViewerActivity getInstance() {
		return instance;		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lists_viewer);
		instance = this;

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
                doRefresh();
            } 
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, 
                android.R.color.holo_green_light, 
                android.R.color.holo_orange_light, 
                android.R.color.holo_red_light);
		
		staggeredGridView = (StaggeredGridView) findViewById(R.id.grid_view);

		adapter = new TodoListAdapter(getBaseContext(), ModelManagerService.getCachedTodoLists());

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
				// Toast.makeText(ListsViewerActivity.this, "In setOnItemClickListener, position=" + position + ", list name=" + todoList.getName() + ", list id=" + todoList.getObjectId(), Toast.LENGTH_LONG).show();			
				showTodoListDialog(todoList.getObjectId(), REQUEST_CODE_EDIT_LIST,
						(position % 2 == 0 ) ? R.style.DialogFromLeftAnimation : R.style.DialogFromRightAnimation,
							com.codepath.smartodo.helpers.Utils.getColor(position % 6)	);
			}

		});

	}

	public void onNewTodoRequested(View view) {
		showCreateListActivity();
	}

	private void showCreateListActivity() {
		showTodoListDialog(null, REQUEST_CODE_NEW_LIST, R.style.DialogFromBottomAnimation, R.color.todo_list_backcolor);		
	}
	
	private void showTodoListDialog(String objectID, int requestCode, int animationStyle, int colorId) {
		Intent i = new Intent(ListsViewerActivity.this, ItemsViewerActivity.class);
		i.putExtra(AppConstants.OBJECTID_EXTRA, objectID);
		i.putExtra(AppConstants.KEY_ANIMATION_STYLE, animationStyle);
		i.putExtra(AppConstants.KEY_COLOR_ID, colorId);
		startActivityForResult(i, requestCode);
	
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_from_left);
		
//		FragmentManager manager = getSupportFragmentManager();
//		TodoListFragment dialog = TodoListFragment.newInstance(objectID, animationStyle, colorId);
//		dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
//		dialog.show(manager, "TAG");
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

/*	private class MyAsyncTask extends AsyncTask<Void, Void, Integer> {
		private Runnable runnable;

		public MyAsyncTask(Runnable runnable) {
			this.runnable = runnable;
		}
		
	     @Override
	     protected Integer doInBackground(Void... dummy) {
	         try {
	        	 Log.i("info", "Starting sleep...");
	        	 long initTime = System.currentTimeMillis();
				Thread.sleep(1000);
				Log.i("info", "Finishing sleep after ms " + (System.currentTimeMillis() - initTime));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	 
	    	 return 0;
	     }

	     @Override
	     protected void onPostExecute(Integer result) {
	         // This method is executed in the UIThread
	         // with access to the result of the long running task
	    	 runnable.run();
	     }
	}*/
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		
		// The callback method called form the PersistenceManager would handle the subsequent action.
		if (requestCode == REQUEST_CODE_EDIT_LIST) { // An existing TodoList has been modified
			if (data != null) {
				String todoListName = data.getStringExtra(TodoList.NAME_KEY);
				int operation = data.getIntExtra(TodoList.OPERATION_KEY, PERSISTENCE_OPERATION.UPDATE.ordinal());
				if (operation == PERSISTENCE_OPERATION.UPDATE.ordinal()) {
					Toast.makeText(ListsViewerActivity.this, "Updating the Todo list " + todoListName + "...", Toast.LENGTH_SHORT).show();				
				} else if (operation == PERSISTENCE_OPERATION.DELETE.ordinal()) {
					Toast.makeText(ListsViewerActivity.this, "Deleting the Todo list " + todoListName + "...", Toast.LENGTH_SHORT).show();
				}
			} 
		} else if (requestCode == REQUEST_CODE_NEW_LIST) { // A new list has been added
			if (data != null) {
				String todoListName = data.getStringExtra(TodoList.NAME_KEY);
				if (todoListName != null) {
					Toast.makeText(ListsViewerActivity.this, "Adding a new Todo list " + todoListName + "...", Toast.LENGTH_SHORT).show();
				}
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	
/*	
	//@Override
	protected void onActivityResultOld(int requestCode, int resultCode, final Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_EDIT_LIST:
		case REQUEST_CODE_NEW_LIST:
				if (data != null) {
					swipeContainer.setRefreshing(true);
					
					// TODO This is hacky.We're enforcing a delay before refreshing the item, allowing for enough time to save the data in the previous perspective
					MyAsyncTask task = new MyAsyncTask(new Runnable() {
						public void run() {
							doRefresh();
							
//							try {
//								String objectId = data.getStringExtra(AppConstants.OBJECTID_EXTRA);
//			
//								if (objectId != null) {
//									// Refresh the model with only the modified/added list
//									// without triggering a full refresh
//									TodoList newList = TodoList
//											.findTodoListByObjectId(objectId);
//									int existingListIdx = ModelManagerService
//											.findExistingListIdxByObjectId(objectId);
//			
//									if (existingListIdx == -1) {
//										ModelManagerService.getLists().add(newList);
//									} else {
//										ModelManagerService.getLists().set(existingListIdx,
//												newList);
//									}
//			
//									// adapter.clear();
//									// adapter.addAll(ModelManagerService.getLists());
//									adapter.notifyDataSetChanged();
//								}
//						} catch (ParseException e) {
//							Log.e("error", e.getMessage(), e);
//							Toast.makeText(ListsViewerActivity.this, "Failed refresh", Toast.LENGTH_LONG).show();
//						}
						}
					});
					
					task.execute();
				}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}*/

	@Override
	public void onPreviousListRequested() {
		System.out.println("onPreviousListRequested: " + currentListIndex);
		if (adapter.getCount() == 0) { // nothing to show
			return;
		}
		
		if(currentListIndex == -1 || currentListIndex == 0) {
			currentListIndex = adapter.getCount() - 1;
		}
		else {
			currentListIndex --;
		}
		if (currentListIndex >= 0) { // to be on the safe side
			TodoList todoList = adapter.getItem(currentListIndex);
			showTodoListDialog(todoList.getObjectId(), REQUEST_CODE_EDIT_LIST,
					(currentListIndex % 2 == 0) ? R.style.DialogFromLeftAnimation : R.style.DialogFromRightAnimation,
					Utils.getColor(currentListIndex % 6));
		}
	}

	@Override
	public void onNextListRequested() {
		
		System.out.println("onNextListRequested: " + currentListIndex);
		if (adapter.getCount() == 0) { // nothing to show
			return;
		}
		
		if(currentListIndex == (adapter.getCount() -1) || currentListIndex == -1) {
			currentListIndex = 0;
		}
		else{
			currentListIndex ++;
		}
		
		if (currentListIndex >= 0) { // to be on the safe side
		    TodoList todoList = adapter.getItem(currentListIndex);
		    showTodoListDialog(todoList.getObjectId(), REQUEST_CODE_EDIT_LIST,
				    (currentListIndex % 2 == 0 ) ? R.style.DialogFromLeftAnimation : R.style.DialogFromRightAnimation,
					Utils.getColor(currentListIndex % 6)	);
		    }
	}

	public void doRefresh() {
		// Your code to refresh the list here.
		// Make sure you call swipeContainer.setRefreshing(false)
		// once the network request has completed successfully.
		AsyncTask task = new AsyncTask() {

			@Override
			protected Object doInBackground(Object... params) {
				try {
					Log.i("info", "Refreshing local cache");
					ModelManagerService.refreshCachedTodoLists(ACCESS_LOCATION.CLOUD_ELSE_LOCAL); // TODO: ACCESS_LOCATION.CLOUD_ELSE_LOCAL ok?
					// persistenceManager.refreshTodoListsForUser(ListsViewerActivity.this, ModelManagerService.getUser(), ACCESS_LOCATION.CLOUD_ELSE_LOCAL);
				} catch (ParseException e) {
					Log.e("error", e.getMessage(), e);
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Object result) {
				adapter.clear();
				adapter.addAll(ModelManagerService.getCachedTodoLists());
				adapter.notifyDataSetChanged();
				swipeContainer.setRefreshing(false);
			}
		};
		
		task.execute();
	}

	@Override
	public void added(Exception exception, TodoList todoList) {
		if (exception == null) {
			// Toast.makeText(ListsViewerActivity.this, "Added the Todo list " + todoList.getName(), Toast.LENGTH_LONG).show();
		    adapter.add(todoList);  // Will it also add the todoList to the ModelManagerService.cachedTodoLists?
		    adapter.notifyDataSetChanged();
		} else {
			Log.e("Add error", exception.getMessage(), exception);
		}	
	}

	@Override
	public void updated(Exception exception, TodoList todoList) {
		if (exception == null) {
			// Toast.makeText(ListsViewerActivity.this, "Updated the Todo list " + todoList.getName(), Toast.LENGTH_LONG).show();
			int existingListIdx = ModelManagerService.findCachedIndexForATodoListByObjectId(todoList.getObjectId());
			if (existingListIdx == -1) {
				Log.e("Update error", "In callback updated, no existing TodoList " + todoList.getName());
			} else {
			    ModelManagerService.setCachedTodoList(existingListIdx, todoList);
			}
			adapter.notifyDataSetChanged();
		} else {
			Log.e("Update error", exception.getMessage(), exception);
		}
	}

	@Override
	public void deleted(Exception exception, TodoList todoList) {		
		if (exception == null) {
			// Toast.makeText(ListsViewerActivity.this, "Deleted the Todo list " + todoList.getName(), Toast.LENGTH_LONG).show();
			TodoList cachedTodoList = ModelManagerService.findCachedTodoListByObjectId(todoList.getObjectId());
			adapter.remove(cachedTodoList);
			ModelManagerService.removeFromCachedTodoLists(cachedTodoList);  // necessary?
			adapter.notifyDataSetChanged();			
		} else {
			Log.e("Delete error", exception.getMessage(), exception);
		}
	}
		
}
