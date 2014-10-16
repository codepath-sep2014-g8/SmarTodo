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
import android.widget.ImageView;
import android.widget.TextView;

public class TodoItemsAdapter extends ArrayAdapter<TodoItem> {

	private class ViewHolder {
		ImageView ivImage;
		TextView tvtemText;
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

			viewHolder.tvtemText = (TextView) convertView
					.findViewById(R.id.tvItemText_ftl);
			viewHolder.ivImage = (ImageView) convertView
					.findViewById(R.id.ivCheckbox_ftl);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvtemText.setText(todoItem.getText());

		updateImage(viewHolder, todoItem);
		updateCompletedStatus(viewHolder, todoItem);
		
		viewHolder.ivImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				todoItem.setCompleted(!todoItem.isCompleted());
				todoItem.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException arg0) {
						updateImage(viewHolder, todoItem);
						// Show saving status somewhere
						updateCompletedStatus(viewHolder, todoItem);
					}
				});
			}

		});

		return convertView;
	}

	private void updateImage(ViewHolder viewHolder, TodoItem todoItem) {

		if (todoItem.isCompleted()) {
			viewHolder.ivImage.setImageResource(R.drawable.ic_checkbox_full);
		} else {

			if (todoItem.getText() == null || todoItem.getText().isEmpty()) {
				viewHolder.ivImage
						.setImageResource(R.drawable.ic_content_new_hint);
			} else {
				viewHolder.ivImage
						.setImageResource(R.drawable.ic_checkbox_empty);
			}
		}
	}

	private void updateCompletedStatus(ViewHolder viewHolder, TodoItem todoItem) {

		if (todoItem.isCompleted()) {
			viewHolder.tvtemText.setPaintFlags(viewHolder.tvtemText
					.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		} else {
			viewHolder.tvtemText.setPaintFlags(viewHolder.tvtemText
					.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
		}
	}

}
