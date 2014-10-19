package com.codepath.smartodo.notifications;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.codepath.smartodo.services.ModelManagerService;
import com.parse.ParsePushBroadcastReceiver;

// See the following for the reason of creating this class:
// http://stackoverflow.com/questions/26154855/exception-when-opening-parse-push-notification
public class ModifiedParsePushBroadcastReceiver extends
		ParsePushBroadcastReceiver {

	public ModifiedParsePushBroadcastReceiver() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onPushOpen(Context context, Intent intent) {
		Log.e("Push", "Clicked");
		Intent i = new Intent(context, ModelManagerService.class);
		i.putExtras(intent.getExtras());
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}

}
