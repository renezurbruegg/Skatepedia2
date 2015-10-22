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

public class FinishActivity extends Activity {

   
    public void start(View v)
    {
	Intent i =new Intent(this, StartActivity.class);
	startActivity(i);
	finish();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_finish);

    }

  

}
