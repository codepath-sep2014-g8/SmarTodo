package com.codepath.smartodo.model;

import java.util.Arrays;
import java.util.Date;

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
		final User u = createUser("notsure", "not@sure.com", "Not Sure");
		
		runNext(null, new Runnable() {
			public void run() {
				testAddress(u);
			}
		});
	}

	public static User createUser(String username, String email, String realname) {
		final User u = new User();
		u.setUsername(username);
		u.setEmail(email);
		u.setPhoneNumber("4084084088");
		u.setRealName(realname);
		u.setPassword("certain");
		
		try {
			u.signUp();
		} catch (ParseException e) {
			fail(e);
		}
		
		return u;
	}
	
	private static void testAddress(User u) {
		final Address a = new Address();
		a.setLocation(new ParseGeoPoint(-10.22, +30.44));
		a.setName("home");
		a.setUser(u);
		
		runNext(a, new Runnable() {
			public void run() {
				testTodoList(a);
			}
		});
	}
	
	protected static void testTodoList(final Address a) {
		final TodoList list = new TodoList();
		list.setAddress(a);
		list.setCompleted(true);
		list.setName("testlist321");
		list.setNotificationTime(new Date());
		
		User u2 = createUser("notsure1", "not2@sure.com", "Not Sure 2");
		User u3 = createUser("notsure2", "not3@sure.com", "Not Sure 3");
		
		list.addToSharing(Arrays.asList(new User[]{u2, u3}));
		
		runNext(list, new Runnable() {
			public void run() {
				testTodoItem(list, a);
			}
		});
	}

	protected static void testTodoItem(TodoList list, Address a) {
		TodoItem item = new TodoItem();
		item.setAddress(a);
		item.setCompleted(true);
		item.setList(list);
		item.setNotificationTime(new Date());
		item.setText("testitem321");
		
		runNext(item, null);
	}

	private static void runNext(ParseObject obj, final Runnable runnable) {
		if(obj != null) {
			obj.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException ex) {
					if(ex != null) {
						fail(ex);
					} else {
						if(runnable != null) {
							runnable.run();
						}
					}
				}
			});
		} else {
			if(runnable != null) {
				runnable.run();
			}
		}

	}

	private static void fail(Throwable th) {
		Log.e("test", th.getMessage(), th);
	}
}
