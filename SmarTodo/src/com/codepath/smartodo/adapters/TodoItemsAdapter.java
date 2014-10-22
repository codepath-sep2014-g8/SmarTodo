package com.codepath.smartodo.adapters;

import java.util.List;

import com.codepath.smartodo.R;
import com.codepath.smartodo.model.TodoItem;
import com.parse.ParseException;
import com.parse.SaveCallback;

import android.content.Context;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class TodoItemsAdapter extends ArrayAdapter<TodoItem> {

	private class ViewHolder {
		ImageView ivImage;
		TextView tvItemText;
	}

	public TodoItemsAdapter(Context context, List<TodoItem> objects) {
		super(context, R.layout.item_todo_item, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		final TodoItem todoItem = getItem(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_todo_item, parent, false);

			viewHolder = new ViewHolder();

			viewHolder.tvItemText = (TextView) convertView
					.findViewById(R.id.tvItemText_ftl);
			viewHolder.ivImage = (ImageView) convertView
					.findViewById(R.id.ivCheckbox_ftl);
			
			//?? Refactor later
			viewHolder.ivImage.setClickable(true);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvItemText.setText(todoItem.getText());

		//?? Refactor later
		viewHolder.ivImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				todoItem.setCompleted(true);
				try {
					todoItem.save();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				updateImage(viewHolder, todoItem);
				updateCompletedStatus(viewHolder, todoItem);
			}
		});
		updateImage(viewHolder, todoItem);
		updateCompletedStatus(viewHolder, todoItem);

		return convertView;
	}

	private void updateImage(ViewHolder viewHolder, TodoItem todoItem) {

		if (todoItem.isCompleted()) {
			viewHolder.ivImage.setImageResource(R.drawable.ic_checkbox_full);
		} else {
			viewHolder.ivImage.setImageResource(R.drawable.ic_checkbox_empty_medium);
		}
	}

	private void updateCompletedStatus(ViewHolder viewHolder, TodoItem todoItem) {

		if (todoItem.isCompleted()) {
			viewHolder.tvItemText.setPaintFlags(viewHolder.tvItemText
					.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		} else {
			viewHolder.tvItemText.setPaintFlags(viewHolder.tvItemText
					.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
		}
	}

}
