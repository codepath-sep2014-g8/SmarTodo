package com.codepath.smartodo.notifications;

import org.json.JSONException;
import org.json.JSONObject;

import com.codepath.smartodo.model.TodoList;
import com.codepath.smartodo.services.ModelManagerService;
import com.parse.ParseException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationsReciever extends BroadcastReceiver {

	private static final String TAG = "SmarTodoNotificationsReciever";

	public NotificationsReciever() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			if (intent == null) {
				Log.d(TAG, "Receiver intent null");
			} else {
				String action = intent.getAction();
				Log.d(TAG, "got action " + action);
				if (action.equals(NotificationsSender.ACTION_SHARE_TODOLIST_VALUE)) {
					JSONObject data = new JSONObject(intent.getExtras().getString("com.parse.Data"));
					
					Log.d(TAG, "Received: " + data.toString());
					
					String todoListName = data.getString(NotificationsSender.SHARING_LISTNAME_KEY);
					String todoListId = data.getString(NotificationsSender.TODOLIST_ID_KEY);
					String sharedByUserName = data.getString(NotificationsSender.SHAREDBY_USER_KEY);
					String sharedWithUserName = data.getString(NotificationsSender.SHAREDWITH_USER_KEY);
					String sharedByObjectId = data.getString(NotificationsSender.SHAREDBY_OBJECTID_KEY);
					String sharedWithObjectId = data.getString(NotificationsSender.SHAREDWITH_OBJECTID_KEY);

					// Todo: Take the next action from here
					ModelManagerService.getInstance().displayNotification("List " + todoListName + " was shared with you", "by " + sharedByUserName);
					
					try {
						TodoList sharedTodoList = TodoList.findTodoListByName(context, todoListName);
						ModelManagerService.processListNotifications(sharedTodoList);
						ModelManagerService.getLists().add(sharedTodoList);
					} catch (ParseException e) {
						Log.e("error", e.getMessage(), e);
					}
				}
			}
		} catch (JSONException e) {
			Log.d(TAG, "JSONException: " + e.getMessage());
		}
	}
}
