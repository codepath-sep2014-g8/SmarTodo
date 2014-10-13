package com.codepath.smartodo.fragments;

import com.codepath.smartodo.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListPropertiesDialogFragment extends DialogFragment {
	public static ListPropertiesDialogFragment newInstance(String title) {
		ListPropertiesDialogFragment frag = new ListPropertiesDialogFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		frag.setArguments(args);
		return frag;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// Defines the xml file for the fragment
	      View view = inflater.inflate(R.layout.fragment_list_properties, container, false);
	      // Setup handles to view objects here
	      // etFoo = (EditText) v.findViewById(R.id.etFoo);
//	      TextView tvFoo = (TextView) view.findViewById(R.id.textView1);
	      return view;
	}
}
