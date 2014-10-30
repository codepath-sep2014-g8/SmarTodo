package com.codepath.smartodo.activities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.codepath.smartodo.R;
import com.codepath.smartodo.adapters.ShareListAdapter;
import com.codepath.smartodo.adapters.SharedWithAdapter;
import com.codepath.smartodo.helpers.AppConstants;
import com.codepath.smartodo.model.ShareUser;
import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.model.User;
import com.codepath.smartodo.services.ModelManagerService;
import com.parse.ParseException;
import com.parse.SaveCallback;

public class ShareActivity extends Activity {

	private static final String TAG = ShareActivity.class.getSimpleName();
	
	private SearchView searchView;
	public ShareListAdapter shareListAdapter;
	private List<ShareUser> users;
	private ListView lvUsers;
	private String listName;
	private TodoList todoList;

	private GridView gvSharedWith;

	public SharedWithAdapter gvSharedWithAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		
		initialize();
	}
	
	private void initialize(){
		
		listName = getIntent().getExtras().getString(AppConstants.OBJECTID_EXTRA);
		try {
			todoList = TodoList.findTodoListByObjectId(listName);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		lvUsers = (ListView)findViewById(R.id.lvPeopleForShare);
		users = new ArrayList<ShareUser>(convertToSharedUsers(User.findAll(), true));
		shareListAdapter = new ShareListAdapter(this, users);
		lvUsers.setAdapter(shareListAdapter);
		
		gvSharedWith = (GridView) findViewById(R.id.gvSharedWith);
		gvSharedWithAdapter = new SharedWithAdapter(this, selectedUsers, false);
		gvSharedWith.setAdapter(gvSharedWithAdapter);
	}

	private Collection<ShareUser> convertToSharedUsers(Collection<User> users, boolean skipCurrentUser) {
		Collection<ShareUser> sharedUsers = new ArrayList<ShareUser>();
		for (User user:users) {
			if(skipCurrentUser==true && user.equals(ModelManagerService.getUser())) {
				continue; // Skip the current user
			}
			sharedUsers.add(new ShareUser(user));
		}
		return sharedUsers;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.share, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
	    searchView = (SearchView) searchItem.getActionView();
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {
	    	
	       @Override
	       public boolean onQueryTextSubmit(String query) {
	            
	    	   getMatchingUsers(query);
	            return true;
	       }

	       @Override
	       public boolean onQueryTextChange(String newText) {
	    	   
	    	   getMatchingUsers(newText);
	    	   
	           return false;
	       }
	   });
	   return super.onCreateOptionsMenu(menu);
	}
	
	private void getMatchingUsers(String newText){
		if(newText != null && !newText.isEmpty()){
			List<User> users = new ArrayList<User>( User.findAllLike(newText));
			List<ShareUser> shareUsers = new ArrayList<ShareUser>();
			for(User user : users){
				if(user.equals(ModelManagerService.getUser())) {
					continue; // Skip the current user 
				}
				shareUsers.add(new ShareUser(user));
			}
			shareListAdapter.clear();
			shareListAdapter.addAll(shareUsers);
		}
	}
	
	public void onShareRequested(final View view){
		
		final List<User> users = shareListAdapter.getSelectedUsers();
		
		todoList.addToSharing(users);

		ModelManagerService.saveList(todoList, null, new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if(e == null) {
					Toast.makeText(ShareActivity.this, "Share notifications sent", Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(view.getContext(), "Error saving list. Try again.", Toast.LENGTH_SHORT).show();
				}
			}
			
		});
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

	private List<User> selectedUsers = new ArrayList<User>();
	
	public void selectUser(User user, boolean selected) {
		Log.i("info", "Changing user selection for " + user.getEmail() + " to " + selected);
		
		if(selected) {
			// Add only if it doesn't already exist
			boolean found = false;
			for(int i=0;i<gvSharedWithAdapter.getCount();i++) {
				User u = gvSharedWithAdapter.getItem(i);
				if(u.equals(user)) {
					found = true;
					break;
				}
			}
			
			if(!found) gvSharedWithAdapter.add(user);
		} else {
			gvSharedWithAdapter.remove(user);
		}
	}
}
