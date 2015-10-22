/*
 * 
 */
package com.skatepedia.android.markers;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Fragments.MarkerOverViewFragment;
import Fragments.MarkerTabAllgemeinFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.widget.RatingBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skatepedia.android.Functions;
import com.skatepedia.android.MapActivity;
import com.skatepedia.android.R;

/**
 * The Class SkatepediaMarker.
 */
public class SkatepediaMarker implements OnMarkerClickListener {

    /** The Constant color. Used as Color in adressTextField */
    private static final int color = Color.BLACK;

    /** Constant to identify parkmarker */
    public static final int MARKER_PARK = 0;

    /** Constant to identify shopmarker */
    public static final int MARKER_SHOP = 1;

    /** Constant to identify spotmarker */
    public static final int MARKER_SPOT = 2;

    /** The Constant type. */
    private static final int type = 0;

    /** markers adress. */
    String address = null;

    /** Flag to show if marker is clickable */
    public boolean clickable = true;

    /** german comments. */
    String de_comments = "";

    /** The distance between marker and position. */
    private double distance = 0.0;

    /** The english comments. */
    protected String en_comments = "";

    /** The id. */
    String ID = "";

    /** The latitude Coordinate. */
    private double latitude;

    /** The longitude Coordinate. */
    private double longitude;

    /** The map. */
    private GoogleMap map;

    /** The map activity. */
    MapActivity mapActivity;

    /** The marker. */
    Marker marker = null;

    /** The marker options. */
    MarkerOptions markerOptions;

    /** The marker overviewfragment to show when marker was clicked. */
    private MarkerOverViewFragment markerOverViewFragment;

    /** The rating of the marker. */
    float rating = 0;

    /** The snippet with description */
    String snippet;

    /** The title. */
    String title;

    /** int to show how many persons rated the marker */
    int vote = 0;

    /**
     * Instantiates a new skatepedia marker.
     */
    public SkatepediaMarker() {

    }

    /**
     * Used in Map.OnClickListener to add a Marker.
     *
     * @param marker
     *            the marker
     * @param rating
     *            rating
     * @param mapActivity
     *            the map activity
     * @param markerOptions
     *            the marker options
     */
    public SkatepediaMarker(Marker marker, int rating, MapActivity mapActivity,
	    MarkerOptions markerOptions) {
	this.marker = marker;
	this.rating = rating;
	this.mapActivity = mapActivity;
	this.markerOptions = markerOptions;
	this.marker = marker;
	latitude = markerOptions.getPosition().latitude;
	longitude = markerOptions.getPosition().longitude;
	snippet = markerOptions.getSnippet();
	title = markerOptions.getTitle();
	marker.setDraggable(true);
	setId();

	try {
	    final Geocoder geocoder = new Geocoder(mapActivity,
		    Locale.getDefault());

	    final List<Address> addresses = geocoder.getFromLocation(latitude,
		    longitude, 1);
	    address = new String(addresses.get(0).getAddressLine(0)
		    .getBytes("US-ASCII"), "UTF-8");

	}

	catch (final IOException e) {
	    e.printStackTrace();
	}

    }

    /**
     * This is just a "fake" Marker. Used while marker are loading in drawer.
     *
     * @param title
     *            the title
     * @param rating
     *            the rating
     */
    public SkatepediaMarker(String title, int rating) {
	this.rating = rating;
	this.title = title;

    }

    /**
     * Instantiates a new skatepedia marker.
     *
     * @param name
     *            the name
     * @param snippet
     *            the snippet
     * @param latitude
     *            the latitude coord
     * @param longitude
     *            the longitude coord
     * @param rating
     *            the rating
     * @param voteNr
     *            votes
     * @param adresse
     *            the adress
     * @param ID
     *            the id
     * @param de_comments
     *            german comments
     * @param en_comments
     *            english comments
     */
    public SkatepediaMarker(String name, String snippet, double latitude,
	    double longitude, float rating, int voteNr, String adresse,
	    String ID, String de_comments, String en_comments) {

	title = name;
	this.snippet = snippet;
	this.latitude = latitude;
	this.longitude = longitude;
	this.rating = rating;
	vote = voteNr;
	address = adresse;
	this.ID = ID;

	markerOptions = new MarkerOptions().snippet(snippet).title(name)
		.position(getLatlng());

	if (de_comments != null)
	    this.de_comments = de_comments;
	if (en_comments != null)
	    this.en_comments = en_comments;
    }

    /**
     * Add a image to markeroverview
     *
     * @param bitmap
     *            the bitmap
     */
    public void addImage(Bitmap bitmap) {
	final MarkerTabAllgemeinFragment allgFrag = getMarkerOverViewFragment()
		.getAllgemeinFragment();

	if (allgFrag != null)
	    allgFrag.addImage(bitmap);

    }

    /**
     * Add a vote to marker
     */
    public void addVote() {
	vote += 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
	if (o instanceof SkatepediaMarker)
	    if ((((SkatepediaMarker) o).getID()).equals(getID()))
		return true;
	return false;
    }

    public MapActivity getActivity() {
	return mapActivity;
    }

    /**
     * Gets the address.
     *
     * @return the address
     */
    public String getAddress() {
	return address;
    }

    /**
     * Gets the color.
     *
     * @return the color
     */
    public int getColor() {
	return color;
    }

