package com.codepath.smartodo.adapters;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Text;

import com.codepath.smartodo.R;
import com.codepath.smartodo.model.ShareUser;
import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.model.User;
import com.google.android.gms.internal.cb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class ShareListAdapter extends ArrayAdapter<ShareUser> {
	
	private class ViewHolder{
		CheckBox cbCheck;
		TextView tvUserName;
		TextView tvUserEmail;
		
		void init(View convertView){
			
			cbCheck = (CheckBox)convertView.findViewById(R.id.cbSelectUser_isu);
			tvUserName = (TextView)convertView.findViewById(R.id.tvUserName_isu);
			tvUserEmail = (TextView)convertView.findViewById(R.id.tvUserEmial_isu);
		}
		
		void populateData(ShareUser user){
			tvUserName.setText(user.getUser().getRealName());
			tvUserEmail.setText(user.getUser().getEmail());
			cbCheck.setChecked(user.isSelected());
		}
	}
	
	public ShareListAdapter(Context context, List<ShareUser> objects) {
		super(context, R.layout.item_share_user, objects);
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
