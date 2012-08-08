package com.tonydantona.yamba;

import winterwell.jtwitter.Twitter;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

// this class is a singleton - a place to put common data that can
// be easily shared throughout the app
// app is created ONCE whenever anything that uses it is needed
public class YambaApplication extends Application implements OnSharedPreferenceChangeListener
{
	SharedPreferences prefs;
	private Twitter twitter = null;
	StatusData statusData;
	boolean isServiceRunning = false;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		// need to listen for changes to the prefs
		prefs.registerOnSharedPreferenceChangeListener(this);
		
		statusData = new StatusData(this);
	}
	
	public synchronized Twitter getTwitter()
	{
		// create twitter object
		if( twitter == null )
		{
			String username = prefs.getString("username", ""); // "yamba"
			String password = prefs.getString("password", ""); // "dalvik"
			String server = prefs.getString("server", "");	   // "http://yamba.marakana.com/api"
			
			twitter = new Twitter(username, password);
			twitter.setAPIRootUrl(server);
		}
		
		return twitter;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		// invalidate the twitter object - i.e. force app to read prefs again if needed
		twitter = null;
	}

	@Override
	public void onTerminate()
	{
		super.onTerminate();
		
		// good idea to clean up anything we new'ed in the onCreate() here
		statusData.close();
	}
	
	
}
