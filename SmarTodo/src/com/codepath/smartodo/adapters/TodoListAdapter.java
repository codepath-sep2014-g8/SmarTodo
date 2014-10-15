package com.codepath.smartodo.adapters;

import java.util.ArrayList;
import java.util.List;

import com.codepath.smartodo.R;
import com.codepath.smartodo.model.TodoItem;
import com.codepath.smartodo.model.TodoList;

import android.content.Context;
import android.net.ParseException;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.etsy.android.grid.util.*;

public class TodoListAdapter extends ArrayAdapter<TodoList> {

	private static final StrikethroughSpan STRIKE_THROUGH_SPAN = new StrikethroughSpan();

	private class ViewHolder {
		DynamicHeightTextView txtItem;
		TextView txtTitle;
	}

	public TodoListAdapter(Context context, List<TodoList> objects) {
		super(context, R.layout.item_todo_list, objects);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TodoList todoList = getItem(position);

		ViewHolder viewHolder = null;

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_todo_list, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.txtItem = (DynamicHeightTextView) convertView
					.findViewById(R.id.txtItemList);
			viewHolder.txtTitle = (TextView) convertView
					.findViewById(R.id.txtTitle);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

	 //convertView.setBackgroundColor(todoList.getColor);
		//?? Remove later
		if(position % 2 == 0){
		convertView.setBackgroundResource(R.color.blue);
		}
		else{
			convertView.setBackgroundResource(R.color.orange);
		}

		// get all the to do item
		// iterate through them
		// strike the one's which are completed
		try {
			List<TodoItem> todoItems = todoList.getAllItems();
			for (TodoItem item : todoItems) {
				String str = item.getText();
				if (item.isCompleted()) {

					// ??? Check the logic.. does not work properly
					int start = viewHolder.txtItem.getText().toString()
							.length();
					viewHolder.txtItem.append(str + "\r\n");
					Spannable spannable = (Spannable) viewHolder.txtItem
							.getText();
					spannable.setSpan(STRIKE_THROUGH_SPAN, start,
							start + str.length() - 1,
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				} else {
					viewHolder.txtItem.append(str + "\r\n");
				}

			}
		} catch (com.parse.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		viewHolder.txtTitle.setText(todoList.getName());

		return convertView;
	}

}
