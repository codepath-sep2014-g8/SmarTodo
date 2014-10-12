package com.codepath.smartodo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.smartodo.R;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

public class LoginActivity extends Activity {
	public static int LOGIN_REQUEST = 0;
	private Button btnLogin;
	private ParseUser currentUser;
	boolean loginInProgress = false;
	// Set it to true if we want email verifications to happen.
	// Setting the following variable to false for testing with existing accounts
	// whose emails might not have been verified.
	boolean checkEmailVerification = false;   

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// Log.d("DEBUG", "In LoginActivity.onCreate");
		
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				doParseLogin();					
			}
		});
				
		if (checkCurrentUserOK()) {
			// Send the logged in user to our main class
			startListsViewerActivity();
		} else {		
			btnLogin.setVisibility(View.VISIBLE);		
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
        	reportEmailVerificationNeeded();
        	return false;
        }
        if (!currentUser.getBoolean("emailVerified")) {
        	reportEmailVerificationNeeded();
        	return false;
        }
        return true;
	}

	private void reportEmailVerificationNeeded() {
		String message = "user email has not been verified for " + currentUser.getEmail();
    	Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    	Log.d("DEBUG", message);
	}

	private void startListsViewerActivity() {		
		// Log.d("DEBUG", "In LoginActivity.startListsViewerActivity");		
		Intent intent = new Intent(LoginActivity.this,
				ListsViewerActivity.class);
		startActivity(intent);
		finish();
	}
	
	// Send user to ParseLogin
	private void doParseLogin() {
		// Log.d("DEBUG", "In LoginActivity.doParseLogin");
		if (loginInProgress) {
			return;
		}
		loginInProgress = true;
		ParseLoginBuilder loginBuilder = new ParseLoginBuilder(
				LoginActivity.this);
		startActivityForResult(loginBuilder.build(), LOGIN_REQUEST);	
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);	
		if (requestCode == LOGIN_REQUEST) {
			loginInProgress = false;
			if (resultCode == RESULT_OK && checkCurrentUserOK()) {
			   startListsViewerActivity();
			} else { // stay in the login screen
			    // finish(); 
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
