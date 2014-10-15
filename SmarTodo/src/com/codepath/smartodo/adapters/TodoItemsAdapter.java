package com.codepath.smartodo.adapters;

import java.util.List;

import com.codepath.smartodo.R;
import com.codepath.smartodo.model.TodoItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TodoItemsAdapter extends ArrayAdapter<TodoItem> {

	private class ViewHolder{
		TextView tvItemText;
	}
	public TodoItemsAdapter(Context context, List<TodoItem> objects) {
		super(context, R.layout.item_todo_item, objects);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		TodoItem todoItem = getItem(position);
		
		if(convertView == null){
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo_item, parent, false);
			
			viewHolder = new ViewHolder();
			viewHolder.tvItemText = (TextView)convertView.findViewById(R.id.tvTodoItemText);
			convertView.setTag(viewHolder);
		}
		else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		viewHolder.tvItemText.setText(todoItem.getText());
		
		return convertView;
	}
	
	
	
	

}
