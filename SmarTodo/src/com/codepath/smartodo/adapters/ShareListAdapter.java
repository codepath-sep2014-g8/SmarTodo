package com.codepath.smartodo.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.smartodo.R;
import com.codepath.smartodo.activities.ShareActivity;
import com.codepath.smartodo.model.ShareUser;
import com.codepath.smartodo.model.User;
import com.parse.ParseException;


public class ShareListAdapter extends ArrayAdapter<ShareUser> {
	
	private ShareActivity shareActivity;

	private class ViewHolder{
		View view;
		
		CheckBox cbCheck;
		TextView tvUserName;
		TextView tvUserEmail;
		
		void init(View convertView){
			this.view = convertView;
			cbCheck = (CheckBox)convertView.findViewById(R.id.cbSelectUser_isu);
			tvUserName = (TextView)convertView.findViewById(R.id.tvUserName_isu);
			tvUserEmail = (TextView)convertView.findViewById(R.id.tvUserEmial_isu);
		}
		
		void populateData(ShareUser user){
			try {
				tvUserName.setText(user.getUser().getRealName());
			} catch (ParseException e) {
				Log.e("error", e.getMessage(), e);
			}
			tvUserEmail.setText(user.getUser().getEmail());
			cbCheck.setChecked(user.isSelected());
			
			shareActivity.selectUser(user.getUser(), user.isSelected());
			
			SharedWithAdapter.loadImageFromGithub(view, user.getUser(), R.id.ivUserForShare);
		}
	}
	
	public ShareListAdapter(Context context, List<ShareUser> objects) {
		super(context, R.layout.item_share_user, objects);
		shareActivity = (ShareActivity) context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder;
		final ShareUser user = getItem(position);
		
		if(convertView == null){
			
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_share_user, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.init(convertView);
			
			convertView.setTag(viewHolder);
			
		}
		else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		viewHolder.cbCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				user.setSelected(isChecked);
				shareActivity.selectUser(user.getUser(), isChecked);
			}
		});
		
		viewHolder.populateData(user);
		return convertView;
	}
	
	public List<User> getSelectedUsers(){
		List<User> list = new ArrayList<User>();
		
		for(int i = 0; i < getCount(); i ++){
			ShareUser user = getItem(i);
			if(user.isSelected()){
				list.add(user.getUser());
			}
		}
		
		return list;
	}
}
