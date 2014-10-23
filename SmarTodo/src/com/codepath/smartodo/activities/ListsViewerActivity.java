package com.codepath.smartodo.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.codepath.smartodo.R;
import com.codepath.smartodo.adapters.TodoListAdapter;
import com.codepath.smartodo.dialogs.NotificationSelectorDialog;
import com.codepath.smartodo.fragments.TodoListDetailsFragment;
import com.codepath.smartodo.helpers.AppConstants;
import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.services.ModelManagerService;
import com.codepath.smartodo.utils.Utils;
import com.etsy.android.grid.StaggeredGridView;
import com.parse.ParseException;

public class ListsViewerActivity extends FragmentActivity {
	public static final int REQUEST_CODE_NEW_LIST = 333;
	protected static final int REQUEST_CODE_EDIT_LIST = 334;
	private StaggeredGridView staggeredGridView;
	private TodoListAdapter adapter;
	//private ImageView ivAdd;

	private String editedObjectId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lists_viewer);

		initialize();
		setupListeners();
	}

	private void initialize() {

		initializeActionBar();

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

		//ivAdd = (ImageView) view.findViewById(R.id.ivAdd_todolist);

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
				editedObjectId = todoList.getObjectId();

				Intent i = new Intent(ListsViewerActivity.this,
						ItemsViewerActivity.class);
				i.putExtra(AppConstants.OBJECTID_EXTRA, todoList.getObjectId());
				startActivityForResult(i, REQUEST_CODE_EDIT_LIST);
				
//				FragmentManager manager = getSupportFragmentManager();
//				TodoListDetailsFragment dialog= new TodoListDetailsFragment();
//				dialog.show(manager, "TAG");
			}

		});

//		ivAdd.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				showCreateListActivity();
//			}
//		});

	}

	public void onNewTodoRequested(View view) {
		showCreateListActivity();
	}

	private void showCreateListActivity() {
		Intent intent = new Intent(ListsViewerActivity.this,
				ItemsViewerActivity.class);

		editedObjectId = null;
		startActivityForResult(intent, REQUEST_CODE_NEW_LIST);
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
}
