package com.codepath.smartodo.model;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

/**
 * A very simplistic test of the Parse DB data model.
 * 
 * @author renyedi
 */
public class ParseDbTest {
	public static void test() {
		final User u = new User();
		u.setUsername("notsure");
		u.setEmail("not@sure.com");
		u.setPhoneNumber("4084084088");
		u.setRealName("Not Sure");
		u.setPassword("certain");
		
		try {
			u.signUp();
		} catch (ParseException e) {
			fail(e);
		}
		
		runNext(null, new Runnable() {
			public void run() {
				testAddress(u);
			}
		});
	}
	
	private static void testAddress(User u) {
		Address a = new Address();
		a.setLocation(new ParseGeoPoint(-10.22, +30.44));
		a.setName("home");
		a.setUser(u);
		
		runNext(a, new Runnable() {
			public void run() {
				testTodoList();
			}
		});
	}
	
	protected static void testTodoList() {
		// TODO Auto-generated method stub
		
	}

	private static void runNext(ParseObject obj, final Runnable runnable) {
		if(obj != null) {
			obj.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException ex) {
					if(ex != null) {
						fail(ex);
					} else {
						runnable.run();
					}
				}
			});
		} else {
			runnable.run();
		}

	}

	private static void fail(Throwable th) {
		Log.e("test", th.getMessage(), th);
	}
	
	private static void testNext() {

//		runNext(u, new Runnable() { 
//						public void run() {
//							testNext();
//						}
//					});
	}
}
