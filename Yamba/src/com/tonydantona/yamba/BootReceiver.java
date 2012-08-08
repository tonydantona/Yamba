package com.tonydantona.yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver
{
	SharedPreferences prefs;
	
	// this is our static (because runs even if app is not started) system Receiver
	@Override
	public void onReceive(Context context, Intent intent)
	{
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean startOnBoot = prefs.getBoolean("startOnBoot", false);
		
		// should we start the svc at boot
		if(startOnBoot)
		{
			// notice the intent parameter is Not the intent we want to start, it's the one that notified us
			context.startService(new Intent(context, UpdaterService.class));			
		}
		
		Log.d("BootReceiver", "onReceived prefs.startOnBoot: " + startOnBoot);
	}
}
