package com.codepath.smartodo.activities;

import java.util.ArrayList;
import java.util.List;

import com.codepath.smartodo.R;
import com.codepath.smartodo.R.id;
import com.codepath.smartodo.R.layout;
import com.codepath.smartodo.R.menu;
import com.codepath.smartodo.adapters.ShareListAdapter;
import com.codepath.smartodo.enums.TodoListDisplayMode;
import com.codepath.smartodo.helpers.AppConstants;
import com.codepath.smartodo.model.ShareUser;
import com.codepath.smartodo.model.TodoItem;
import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.model.User;
import com.codepath.smartodo.notifications.NotificationsSender;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class ShareActivity extends Activity {

	private static final String TAG = ShareActivity.class.getSimpleName();
	
	private SearchView searchView;
	private ShareListAdapter adapter;
	private List<ShareUser> users;
	private ListView lvUsers;
	private String listName;
	private TodoList todoList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		
		initialize();
	}
	
	private void initialize(){
		
		listName = getIntent().getExtras().getString(AppConstants.KEY_TODOLIST);
		
		ParseQuery<TodoList> itemQuery = ParseQuery.getQuery(TodoList.class);
		itemQuery.whereEqualTo(TodoList.NAME_KEY, listName);
		
		try {
			List<TodoList> list = itemQuery.find();
			
			todoList = list.get(0);
			
		} catch (ParseException e1) {
			
			Log.d(TAG, "Excpetion while getting the todo list");
			e1.printStackTrace();
		}
		
		lvUsers = (ListView)findViewById(R.id.lvPeopleForShare);
		users = new ArrayList<ShareUser>();
		adapter = new ShareListAdapter(this, users);
		lvUsers.setAdapter(adapter);
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
				shareUsers.add(new ShareUser(user));
			}
			adapter.clear();
			adapter.addAll(shareUsers);
		}
	}
	
	public void onShareRequested(View view){
		
		List<User> users = adapter.getSelectedUsers();
		
		for(User user : users){
			NotificationsSender.shareTodoList(todoList, user.getParseUser());
		}
		
		finish();
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
}
