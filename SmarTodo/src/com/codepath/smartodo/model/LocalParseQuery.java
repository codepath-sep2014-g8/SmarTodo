package com.codepath.smartodo.model;

import com.parse.ParseObject;
import com.parse.ParseQuery;

public class LocalParseQuery {
	public static <T extends ParseObject> ParseQuery<T> getQuery(Class<T> clazz) {
		ParseQuery<T> query = ParseQuery.getQuery(clazz);
		query.fromLocalDatastore();
		return query;
	}
}
