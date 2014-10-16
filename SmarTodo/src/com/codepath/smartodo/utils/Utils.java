package com.codepath.smartodo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
	
}
