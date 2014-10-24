package com.codepath.smartodo.helpers;

import com.codepath.smartodo.R;


public class Utils {

	private static int[] colorsList = new int[6];
	
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
}
