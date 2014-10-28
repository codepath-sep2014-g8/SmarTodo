package com.codepath.smartodo.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.smartodo.R;
import com.codepath.smartodo.fragments.TodoListFragment;
import com.codepath.smartodo.helpers.AppConstants;
import com.codepath.smartodo.helpers.Utils;
import com.codepath.smartodo.interfaces.TouchActionsListener;
import com.codepath.smartodo.model.TodoList;


public class ItemsViewerActivity extends FragmentActivity implements TouchActionsListener{
	
	private ImageView ivBack;
	private ImageView ivShare;
	String objectId = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_items_viewer);
		
		initialize();
		
		setupListeners();
	}

	private void initialize(){
		initializeActionBar();

		
		if (getIntent().hasExtra(AppConstants.OBJECTID_EXTRA)) {			
			objectId = (String) getIntent().getStringExtra(AppConstants.OBJECTID_EXTRA);
		}

		TodoListFragment fragmentTodoList = TodoListFragment.newInstance(objectId, R.style.DialogFromLeftAnimation, R.color.todo_list_backcolor);
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.fragmentContainer, fragmentTodoList);
		transaction.commit();
	}
	
	
	private void initializeActionBar(){

        ActionBar actionBar = getActionBar();

        View view = getLayoutInflater().inflate(R.layout.action_bar_grid_view, null);
        
        ivBack = (ImageView)view.findViewById(R.id.ivBackButton_grid_view);
        ivBack.setVisibility(View.VISIBLE);
        
        ivShare = (ImageView)view.findViewById(R.id.ivShare);
        ivShare.setVisibility(View.VISIBLE);
        
        TextView tvTitle_home = (TextView) view.findViewById(R.id.tvTitle_home);
        tvTitle_home.setText(Utils.buildTitleText());
        
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
	
	private void setupListeners(){
		ivBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		
		ivShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(ItemsViewerActivity.this, ShareActivity.class);
				intent.putExtra(AppConstants.OBJECTID_EXTRA, objectId);
				startActivityForResult(intent, 200);	
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_items_properties) {
			Log.d("debug", "here");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onPreviousListRequested() {
//		System.out.println("onPreviousListRequested: " + currentListIndex);
//		if(currentListIndex == -1 || currentListIndex == 0){
//			currentListIndex = adapter.getCount() - 1;
//		}
//		else{
//			currentListIndex --;
//		}
//		TodoList todoList = adapter.getItem(currentListIndex);
//		showTodoListDialog(todoList.getObjectId(), 
//				(currentListIndex % 2 == 0 ) ? R.style.DialogFromLeftAnimation : R.style.DialogFromRightAnimation,
//					com.codepath.smartodo.helpers.Utils.getColor(currentListIndex % 6)	);
		
	}

	@Override
	public void onNextListRequested() {
		
//		System.out.println("onNextListRequested: " + currentListIndex);
//		
//		if(currentListIndex == adapter.getCount() -1 || currentListIndex == -1){
//			currentListIndex = 0;
//		}
//		else{
//			currentListIndex ++;
//		}
//		
//		TodoList todoList = adapter.getItem(currentListIndex);
//		showTodoListDialog(todoList.getObjectId(), 
//				(currentListIndex % 2 == 0 ) ? R.style.DialogFromLeftAnimation : R.style.DialogFromRightAnimation,
//					com.codepath.smartodo.helpers.Utils.getColor(currentListIndex % 6)	);
	}
}
