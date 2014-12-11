package com.codepath.smartodo.model;

import android.content.Context;

import com.codepath.smartodo.helpers.Utils;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class LocalParseQuery {
	public static <T extends ParseObject> ParseQuery<T> getQuery(Class<T> clazz, Context context) {
		ParseQuery<T> query = ParseQuery.getQuery(clazz);
		
		if(!Utils.isNetworkAvailable(context)) {
			query.fromLocalDatastore();
		}
		
		return query;
	}
}
