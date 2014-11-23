package com.codepath.smartodo.activities;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.smartodo.R;
import com.codepath.smartodo.model.ReminderLocation;
import com.squareup.picasso.Picasso;

class ReminderLocationsAdapter extends ArrayAdapter<ReminderLocation> {

	public ReminderLocationsAdapter(Context context, List<ReminderLocation> reminderLocations) {
		super(context, R.layout.todo_location, reminderLocations);
		// TODO Auto-generated constructor stub
	}
	
	// View lookup cache
	private class ViewHolder {
		ImageView ivLocationImage;
		TextView tvLocName;		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item for this position
		ReminderLocation reminderLocation = getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		ViewHolder viewHolder; // view lookup cache stored in tag
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.todo_location, parent, false);
			viewHolder.ivLocationImage = (ImageView) convertView.findViewById(R.id.ivLocationImage);
			viewHolder.tvLocName = (TextView) convertView.findViewById(R.id.tvLocName);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			// reset the image from the recycled view
			viewHolder.ivLocationImage.setImageResource(0);
			viewHolder.tvLocName.setText("");
		}
		// Remotely download the image data in the background (with Picasso)
		Picasso.with(getContext()).load(reminderLocation.getImageResourceId()).placeholder(R.drawable.ic_launcher).into(viewHolder.ivLocationImage);
		viewHolder.tvLocName.setText(reminderLocation.getName());
		
		// Return the completed view to be displayed
		return convertView;
	}
}