package com.codepath.smartodo.helpers;

import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.codepath.smartodo.R;
import com.parse.ParseUser;


public class Utils {

	private static int[] colorsList = new int[6];
	static public final String NETWORK_UNAVAILABLE_MSG = "Network not available...";
	
	static{
		initializeColors();
	}
	
	private Utils(){
		
	}
	
	public static int getColor(int itemIndex){
		return colorsList[itemIndex];
	}
	
	private static void initializeColors(){
		
		colorsList[0] = R.color.bg_list_greenish;
		colorsList[1] = R.color.bg_list_blue;
		colorsList[2] = R.color.bg_list_gray;
		colorsList[3] = R.color.bg_list_red;
		colorsList[4] = R.color.bg_list_purple;
		colorsList[5] = R.color.bg_list_green;
	} 
	
	
	public static boolean isNullOrEmpty(String str) {
		return (str == null || str.trim().length() == 0);
	}
	
	public static boolean isNullOrEmpty(List list) {
		return (list == null || list.size() == 0);
	}
	
	public static Boolean isNetworkAvailable(Context context) {
		if(context == null) {
			return true; // Yeah, as far as we know
		}
		
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
		
		//return "SmarTodo - " + username;
		return "SmarTodo";
	}
}
