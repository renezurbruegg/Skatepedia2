/*
 * 
 */
package com.skatepedia.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import Fragments.MarkerTabAllgemeinFragment;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionItemTarget;
import com.github.amlcurran.showcaseview.targets.PointTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.idunnololz.widgets.AnimatedExpandableListView;
import com.skatepedia.android.adapter.DrawerAdapter;
import com.skatepedia.android.adapter.MapExpListAdapter;
import com.skatepedia.android.markers.ParkMarker;
import com.skatepedia.android.markers.ShopMarker;
import com.skatepedia.android.markers.SkatepediaMarker;
import com.skatepedia.android.markers.SpotMarker;
import com.skatepedia.android.services.ImageUploadService;

/**
 * Creates a Map with Skatespots saved on Phone and server.
 *
 * @author Rene Zurbrügg
 */
@SuppressWarnings("deprecation")
public class MapActivity extends ActionBarActivity {

    /**
     * 
     * Deletes Marker from Server using an AsyncTask and send Marker ID as
     * parameter to phpfile on server.
     * 
     * @author Rene Zurbrügg
     * 
     * 
     *
     */
    private class DeleteMarkerFromServerTask extends
	    AsyncTask<SkatepediaMarker, Void, Void> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
	 */@Override
	/**
	 * @param spM Markers to delete
	 */
	protected Void doInBackground(SkatepediaMarker... spM) {

	    final HttpClient httpclient = new DefaultHttpClient();
	    final HttpPost httppost = new HttpPost(
		    "http://www.skateparkwetzikon.ch/Skatepedia/Map/deleteMarker.php");

	    final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	    try {
		nameValuePairs
			.add(new BasicNameValuePair("ID", spM[0].getID()));
		nameValuePairs.add(new BasicNameValuePair("database",
			DATABASE[spM[0].getType()]));
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		final HttpResponse response = httpclient.execute(httppost);

		Functions.log("response", response + "");
	    } catch (final ClientProtocolException e) {
		e.printStackTrace();
	    } catch (final IOException e) {
		e.printStackTrace();
	    }
	    return null;
	}

    }

    /**
     * Downloads Marker from Server using php-script and saves them in
     * sharedpreferences No Params needed.
     *
     * @author Rene Zurbrügg
     */
    private class DownloadeMarkerTask extends AsyncTask<String, Void, Void> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
	 */@Override
	protected Void doInBackground(String... params) {

	    for (int i = 0; i < params.length; i++) {

		final ArrayList<SkatepediaMarker> savedMarkers = getSavedMarkers(i);
		final ArrayList<SkatepediaMarker> onlineMarkers = new ArrayList<SkatepediaMarker>();

		final String[] splittedMarkers = getMarkerFromServerAsString(i,
			params);
		try {
		    for (int j = 0; j < splittedMarkers.length; j++)
			onlineMarkers.add(Functions
				.stringToMarker(splittedMarkers[j]));

		} catch (final Exception e) {
		    Functions
			    .log("FEHLER",
				    "FEHLER BEIM AUSLESEN DES MARKERS AUS DER HOMEPAGE");
		}
		Functions.log("DownloadMarkerFromServerTask",
			"Anzahl OnlineMarkers:  " + onlineMarkers.size());

		Functions.log("DownloadMarkerFromServerTask",
			"Anzahl Saved Markers:  " + savedMarkers.size());
		final SharedPreferences sharedpreferences = getSharedPreferences(
			"skatepedia-" + params[i], Context.MODE_PRIVATE);

		final Editor edit = sharedpreferences.edit();
		final ArrayList<SkatepediaMarker> removeList = new ArrayList<SkatepediaMarker>();
		if (onlineMarkers != null && savedMarkers != null
			&& onlineMarkers.size() != 0) {

		    for (final SkatepediaMarker skatepediaMarker : savedMarkers)
			try {
			    /*
			     * Functions .log("DownloadMarkerTask",
			     * " -------- Teste ob Marker online Gelöscht wurde --------------"
			     * );
			     */
			    if (!onlineMarkers.contains(skatepediaMarker)) {
				/*
				 * Functions.log("DownloadMarkerTask",
				 * " Marker: " + skatepediaMarker.getID() +
				 * " wurde online gelöscht");
				 */
				removeList.add(skatepediaMarker);
				edit.remove(skatepediaMarker.getID());
				skatepediaMarker.getMarker().remove();

			    }
			} catch (final Exception e) {
			    Functions.log("FEHLER BEIM löschen eines Markers",
				    "");
			    e.printStackTrace();
			}

		    int download = 0;
		    for (SkatepediaMarker skatepediaMarker : onlineMarkers) {
			updateMarker(skatepediaMarker.getID(),
				skatepediaMarker.getLatlng());

			try {
			    skatepediaMarker = cast(skatepediaMarker, i);
			    /*
			     * Functions .log("DownloadMarkerTask",
			     * " --------Teste ob Marker noch nicht heruntergeladen wurde --------------"
			     * );
			     */
			    if (!savedMarkers.contains(skatepediaMarker)) {
				download++;
				/*
				 * Functions.log("DownloadMarkerTask",
				 * "Neuer Marker Gefunden: " +
				 * skatepediaMarker.getID());
				 */
				savedMarkers.add(skatepediaMarker);
				markerzumupdaten.add(skatepediaMarker);
				edit.putString(skatepediaMarker.getID(),
					skatepediaMarker.toString());

			    } else /*
				    * Functions .log("DownloadMarkerTask",
				    * "---------- Teste ob Marker neue Comments hat --------------"
				    * );
				    * Functions.log("DownloadMarkerTask SAVEDMARKER"
				    * , "ID" + skatepediaMarker.getID());
				    * 
				    * Functions
				    * .log("DownloadMarkerTask SAVEDMARKER",
				    * "saved" + savedMarkers .get(savedMarkers
				    * .indexOf(skatepediaMarker)) .toString());
				    * Functions
				    * .log("DownloadMarkerTask SAVEDMARKER",
				    * "saved" + skatepediaMarker
				    * .getCommentsAsString(MapActivity.this));
				    * 
				    * Functions .log("DownloadMarkerTask",
				    * " Marker ID: " + savedMarkers
				    * .get(savedMarkers
				    * .indexOf(skatepediaMarker)) .getID() +
				    * "  =  " + skatepediaMarker .getID() +
				    * " &&  " + savedMarkers .get(savedMarkers
				    * .indexOf(skatepediaMarker))
				    * .getCommentsAsString( MapActivity.this) +
				    * "  !=  " + skatepediaMarker
				    * .getCommentsAsString(MapActivity.this));
				    */
			    if (!savedMarkers
				    .get(savedMarkers.indexOf(skatepediaMarker))
				    .getCommentsAsString(MapActivity.this)
				    .equals(skatepediaMarker
					    .getCommentsAsString(MapActivity.this))) {
				/*
				 * Functions .log("DownloadMarkerTask",
				 * " Marker ID: " + savedMarkers
				 * .get(savedMarkers .indexOf(skatepediaMarker))
				 * .getID() + "  =  " + skatepediaMarker
				 * .getID() + " &&  " + savedMarkers
				 * .get(savedMarkers .indexOf(skatepediaMarker))
				 * .getCommentsAsString( MapActivity.this) +
				 * "  !=  " + skatepediaMarker
				 * .getCommentsAsString(MapActivity.this));
				 */
				removeList.add(savedMarkers.get(savedMarkers
					.indexOf(skatepediaMarker)));
				markerzumupdaten.add(skatepediaMarker);
				edit.putString(skatepediaMarker.getID(),
					skatepediaMarker.toString());
			    }
			} catch (final Exception e) {
			    Functions.log("ERROR", "Err bei getMarker");
			}
			;
		    }
		    edit.apply();
		    /*
		     * Log.d("EDITOR", "edit   " + edit.toString());
		     * Log.d("PREFS", "PREFS " +
		     * sharedpreferences.getAll().toString());
		     */
		    Functions.log("DownloadMarkerTask", download
			    + " Marker wurden heruntergeladen");

		    Functions.log("DownloadMarkerTask", removeList.size()
			    + " Marker wurden von Gerät gelöscht ");
		    Log.d("MapActivity","lösche Marker von Liste");
		    for (final SkatepediaMarker spM : removeList){
			savedMarkers.remove(spM);
			 Log.d("MapActivity","lösche");
		    }
		}

	    }
	    Log.d("MapActivity","Download Task beendet------------");
	    return null;
	}

	/**
	 * Downlad marker from server
	 *
	 * @param dbNr
	 *            the number of the database
	 * @param db
	 *            Array with database names
	 * @return the marker from server as String
	 */
	public String[] getMarkerFromServerAsString(int dbNr, String db[]) {
	    Functions.log("DownloadMarkerFromServerTask", "DATABASE:  "
		    + DATABASE[dbNr]);
	    String answer = "";
	    BufferedReader in = null;

	    try {
		final HttpClient client = new DefaultHttpClient();

		final HttpPost request = new HttpPost();

		request.setURI(new URI("http://skateparkwetzikon.ch/"
			+ "Skatepedia/Map/getMarkers.php"));

		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs
			.add(new BasicNameValuePair("database", db[dbNr]));
		request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		final HttpResponse response = client.execute(request);
		in = new BufferedReader(new InputStreamReader(response
			.getEntity().getContent()));
		final StringBuffer sb = new StringBuffer("");
		String line = "";
		final String NL = System.getProperty("line.separator");
		while ((line = in.readLine()) != null)
		    sb.append(line + NL);
		in.close();
		final String page = sb.toString();
		if (!page.contains("\\"))
		    Functions.log("DownloadMarkerFromServerTask :",
			    "Antwort des Servers:  " + page);

		answer = page;
	    } catch (final Exception e) {
	    } finally {
		if (in != null)
		    try {
			in.close();
		    } catch (final IOException e) {
			e.printStackTrace();
		    }
	    }

	    final String[] splittedMarkers = answer.split("//");
	    final String[] realMarkers = new String[splittedMarkers.length - 1];
	    for (int j = 0; j < splittedMarkers.length - 1; j++)
		realMarkers[j] = splittedMarkers[j];
	    return realMarkers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */@Override
	protected void onPostExecute(Void result) {
	    for (final SkatepediaMarker spM : markerzumupdaten)
		initMarker(spM);
	    for (final SkatepediaMarker spM : markerzumupdaten)
		try {
		    addMarkerToDrawer(spM, spM.getType());

		} catch (final Exception e) {
		    e.printStackTrace();
		}
	    Log.v("ShowCurrentTask", "Show current Task is done------");
	    if (actionBarProgressBar != null) {
		try {
		    actionBarProgressBar.collapseActionView();
		    actionBarProgressBar.setActionView(null);
		} catch (final Exception e) {
		}
		;
	    }
	    updateAdaper();
	}

	private void updateMarker(String id, LatLng latlng) {

	    final Geocoder geocoder = new Geocoder(MapActivity.this,
		    Locale.getDefault());

	    List<Address> addresses;
	    try {
		addresses = geocoder.getFromLocation(latlng.latitude,
			latlng.longitude, 1);
		String address = "";
		try {
		    address = new String(addresses.get(0).getAddressLine(0)
			    .getBytes("ASCII"), "UTF-8");
		} catch (final UnsupportedCharsetException e) {
		    Log.e("Unsupported Charsett", " detection skipped");
		    address = addresses.get(0).getAddressLine(0);
		}

		final HttpClient client = new DefaultHttpClient();

		final HttpPost request = new HttpPost();

		request.setURI(new URI("http://skateparkwetzikon.ch/"
			+ "Skatepedia/Map/updateAddress.php"));

		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("ID", id));
		nameValuePairs.add(new BasicNameValuePair("Address", address));
		request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		final HttpResponse response = client.execute(request);
		final BufferedReader in = new BufferedReader(
			new InputStreamReader(response.getEntity().getContent()));
		final StringBuffer sb = new StringBuffer("");
		String line = "";
		final String NL = System.getProperty("line.separator");
		while ((line = in.readLine()) != null)
		    sb.append(line + NL);
		in.close();
		sb.toString();

	    } catch (final Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	}

    }

    /**
     * Used to find location with same address as parameter. Set Market at this
     * place and move Camera.
     * 
     * 
     */
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
	 */@Override
	protected List<Address> doInBackground(String... locationName) {
	    final Geocoder geocoder = new Geocoder(getBaseContext());
	    List<Address> addresses = null;

	    try {
		addresses = geocoder.getFromLocationName(locationName[0], 3);
	    } catch (final IOException e) {
		e.printStackTrace();
	    }
	    return addresses;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */@Override
	protected void onPostExecute(List<Address> addresses) {
	    for (final Marker m : locMarker)
		m.remove();

	    if (addresses == null || addresses.size() == 0) {
		Toast.makeText(getBaseContext(), "No Location found",
			Toast.LENGTH_SHORT).show();
		return;
	    }

	    for (int i = 0; i < addresses.size(); i++) {

		final Address address = addresses.get(i);
		final LatLng latLng = new LatLng(address.getLatitude(),
			address.getLongitude());

		final String addressText = String.format(
			"%s, %s",
			address.getMaxAddressLineIndex() > 0 ? address
				.getAddressLine(0) : "", address
				.getCountryName());

		final MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(latLng);
		markerOptions.title(addressText);

		locMarker.add(map.addMarker(markerOptions));
		if (i == 0){
		    map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
		    Log.e("moved","moved camera to loc marker");
		}
	    }
	}
    }

    /**
     * Saves marker on Server using PHP-script
     *
     * @author Rene Zurbrügg
     * 
     * 
     */
    private class PutMarkerOnServerTask extends
	    AsyncTask<SkatepediaMarker, Void, Void> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
	 */@Override
	protected Void doInBackground(SkatepediaMarker... spM) {
	    for (int i = 0; i < spM.length; i++) {

		final HttpClient httpclient = new DefaultHttpClient();
		final HttpPost httppost = new HttpPost(
			"http://www.skateparkwetzikon.ch/Skatepedia/Map/addMarkers.php");

		final String[] markerAsString = spM[i].toString().split(";");
		final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		try {
		    Functions.log("Marker als String ", spM[i].toString());
		    nameValuePairs.add(new BasicNameValuePair("database",
			    DATABASE[spM[i].getType()]));

		    nameValuePairs.add(new BasicNameValuePair("namer",
			    markerAsString[0]));
		    nameValuePairs.add(new BasicNameValuePair("snippet",
			    markerAsString[1]));
		    nameValuePairs.add(new BasicNameValuePair("LatLng",
			    markerAsString[2]));
		    nameValuePairs.add(new BasicNameValuePair("adresse",
			    markerAsString[5]));
		    nameValuePairs.add(new BasicNameValuePair("ID",
			    markerAsString[6]));

		    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		    final HttpResponse response = httpclient.execute(httppost);

		    Functions.log("database", DATABASE[spM[i].getType()]);

		    final BufferedReader in = new BufferedReader(
			    new InputStreamReader(response.getEntity()
				    .getContent()));

		    String line = "";
		    while ((line = in.readLine()) != null)
			Functions.log("Antwort des Servers:     ", line);
		    in.close();

		} catch (final Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

	    }

	    return null;
	}

    }

    /**
     * Shows marker saved on Sharedpreferences on phone
     *
     * 
     * 
     */
    private class ShowCurrentMarkerTask extends AsyncTask<Void, Void, Void> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
	 */
	ArrayList<ArrayList<SkatepediaMarker>> markers = new ArrayList<ArrayList<SkatepediaMarker>>();

	@Override
	protected Void doInBackground(Void... params) {
	    for (int i = 0; i < DATABASE.length; i++) {
		final ArrayList<SkatepediaMarker> tmpList = new ArrayList<SkatepediaMarker>();
		for (final SkatepediaMarker spM : getSavedMarkers(i)) {
		    Log.d("init Marker","init Marker "+spM.getSnippet());
		    initMarker(spM);
		    tmpList.add(spM);
		}
		markers.add(tmpList);
	    }
	    return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */@Override
	protected void onPostExecute(Void v) {

	    for (final ArrayList<SkatepediaMarker> markerList : markers)
		for (final SkatepediaMarker skatepediaMarker : markerList) {
		    
		    final Marker m = map.addMarker(skatepediaMarker
			    .getMarkerOptions());
		    MapActivity.this.markers.put(m.getId(), skatepediaMarker);
		    m.setIcon(skatepediaMarker.getIcon());
		    skatepediaMarker.setMarker(m);
		    skatepediaMarker.setDistance(getDistance(skatepediaMarker.getLatlng()));
		    addMarkerToDrawer(skatepediaMarker, skatepediaMarker.getType());
		    m.setSnippet(Math.round(skatepediaMarker.getDistance())
			    * Math.pow(10, 2) / Math.pow(10, 2) + " km");
		}
	    if (currentMarker != null) {
		currentMarker.onMarkerClick(currentMarker.getMarker());
		Functions.log("current Marker Click",
			"current Marker Click ausgeführt");
	    }
	    
	    updateAdaper();
	    if (!downloadThreadRunning)
		if (actionBarProgressBar != null) {
		    try {
			actionBarProgressBar.collapseActionView();
			actionBarProgressBar.setActionView(null);
		    } catch (final Exception e) {
		    }
		    ;
		}
	}
    }

    /**
     * Updates Rating from selected Marker using PHP
     *
     * @author Rene Zurbrügg
     * 
     * 
     */
    private class UpdateRatingTask extends AsyncTask<Float, Void, Void> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
	 */@Override
	protected Void doInBackground(Float... rating) {
	    Functions.log("updateRatingTask", " Comments: " + rating[0]);
	    final HttpClient httpclient = new DefaultHttpClient();
	    final HttpPost httppost = new HttpPost(
		    "http://www.skateparkwetzikon.ch/Skatepedia/Map/UpdateRating.php");

	    final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    try {
		nameValuePairs.add(new BasicNameValuePair("database",
			DATABASE[selectedMarker.getType()]));

		nameValuePairs.add(new BasicNameValuePair("ID", selectedMarker
			.getID()));
		nameValuePairs.add(new BasicNameValuePair("rating", rating[0]
			+ ""));
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		final HttpResponse response = httpclient.execute(httppost);

		final BufferedReader in = new BufferedReader(
			new InputStreamReader(response.getEntity().getContent()));

		String line = "";
		while ((line = in.readLine()) != null)
		    Functions.log("Antwort des Servers:     ", line);
		in.close();

	    } catch (final Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    return null;
	}
    }

    /** Request on activityresult to crop picture. */
    private static final int ACTION_PIC_CROP = 132;

    /** The Constant with the name of the databases. */
    public final static String[] DATABASE = { "park_markers", "shop_markers",
	    "spot_markers" };

    /** Constant delete used in sharedpreferences. */
    private static final String SHAREDPREFS_STR_DELETE = "delete";
    /** Constant markersave used in sharedpreferences */
    private static final String SHAREDPREFS_STRING_MARKERSAVE = "markerSave";
    /** The action bar progress bar. */
    private MenuItem actionBarProgressBar;

    /** The adress field. */
    public TextView adressField;

    /** Flag to show if back was pressed. */
    private Boolean backPressed = false;

    /** The current marker. */
    private SkatepediaMarker currentMarker;

    /** Flag if download thread is running. */
    private Boolean downloadThreadRunning = false;

    /** The hidden panel. */
    private ViewGroup hiddenPanel;

    /** Flag if infoHeader is visible */
    public boolean isInfoHeaderVisible;

    /** Flag if markerFragment is visible. */
    public boolean isMarkerFragmentVisible;

    /** Adapter used in listView in right Drawer. */
    private MapExpListAdapter listAdapter;

    /** ArrayList with markes with similar adress as input in Actionbar. */
    private final ArrayList<Marker> locMarker = new ArrayList<Marker>();

    /** The map. */
    private GoogleMap map;

    /** HashMap with markers shown on map. */
    private HashMap<String, SkatepediaMarker> markers;
    private final ArrayList<SkatepediaMarker> markersForDrawer = new ArrayList<SkatepediaMarker>();

    /** The ArrayList with marker to update. */
    private final ArrayList<SkatepediaMarker> markerzumupdaten = new ArrayList<SkatepediaMarker>();

    /** The Uri to the (croped) image. */
    private Uri mCropImagedUri;

    /** The Layout used in drawer */
    public DrawerLayout mDrawerLayout;

    /** The toggle to open drawer */
    private ActionBarDrawerToggle mDrawerToggle;

    /** The left drawer */
    private View mLeftDrawerView;

    /** The right drawer */
    public AnimatedExpandableListView mRightDrawerView;

    /** The nearest markers. */
    private ArrayList<ArrayList<SkatepediaMarker>> nearestMarkers;

    /** the last Fragment displayed in MarkerFragment */
    private Fragment oldFrag;

    /** The current position */
    private LatLng pos;

    /** The selected marker. */
    public SkatepediaMarker selectedMarker;

    /** The SohwcaseView */
    private ShowcaseView showcaseView;

    /** The show case view on click listener. */
    private OnClickListener showCaseViewOnClickListener;

    /** Headers in right drawer. */
    private String[] toggle;

    private Toolbar toolbar;

    /** The GPSracker. */
    private GPSTracker tracker;

    /** The zoom level. */
    private final int zoomLevel = 20;

    /**
     * Adds the marker to drawer.
     *
     * @param marker
     *            the Marker to add
     * @param markerType
     *            the type of the marker
     */
    private void addMarkerToDrawer(SkatepediaMarker marker, int markerType) {

	Log.e("addMarkerToDrawer","Distance "+marker.getDistance()+"    "+marker.toString());
	if (marker.getDistance() == 0.0
		|| nearestMarkers.get(markerType).contains(marker))
	    return;

	if (nearestMarkers.get(markerType).size() >= 3)
	    for (int j = 0; j < 3; j++) {
		Functions.log("Add marker to drawer", "checking with  "
			+ nearestMarkers.get(markerType).get(j).getAddress()
			+ " with distance  "
			+ nearestMarkers.get(markerType).get(j).getDistance());
		if (((nearestMarkers.get(markerType).get(j).getDistance()) > marker
			.getDistance())
			|| (nearestMarkers.get(markerType).get(j).getDistance() == 0.0)) {

		    nearestMarkers.get(markerType).set(j, marker);
		    Functions.log("Add marker to drawer",
			    "added " + marker.getAddress() + " with distance  "
				    + marker.getDistance());
		}
	    }
	else {
	    nearestMarkers.get(markerType).add(marker);
	    Functions.log("Add marker to drawer",
		    "added " + marker.getAddress() + " with distance  "
			    + marker.getDistance());
	}

    }

    /**
     * Casts a SkatepediaMarker to a Park, Spot or Shop Marker.
     *
     * @param marker
     *            the marker
     * @param type
     *            the type
     * @return the casted katepedia marker
     */
    private SkatepediaMarker cast(SkatepediaMarker marker, int type) {
	switch (type) {
	case SkatepediaMarker.MARKER_PARK:
	    marker = new ParkMarker(marker);
	    break;
	case SkatepediaMarker.MARKER_SHOP:
	    marker = new ShopMarker(marker);
	    break;
	case SkatepediaMarker.MARKER_SPOT:
	    marker = new SpotMarker(marker);
	    break;
	}
	return marker;
    }

    /**
     * Check if Marker can be removed from Map.
     *
     * @param strDelete
     *            the ID of the Marker to delete
     * @return true, if marker can be removed
     */
    private boolean checkForDelete(String strDelete) {

	final SharedPreferences sp = getSharedPreferences("skatepedia",
		MODE_PRIVATE);
	final Date dt = new Date();

	int counter = sp.getInt(strDelete + "Int", 0);
	final long timeMS = sp.getLong(strDelete, dt.getTime());
	Functions
		.log("Time ",
		"Time defferent: "
				+ TimeUnit.MILLISECONDS.toSeconds(dt.getTime()
					- timeMS));

	if (counter >= 3) {
	    if ((TimeUnit.MILLISECONDS.toMinutes(dt.getTime() - timeMS) <= 5)) {
		Toast.makeText(
			MapActivity.this,
			MapActivity.this.getResources().getString(
				R.string.errToManyMarkers)
				+ ((5 * 60) - TimeUnit.MILLISECONDS
					.toSeconds(dt.getTime() - timeMS))
				+ "Sekunden ", Toast.LENGTH_LONG).show();
		return false;
	    }
	    sp.edit().putInt(strDelete + "Int", 1).apply();
	    sp.edit().putLong(strDelete, dt.getTime()).apply();
	    ;
	    return true;

	} else if (counter <= 3
		&& (TimeUnit.MILLISECONDS.toMinutes(dt.getTime() - timeMS) <= 5)) {
	    Functions.log(
		    "Time ",
		    "Time defferent: "
			    + TimeUnit.MILLISECONDS.toMinutes(dt.getTime()
				    - timeMS));
	    counter++;
	    sp.edit().putInt(strDelete + "Int", counter).apply();
	    sp.edit().putLong(strDelete, dt.getTime()).apply();
	    ;
	    return true;
	}

	Toast.makeText(
		MapActivity.this,
		R.string.errToManyMarkers
			+ ((5 * 60) - TimeUnit.MILLISECONDS.toSeconds(dt
				.getTime() - timeMS)) + "Sekunden ",
		Toast.LENGTH_LONG).show();
	return false;

    }

    /**
     * Check if User allready rated marker.
     *
     * @param markerID
     *            the marker id
     * @return true if user didn't rate this marker
     */
    private Boolean checkIfRated(String markerID) {
	final SharedPreferences sp = getSharedPreferences("skatepedia-"
		+ SHAREDPREFS_STRING_MARKERSAVE, MODE_PRIVATE);
	Functions.log("chekifRated", sp.getAll().toString());
	if (sp.contains(markerID))
	    return false;

	return true;

    }

    /**
     * Creates a new file to save images.
     *
     * @param prefix
     *            the prefix to append to the file
     * @return the file
     */
    private File createNewFile(String prefix) {
	if (prefix == null || "".equalsIgnoreCase(prefix))
	    prefix = "IMG_";
	final File newDirectory = new File(
		Environment.getExternalStorageDirectory() + "/mypics/");
	if (!newDirectory.exists())
	    newDirectory.mkdir();

	final File file = new File(newDirectory, (prefix
		+ System.currentTimeMillis() + ".jpg"));
	if (file.exists()) {
	    file.delete();
	    try {
		file.createNewFile();
	    } catch (final IOException e) {
		e.printStackTrace();
	    }
	}

	return file;
    }

    /**
     * Gets the builder to zoom the map.
     *
     * @param arrayList
     *            the spotMarkers
     * @param arrayList2
     *            the shopMarkers
     * @param arrayList3
     *            the parkMarkers
     * @return the builder
     */
    private Builder getBuilder(ArrayList<SkatepediaMarker> arrayList,
	    ArrayList<SkatepediaMarker> arrayList2,
	    ArrayList<SkatepediaMarker> arrayList3) {

	final ArrayList<SkatepediaMarker> list = new ArrayList<SkatepediaMarker>();
	if (arrayList.size() != 0)
	    for (final SkatepediaMarker spm1 : arrayList)
		if (list.size() < 3)
		    list.add(spm1);
		else
		    for (int i = 0; i < list.size(); i++) {
			final SkatepediaMarker spm2 = list.get(i);
			if (spm1.getDistance() < spm2.getDistance()) {
			    list.remove(spm2);
			    list.add(spm1);
			}

		    }
	if (arrayList2.size() != 0)
	    for (final SkatepediaMarker spm1 : arrayList2)
		if (list.size() < 3)
		    list.add(spm1);
		else
		    for (int i = 0; i < list.size(); i++) {
			final SkatepediaMarker spm2 = list.get(i);
			if (spm1.getDistance() < spm2.getDistance()) {
			    list.remove(spm2);
			    list.add(spm1);
			}

		    }
	if (arrayList3.size() != 0)
	    for (final SkatepediaMarker spm1 : arrayList3)
		if (list.size() < 3)
		    list.add(spm1);
		else
		    for (int i = 0; i < list.size(); i++) {
			final SkatepediaMarker spm2 = list.get(i);
			if (spm1.getDistance() < spm2.getDistance()) {
			    list.remove(spm2);
			    list.add(spm1);
			}

		    }

	final LatLngBounds.Builder builder = new LatLngBounds.Builder();
	for (final SkatepediaMarker skatepediaMarker : list)
	{
	    builder.include(skatepediaMarker.getLatlng());
	    Log.e("MARKER",skatepediaMarker.getLatlng()+"");
	}
	return builder;

    }

    /**
     * Get the distance between user and Marker.
     *
     * @param markerPos
     *            the position of the Marker
     * @return the distance between user and marker
     */
    private double getDistance(LatLng markerPos) {
	Log.e("POS: ","LatLNG: " +markerPos);
	
	if (pos != null) {
	    try {
		final LatLng pos1 = pos;
		final double rho = 3960.0; // Erdradius
		final double phi_1 = (90.0 - pos1.latitude) * Math.PI / 180.0;
		final double phi_2 = (90.0 - markerPos.latitude) * Math.PI
			/ 180.0;
		final double theta_1 = pos1.longitude * Math.PI / 180.0;
		final double theta_2 = markerPos.longitude * Math.PI / 180.0;
		Log.e("Distance: ","distance: " +(rho * Math.acos(Math.sin(phi_1) * Math.sin(phi_2)
			* Math.cos(theta_1 - theta_2) + Math.cos(phi_1)
			* Math.cos(phi_2))));
		return (rho * Math.acos(Math.sin(phi_1) * Math.sin(phi_2)
			* Math.cos(theta_1 - theta_2) + Math.cos(phi_1)
			* Math.cos(phi_2)));
	    } catch (final Exception e) {
		e.printStackTrace();
	    }
	    ;
	}
	return 0.0;
    }

    /**
     * Gets the map.
     *
     * @return the map
     */
    public GoogleMap getMap() {
	return map;
    }

    /**
     * Gets the markers saved on Phone.
     *
     * @param db
     *            the Database (Spot, Shop, Park)
     * @return all saved Marker in an Arraylist
     */
    private ArrayList<SkatepediaMarker> getSavedMarkers(int db) {

	Log.d("getSavedMarker", "Saved Marker werden ausgelesn");
	final SharedPreferences sharedpreferences = getSharedPreferences(
		"skatepedia-" + DATABASE[db], Context.MODE_PRIVATE);

	final Map<String, ?> hm = sharedpreferences.getAll();
	final ArrayList<SkatepediaMarker> markers = new ArrayList<SkatepediaMarker>();
	Log.d("getSavedMarker", "MAP: " + hm.toString());
	try {
	    for (final Map.Entry<String, ?> entry : hm.entrySet()) {
		final String value = entry.getValue().toString();
		markers.add(cast(Functions.stringToMarker(value), db));

	    }

	} catch (final Exception e) {
	    Functions.log("ERROR", "fehler bei getSavedMarkers");
	}
	return markers;
    }

    /**
     * Inits the drawer.
     */
    private void initDrawer() {
	if (mDrawerLayout == null || mLeftDrawerView == null
		|| mRightDrawerView == null || mDrawerToggle == null) {

	    // Configure navigation drawer
	    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	    mLeftDrawerView = findViewById(R.id.left_drawer);
	    mRightDrawerView = (AnimatedExpandableListView) findViewById(R.id.right_drawer);
	    mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
		    toolbar, R.string.drawer_open, R.string.drawer_close) {

		@Override
		public void onDrawerClosed(View drawerView) {
		    if (drawerView.equals(mLeftDrawerView)) {
			getSupportActionBar().setTitle(getTitle());
			invalidateOptionsMenu(); // creates call to
			// onPrepareOptionsMenu()
			mDrawerToggle.syncState();
		    }
		    super.onDrawerClosed(drawerView);
		}

		@Override
		public void onDrawerOpened(View drawerView) {
		    if (drawerView.equals(mLeftDrawerView)) {
			getSupportActionBar().setTitle(getTitle());
			invalidateOptionsMenu(); // creates call to
			// onPrepareOptionsMenu()
			mDrawerToggle.syncState();
		    }
		    if (showcaseView != null)
			showCaseViewOnClickListener.onClick(null);

		    super.onDrawerOpened(drawerView);

		}

	    };

	    mDrawerLayout.setDrawerListener(mDrawerToggle); // Set the drawer
	    // toggle as the
	    // DrawerListener

	}

	getResources().getStringArray(R.array.activitysTitles);

	if (mLeftDrawerView != null) {
	    Functions.log("leftview = 0", "");

	    final String[] names = getResources().getStringArray(
		    R.array.activitysTitles);
	    final DrawerItem[] items = new DrawerItem[names.length];
	    for (int i = 0; i < names.length; i++)
		items[i] = new DrawerItem(names[i]);
	    DrawerAdapter adapt;
	    if (Functions.getLanguage(this).equals("de"))
		adapt = new DrawerAdapter(this, MapActivity.this, items, 5);
	    else
		adapt = new DrawerAdapter(this, MapActivity.this, items, 4);
	    ((ListView) mLeftDrawerView).setAdapter(adapt);
	}
	final ArrayList<String> listDataHeader = new ArrayList<String>();
	final HashMap<String, List<SkatepediaMarker>> listDataChild = new HashMap<String, List<SkatepediaMarker>>();
	for (int i = 0; i < 3; i++) {
	    final List<SkatepediaMarker> list = new ArrayList<SkatepediaMarker>();
	    listDataHeader.add(toggle[i]);
	    final SkatepediaMarker spm = new SkatepediaMarker("laden...", 0);
	    spm.clickable = false;
	    list.add(spm);
	    listDataChild.put(toggle[i], list);
	}

	final MapExpListAdapter listAdapter = new MapExpListAdapter(this,
		listDataHeader, listDataChild, this);

	mRightDrawerView.setAdapter(listAdapter);
	for (int i = 0; i < 3; i++)
	    mRightDrawerView.expandGroup(i);

	 mRightDrawerView
		.setOnGroupCollapseListener(new OnGroupCollapseListener() {

		    @Override
		    public void onGroupCollapse(int groupPosition) {
			for (final SkatepediaMarker marker : markers.values())
			    if (marker.getType() == groupPosition)
				marker.getMarker().setVisible(false);

		    }
	});

	 mRightDrawerView
		.setOnGroupExpandListener(new OnGroupExpandListener() {

		    @Override
		    public void onGroupExpand(int groupPosition) {
			for (final SkatepediaMarker marker : markers.values())
			    if (marker.getType() == groupPosition)
				marker.getMarker().setVisible(true);

		    }
	});
	 mRightDrawerView.setOnGroupClickListener(new OnGroupClickListener() {

		    @Override
		    public boolean onGroupClick(ExpandableListView parent, View v,
			    int groupPosition, long id) {
			if ( mRightDrawerView.isGroupExpanded(groupPosition))
			    mRightDrawerView.collapseGroupWithAnimation(groupPosition);
			else
			    mRightDrawerView.expandGroupWithAnimation(groupPosition);
			return true;
		    }

		});

    }

    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {

	if (map == null) {

	    map = ((MapFragment) getFragmentManager()
		    .findFragmentById(R.id.map)).getMap();

	    map.setOnMarkerClickListener(new OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(Marker m) {

		    try {

			final SkatepediaMarker spM = markers.get(m.getId());
			selectedMarker = spM;
			adressField.setTextColor(spM.getColor());
			final Fragment frag = selectedMarker
				.getMarkerOverViewFragment();
			Functions.log("map activity", "marker clickerhalten "
				+ selectedMarker.getAddress());

			// Start a new FragmentTransaction
			final FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();

			if (frag.isAdded()) {
			    Log.d("MAP ACTIVITY", "Show() BFrag");
			    fragmentTransaction.show(frag);
			} else {
			    Log.d("MAP ACTIVITY", "Replacing AFrag -> BFrag");
			    if (oldFrag != null)
				fragmentTransaction.remove(oldFrag);
			    fragmentTransaction.add(R.id.contentFrame, frag);
			}
			oldFrag = frag;
			fragmentTransaction.addToBackStack(null);

			fragmentTransaction.commit();

			spM.onMarkerClick(m);
			map.moveCamera(CameraUpdateFactory.newLatLng(m
				.getPosition()));
			map.animateCamera(CameraUpdateFactory.zoomTo(15));

		    } catch (final Exception e) {
			e.printStackTrace();
		    }
		    ;
		    return true;
		}
	    });
	    map.setOnMapLongClickListener(new OnMapLongClickListener() {

		@SuppressLint("InflateParams")
		@Override
		public void onMapLongClick(LatLng laln) {

		    final LatLng ll = laln;

		    final LayoutInflater li = LayoutInflater
			    .from(MapActivity.this);
		    final View promptsView = li.inflate(R.layout.inpudialog,
			    null);

		    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
			    MapActivity.this);

		    alertDialogBuilder.setView(promptsView);

		    final EditText userInput = (EditText) promptsView
			    .findViewById(R.id.editTextDialogUserInput);

		    final Spinner spinner = (Spinner) promptsView
			    .findViewById(R.id.inputDialogSpinner);
		    final ArrayAdapter<CharSequence> adapter = ArrayAdapter
			    .createFromResource(MapActivity.this,
				    R.array.markers,
				    android.R.layout.simple_spinner_item);
		    // Specify the layout to use when the list of choices
		    // appears
		    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    // Apply the adapter to the spinner
		    spinner.setAdapter(adapter);
		    // set dialog message
		    alertDialogBuilder
			    .setCancelable(false)
			    .setPositiveButton("OK",
				    new DialogInterface.OnClickListener() {
					@Override
					public void onClick(
				DialogInterface dialog, int id) {
					    final MarkerOptions mo = new MarkerOptions()
						    .title(spinner
							    .getSelectedItem()
							    + "")
						    .snippet(
							    userInput.getText()
								    + "")
						    .position(ll);
					    final Marker m = map.addMarker(mo);
					    final SkatepediaMarker spM = new SkatepediaMarker(
						    m, 0, MapActivity.this, mo);
					    final SkatepediaMarker spM_Send = cast(
						    spM,
						    spinner.getSelectedItemPosition());
					    m.setIcon(spM_Send.getIcon());
					    final PutMarkerOnServerTask task = new PutMarkerOnServerTask();
					    task.execute(spM_Send);
					    markers.put(m.getId(),

					    spM_Send);

					}
				    })
			    .setNegativeButton(R.string.cancel,
				    new DialogInterface.OnClickListener() {
					@Override
					public void onClick(
				DialogInterface dialog, int id) {
					    dialog.cancel();
					}
				    });

		    // create alert dialog
		    final AlertDialog alertDialog = alertDialogBuilder.create();

		    // show it
		    alertDialog.show();
		}
	    });
	    // check if map is created successfully or not

	    map.setOnMarkerDragListener(new OnMarkerDragListener() {

		@Override
		public void onMarkerDrag(Marker marker) {
		    final SkatepediaMarker spm = markers.get(marker.getId());
		    spm.getMarker().setPosition(spm.getLatlng());

		}

		@Override
		public void onMarkerDragEnd(Marker marker) {

		    final SkatepediaMarker spm = markers.get(marker.getId());
		    spm.getMarker().setPosition(spm.getLatlng());

		}

		@Override
		public void onMarkerDragStart(Marker marker) {
		    if (checkForDelete(SHAREDPREFS_STR_DELETE)) {

			final SkatepediaMarker spm = markers.get(marker.getId());

			final Marker m = marker;
			final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				MapActivity.this);
			myAlertDialog.setTitle(MapActivity.this.getResources()
				.getString(R.string.delete));
			myAlertDialog.setMessage(MapActivity.this
				.getResources().getString(
					R.string.warningDelete));
			myAlertDialog.setPositiveButton(MapActivity.this
				.getResources().getString(R.string.delete),
				new DialogInterface.OnClickListener() {

				    @Override
				    public void onClick(DialogInterface arg0,
					    int arg1) {

					if (spm.getVote() < 10) {
					    m.setVisible(false);
					    new DeleteMarkerFromServerTask()
						    .execute(spm);
					} else
					    Toast.makeText(
						    MapActivity.this,
						    MapActivity.this
							    .getResources()
							    .getString(
								    R.string.errToManyMarkers),
						    Toast.LENGTH_LONG).show();

				    }
				});
			myAlertDialog.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

				    @Override
				    public void onClick(DialogInterface arg0,
					    int arg1) {

				    }
				});

			myAlertDialog.show();

		    }

		}
	    });

	    if (map == null)
		Toast.makeText(MapActivity.this,
			"Sorry! unable to create maps", Toast.LENGTH_SHORT)
			.show();
	    else
		map.setMyLocationEnabled(true);
	}
    }

    /**
     * Inits the markern and shows them on Map.
     *
     * @param skatepediaMarker
     *            the skatepedia Marker.
     */
    private void initMarker(SkatepediaMarker skatepediaMarker) {

	if (skatepediaMarker.getID() != null) {
	    try {
		if ((skatepediaMarker.getID()).equals(""))
		    skatepediaMarker.setId();

		skatepediaMarker.setMapActivity(this);

		skatepediaMarker.setDistance(getDistance(skatepediaMarker
			.getLatlng()));

	    } catch (final Exception e) {
		e.printStackTrace();
	    }

	    ;
	}
	;
    }

    /**
     * Inits the marker slide view.
     */
    private void initMarkerSlideView() {

	adressField = (TextView) findViewById(R.id.markerAdress);
	hiddenPanel.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		slideContent(v);
	    }
	});

	isInfoHeaderVisible = false;
	isMarkerFragmentVisible = false;

    }

    public void markerClick(SkatepediaMarker marker) {
	try {
	    selectedMarker = marker;
	    final SkatepediaMarker spM = marker;
	    adressField.setTextColor(spM.getColor());
	    final Fragment frag = selectedMarker.getMarkerOverViewFragment();
	    Functions.log("map activity", "marker clickerhalten "
		    + selectedMarker.getAddress());

	    // Start a new FragmentTransaction
	    final FragmentTransaction fragmentTransaction = getSupportFragmentManager()
		    .beginTransaction();

	    if (frag.isAdded()) {
		Log.d("MAP ACTIVITY", "Show() BFrag");
		fragmentTransaction.show(frag);
	    } else {
		Log.d("MAP ACTIVITY", "Replacing AFrag -> BFrag");
		if (oldFrag != null)
		    fragmentTransaction.remove(oldFrag);
		fragmentTransaction.add(R.id.contentFrame, frag);
	    }
	    oldFrag = frag;
	    fragmentTransaction.addToBackStack(null);

	    fragmentTransaction.commit();

	    spM.onMarkerClick(spM.getMarker());
	    map.moveCamera(CameraUpdateFactory.newLatLng(spM.getMarker()
		    .getPosition()));
	    map.animateCamera(CameraUpdateFactory.zoomTo(15));

	} catch (final Exception e) {
	    e.printStackTrace();
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
     * android.content.Intent)
     */@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);

	if (resultCode == RESULT_OK) {
	    Functions.log("map ACtivity", "Got answer  " + resultCode);

	    if (requestCode == MarkerTabAllgemeinFragment.CAMERA_REQUEST) {

		final Intent intent = new Intent(
			"com.android.camera.action.CROP");
		intent.setDataAndType(
			MarkerTabAllgemeinFragment.mImageCaptureUri, "image/*");
		intent.putExtra("outputX", 500);
		intent.putExtra("crop", "true");
		intent.putExtra("outputY", 500);
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		final File f = createNewFile("CROP_");
		try {
		    f.createNewFile();
		} catch (final IOException ex) {
		    Functions.log("io", ex.getMessage());
		}
		mCropImagedUri = Uri.fromFile(f);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropImagedUri);
		// start the activity - we handle returning in onActivityResult
		try {

		    startActivityForResult(intent, ACTION_PIC_CROP);
		} catch (final Exception e) {
		    Toast.makeText(MapActivity.this, R.string.Err_Crop,
			    Toast.LENGTH_LONG).show();
		}

	    } else if (requestCode == ACTION_PIC_CROP) {

		final Intent uploadIntent = new Intent(MapActivity.this,
			ImageUploadService.class);
		uploadIntent.putExtra(ImageUploadService.ID,
			selectedMarker.getID());

		uploadIntent.putExtra(ImageUploadService.IMAGE, mCropImagedUri);

		try {
		    final Bitmap bitmap = MediaStore.Images.Media.getBitmap(
			    getContentResolver(), mCropImagedUri);
		    selectedMarker.addImage(bitmap);

		} catch (final FileNotFoundException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (final IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		startService(uploadIntent);
	    }

	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v7.app.ActionBarActivity#onBackPressed()
     */@Override
    public void onBackPressed() {
	if (isMarkerFragmentVisible)
	    slideContent(null);

	else if (mDrawerLayout.isDrawerOpen(mLeftDrawerView))
	    mDrawerLayout.closeDrawer(mLeftDrawerView);

	else if (backPressed)
	    Toast.makeText(this, R.string.longPressToDelete, Toast.LENGTH_SHORT)
		    .show();

	mDrawerLayout.openDrawer(mRightDrawerView);
	backPressed = true;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.support.v7.app.ActionBarActivity#onConfigurationChanged(android
     * .content.res.Configuration)
     */@Override
    public void onConfigurationChanged(Configuration newConfig) {
	super.onConfigurationChanged(newConfig);

	mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
     */@Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	toggle = getResources().getStringArray(R.array.toggle);
	Functions.log("On Create, MapActivity",
		"-------------MapActivity Gestartet------");
	
	showCaseViewOnClickListener = new OnClickListener() {
	    private int counter = 0;

	    @Override
	    public void onClick(View v) {

		switch (counter) {

		case 0:
		    showcaseView
			    .setShowcase(
				    new PointTarget(
					    new Point(
						    getResources()
							    .getDisplayMetrics().widthPixels,
						    (getResources()
							    .getDisplayMetrics().heightPixels / 2))),
				    true);
		    showcaseView
			    .setContentTitle(getString(R.string.nextMarker));
		    showcaseView
			    .setContentText(getString(R.string.swipeFingerToOpenNext));
		    showcaseView.setBlocksTouches(true);
		    showcaseView.setClickable(false);
		    break;

		case 1:
		    try {
			showcaseView.hide();
			showcaseView
				.setShowcase(
					new ViewTarget(listAdapter
						.getLastView()), true);
			showcaseView
				.setContentTitle(getString(R.string.nextSpot));
			showcaseView
				.setContentText(getString(R.string.clickSpotMarkerForInfos));
			Log.d("onclcik", "showing marker");
		    } catch (final Exception e) {
		    }
		    ;
		    break;
		case 2:
		    /*
		     * if(showcaseView!=null) {
		     * try{showcaseView.hide();}catch(Exception e){}; }
		     * Log.d("onclcik", "hiding"); break;
		     */
		}
		counter++;

	    }
	};
	setContentView(R.layout.activity_map);
	toolbar = (Toolbar) findViewById(R.id.toolbar);
	setSupportActionBar(toolbar);
	hiddenPanel = (ViewGroup) findViewById(R.id.headerRow);
	hiddenPanel.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		slideContent(v);

	    }
	});

	markers = new HashMap<String, SkatepediaMarker>();
	nearestMarkers = new ArrayList<ArrayList<SkatepediaMarker>>();
	for (int i = 0; i < 4; i++)
	    nearestMarkers.add(new ArrayList<SkatepediaMarker>());

	getSupportActionBar().setTitle("Karte");
	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	getSupportActionBar().setHomeButtonEnabled(true);

	initMarkerSlideView();
	initDrawer();

	try {

	    initilizeMap();
	   // zoomToPos();

	} catch (final Exception e) {
	    e.printStackTrace();
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */@Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.map_menu, menu);

	actionBarProgressBar = menu.findItem(R.id.progress);
	actionBarProgressBar.setActionView(R.layout.navigationrefresh);
	actionBarProgressBar.expandActionView();

	menu.findItem(R.id.menuMapStandard).setOnMenuItemClickListener(
        	     new OnMenuItemClickListener() {

		    @Override
		    public boolean onMenuItemClick(MenuItem item) {
			if (map != null)
			    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			if (showcaseView != null)
			    showCaseViewOnClickListener.onClick(null);
			return false;
		    }
        	     });
	menu.findItem(R.id.menuMapSatelite).setOnMenuItemClickListener(
        	     new OnMenuItemClickListener() {

		    @Override
		    public boolean onMenuItemClick(MenuItem item) {
			if (map != null)
			    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			if (showcaseView != null)
			    showCaseViewOnClickListener.onClick(null);

			return false;
		    }
        	     });

	menu.findItem(R.id.menuMapUnderground).setOnMenuItemClickListener(
        	     new OnMenuItemClickListener() {

		    @Override
		    public boolean onMenuItemClick(MenuItem item) {
			if (map != null)
			    map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			if (showcaseView != null)
			    showCaseViewOnClickListener.onClick(null);
			return false;
		    }
        	     });

	final MenuItem menuItem = menu.findItem(R.id.search_action);
	final SearchView sw = (SearchView) menuItem.getActionView();
	final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {

	    @Override
	    public boolean onQueryTextChange(String arg0) {
		return false;
	    }

	    @Override
	    public boolean onQueryTextSubmit(String adress) {
		sw.setIconified(true);
		sw.invalidate();
		new GeocoderTask().execute(adress);
		return false;
	    }
	};

	sw.setOnQueryTextListener(queryTextListener);
	return true;

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onKeyLongPress(int, android.view.KeyEvent)
     */@Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    finish();
	    return true;
	}
	return super.onKeyLongPress(keyCode, event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

	switch (item.getItemId()) {
	case android.R.id.home:
	    mDrawerToggle.onOptionsItemSelected(item);

	    if (mDrawerLayout.isDrawerOpen(mRightDrawerView))
		mDrawerLayout.closeDrawer(mRightDrawerView);

	    return true;
	case R.id.progress:
	    map.clear();
	    map = null;
	    initilizeMap();
	   // zoomToPos();

	    if (Functions.isNetworkAvailable(MapActivity.this)) {
		final DownloadeMarkerTask task = new DownloadeMarkerTask();
		task.execute(DATABASE[0], DATABASE[1], DATABASE[2]);
		downloadThreadRunning = true;
	    }
	    final ShowCurrentMarkerTask showTask = new ShowCurrentMarkerTask();
	    showTask.execute();
	    if (actionBarProgressBar != null) {
		actionBarProgressBar.setActionView(R.layout.navigationrefresh);
		actionBarProgressBar.expandActionView();

	    }
	    return true;

	}

	return super.onOptionsItemSelected(item);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onPostCreate(android.os.Bundle)
     */@Override
    protected void onPostCreate(Bundle savedInstanceState) {
	super.onPostCreate(savedInstanceState);

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */@Override
    public boolean onPrepareOptionsMenu(Menu menu) {

	for (int i = 0; i < menu.size(); i++)
	    menu.getItem(i).setVisible(
		    !mDrawerLayout.isDrawerOpen(mLeftDrawerView));

	return super.onPrepareOptionsMenu(menu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onResume()
     */@Override
    protected void onResume() {
	super.onResume();
	initilizeMap();
    }

    @Override
    public void onStart() {
	super.onStart();
	tracker = new GPSTracker(this);
	 pos=new LatLng(tracker.latitude,tracker.longitude);
	final ShowCurrentMarkerTask showTask = new ShowCurrentMarkerTask();
	showTask.execute();
	if (Functions.isNetworkAvailable(MapActivity.this)) {
	    final DownloadeMarkerTask task = new DownloadeMarkerTask();
	    task.execute(DATABASE[0], DATABASE[1], DATABASE[2]);
	    downloadThreadRunning = true;
	}

    }

    /**
     * Rate marker.
     *
     * @param v
     *            not used. but necessary to add onClick in XML.
     */
    @SuppressLint("InflateParams")
    public void rateMarker(View v) {
	Functions.log("rateMarker", "markerevent");
	if (!checkIfRated(selectedMarker.getID()))
	    return;

	final LayoutInflater li = LayoutInflater.from(MapActivity.this);
	final View promptsView = li.inflate(
        		  R.layout.dialog_rating_mapactivity_marker, null);

	final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
        		  MapActivity.this);

	Functions
		.log("RATE MARKER ---", "marker: " + selectedMarker.toString());
	alertDialogBuilder.setView(promptsView);
	final RatingBar rb = (RatingBar) promptsView
		.findViewById(R.id.dialog_mapActivity_ratingBar);

	alertDialogBuilder
		.setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int id) {
			// float rating = rb.getRating();

			final UpdateRatingTask urt = new UpdateRatingTask();
			urt.execute(rb.getRating());
			getSharedPreferences(
				"skatepedia-" + SHAREDPREFS_STRING_MARKERSAVE,
        		      MODE_PRIVATE).edit()
				.putString(selectedMarker.getID(), "rated")
				.apply();
			selectedMarker.setRating(rb.getRating());
			selectedMarker.addVote();

		    }
        	  })
		.setNegativeButton(R.string.cancel,
			new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int id) {
        		      dialog.cancel();
			    }
			});

	// create alert dialog
	final AlertDialog alertDialog = alertDialogBuilder.create();

	// show it
	alertDialog.show();

    }

    /**
     * Slide content.
     *
     * @param view
     *            not used. but necessary to add onClick in XML.
     */
    public void slideContent(View view) {

	final View contRow = findViewById(R.id.invisLayout);
	final View animRow = findViewById(R.id.animationLayout);

	if (!isMarkerFragmentVisible) {
	    final Animation bottomUp = AnimationUtils.loadAnimation(
		    MapActivity.this, R.anim.bottom_up);

	    animRow.startAnimation(bottomUp);
	    contRow.setVisibility(View.VISIBLE);
	    isMarkerFragmentVisible = true;
	} else {
	    final Animation bottomDown = AnimationUtils.loadAnimation(
		    MapActivity.this, R.anim.bottom_down);
	    final ActionItemTarget target = new ActionItemTarget(this,
		    R.id.map_change_menuItem);

	    showcaseView = Functions.showShowCaseView(target, this,
		    getString(R.string.enable_other_maps),
		    getString(R.string.click_here_to_change_the_map),
		    "menuItem", showCaseViewOnClickListener);

	    if (showcaseView != null)
		showcaseView.setOnClickListener(showCaseViewOnClickListener);

	    contRow.startAnimation(bottomDown);
	    contRow.setVisibility(View.GONE);
	    isMarkerFragmentVisible = false;

	}

    }

    /**
     * Slide content up or down
     */
    public void slideUpDown() {
	if (!isInfoHeaderVisible) {
	    // Show the panel
	    final Animation bottomUp = AnimationUtils.loadAnimation(this,
		    R.anim.bottom_up);

	    showcaseView = Functions.showShowCaseView(hiddenPanel, this,
		    getString(R.string.showcase_detail_marker),
		    getString(R.string.showcase_detail_marker));

	    hiddenPanel.startAnimation(bottomUp);
	    hiddenPanel.setVisibility(View.VISIBLE);
	    isInfoHeaderVisible = true;
	} else {
	    // Hide the Panel
	    final Animation bottomDown = AnimationUtils.loadAnimation(this,
		    R.anim.bottom_down);

	    hiddenPanel.startAnimation(bottomDown);
	    hiddenPanel.setVisibility(View.INVISIBLE);
	    isInfoHeaderVisible = false;
	}
    }

    /**
     * Shows marker on right Drawer and zoom that 4 markers are visible.
     */
    private void updateAdaper() {

	final ArrayList<String> listDataHeader = new ArrayList<String>();
	final HashMap<String, List<SkatepediaMarker>> listDataChild = new HashMap<String, List<SkatepediaMarker>>();

	for (int i = 0; i < 3; i++) {
	    final ArrayList<SkatepediaMarker> list = new ArrayList<SkatepediaMarker>();
	    listDataHeader.add(toggle[i]);
	    Log.e("marker",nearestMarkers.get(i).size()+"");
	    for (int j = 0; j < nearestMarkers.get(i).size(); j++){
		list.add(nearestMarkers.get(i).get(j));
		Log.e("marker",nearestMarkers.get(i).get(j).toString());
	    }
	    listDataChild.put(toggle[i], list);

	}
	try {
	    if (nearestMarkers.size() >= 2) {
		final LatLngBounds.Builder build = getBuilder(
			nearestMarkers.get(0), nearestMarkers.get(1),
			nearestMarkers.get(2));
		if(tracker.latitude!=0.0){
		build.include(new LatLng(tracker.latitude, tracker.longitude));
		Log.e("animate","animate Camera "+nearestMarkers.size());
		if(nearestMarkers.get(0).get(0)!=null)
		{
		for (int i = 0; i < nearestMarkers.size(); i++) {
		    try
		    {
			
			Log.d("test "+i,(nearestMarkers.get(i).get(0).getSnippet())+"");
		    }catch(Exception e){};
		}
		map.animateCamera(CameraUpdateFactory.newLatLngBounds(
			build.build(), 20));
		}}
	    }
	} catch (final Exception e) {
	    e.printStackTrace();
	}
	;
	for (int i = 0; i < listDataHeader.size(); i++)
	    Log.d("Header", "Head: " + listDataHeader.get(i));

	listAdapter = new MapExpListAdapter(MapActivity.this, listDataHeader,
        		  listDataChild, this);

	((ExpandableListView) mRightDrawerView).setAdapter(listAdapter);
	((ExpandableListView) mRightDrawerView).invalidate();
	for (int i = 0; i < 3; i++)
	    ((ExpandableListView) mRightDrawerView).expandGroup(i);

    }

    /**
     * Zoom to current position
     */
    private void zoomToPos() {
	Log.d("in Zoom","Zoom to pos");
	// Enable MyLocation Layer of Google Map
	
	tracker.getLocation();
	Log.e("", tracker.latitude + " Location: " + tracker.getLocation());
	if (tracker.latitude != 0.0) {
	    map.setMyLocationEnabled(true);
	    Log.d("in Zoom","Moved");
	    Log.d("in Zoom","Zoom to pos   "+tracker.latitude);
	    map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(
		    tracker.latitude, tracker.longitude)));
	    pos = new LatLng(tracker.latitude, tracker.longitude);
	    // Zoom in the Google Map
	    if (zoomLevel != 20)
		map.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
	    // else
	    // map.animateCamera(CameraUpdateFactory.zoomTo(12));
	}
    }
}