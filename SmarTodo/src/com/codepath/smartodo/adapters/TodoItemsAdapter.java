package com.codepath.smartodo.adapters;

import java.util.List;

import com.codepath.smartodo.R;
import com.codepath.smartodo.enums.TodoListDisplayMode;
import com.codepath.smartodo.model.TodoItem;
import com.parse.ParseException;
import com.parse.SaveCallback;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class TodoItemsAdapter extends ArrayAdapter<TodoItem> {

	private static final String TAG = TodoItemsAdapter.class.getSimpleName();
	private TodoListDisplayMode mode = TodoListDisplayMode.GRID;
	private TodoItem dummyItem = new TodoItem();

	private class ViewHolder {
		ImageView ivImage;
		EditText etItemText;
		ImageView ivRemove;
		
		void init(View convertView){
			etItemText = (EditText) convertView
					.findViewById(R.id.tvItemText_ftl);
			etItemText.setTypeface(Typeface.createFromAsset(getContext().getAssets(),  "fonts/AmericanTypewriter.ttc"));
			
			ivImage = (ImageView) convertView
					.findViewById(R.id.ivCheckbox_ftl);
			
			ivRemove = (ImageView)convertView.findViewById(R.id.ivRemove_todoitem);
		}
	}

	public TodoItemsAdapter(Context context, List<TodoItem> objects,
			TodoListDisplayMode mode) {
		super(context, R.layout.item_todo_item, objects);
		this.mode = mode;
		if(mode != TodoListDisplayMode.GRID){
			add(dummyItem);
		}
	}
	
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		final TodoItem todoItem = getItem(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_todo_item, parent, false);

			viewHolder = new ViewHolder();

			viewHolder.init(convertView);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.etItemText.setTag(todoItem);
		
		viewHolder.ivImage.setClickable(false);
//		viewHolder.etItemText.setClickable(false);
		viewHolder.ivRemove.setVisibility(View.GONE);
		if (mode == TodoListDisplayMode.CREATE
				|| mode == TodoListDisplayMode.UPDATE) {
			viewHolder.etItemText.setTextColor(getContext().getResources()
					.getColor(R.color.todo_list_item_text));

			viewHolder.ivImage.setClickable(true);
//			viewHolder.etItemText.setClickable(true);
			viewHolder.ivRemove.setVisibility(View.VISIBLE);
			
			// ?? Refactor later
			viewHolder.ivImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					todoItem.setCompleted(!todoItem.isCompleted());
					todoItem.saveEventually();

					updateImage(viewHolder, todoItem);
					updateCompletedStatus(viewHolder, todoItem);
				}
			});
			
			viewHolder.etItemText.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					viewHolder.etItemText.requestFocus();
				}
			});
			
			viewHolder.etItemText.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if(viewHolder.etItemText.isFocused() == false){
						return;
					}
					TodoItem origTodoItem = (TodoItem)viewHolder.etItemText.getTag();
					if(origTodoItem == dummyItem){
						Log.d(TAG, "Creating New Item");
						String str = viewHolder.etItemText.getText().toString();
						TodoItem ti = dummyItem;
						//itemsList.add(getCount() - 1,  ti);
						//notifyDataSetChanged();
						dummyItem = new TodoItem();
						add(dummyItem);
						ti.setText(str);
						viewHolder.etItemText.setTag(ti);
						viewHolder.etItemText.requestFocus();
						
					}
					origTodoItem.setText(viewHolder.etItemText.getText().toString());
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
		}
		
		viewHolder.etItemText.setText(todoItem.getText());
		
		updateImage(viewHolder, todoItem);
		updateCompletedStatus(viewHolder, todoItem);
		
		if(todoItem == dummyItem){
			viewHolder.ivImage.setImageResource(R.drawable.ic_content_new_hint);
			viewHolder.etItemText.setHint(R.string.hint_todo_item);
		}

		return convertView;
	}

	private void updateImage(ViewHolder viewHolder, TodoItem todoItem) {

		if (todoItem.isCompleted()) {
			viewHolder.ivImage.setImageResource(R.drawable.ic_checkbox_full);
		} else {
			viewHolder.ivImage
					.setImageResource(R.drawable.ic_checkbox_empty_medium);
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
