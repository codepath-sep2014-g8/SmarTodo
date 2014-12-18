package com.codepath.smartodo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.smartodo.R;
import com.codepath.smartodo.geofence.GeofenceUtils;
import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.model.User;
import com.codepath.smartodo.notifications.NotificationsSender;
import com.codepath.smartodo.persistence.ParsePersistenceManager;
import com.codepath.smartodo.services.ModelManagerService;
import com.google.android.gms.location.Geofence;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

public class LoginActivity extends Activity {
	public static int LOGIN_REQUEST = 0;
	private ParseUser currentUser;
	boolean loginInProgress = false;
	
	// Set it to true if we want email verifications to happen.
	// Set  the following variable to false for testing with existing accounts
	// whose emails might not have been verified.
	boolean checkEmailVerification = true;   

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
				
		if (checkCurrentUserOK()) {
			// Send the logged in user to our main class
			lauchMainApp();
		} else {
			doParseLogin();	
		}
	}
	
	private boolean checkCurrentUserOK() {
		// Get current user data from Parse.com		
	    currentUser = ParseUser.getCurrentUser();
	    if (currentUser == null) {
	    	return false;
	    }
	    	    
		if (checkEmailVerification && !emailVerified()) {
	    	return false;
	    }
        
        return true;	
	}
	
	// Checks if the user email has been verified by a separate email during signup
	private boolean emailVerified() {	   
		if (currentUser.containsKey("emailVerified")) {
			if (!currentUser.getBoolean("emailVerified")) {
				reportEmailVerificationNeeded();
				return false;
			}
		}
        return true;
	}

	private void reportEmailVerificationNeeded() {
		String message = "user email has not been verified for " + currentUser.getEmail();
    	Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    	Log.d("DEBUG", message);
	}
	
	private void lauchMainApp() {	
		// Populate the model with the logged in user's data
		// TODO Display progress bar, run outside of UI thread
		try {
			ParsePersistenceManager.refreshFromUser(this, new User(currentUser));
			ModelManagerService.registerInstallation();
		} catch (ParseException e) {
			// TODO Display in UI, add retry option
			Log.e("error", e.getMessage(), e);
		}
		
		// For testing
		someTestCode();
		
		Intent intent = new Intent(LoginActivity.this, ListsViewerActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_from_left);
		finish();
	}
	
	private void someTestCode() {
		// sendTestTodoList();	
	    // setupTestGeofences();
	}

	// This is just for testing purpose. Notice that we are sharing a newly created 
	// Todo list with the current user itself.
	private void sendTestTodoList() {
		TodoList todoList = new TodoList();
		todoList.setName("Damodar's TodoList");
		try {
			todoList.save();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NotificationsSender.shareTodoList(todoList, ParseUser.getCurrentUser());		
	}
	
	private void setupTestGeofences() {
		String address = "1274 Colleen Way, Campbell, CA 95008";
		int radius = 15; // meters
	    // setupTestGeofences(this, currentUser.getObjectId(), address, radius, "MyTodoList1", "MyTodoItem3");
		GeofenceUtils.setupTestGeofences(this, currentUser.getObjectId(), address, radius, 
				Geofence.GEOFENCE_TRANSITION_ENTER, ("Close to " + address), "MyTodoList1", "MyTodoItem3");
		GeofenceUtils.setupTestGeofences(this, currentUser.getObjectId(), address, radius, 
				Geofence.GEOFENCE_TRANSITION_EXIT, ("Leaving " + address), "MyTodoList1", "MyTodoItem3");
	}

	// Send user to ParseLogin
	private void doParseLogin() {
		// Log.d("DEBUG", "In LoginActivity.doParseLogin");
		if (loginInProgress) {
			return;
		}
		loginInProgress = true;
		
		ParseLoginBuilder loginBuilder = new ParseLoginBuilder(LoginActivity.this);
		// loginBuilder.setFacebookLoginEnabled(false).setTwitterLoginEnabled(false);
		loginBuilder.setAppLogo(R.drawable.ic_logo_pencil_smartodo);
		
		startActivityForResult(loginBuilder.build(), LOGIN_REQUEST);
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_from_left);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);	
		if (requestCode == LOGIN_REQUEST) {
			loginInProgress = false;
			if (resultCode == RESULT_OK && checkCurrentUserOK()) {
			   lauchMainApp();
			} else { // exit the login activity
			    finish(); 
			}
		}
	}
	
	 @Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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
}
