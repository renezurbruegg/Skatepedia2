package com.skatepedia.android.receivers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.skatepedia.android.Functions;
import com.skatepedia.android.R;
import com.skatepedia.android.SettingActivity;
import com.skatepedia.android.StartActivity;
import com.skatepedia.android.UpdateActivity;
import com.skatepedia.android.services.UpdateService;

@SuppressLint("NewApi")
public class UpdateReceiver extends BroadcastReceiver {
    public static String CANCEL = "com.skatepedia.android.UPDATES_CANCEL";
    public static String CHECK = "com.skatepedia.android.CHECK_UPDATES";
    public static String DOWNLOAD = "com.skatepedia.android.UPDATES_DOWNLOAD";
    public static String DOWNLOAD_FINISHED = "com.skatepedia.android.DOWNLOAD_FINISHED";
    private static final int ID = 1;
    private Notification noti;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
	Functions.log("RECEIVER",
		"Habe action erhalten : " + intent.getAction());
	final String action = intent.getAction();
	if (action.equals("test")) {
	    // i.e. 24*60*60*1000= 86,400,000 milliseconds in a day
	    final Long time = new GregorianCalendar().getTimeInMillis() + 10 * 1000;
	    Functions.registerAlarm(context, time);
	}
	if (action.equals(CHECK) && Functions.isNetworkAvailable(context)) {

	    final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
	    final String currentDateandTime = sdf.format(new Date());
	    Log.d("Check", "Check Action erhalten." + currentDateandTime);
	    SharedPreferences sharedPrefs = context.getSharedPreferences(Functions.SETTING_PREFERENCES,
			Context.MODE_PRIVATE);
	    if(sharedPrefs.getBoolean("saveOnPhone", true) && sharedPrefs.getBoolean(SettingActivity.LOOK_FOR_UPDATE,
			true))
	    {
	     Intent downloadIntent = new Intent(context, UpdateService.class);
	     
	      context.startService(downloadIntent); /*- Check for updates
	     * ausgeschaltet
	     */// i.e. 24*60*60*1000= 86,400,000 milliseconds in a day
	    }
	      final Long time = new GregorianCalendar().getTimeInMillis() + 24
		    * 60 * 60 * 1000;
	    Functions.registerAlarm(context, time);
	} else if (action.equals(CANCEL)) {
	    if (Context.NOTIFICATION_SERVICE != null) {
		final String ns = Context.NOTIFICATION_SERVICE;
		final NotificationManager nMgr = (NotificationManager) context
			.getSystemService(ns);
		nMgr.cancel(1);
	    }
	} else if (action.equals(DOWNLOAD_FINISHED)) {
	    if (Context.NOTIFICATION_SERVICE != null) {
		final String ns = Context.NOTIFICATION_SERVICE;
		final NotificationManager nMgr = (NotificationManager) context
			.getSystemService(ns);
		nMgr.cancel(1);

		final Notification mBuilder = new Notification.Builder(context)
			.setContentTitle("Update heruntergeladen")
			.setContentText("Update Erfolgreich heruntergeladen")
			.setSmallIcon(R.drawable.ic_launcher).build();

		// Build notification
		// Actions are just fake

		final NotificationManager notificationManager = (NotificationManager) context
			.getSystemService(Context.NOTIFICATION_SERVICE);
		// hide the notification after its selected
		mBuilder.flags |= Notification.FLAG_AUTO_CANCEL;

		final Intent resultIntent = new Intent(context,
			StartActivity.class);
		final PendingIntent resultPendingIntent = PendingIntent
			.getActivity(context, 0, resultIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.contentIntent = resultPendingIntent;
		notificationManager.notify(ID, mBuilder);
		noti = mBuilder;
	    }
	} else if (action.equals(DOWNLOAD)) {
	    Log.d("UpdateReceiver", "Recev. Download");

	    final String ns = Context.NOTIFICATION_SERVICE;
	    final NotificationManager nMgr = (NotificationManager) context
		    .getSystemService(ns);
	    nMgr.cancel(ID);

	    final Notification mBuilder = new Notification.Builder(context)
		    .setContentTitle("Update für Skatepedia vorhanden")
		    .setContentText("Daten werden Heruntergeladen...")
		    .setProgress(0, 0, true)
		    .setSmallIcon(R.drawable.ic_launcher).build();

	    // Build notification
	    // Actions are just fake

	    final NotificationManager notificationManager = (NotificationManager) context
		    .getSystemService(Context.NOTIFICATION_SERVICE);
	    // hide the notification after its selected
	    mBuilder.flags |= Notification.FLAG_AUTO_CANCEL;

	    notificationManager.notify(ID, mBuilder);
	    noti = mBuilder;

	    final Intent downloadIntent = new Intent(context,
		    UpdateService.class);
	    downloadIntent.putExtra(DOWNLOAD, true);
	    context.startService(downloadIntent);

	}

	Log.d("Receiver", "receiver gestartet");
	final Bundle extras = intent.getExtras();

	if (extras != null) {
	    final int update = extras.getInt(UpdateService.ANSWER_UPDATE);
	    if (update == UpdateService.RESULT_UPDATES)
		showNotification(context);
	    Functions.log("Receiver ", "Update " + update);
	    if (update == Activity.RESULT_OK) {
		Functions.log("Receiver ", "Result ok Erhalten");
		if (noti != null) {
		    final String ns = Context.NOTIFICATION_SERVICE;
		    final NotificationManager nMgr = (NotificationManager) context
			    .getSystemService(ns);
		    nMgr.cancel(ID);
		}

	    }

	}
	if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
	    // i.e. 24*60*60*1000= 86,400,000 milliseconds in a day
	    final Long time = new GregorianCalendar().getTimeInMillis() + 60 * 1000;
	    Functions.registerAlarm(context, time);
	}

