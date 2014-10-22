package com.codepath.smartodo.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.smartodo.R;
import com.codepath.smartodo.model.TodoItem;
import com.codepath.smartodo.model.TodoList;

public class TodoListAdapter extends ArrayAdapter<TodoList> {

	private static final StrikethroughSpan STRIKE_THROUGH_SPAN = new StrikethroughSpan();
	private int[] colorsList;

	private class ViewHolder {
		TextView tvTitle;
		TextView tvLocation;
		ListView lvToDoItems;
		TodoItemsAdapter adapter;
		List<TodoItem> itemsList;
		
		void init(View convertView){
			this.tvTitle = (TextView) convertView
					.findViewById(R.id.tvTitle);
			tvLocation = (TextView)convertView.findViewById(R.id.tvLocation);
			itemsList = new ArrayList<TodoItem>();
		
			this.lvToDoItems = (ListView)convertView.findViewById(R.id.lvToDoItemsList_item_todo_list);
			
			adapter = new TodoItemsAdapter(getContext(), itemsList);
			lvToDoItems.setAdapter(adapter);
		}
		
		void populateData(TodoList todoList){
			
			tvTitle.setText(todoList.getName());
			tvTitle.append(":");
			
			try {
				
				adapter.clear();
				adapter.addAll(todoList.getAllItems());	
				
				setListViewHeight();
			
			} catch (com.parse.ParseException e) {
				e.printStackTrace();
			}			
		}
		
		private void setListViewHeight() {

		    int totalHeight = 0;

		    for (int i = 0; i < adapter.getCount(); i++) {
		        View listItem = adapter.getView(i, null, lvToDoItems);
		        listItem.measure(0, 0);
		        totalHeight += listItem.getMeasuredHeight();
		    }

		    ViewGroup.LayoutParams params = lvToDoItems.getLayoutParams();
		    params.height = totalHeight + (lvToDoItems.getDividerHeight() * (adapter.getCount() - 1));
		    
		    lvToDoItems.setLayoutParams(params);
		    lvToDoItems.requestLayout();
		}
	}

	public TodoListAdapter(Context context, List<TodoList> objects) {
		super(context, R.layout.item_todo_list, objects);
		initializeColors();
	}
	
	private void initializeColors(){
		
		colorsList = new int[6];
		
		colorsList[0] = getContext().getResources().getColor(R.color.bg_list_green);
		colorsList[1] = getContext().getResources().getColor(R.color.bg_list_blue);
		colorsList[2] = getContext().getResources().getColor(R.color.bg_list_greenish);
		colorsList[3] = getContext().getResources().getColor(R.color.bg_list_gray);
		colorsList[4] = getContext().getResources().getColor(R.color.bg_list_purple);
		colorsList[5] = getContext().getResources().getColor(R.color.bg_list_red);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TodoList todoList = getItem(position);

		ViewHolder viewHolder = null;

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_todo_list, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.init(convertView);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		setViewColor(convertView, position);
		
		viewHolder.populateData(todoList);

		return convertView;
	}
	
	private void setViewColor(View convertView, int position){
		StateListDrawable sld = (StateListDrawable)convertView.getBackground();
        GradientDrawable gd = (GradientDrawable)sld.getCurrent();
        gd.setColor(colorsList[position % 6]);
		
	}

}
