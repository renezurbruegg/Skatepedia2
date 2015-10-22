package com.skatepedia.android;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.location.LocationListener;

// TODO: Auto-generated Javadoc
/**
 * GPSTracker to get Acces to the current Location over network or GPS
 */
public class GPSTracker extends Service implements LocationListener,
	android.location.LocationListener {

    /** The minimum distance to change updates in metters */
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10
								    // metters

    /** he minimum time beetwen updates in milliseconds */
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    /** Flag if Tracker can get the Location. */
    boolean canGetLocation = false;

    /** flag for GPS Status */
    boolean isGPSEnabled = false;

    /** flag for network status */

    boolean isNetworkEnabled = false;

    /** The latitude coordinate. */
    double latitude = 0;

    /** The location. */
    Location location;

    /** Declaring a Location Manager */
    protected LocationManager locationManager;

    /** The longitude coordinate. */
    double longitude = 0;

    /** The context. */
    private final Context mContext;

    /**
     * Instantiates a new GPS tracker.
     *
     * @param context
     *            the current context of the activity to get access to the
     *            location
     */
    public GPSTracker(Context context) {
	mContext = context;
	getLocation();
    }

    /**
     * returns the location.
     *
     * @return the location
     */
    public Location getLocation() {
	try {
	    locationManager = (LocationManager) mContext
		    .getSystemService(LOCATION_SERVICE);

	    // getting GPS status
	    isGPSEnabled = Functions.GPSisEnabled(mContext);
	    // getting network status
	    isNetworkEnabled = Functions.isNetworkAvailable(mContext);
	    if (!isGPSEnabled)
		showSettingsAlert();
	    else if (!isNetworkEnabled) {
		Functions.log("GPs", "kein empfang");
		showSettingsAlert();
	    } else {
		canGetLocation = true;

		// First get location from Network Provider
		if (isNetworkEnabled) {
		    locationManager.requestLocationUpdates(
			    LocationManager.NETWORK_PROVIDER,
			    MIN_TIME_BW_UPDATES,
			    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

		    Log.d("Network", "Network");

		    if (locationManager != null) {
			location = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			updateGPSCoordinates();
		    }
		}

		// if GPS Enabled get lat/long using GPS Services
		if (isGPSEnabled)
		    if (location == null) {
			locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				MIN_TIME_BW_UPDATES,
				MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

			Log.d("GPS Enabled", "GPS Enabled");

			if (locationManager != null) {
			    location = locationManager
				    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
			    updateGPSCoordinates();
			}
		    }
	    }
	} catch (final Exception e) {
	    // e.printStackTrace();
	    Log.e("Error : Location",
		    "Impossible to connect to LocationManager", e);
	}

	return location;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.android.gms.location.LocationListener#onLocationChanged(android
     * .location.Location)
     */
    @Override
    public void onLocationChanged(Location location) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.location.LocationListener#onProviderDisabled(java.lang.String)
     */
    @Override
    public void onProviderDisabled(String provider) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.location.LocationListener#onProviderEnabled(java.lang.String)
     */
    @Override
    public void onProviderEnabled(String provider) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.location.LocationListener#onStatusChanged(java.lang.String,
     * int, android.os.Bundle)
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    /**
     * Show settings alert.
     */
    public void showSettingsAlert() {
	final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
		mContext);

	// Setting Dialog Title
	alertDialog.setTitle("GPS");

	// Setting Dialog Message
	alertDialog
		.setMessage("ihr GPS ist deaktiviert. Bitte aktivieren sie ihr GPS um alle Funktionen zu nutzen.");

	// On Pressing Setting button
	alertDialog.setPositiveButton("GPS Aktivieren",
		new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			final Intent intent = new Intent(
				Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			mContext.startActivity(intent);
		    }
		});

	// On pressing cancel button
	alertDialog.setNegativeButton("abbrechen",
		new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			dialog.cancel();
		    }
		});

	alertDialog.show();
    }

    /**
     * Update gps coordinates.
     */
    public void updateGPSCoordinates() {
	if (location != null) {
	    latitude = location.getLatitude();
	    longitude = location.getLongitude();
	}
    }
}