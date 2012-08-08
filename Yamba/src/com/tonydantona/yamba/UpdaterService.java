package com.tonydantona.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter.Status;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service
{
	private static final String TAG = UpdaterService.class.getSimpleName();
	public static final String ACTION_NEW_STATUS = "Yamba.NewStatus";
	private Updater updater;
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		
		updater = new Updater();
		
		Log.d(TAG, "onCreate 'd");
	}

	@Override
	public synchronized void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);
		
		// start updater if it's not already running
		if(!updater.yamba.isServiceRunning)
		{
			// this start will call the run() method of the thread
			updater.start();
			updater.yamba.isServiceRunning = true;
		}
		
		Log.d(TAG, "onStart'd");
	}
	
	@Override
	public synchronized void onDestroy()
	{
		super.onDestroy();
		
		if(updater.yamba.isServiceRunning)
		{
			updater.interrupt();
		}
		
		updater = null;
		
		Log.d(TAG, "onDestroy'd");
	}
	
	//// Updater Thread
	class Updater extends Thread
	{
		static final long DELAY = 30000;
		YambaApplication yamba;
		
		// name the thread - used for tracing
		public Updater()
		{
			super("Updater");
			yamba =  ((YambaApplication) getApplication());
		}
		
		@Override
		// this method is called when the start() method is called from an instance of Updater
		public void run()
		{
			boolean hasNewStatuses = false;
			
			// so the idea is while the thread is running, we'll execute the try continously, i.e. do something
			// then sleep for DELAY, then wake and do something then sleep...
			// when the thread is interrupted an exception is thrown and we catch it and set isRunning to false
			while (yamba.isServiceRunning)
			{
				try
				{
					Log.d(TAG, "updater running");
					
					// get friends status (the Status type comes from JTwitter library)
					List<Status> statuses = yamba.getTwitter().getFriendsTimeline();
					
					
					// realize for values.put we're using our StatusData class for constants (not properties)
					for(Status status: statuses)
					{
						// Insert data
						if( yamba.statusData.insert(status) > 0)
						{
							// we have a valid insert
							hasNewStatuses = true;
						}
						
						Log.d(TAG, String.format("%s: %s", status.user.name, status.text));
					}
					
					// broadcast if there are new statuses
					if( hasNewStatuses )
					{
						Log.d(TAG, "Ready to broadcast");
						// any receiver registered (i.e. listening) for this Intent (ACTION_NEW_STATUS) will receive this broadcast
						sendBroadcast(new Intent(ACTION_NEW_STATUS));
					}
					
					// sleep
					Thread.sleep(DELAY);
				}
				catch (InterruptedException e)
				{
					// we were sleeping but somebody woke us up
					// so I guess calling interrupt() as we do in onDestroy() cause an exception in our try
					yamba.isServiceRunning = false;
				}
			} // while
		}
		
		public boolean isRunning()
		{
			return yamba.isServiceRunning;
		}
	}
	
}
