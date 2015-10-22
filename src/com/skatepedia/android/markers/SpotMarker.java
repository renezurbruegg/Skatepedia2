package com.skatepedia.android.markers;

import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.skatepedia.android.R;

// TODO: Auto-generated Javadoc
/**
 * The Class SpotMarker.
 */
public class SpotMarker extends SkatepediaMarker {

    /** The Constant color. */
 //   private static final int color = Color.parseColor("#458B00");
    private static final int color = Color.parseColor("#ffffff");

    /** The Constant type. */
    private static final int type = MARKER_SPOT;

    /**
     * Instantiates a new spot marker.
     *
     * @param spM
     *            the marker
     */
    public SpotMarker(SkatepediaMarker spM) {
	super();
	marker = spM.getMarker();
	rating = spM.getRating();
	setLatlng(spM.getLatlng());
	snippet = spM.getSnippet();
	title = spM.getTitle();
	mapActivity = spM.mapActivity;
	address = spM.getAddress();
	ID = spM.getID();
	markerOptions = spM.getMarkerOptions();
	rating = spM.rating;
	vote = spM.vote;
	de_comments = spM.de_comments;
	en_comments = spM.en_comments;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.skatepedia.android.markers.SkatepediaMarker#getColor()
     */
    @Override
    public int getColor() {
	return color;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.skatepedia.android.markers.SkatepediaMarker#getIcon()
     */
    @Override
    public BitmapDescriptor getIcon() {

	return (BitmapDescriptorFactory.fromResource(R.drawable.spot_marker));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.skatepedia.android.markers.SkatepediaMarker#getType()
     */
    @Override
    public int getType() {
	return type;
    }
}
