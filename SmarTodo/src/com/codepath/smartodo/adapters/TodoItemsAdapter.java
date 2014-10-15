package com.codepath.smartodo.adapters;

import java.util.List;

import com.codepath.smartodo.R;
import com.codepath.smartodo.model.TodoItem;
import com.codepath.smartodo.model.TodoList;
import com.parse.ParseException;
import com.parse.SaveCallback;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class TodoItemsAdapter extends ArrayAdapter<TodoItem> {

	private class ViewHolder {
		CheckBox cbItemText;
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
			viewHolder.cbItemText = (CheckBox) convertView
					.findViewById(R.id.cbTodoItemText);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.cbItemText.setText(todoItem.getText());
		viewHolder.cbItemText.setChecked(todoItem.isCompleted());

		updateCompletedStatus(viewHolder, todoItem);

		viewHolder.cbItemText
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						todoItem.setCompleted(isChecked);

						todoItem.saveInBackground(new SaveCallback() {

							@Override
							public void done(ParseException arg0) {
								// Show saving status somewhere
								updateCompletedStatus(viewHolder, todoItem);
							}
						});
					}
				});

		return convertView;
	}

	private void updateCompletedStatus(ViewHolder viewHolder, TodoItem todoItem) {

		viewHolder.cbItemText.setPaintFlags(viewHolder.cbItemText
				.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
		if (todoItem.isCompleted()) {
			viewHolder.cbItemText.setPaintFlags(viewHolder.cbItemText
					.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		}

	}

}
