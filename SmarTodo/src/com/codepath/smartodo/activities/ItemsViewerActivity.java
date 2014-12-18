package com.codepath.smartodo.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.codepath.smartodo.R;
import com.codepath.smartodo.fragments.TodoListFragment;
import com.codepath.smartodo.helpers.AppConstants;
import com.codepath.smartodo.helpers.Utils;
import com.codepath.smartodo.interfaces.TouchActionsListener;
import com.codepath.smartodo.model.ReminderLocation;
import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.persistence.ParsePersistenceManager;
import com.parse.ParseException;


public class ItemsViewerActivity extends FragmentActivity implements TouchActionsListener {
	
	static final String TAG = ItemsViewerActivity.class.getSimpleName();
	
	// Some dummy addresses for demo. Todo: They should eventually come from some Settings/Preferences
	private static final String HOME_LOCATION_NAME = "Home";
	private static final String HOME_ADDR = "1274 Colleen Way, Campbell, CA 95008";
	private static final String HOME_IMAGE_URL = "someUrl";
	
	private static final String BOFA_MTNVIEW_LOCATION_NAME = "Bank of America, Mtn View";
	private static final String BOFA_MTNVIEW_ADDR = " 444 Castro St, Mountain View, CA 94041";
	private static final String BOFA_MTNVIEW_IMAGE_URL = "someUrl";
	
	private static final String YAHOO_BUILDING_E_LOCATION_NAME = "Yahoo Building E";
	private static final String YAHOO_BUILDING_E_ADDR = "700 First Ave, Sunnyvale, CA 94089";
	private static final String YAHOO_BUILDING_E_IMAGE_URL = "someUrl";
	
	private static final String YAHOO_BUILDING_F_LOCATION_NAME = "Yahoo Building F";
	private static final String YAHOO_BUILDING_F_ADDR = "1350 North Mathilda Avenue, Sunnyvale, CA 94089";
	private static final String YAHOO_BUILDING_F_IMAGE_URL = "someUrl";
	
	private static final String SAFEWAY_STEVENSCREEK_LOCATION_NAME = "Safeway Stevens Creek";
	private static final String SAFEWAY_STEVENSCREEK_ADDR = "5146 Stevens Creek Blvd, San Jose, CA";
	private static final String SAFEWAY_STEVENSCREEK_IMAGE_URL = "someUrl";
	
	private static final String RIGHT_STUFF_LOCATION_NAME = "Gym";
	private static final String RIGHT_STUFF_ADDR = "1730 W Campbell Ave, Campbell, CA 95008";
	private static final String RIGHT_STUFF_IMAGE_URL = "someUrl";
	
	public static final int RESULT_SHARE = 678;
	
	private ImageView ivBack;
	private ImageView ivShare;
	private ImageView ivNotifications;
	private ImageView ivMoreOptions;
	private ImageView ivDelete;
	private ImageView ivLocationReminder;
	
	String objectId = null;
	private int animationStyle = R.style.DialogFromLeftAnimation;
	int colorId = R.color.todo_list_backcolor;
	private TodoList todoList = null;
	private List<ReminderLocation> reminderLocations;

