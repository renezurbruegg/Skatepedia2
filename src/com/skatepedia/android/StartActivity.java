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
public class StartActivity extends Activity {

    /** Time in Miliseconds until activity starts */
    private final static int SPLASH_TIME_OUT = 2000;
    /** ProgressDialog, to show download Precess */
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
		    startMainActivity(); // start main Activity
		else if (resultCode == Activity.RESULT_CANCELED)
		    Toast.makeText(getApplicationContext(),
			    R.string.ErrorMessageDownladFailed, // Show Error
								// with Toast
			    Toast.LENGTH_LONG).show();
	    }
	}
    };

    /** Shared Preferences with settings(Stream, language etc) */
    private SharedPreferences settingPreferences;
    /** Shared Preferences with Skatetricks */
    private SharedPreferences trickPreferences;

    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.activity_start);

	settingPreferences = getSharedPreferences(
		Functions.SETTING_PREFERENCES, MODE_PRIVATE);

	trickPreferences = getSharedPreferences("skatepedia",
		Context.MODE_PRIVATE);

	if (settingPreferences.getAll().size() != 0)
	    new Handler().postDelayed(new Runnable() {

		/*
		 * Showing splash screen with a timer.
		 */

		@Override
		public void run() {
		    Functions.getLanguage(StartActivity.this);
		    startMainActivity();
		}
	    }, SPLASH_TIME_OUT);
    }

    /**
     * @see android.app.Activity#onPause()
     */
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

    /**
     * @see android.app.Activity#onStart()
     */
    @Override
    public void onStart() {

	super.onStart();

	final Intent intent = new Intent(DownloadService.NOTIFICATION);
	sendBroadcast(intent);

	Log.e("in on start", (settingPreferences.getAll().size() + ""));
	if (settingPreferences.getAll().size() == 0) { // if App is started the
						       // first time -> Setting
						       // pref = 0

	    final AlertDialog.Builder builder = new AlertDialog.Builder(
		    StartActivity.this); // Make new AlertDialog
	    builder.setMessage(R.string.downloadOrStreamDialogBoxMessageText)
		    // To ask if data should be streamed or download
		    .setPositiveButton(
			    R.string.downloadOrStreamDialogBoxAnswerDownload,
			    new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, // if
									    // Download
									    // Clicked
					int id) {

				    final Editor edit = settingPreferences
					    .edit(); // open Editor for settings
				    edit.putBoolean("saveOnPhone", true); // App
									  // should
									  // save
									  // data
									  // on
									  // Phone
				    edit.putBoolean("useExtSave", false);
				    edit.commit(); // save editor
				    pDialog = new ProgressDialog( // createProgressbar
					    StartActivity.this);
				    pDialog.setMessage(getString(R.string.downloadProgressBarDialogDownloadText));
				    pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				    pDialog.setProgress(0);
				    pDialog.setMax(100);
				    pDialog.show(); // show Progressbar

				    startDownloadService(DownloadService.MODE_DOWNLOAD); // Start
											 // DownloadService

				}
			    })
		    .setNegativeButton(
			    R.string.downloadOrStreamDialogBoxAnswerStream,
			    new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, // if
									    // stream
									    // pressed
					int id) {
				    final Editor edit = settingPreferences
					    .edit(); // open Editor for settings
				    edit.putBoolean("saveOnPhone", false); // app
									   // should
									   // not
									   // save
									   // data
									   // on
									   // Phone
				    edit.putBoolean("useExtSave", false);
				    edit.commit();
				    pDialog = ProgressDialog
					    // create ProgressDialog
					    .show(StartActivity.this,
						    "",
						    getString(R.string.downloadProgressBarDialogStreamText),
						    true);
				    startDownloadService(DownloadService.MODE_STREAM); // Start
										       // DownloadActivity
										       // to
										       // stream
										       // (Download
										       // skatetricks.txt)
				}

			    });
	    builder.create().show(); // Show Dialog.

	}

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

    /**
     * Function to start Mainactivity and download all tricks from the
     * Tricklist.txt on the skatepediaserver.
     */
    private void startMainActivity() {
	Functions.log("StartActivity ", "Download Erfolgreich");

	final Skatetrick[] savedTricks = Functions
		.getTricks(StartActivity.this); // Download Tricks from Server
						// and save them in Array.
	if (trickPreferences.getAll().size() != savedTricks.length) {

	    final Editor edit = trickPreferences.edit();
	    edit.clear();
	    for (int i = 0; i < savedTricks.length; i++)
		edit.putString(savedTricks[i].toString(), // Save Tricks as
							  // String in
							  // sharedpreferences.
			Functions.ObjToString(savedTricks[i]));
	    edit.commit();
	    if (pDialog != null)
		pDialog.dismiss(); // Close Progressdialog if stil visible
	}

	
	final Intent it = new Intent(getApplicationContext(),
		MainActivity.class); // Create new Intent to start MainAcitivty.
	startActivity(it); // Start intent
	finish(); // finish StartActivity.

    }
}
