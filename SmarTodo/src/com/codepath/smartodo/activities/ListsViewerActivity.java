package com.codepath.smartodo.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.codepath.adapters.TodoListAdapter;
import com.codepath.smartodo.R;
import com.codepath.smartodo.model.TodoItem;
import com.codepath.smartodo.model.TodoList;
import com.etsy.android.grid.StaggeredGridView;




public class ListsViewerActivity extends FragmentActivity {
	private StaggeredGridView staggeredGridView;
	private TodoListAdapter adapter;
	private List<TodoList> list;
	

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
    }
    
    private void initialize(){
    	staggeredGridView = (StaggeredGridView)findViewById(R.id.grid_view);
    	list = new ArrayList<TodoList>();
    	populateTestData();
    	adapter = new TodoListAdapter(getBaseContext(), list);
    	
    	staggeredGridView.setAdapter(adapter);
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
