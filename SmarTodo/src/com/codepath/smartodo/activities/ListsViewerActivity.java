package com.codepath.smartodo.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

import com.codepath.smartodo.R;
import com.codepath.smartodo.adapters.TodoListAdapter;
import com.codepath.smartodo.model.TodoItem;
import com.codepath.smartodo.model.TodoList;
import com.codepath.snartodo.helpers.AppConstants;
import com.etsy.android.grid.StaggeredGridView;




public class ListsViewerActivity extends FragmentActivity {
	private StaggeredGridView staggeredGridView;
	private TodoListAdapter adapter;
	private List<TodoList> list;
	private ImageView ivAdd;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_lists_viewer);
    	
//    	Button btnOpenItemsViewer = (Button) findViewById(R.id.btnOpenItemsViewer);
//    	btnOpenItemsViewer.setOnClickListener(new OnClickListener() {
//			@Override public void onClick(View v) {
//				Intent i = new Intent(ListsViewerActivity.this, ItemsViewerActivity.class);
//				startActivity(i);
//			}
//    	});
    	
    	initialize();
    	setupListeners();
    }
    
    

	private void initialize(){
		
		initializeActionBar();
		
    	staggeredGridView = (StaggeredGridView)findViewById(R.id.grid_view);
    	list = new ArrayList<TodoList>();
    	populateTestData();
    	adapter = new TodoListAdapter(getBaseContext(), list);
    	
    	staggeredGridView.setAdapter(adapter);
    }
	
	private void initializeActionBar(){
		ActionBar actionBar = getActionBar();
		View view = getLayoutInflater().inflate(R.layout.action_bar_grid_view, null);
		
		ivAdd = (ImageView)view.findViewById(R.id.ivAdd_todolist);
		
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
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
				String name = todoList.getName();
				System.out.println("name:" + name);
				Intent i = new Intent(ListsViewerActivity.this, ItemsViewerActivity.class);
				i.putExtra(AppConstants.KEY_TODOLIST, name);
				startActivity(i);
			}
    		
		});
		
	}
    
    private void populateTestData(){
    	
    	try{
    	TodoList l = new TodoList();
    	l.setName("List1");
    	
    	TodoItem item = new TodoItem();
    	item.setText("Item 1");
    	item.setList(l);
    	item.saveInBackground();

    	TodoItem item2 = new TodoItem();
    	item2.setText("Item 2");
    	item2.setList(l);
    	item2.save();
    		
    	l.save();
    	
    	TodoList l1 = new TodoList();
    	l1.setName("List2");
    	
    	TodoItem item3 = new TodoItem();
    	item3.setText("Item 4");
    	item3.setList(l1);
    	item3.save();

    	TodoItem item4 = new TodoItem();
    	item4.setText("Item 5");
    	item4.setList(l1);
    	item4.save();
    	
    	
    	TodoItem item5 = new TodoItem();
    	item5.setText("Item 7");
    	item5.setList(l1);
    	item5.save();
    	
    	
    	
    	l1.save();
    	
    	list.add(l);
    	list.add(l1);
    	}
    	catch(Exception ex){
    		
    	}
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
}
