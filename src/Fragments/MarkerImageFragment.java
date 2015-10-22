package Fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.skatepedia.android.R;

// TODO: Auto-generated Javadoc
/**
 * The Class MarkerImageFragment to show the images based on the marker.
 */
public class MarkerImageFragment extends Fragment {

    /** The onClicklistener to add new Picture. */
    OnClickListener clicki;

    /** The image. */
    Drawable image;

    /** The layout view. */
    View layoutView;

    /** The imageview to display image */
    ImageView mImageView;

    /**
     * Instantiates a new marker image fragment.
     */
    public MarkerImageFragment() {

    }

    /**
     * Instantiates a new marker image fragment.
     *
     * @param image
     *            the image
     */
    public MarkerImageFragment(Drawable image) {
	Log.d("MARKERIMFFRAG", "WUHUU CONSTRUKTOR");
	this.image = image;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	layoutView = inflater.inflate(R.layout.fragment_marker_image,
		container, false);
	if (image != null) {
	    mImageView = (ImageView) layoutView
		    .findViewById(R.id.marker_fragment_imageView_1);
	    mImageView.setImageDrawable(image);
	    if (clicki != null)
		mImageView.setOnClickListener(clicki);
	}
	return layoutView;
    }

    /**
     * Sets the on click listener.
     *
     * @param listener
     *            the new on click listener
     */
    public void setOnClickListener(OnClickListener listener) {
	clicki = listener;
    }

}