    /**
     * Gets all comments as string.
     *
     * @param ctx
     *            the context
     * @return all comments as string
     */
    public String getCommentsAsString(Context ctx) {
	if (Functions.getLanguage(ctx).equals("de")) {
	    if (de_comments.equals(""))
		return "noch keine Kommentare vorhanden";

	    return de_comments;
	}

	if (en_comments.equals(""))
	    return "No comments available";

	return en_comments;
    }

    /**
     * Gets the distance.
     *
     * @return the distance
     */
    public double getDistance() {
	return distance;
    }

    /**
     * Gets the icon.
     *
     * @return the icon
     */
    public BitmapDescriptor getIcon() {
	return (BitmapDescriptorFactory.fromResource(R.drawable.shop_marker));
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getID() {
	if (ID.equals(""))
	    setId();
	return ID;
    }

    /**
     * Gets the latlng.
     *
     * @return the latlng
     */
    public LatLng getLatlng() {
	return (new LatLng(latitude, longitude));
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
     * Gets the normal mapmarker.
     *
     * @return the marker
     */
    public Marker getMarker() {
	return marker;
    }

    /**
     * Gets the marker options.
     *
     * @return the marker options
     */
    public MarkerOptions getMarkerOptions() {
	return markerOptions;
    }

    /**
     * Gets the markeroverviewfragment to show in slide layout.
     *
     * @return the markeroverviewfragment
     */
    public MarkerOverViewFragment getMarkerOverViewFragment() {

	if (markerOverViewFragment != null)
	    return markerOverViewFragment;

	markerOverViewFragment = new MarkerOverViewFragment(this);
	return markerOverViewFragment;
    }

    /**
     * Gets the rating.
     *
     * @return the rating
     */
    public float getRating() {
	if (vote != 0)
	    return (rating / vote);
	return 0;
    }

    /**
     * Gets the snippet.
     *
     * @return the snippet
     */
    public String getSnippet() {
	return snippet;
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {

	return title;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public int getType() {
	return type;
    }

    /**
     * Gets the vote.
     *
     * @return the vote
     */
    public int getVote() {
	return vote;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.android.gms.maps.GoogleMap.OnMarkerClickListener#onMarkerClick
     * (com.google.android.gms.maps.model.Marker)
     */
    @Override
    public boolean onMarkerClick(Marker m) {

	if (mapActivity.isMarkerFragmentVisible
		&& mapActivity.adressField.getText().equals(address))
	    mapActivity.slideContent(null);

	if (mapActivity.isMarkerFragmentVisible)
	    mapActivity.isMarkerFragmentVisible = false;

	else if (!mapActivity.adressField.getText().equals(address))
	    mapActivity.isInfoHeaderVisible = false;

	mapActivity.slideUpDown();

	try {
	    mapActivity.adressField.setText(address);
	    ((RatingBar) mapActivity
		    .findViewById(R.id.ratingBarHeaderMapActivity))
		    .setRating(getRating());
	} catch (final Exception e) {
	    e.printStackTrace();
	}

	mapActivity.selectedMarker = this;
	return true;
    }

    /**
     * Sets the distance.
     *
     * @param distance
     *            the new distance
     */
    public void setDistance(double distance) {
	this.distance = distance;
    }

    /**
     * Sets the id.
     */
    public void setId() {
	if (ID.equals("")) {
	    final Date d = new Date();
	    ID = d.getTime() + "ID";
	}
    }

    /**
     * Sets the latlng.
     *
     * @param latlng
     *            the new latlng
     */
    public void setLatlng(LatLng latlng) {
	latitude = latlng.latitude;
	longitude = latlng.longitude;
    }

    /**
     * Sets the map.
     *
     * @param map
     *            the new map
     */
    public void setMap(GoogleMap map) {
	this.map = map;
    }

    /**
     * Sets the map activity.
     *
     * @param mapActivity
     *            the new map activity
     */
    public void setMapActivity(MapActivity mapActivity) {
	this.mapActivity = mapActivity;
    }

    /**
     * Sets the marker.
     *
     * @param marker
     *            the new marker
     */
    public void setMarker(Marker marker) {
	this.marker = marker;
	marker.setDraggable(true);
    }

    /**
     * Sets the marker options.
     *
     * @param mo
     *            the new marker options
     */
    public void setMarkerOptions(MarkerOptions mo) {
	markerOptions = mo;
    }

    /**
     * Sets the rating.
     *
     * @param f
     *            the new rating
     */
    public void setRating(float f) {
	rating = f;

    }

    /**
     * Sets the snippet.
     *
     * @param snippet
     *            the new snippet
     */
    public void setSnippet(String snippet) {
	this.snippet = snippet;
    }

    /**
     * Sets the title.
     *
     * @param title
     *            the new title
     */
    public void setTitle(String title) {
	this.title = title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return (getTitle() + ";" + getSnippet() + ";" + getLatlng().latitude
		+ ":" + getLatlng().longitude + ";" + getRating() + ";"
		+ getVote() + ";" + getAddress() + ";" + getID() + ";"
		+ de_comments + ";" + en_comments);
    }

    /**
     * Zoom map.
     */
    public void zoomMap() {
	map = mapActivity.getMap();
	mapActivity.mDrawerLayout.closeDrawer(mapActivity.mRightDrawerView);
	map.moveCamera(CameraUpdateFactory.newLatLngZoom(getLatlng(), 14));
	map.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
    }

}
