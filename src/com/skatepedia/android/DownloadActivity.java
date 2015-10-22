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
public class DownloadActivity extends Activity {

    @Override
    protected void onPause() {
	super.onPause();
	unregisterReceiver(receiver);
    }

    /**
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
	super.onResume();
	registerReceiver(receiver, new IntentFilter(
		DownloadService.NOTIFICATION));
    }
    
    
    
    @Override
    public void onStart() {

	super.onStart();

	final Intent intent = new Intent(DownloadService.NOTIFICATION);
	sendBroadcast(intent);


				    pDialog = new ProgressDialog( // createProgressbar
					    DownloadActivity.this);
				    pDialog.setMessage(getString(R.string.downloadProgressBarDialogDownloadText));
				    pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				    pDialog.setProgress(0);
				    pDialog.setMax(100);
				    pDialog.show(); // show Progressbar

				    startDownloadService(DownloadService.MODE_DOWNLOAD); // Start
											 // DownloadService

				
		

	

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

    
    
    private ProgressDialog pDialog = null;
    /**
     * Broadcast Receiver, to Update progressDialog and start Main Activity.
     * Gets started by DownloadService
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
	@Override
	public void onReceive(Context context, Intent intent) {
	    final Bundle bundle = intent.getExtras();
	    if (bundle != null) {
		final int resultCode = bundle.getInt(DownloadService.RESULT); // Get
									      // resultcode
									      // from
									      // DownloadService

		if (resultCode == DownloadService.RESULT_UPDATE_BAR)
		    try {
			final int progress = bundle
				.getInt(DownloadService.UPDATE_VALUE); // Get
								       // progressvalue
								       // from
								       // databundle
			pDialog.setProgress(progress); // Set progressstate.
		    } catch (final Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(),
				"Fehler mit ProgressBar", // --Log Error
				Toast.LENGTH_SHORT);
		    }
		else if (resultCode == RESULT_OK)
		    startFinishActivity(); // start main Activity
		else if (resultCode == Activity.RESULT_CANCELED)
		    Toast.makeText(getApplicationContext(),
			    R.string.ErrorMessageDownladFailed, // Show Error
								// with Toast
			    Toast.LENGTH_LONG).show();
	    }
	}
    };
    
    
    public void startFinishActivity()
    {
	
	Intent i = new Intent(this,FinishActivity.class);
	startActivity(i);
	finish();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_download);

    }

  

}
