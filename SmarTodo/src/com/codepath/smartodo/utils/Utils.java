package com.codepath.smartodo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.parse.ParseUser;

public class Utils {
	
	static public final String NETWORK_UNAVAILABLE_MSG = "Network not available...";
	
	public static boolean isNullOrEmpty(String str) {
		return (str == null || str.trim().length() == 0);
	}
	
	public static Boolean isNetworkAvailable(Context context) {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

	public static String buildTitleText() {
		String username = ParseUser.getCurrentUser().getUsername();
		int idx = username.indexOf('@');

		if(idx != -1) {
			username = username.substring(0, idx + 2);
		}
		
		return "SmarTodo - " + username;
	}
	
}
