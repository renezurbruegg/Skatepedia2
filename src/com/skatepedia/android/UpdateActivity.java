package com.skatepedia.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.skatepedia.android.services.DownloadService;

/**
 * Activity to start Saktepedia. If started the first time, it if data should be
 * streamed or saved.
 * 
 * @author Rene Zurbrügg
 * @version 1.0
 * 
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class UpdateActivity extends Activity {

    public void download(View v) {
	Intent intent= new Intent(this,DownloadActivity.class);
	startActivity(intent);
	finish();
    }

    public void dismiss(View v) {
	finish();

    }

    public void block(View v) {
	getSharedPreferences(Functions.SETTING_PREFERENCES,
		MODE_PRIVATE).edit().putBoolean(SettingActivity.LOOK_FOR_UPDATE,
		false).apply();

	finish();
	
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_update);

    }

    /**
     * Function to start Downloadservice,
     * 
     * @param mode
     *            int to show if Service should Stream or Download data.
     */
    private void startDownloadService(int mode) {

	final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	final NetworkInfo netInfo = cm.getActiveNetworkInfo(); // Check if
							       // Internet is
							       // avaiable
	if (netInfo != null && netInfo.isConnectedOrConnecting()) {

	    final Intent downloadIntent = new Intent(getApplicationContext(),
		    DownloadService.class); // if yes,
	    downloadIntent.putExtra(DownloadService.MODE, mode); // Start
								 // downloadService
								 // with param
								 // mode
	    startService(downloadIntent);

	} else {
	    Toast.makeText(getApplicationContext(),
		    R.string.ErrorMessageNoInternettConnection, // else make
								// toast, to
								// show that
								// there is no
								// connecton
		    Toast.LENGTH_LONG).show();
	    finish(); // close applicaton
	}
    }

}
