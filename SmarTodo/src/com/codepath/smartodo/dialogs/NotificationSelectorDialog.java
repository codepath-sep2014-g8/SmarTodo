package com.codepath.smartodo.dialogs;

import com.codepath.smartodo.R;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NotificationSelectorDialog extends DialogFragment {

	public static NotificationSelectorDialog newInstance(){
		NotificationSelectorDialog frag = new NotificationSelectorDialog();
        
        return frag;
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_notification_selector, container);
		
		initialize();
		
		return view;
	}
	
	private void initialize(){
		
	}
}
