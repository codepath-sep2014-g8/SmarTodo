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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

public class TodoItemsAdapter extends ArrayAdapter<TodoItem> {

	private class ViewHolder {
		ImageView ivImage;
		EditText etItemText;
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

			viewHolder.etItemText = (EditText) convertView
					.findViewById(R.id.etItemText_ftl);
			viewHolder.ivImage = (ImageView) convertView
					.findViewById(R.id.ivCheckbox_ftl);
			
			/*
			 * 
			 * 
			 * Code below is temporary move to appropriate place
			 * 
			 * 
			 */
			
			viewHolder.etItemText.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					String text = viewHolder.etItemText.getText().toString();
					todoItem.setText(text);
					if(text == null || text.isEmpty()){
						//Hide if any addition dummy was added
					}
					else{
						//Show a dummy one
					}
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					
				}
			});
			
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

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.etItemText.setText(todoItem.getText());

		updateImage(viewHolder, todoItem);
		updateCompletedStatus(viewHolder, todoItem);
		
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
			viewHolder.etItemText.setPaintFlags(viewHolder.etItemText
					.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		} else {
			viewHolder.etItemText.setPaintFlags(viewHolder.etItemText
					.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
		}
	}

}
