package com.tonydantona.yamba;

import android.os.Bundle;
import android.preference.PreferenceActivity;

// this is how the prefs menu gets loaded
// the when comes from firing up an intent
// if it were the main, that intent would be listed in the manifest

public class PrefsActivity extends PreferenceActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// in our main activity we used setContent at this point, but here we making a prefs menu
		addPreferencesFromResource(R.xml.prefs);
	}	
}
