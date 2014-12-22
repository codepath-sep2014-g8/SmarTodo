package com.codepath.smartodo.persistence;

import android.content.Context;

import com.codepath.smartodo.helpers.Utils;
import com.codepath.smartodo.persistence.PersistenceManager.ACCESS_LOCATION;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * A class to provide an appropriate ParseQuery object depending upon the desired access location
 * and network availability.
 * 
 * @author Damodar
 *
 */
public class ParseQueryFactory {
	
	public static <T extends ParseObject> ParseQuery<T> getQuery(Class<T> clazz, Context context, ACCESS_LOCATION accessLocation) {
		ParseQuery<T> query = ParseQuery.getQuery(clazz);  // default is for cloud
		if (accessLocation == ACCESS_LOCATION.CLOUD) {
			return query;
		} else if (accessLocation == ACCESS_LOCATION.CLOUD_ELSE_LOCAL) {
			if(!Utils.isNetworkAvailable(context)) { 
				query.fromLocalDatastore();
			}
		} else if (accessLocation == ACCESS_LOCATION.LOCAL) {
			query.fromLocalDatastore();
			
		}		
		return query;
	}
}
