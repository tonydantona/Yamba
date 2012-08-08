package com.tonydantona.yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class TimelineActivity extends BaseActivity
{
	private static final String TAG = StatusData.class.getSimpleName();
	ListView listStatus;
	Cursor cursor;
	SimpleCursorAdapter adapter;
	TimelineReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// inflate the layout (into the memory space)
		setContentView(R.layout.timeline);
		
		// get a reference to the edit view
		listStatus = (ListView)findViewById(R.id.status);
		
		// set up the list
		setupList();
		
	}
	
	/* Our custom binder to bind createdAt column to its view
	 * and change data from timestamp to relative time
	 */
	static final ViewBinder VIEW_BINDER = new ViewBinder()
	{
		// this method will be called for every column in our record, so we need to ignore the ones we don't want
		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex)
		{
			if(cursor.getColumnIndex(StatusData.C_CREATED_AT) != columnIndex)
			{
				// only concerned with createdAt, so ignore
				return false;
			}
			else
			{
				long timestamp = cursor.getLong(columnIndex);
				CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(timestamp);
				((TextView)view).setText(relativeTime);
				return true;
			}
		}
		
	};
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		// register TimelineReceiver
		if( receiver == null)
		{
			receiver = new TimelineReceiver();
		}
		
		registerReceiver(receiver, new IntentFilter(UpdaterService.ACTION_NEW_STATUS));
	}	
	
	@Override
	protected void onPause()
	{
		super.onPause();
		
		// Unregister TimelineReceiver
		unregisterReceiver(receiver);
	}

	private void setupList()
	{
		// get the data
		cursor = yamba.statusData.query();
		startManagingCursor(cursor);
		
		// This is for our last 2 args.  We're mapping from col names to ids on the screen
		String[] from = {StatusData.C_USER, StatusData.C_TEXT, StatusData.C_CREATED_AT};
		int[] to = {R.id.textUser, R.id.textText, R.id.textCreatedAt};
		
		// Set up adapter
		adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, from, to);
		
		// modify our createdAt column
		adapter.setViewBinder(VIEW_BINDER);
		
		// bind the list view to the adapter
		listStatus.setAdapter(adapter);
	}
	
	// TimelineReceiver wakes up when there's a new status
	class TimelineReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context arg0, Intent arg1)
		{
			Log.d(TAG,"onReceive got notified");
			// refresh list when there's a new status
			setupList();
		}
		
	}	
}
