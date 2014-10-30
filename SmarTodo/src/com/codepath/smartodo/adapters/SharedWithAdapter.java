package com.codepath.smartodo.adapters;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.smartodo.R;
import com.codepath.smartodo.activities.ShareActivity;
import com.codepath.smartodo.model.ShareUser;
import com.codepath.smartodo.model.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.ParseException;
import com.squareup.picasso.Picasso;

public class SharedWithAdapter extends ArrayAdapter<User> {

	/**
	 * 
	 */
	private boolean readonly;
	private Activity activity;

	public SharedWithAdapter(Activity shareActivity, List<User> objects, boolean readonly) {
		super(shareActivity, R.layout.view_share_user, objects);
		this.activity = shareActivity;
		this.readonly = readonly;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		
		if(convertView != null) {
			v = convertView;
		} else {
			v = activity.getLayoutInflater().inflate(R.layout.view_share_user, null);
		}
		
		final User user = getItem(position);

		TextView tv = (TextView) v.findViewById(R.id.tvSuUserName);

		try {
			tv.setText(user.getRealName());
		} catch (ParseException e) {
			tv.setText(user.getEmail());
		}
		
		try {
			String githubUsername = user.getGithubUsername();
			
			if(githubUsername != null) {
				final ImageView ivUserPhoto = (ImageView)v.findViewById(R.id.ivUserPhoto);
				
				String githubApiUserUrl = "https://api.github.com/users/" + githubUsername;
				AsyncHttpClient client = new AsyncHttpClient();
				client.get(githubApiUserUrl, new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonObj) {
						if(jsonObj != null) {
							try {
								Picasso.with(getContext()).load(jsonObj.getString("avatar_url")).placeholder(R.drawable.ic_users_share).resize(50, 50).into(ivUserPhoto);
							} catch (JSONException e) {
								Log.e("error", e.getMessage(), e);
							}
						}
					}
				});
			}
		} catch (ParseException e) {
			Log.e("error", e.getMessage(), e);
		}

		ImageButton btn = (ImageButton) v.findViewById(R.id.btnSuRemove);

		if(readonly) {
			btn.setVisibility(Button.GONE); // Don't let it take up space
		} else {
			btn.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View btnView) {
					ShareActivity shareActivity = (ShareActivity) activity;
					shareActivity.gvSharedWithAdapter.remove(user);
					
					// Uncheck the user in the list above
					for(int i=0;i<shareActivity.shareListAdapter.getCount();i++) {
						ShareUser su = shareActivity.shareListAdapter.getItem(i);
						if(su.getUser().equals(user)) {
							su.setSelected(false);
							shareActivity.shareListAdapter.notifyDataSetChanged();
							break;
						}
					}
				}
			});
		}
		
		return v;
	}
}