	private TodoListFragment fragmentTodoList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_items_viewer);
		
		initialize();
		
		setupListeners();
	}

	private void initialize(){
		
		initializeTodoList();
		
		if (getIntent().hasExtra(AppConstants.KEY_ANIMATION_STYLE)) {			
			animationStyle = (int) getIntent().getIntExtra(AppConstants.KEY_ANIMATION_STYLE, 0);
		}
		
		if (getIntent().hasExtra(AppConstants.KEY_COLOR_ID)) {			
			colorId = getIntent().getIntExtra(AppConstants.KEY_COLOR_ID, 0);
		}
		
		initializeActionBar();

		fragmentTodoList = TodoListFragment.newInstance(objectId, R.style.DialogFromLeftAnimation, colorId);
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.fragmentContainer, fragmentTodoList);
		transaction.commit();
		
		initializeData();
	}
	
    private void initializeData() {
       reminderLocations = new ArrayList<ReminderLocation>();
       reminderLocations.add(new ReminderLocation(HOME_LOCATION_NAME, HOME_ADDR, HOME_IMAGE_URL, R.drawable.ic_home));
       reminderLocations.add(new ReminderLocation(BOFA_MTNVIEW_LOCATION_NAME, BOFA_MTNVIEW_ADDR, BOFA_MTNVIEW_IMAGE_URL, R.drawable.ic_dollar));
       reminderLocations.add(new ReminderLocation(YAHOO_BUILDING_E_LOCATION_NAME, YAHOO_BUILDING_E_ADDR, YAHOO_BUILDING_E_IMAGE_URL, R.drawable.ic_yahoo_logo));
       reminderLocations.add(new ReminderLocation(YAHOO_BUILDING_F_LOCATION_NAME, YAHOO_BUILDING_F_ADDR, YAHOO_BUILDING_F_IMAGE_URL, R.drawable.ic_yahoo_logo));
       reminderLocations.add(new ReminderLocation(SAFEWAY_STEVENSCREEK_LOCATION_NAME, SAFEWAY_STEVENSCREEK_ADDR, SAFEWAY_STEVENSCREEK_IMAGE_URL, R.drawable.ic_shopping_cart));
       reminderLocations.add(new ReminderLocation(RIGHT_STUFF_LOCATION_NAME, RIGHT_STUFF_ADDR, RIGHT_STUFF_IMAGE_URL, R.drawable.ic_gym));		
	}

    private void initializeTodoList(){
		
	    if (getIntent().hasExtra(AppConstants.OBJECTID_EXTRA)) {			
		    objectId = (String) getIntent().getStringExtra(AppConstants.OBJECTID_EXTRA);
	    }
		
		if(objectId == null || objectId.isEmpty()){	
			todoList = new TodoList();
			return;
		}
		
		try {
			todoList = ParsePersistenceManager.findTodoListByObjectId(this, objectId);
		} catch (ParseException e1) {
			
			Log.d(TAG, "Excpetion while getting the todo list");
			e1.printStackTrace();
			
			todoList = new TodoList();
		}
		
		if(todoList == null){
			todoList = new TodoList();
		}
		
	}
	
	
	private void initializeActionBar(){

        ActionBar actionBar = getActionBar();

        View view = getLayoutInflater().inflate(R.layout.action_bar_grid_view, null);
        
        view.setBackgroundColor(getResources().getColor(colorId));
        
        ivBack = (ImageView)view.findViewById(R.id.ivBackButton_grid_view);
        ivBack.setVisibility(View.VISIBLE);
        
        ivShare = (ImageView)view.findViewById(R.id.ivShare);
        ivShare.setVisibility(View.VISIBLE);
        
        ivNotifications = (ImageView)view.findViewById(R.id.ivNotifications);
//        ivNotifications.setVisibility(View.VISIBLE);
        
        ivMoreOptions = (ImageView)view.findViewById(R.id.ivMoreOptions);
        ivMoreOptions.setVisibility(View.VISIBLE);
        
        ivDelete = (ImageView)view.findViewById(R.id.ivDelete);
        ivDelete.setVisibility(View.GONE);
        
        ivLocationReminder = (ImageView)view.findViewById(R.id.ivLocationReminder);
        ivLocationReminder.setVisibility(View.VISIBLE);
        
        TextView tvTitle_home = (TextView) view.findViewById(R.id.tvTitle_home);
        tvTitle_home.setText(Utils.buildTitleText());
        //tvTitle_home.setTextColor(getResources().getColor(R.color.white));
        
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
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(AppConstants.OBJECTID_EXTRA, objectId);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
        overridePendingTransition (R.anim.slide_in_from_right, R.anim.slide_out_from_left);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == RESULT_SHARE) {
			Log.i("info", "Refreshing the shared view");
			refreshTodoList();
		}
	}
	
	private void refreshTodoList() {
		try {
			todoList = ParsePersistenceManager.findTodoListByObjectId(this, objectId);
			fragmentTodoList.sharedWithListAdapter.clear();
			fragmentTodoList.sharedWithListAdapter.addAll(todoList.getSharing());
		} catch (ParseException e) {
			Log.e("error", "Error refresshing todo item", e);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.items_viewer, menu);
		return true;
	}
	
	private void setupListeners(){
		
		ivMoreOptions.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				PopupMenu popupMenu = new PopupMenu(ItemsViewerActivity.this, ivMoreOptions);
				popupMenu.getMenuInflater().inflate(R.menu.poupup_menu, popupMenu.getMenu());
				
				popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						if(item.getItemId() == R.id.deleteMenu){
							//deleteList();
							showDeleteConfirmationDialog();
						}
						
						if(item.getItemId() == R.id.timeReminderMenu){
							DialogFragment dialog = new TimePickerFragment(todoList);
							if (dialog != null) {
								dialog.show(getSupportFragmentManager(), "timePicker");	
							}
						}
						return true;
					}
				});
				
				popupMenu.show();
			}
		});
		
		ivBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
				
			}
		});
		
		ivShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(ItemsViewerActivity.this, ShareActivity.class);
				intent.putExtra(AppConstants.OBJECTID_EXTRA, objectId);
				intent.putExtra(AppConstants.KEY_COLOR_ID, colorId);
				startActivityForResult(intent, RESULT_SHARE);	
				overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_from_left);
			}
		});
		
		ivDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				try {
					todoList.deleteEventually();
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally{
					onBackPressed();
				}
			}
		});
		
		ivNotifications.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogFragment dialog = new TimePickerFragment(todoList);
				if (dialog != null) {
					dialog.show(getSupportFragmentManager(), "timePicker");	
				}
			}
		});
		
		ivLocationReminder.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LocationDialogFragment dialog = new LocationDialogFragment(ItemsViewerActivity.this, todoList, reminderLocations);
				if (dialog != null) {
					dialog.show(getSupportFragmentManager(), "fragment_notification_selector");
				}
			}
		});
	}
	
	private void showDeleteConfirmationDialog(){
		new AlertDialog.Builder(this)
        .setTitle(getResources().getString(R.string.confirm_title_todo_list_remove))
        .setMessage(
        		getResources().getString(R.string.confirm_todo_list_remove))
        .setIcon(
        		getResources().getDrawable(
                        android.R.drawable.ic_dialog_alert))
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d(TAG, "Deleting todo list " + todoList.getName());
				deleteList();
				
			}
		}) 
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		})
		.show();
		
	}
	
	private void deleteList(){

		try {
			todoList.deleteEventually();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			onBackPressed();
		}
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

