package com.codepath.smartodo.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.smartodo.R;
import com.codepath.smartodo.fragments.ListPropertiesDialogFragment;
import com.codepath.smartodo.fragments.TodoListFragment;
import com.codepath.smartodo.helpers.AppConstants;
import com.codepath.smartodo.utils.Utils;


public class ItemsViewerActivity extends FragmentActivity {
	
	private ImageView ivBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_items_viewer);
		
		initialize();
	}

	private void initialize(){
		initializeActionBar();

		String objectId = null;
		if (getIntent().hasExtra(AppConstants.OBJECTID_EXTRA)) {			
			objectId = (String) getIntent().getStringExtra(AppConstants.OBJECTID_EXTRA);
		}

		TodoListFragment fragmentTodoList = TodoListFragment.newInstance(objectId, R.style.DialogFromLeftAnimation);
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