	// startmyService(context);
    }

    @SuppressLint("NewApi")
    private void showNotification(Context context) {
	// Yes intent

	Functions.log("UpdateReceiver", "Wanna show Notification");
	final Intent yesReceive = new Intent();
	yesReceive.setAction("com.skatepedia.android.UPDATES_DOWNLOAD");
	final PendingIntent pendingIntentYes = PendingIntent.getBroadcast(
		context, 12345, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);

	try {
	    context.getSharedPreferences("skatepedia", Context.MODE_PRIVATE)
		    .edit().clear().apply();
	    ;
	} catch (final Exception e) {
	}
	// No intent
	final Intent noReceive = new Intent();
	noReceive.setAction("com.skatepedia.android.UPDATES_CANCEL");
	final PendingIntent pendingIntentNo = PendingIntent.getBroadcast(
		context, 12345, noReceive, PendingIntent.FLAG_UPDATE_CURRENT);

	final Notification mBuilder = new NotificationCompat.Builder(context)
		.setContentTitle("Update für Skatepedia vorhanden")
		.setContentText("Soll Update heruntergeladen werden? ")
		.setSmallIcon(R.drawable.ic_launcher)
		.addAction(R.drawable.ic_action_download, "Download",
			pendingIntentYes)


		.setPriority(Notification.PRIORITY_MAX) 
		.addAction(R.drawable.ic_action_cancel, "Später",
		
			pendingIntentNo).build();
	final Intent resultIntent = new Intent(context,
		UpdateActivity.class);
	final PendingIntent resultPendingIntent = PendingIntent
		.getActivity(context, 0, resultIntent,
			PendingIntent.FLAG_UPDATE_CURRENT);
	mBuilder.contentIntent = resultPendingIntent;

	noti = mBuilder;

	Log.d("Receiver", "Notification start");
	// Build notification
	// Actions are just fake

	final NotificationManager notificationManager = (NotificationManager) context
		.getSystemService(Context.NOTIFICATION_SERVICE);
	// hide the notification after its selected
	mBuilder.flags |= Notification.FLAG_AUTO_CANCEL;

	notificationManager.notify(ID, mBuilder);
	noti = mBuilder;
	Log.d("Receiver", "Notification end");

    }

}
