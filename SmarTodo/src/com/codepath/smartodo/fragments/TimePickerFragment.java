package com.codepath.smartodo.fragments;

import java.util.Calendar;
import java.util.Date;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.codepath.smartodo.model.TodoList;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {	
	private final TodoListFragment todoListFragment;
	private TodoList todoList;
	final Calendar c = Calendar.getInstance();

	public TimePickerFragment(TodoListFragment todoListFragment, TodoList todoList) {
		super();
		this.todoListFragment = todoListFragment;
		this.todoList = todoList;	
		initCurrentDate();
	}
	
	private void initCurrentDate() {
		Date notificationTime = todoList.getNotificationTime();
		if (notificationTime == null) {
			notificationTime = new Date();
		}
		c.setTime(notificationTime);		
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {		
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.MINUTE, minute);
		todoList.setNotificationTime(c.getTime());	
		this.todoListFragment.tvReminder.setText(this.todoListFragment.getReminderDisplay()); // refresh
		// Save the todoList now?
	};
}