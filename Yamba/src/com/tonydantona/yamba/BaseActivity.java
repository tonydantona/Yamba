package com.tonydantona.yamba;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class BaseActivity extends Activity
{    
    YambaApplication yamba;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// get the application object, this is a system call (i.e. we didn't write getApplication())
		// also, we had to tell our manifest that YambaApplication class is the class we want
		// to use to define the application object (we use the android:name attribute in the manifest)
        yamba = (YambaApplication) this.getApplication();
	}
	
///// Menu stuff
	
	@Override
	// this gets called when the user presses the menu button
	// but it gets called only once then it saves it.
	// inflater opens up the xml file and parses it into a menu
	// (it's inflating our xml into a java object using our xml attribs as properties)
	// 1st param is our xml menu which is passed in to 2nd param to create
	// the menu.  Realize the 2nd param is the parameter passed in by reference.
	// So we're inflating our menu and setting this parameter to it.
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu, menu);
		return true; //super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.itemPrefs:
				// launch our intent (for params think of it as from, to)
				startActivity(new Intent(this, PrefsActivity.class) );
				break;
				
			case R.id.itemToggleService:
				if( yamba.isServiceRunning)
				{
					stopService(new Intent(this, UpdaterService.class));
				}
				else
				{
					startService(new Intent(this, UpdaterService.class));
				}
				
				break;
				
			case R.id.itemPurge:
					yamba.statusData.delete();
					Toast.makeText(this, R.string.msgDataPurged, Toast.LENGTH_LONG).show();
				break;
				
			case R.id.itemTimeline:
				// launch our intent (for params think of it as from, to)
				// you can also add flags so you don't get multiples of the same activity stacked up
				startActivity(new Intent(this, TimelineActivity.class)
									.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
									.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
				break;
				
			case R.id.itemStatus:
				startActivity(new Intent(this, StatusActivity.class)
									.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
				break;
		}
		
		return true;
	}

	// we want to toggle the icon for start/stop service
	// this method is called everytime the menu is open
	@Override
	public boolean onMenuOpened(int featureId, Menu menu)
	{
		// get the menu item (remember, the menuItem is simply an object w/ id, title and icon)
		MenuItem toggleItem = menu.findItem(R.id.itemToggleService);
		
		if(yamba.isServiceRunning)
		{
			toggleItem.setTitle(R.string.titleServiceStop);
			
			// realize here it's not a local resource so can't use R....
			toggleItem.setIcon(android.R.drawable.ic_media_pause);
		}
		else
		{
			toggleItem.setTitle(R.string.titleServiceStart);
			
			// realize here it's not a local resource so can't use R....
			toggleItem.setIcon(android.R.drawable.ic_media_play);
		}
		return true;
	}

}






















