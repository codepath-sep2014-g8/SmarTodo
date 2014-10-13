package com.codepath.smartodo.activities;

import com.codepath.smartodo.R;
import com.codepath.smartodo.R.id;
import com.codepath.smartodo.R.layout;
import com.codepath.smartodo.R.menu;
import com.codepath.smartodo.fragments.ListPropertiesDialogFragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class ItemsViewerActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_items_viewer);
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
