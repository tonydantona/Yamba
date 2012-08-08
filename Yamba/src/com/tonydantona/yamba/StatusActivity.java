package com.tonydantona.yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StatusActivity extends BaseActivity implements OnClickListener, LocationListener 
{
    EditText editStatus;
    Button buttonUpdate;
    
    ProgressDialog postingDialog;
    
    LocationManager locationManager;
    Location location;
    String provider = LocationManager.GPS_PROVIDER;
    
    static final int DIALOG_ID = 119;
//    private static final long LOCATION_MIN_TIME = 3600000; // one hour
//    private static final long LOCATION_MIN_DISTANCE = 1000; // one kilometer
    
	private static final String TAG = StatusData.class.getSimpleName();
    
	/** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {    	
        super.onCreate(savedInstanceState);
		
        // intialize the churning progress symbol in the title bar while posting to twitter to false
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        setContentView(R.layout.status);
        
        // turn tracing on
        //Debug.startMethodTracing("Yamba.trace");
        
		// inflate our xml to java objects
        editStatus = (EditText) findViewById(R.id.editStatus);
        buttonUpdate = (Button)findViewById(R.id.buttonUpdate);
        
        buttonUpdate.setOnClickListener(this); 
        
        setProgressBarIndeterminateVisibility(false);
    }

    // location stuff 
    @Override
    protected void onResume()
    {
    	super.onResume();
    	
    	locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
    	
    	if( locationManager != null)
    	{
            Log.d(TAG, "In onResume - locationManager not null");
    		location = locationManager.getLastKnownLocation(provider);

    		// register with the location sensor
    		
    		/* Seems like the following line is causing a crash with the logcat message:
    		 * android removing dead content provider settings
    		 */
    		
//    		locationManager.requestLocationUpdates(provider, LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, this);
    		
    		if( location != null )
    		{ 
				Log.d(TAG, "Lat: " + location.getLatitude());
    		}
    		else
    		{
    			Log.d(TAG, "In onResume - location = null");
    		}
    	}
    	
    }

	@Override
	protected void onPause()
	{
		super.onPause();
		
        Log.d(TAG, "In onPause");
        
//		if (locationManager != null)
//		{
//			locationManager.removeUpdates(this);
//		}
	}
	
	@Override
	public void onLocationChanged(Location location)
	{
    	Log.d(TAG, "In onLocationChanged");
		//		this.location = location;
	}

	@Override
	public void onProviderDisabled(String provider)
	{
    	Log.d(TAG, "In onProviderDisabled");
//		if( this.provider.equals(provider))
//		{
//			locationManager.removeUpdates(this);
//		}
	}

	@Override
	public void onProviderEnabled(String provider)
	{
    	Log.d(TAG, "In onProviderEnabled");
//		if( this.provider.equals(provider))
//		{
//			locationManager.requestLocationUpdates(this.provider, LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, this);
//		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
    	Log.d(TAG, "In onStatusChanged");
	}

	



	@Override
	protected void onStop()
	{
		super.onStop();
		
		// turn tracing off
		//Debug.stopMethodTracing();
	}

	/////// Button stuff
	
	@Override
	public void onClick(View v)
	{
		String status = editStatus.getText().toString();
		
        // show the progress dialog box.  this will generate a callback to onCreateDialog(int d)
		showDialog(DIALOG_ID);
		
		// show twirling progress circle in the title bar.  this call does not need a callback
		setProgressBarIndeterminateVisibility(true);

		// notice here, even though we generically created the class (private class PostToTwitter extends AsyncTask<String, String, String>)
		// w/ 3 params we only pass in 1.
		// the others are created internally in the class (in our case we're not using the 2nd param)
		new PostToTwitter().execute(status);
		
		Log.d("StatusActivity", "onClick'd with status: " + status);
	}	

	///// Dialog stuff
	@Override
	protected Dialog onCreateDialog(int id)
	{
       switch (id) 
       {
           case DIALOG_ID: 
           {
               ProgressDialog dialog = new ProgressDialog(this);
               dialog.setTitle("Posting to twitter");
               dialog.setMessage("Please wait while posting...");
               dialog.setIndeterminate(true);
               dialog.setCancelable(true);
               return dialog;
           }
       }
   
       return null;
	}
	

	//// Posting to Twitter stuff
	private class PostToTwitter extends AsyncTask<String, String, String>
	{
		@Override
		// this is the work that will run on a separate thread
		protected String doInBackground(String... status)
		{
			String result = null;
			
			try
			{												
				// get (or create if first time) the twitter object from the application object
				Twitter twitter = yamba.getTwitter();

				// check if we have the location
//				if( location != null)
//				{
//					double latlong[] = {location.getLatitude(), location.getLongitude()};
//					twitter.setMyLocation(latlong);
//				}
				
						
				// use it to update status online
				twitter.setStatus(status[0]);
				
				// realize here the api is not aware of system rource IDs (i.e. R).  So we need to look it up.
				// normally we could use 'this' as context, but we're in another class here
				result = StatusActivity.this.getString(R.string.msgStatusUpdatedSuccessfully);
			}
			catch (TwitterException e)
			{
				e.printStackTrace();
				result = StatusActivity.this.getString(R.string.msgStatusUpdatedFailed);
			}
			
			return result;
		}
		
		@Override
		// this work will be done back on the UI (the main thread)
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			
			// turn off our progress displays
			// the dialog one
			dismissDialog(DIALOG_ID);
			// the one in the title bar
            setProgressBarIndeterminateVisibility(false);

			Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
		}		
	}
}









