package com.codepath.smartodo.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * A very simplistic test of the Parse DB data model.
 * 
 * @author renyedi
 */
public class ParseDbTest {
	public static void test() {
		Log.i("test", "Running tests");
		final User u = createUser("notsure", "not@sure.com", "Not Sure");
		
		runNext(null, new Runnable() {
			public void run() {
				testAddress(u);
			}
		});
	}

	public static User createUser(String username, String email, String realname) {
		final User u = new User(new ParseUser());
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
		a.setName("testlocation123");
		a.setUser(u.getParseUser());
		
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
		list.setName("testlist" + System.currentTimeMillis());
		list.setNotificationTime(new Date());
		
		User u2 = createUser("notsure1", "not2@sure.com", "Not Sure 2");
		User u3 = createUser("notsure2", "not3@sure.com", "Not Sure 3");
		
		list.addToSharing(Arrays.asList(new User[]{u2, u3}));

		runNext(list, new Runnable() {
			public void run() {
				testSharingRead(list, a);
			}
		});

	}
	
	protected static void testSharingRead(final TodoList list, final Address a) {
		try {
			TodoList readList = TodoList.findTodoListByName(list.getName());
			
			softAssertEquals(list, readList);
			
			List<User> sharedWith = readList.getSharing();
			
			softAssertEquals(2, sharedWith.size());
			
			runNext(list, new Runnable() {
				public void run() {
					testFindUsersAllLike(list, a);
				}
			});
		} catch (ParseException e) {
			fail(e);
		}
	}

	protected static void testFindUsersAllLike(final TodoList list, final Address a) {
		Collection<User> users = User.findAllLike("sure.co");
		softAssertEquals(3, users.size());
		
		users = User.findAllLike("notsu");
		softAssertEquals(3, users.size());
		
		users = User.findAllLike(" Sure ");
		softAssertEquals(3, users.size());
		
		runNext(list, new Runnable() {
			public void run() {
				testTodoItem(list, a);
			}
		});
	}

	protected static void testTodoItem(final TodoList list, Address a) {
		final TodoItem item = new TodoItem();
		item.setAddress(a);
		item.setCompleted(true);
		item.setList(list);
		item.setNotificationTime(new Date());
		item.setText("testitem321");
		
		runNext(item, new Runnable() {
			public void run() {
				testGetAllItems(list, item);
			}
		});
	}

	protected static void testGetAllItems(TodoList list, TodoItem item) {
		try {
			List<TodoItem> items = list.getAllItems();
			
			softAssertEquals(1, items.size());
			softAssertEquals(item, items.get(0));
			
			runNext(null, null); // Cleanup and exit
		} catch (ParseException ex) {
			fail(ex);
		}
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
						} else {
							cleanup();
						}
					}
				}
			});
		} else {
			if(runnable != null) {
				runnable.run();
			} else {
				cleanup();
			}
		}

	}

	private static void cleanup() {
		try {
			deleteRows(TodoItem.class, TodoItem.TEXT_KEY, "testitem*", 1);
			deleteRows(TodoList.class, TodoList.NAME_KEY, "testlist*", 1);
			deleteRows(Address.class, Address.NAME_KEY, "testlocation*", 1);
			deleteRows(ParseUser.class, User.REALNAME_KEY, "notsure*", 3);
		} catch(Throwable th) {
			Log.e("error", "Cleanup failed! Finish it manually at https://parse.com/apps/smartodo/collections", th);
		}
		Log.i("test", "Finished running tests");
	}

	public static <T extends ParseObject> void deleteRows(Class<T> modelClass, String columnName, String valuePattern, int expectedSize) throws ParseException {
		ParseQuery<T> itemQuery = ParseQuery.getQuery(modelClass);
		
		itemQuery.whereMatches(columnName, valuePattern);
		List<T> items = itemQuery.find();
		
		softAssertEquals(expectedSize, items.size());
		
		for(T item : items) {
			item.delete();
		}
	}

	private static void softAssertEquals(Object i, Object size) {
		if(!i.equals(size)) {
			Log.w("assertion", i + " != " + size + " in " + new Exception().getStackTrace()[2]);
		}
	}

	private static void fail(Throwable th) {
		Log.e("test", th.getMessage(), th);
	}
}
