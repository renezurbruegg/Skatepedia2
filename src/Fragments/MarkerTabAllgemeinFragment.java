package Fragments;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTPFile;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.skatepedia.android.Functions;
import com.skatepedia.android.R;
import com.skatepedia.android.SkatepediaFileClient;
import com.skatepedia.android.services.DownloadService;

// TODO: Auto-generated Javadoc
/**
 * The Class MarkerTabAllgemeinFragment contains infos about the marker and
 * images.
 */
public class MarkerTabAllgemeinFragment extends Fragment implements
	OnClickListener {

    /**
     * The Class gets images from server using php
     */
    private class GetImagesFromMarkerTask extends
	    AsyncTask<Void, Void, Drawable[]> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
	 */
	@Override
	protected Drawable[] doInBackground(Void... v) {

	    try {
		final SkatepediaFileClient mFTPClient = new SkatepediaFileClient();

		if (!mFTPClient.changeWorkingDirectory("Map/markers/" + ID))
		    return null;

		mFTPClient.enterLocalPassiveMode();
		final FTPFile[] files = DownloadService.clear(mFTPClient
			.listFiles());

		final Drawable[] d = new Drawable[files.length];
		for (int i = 0; i < files.length; i++) {
		    Functions.log("getDraw",
			    "http://www.skateparkwetzikon.ch/Skatepedia/Map/markers/"
				    + ID + "/" + files[i].getName());
		    final HttpURLConnection connection = (HttpURLConnection) new URL(
			    "http://www.skateparkwetzikon.ch/Skatepedia/Map/markers/"
				    + ID + "/" + files[i].getName())
			    .openConnection();
		    connection.connect();
		    final InputStream input = connection.getInputStream();
		    if (input != null && getActivity() != null)
			d[i] = new BitmapDrawable(getActivity().getResources(),
				Functions.getRoundedCornerBitmap(
					BitmapFactory.decodeStream(input), 30));
		}
		return d;

	    } catch (final IOException e) {
		e.printStackTrace();
	    }
	    return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Drawable[] result) {
	    try {
		imgs = result;
		initDrawables();
	    } catch (final Exception e) {
		Functions.log("Marker Tab", "Error init");

	    }
	}

    }

    /**
     * The Class MarkerTabPageAdapter to show images.
     */
    public class MarkerTabPageAdapter extends FragmentPagerAdapter {

	/** The images. */
	private ArrayList<Fragment> fragments;

	/**
	 * Instantiates a new marker tab page adapter.
	 *
	 * @param fm
	 *            the fragmentManager
	 * @param fragments
	 *            the fragments to show
	 */
	public MarkerTabPageAdapter(FragmentManager fm,
		ArrayList<Fragment> fragments) {
	    super(fm);
	    this.fragments = fragments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
	    return fragments.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
	 */
	@Override
	public Fragment getItem(int position) {

	    if (position <= fragments.size())
		return fragments.get(position);

	    return null;
	}

	/**
	 * Refresh.
	 *
	 * @param fragments
	 *            the fragments to refresh
	 */
	public void refresh(ArrayList<Fragment> fragments) {

	    for (int i = 0; i < this.fragments.size(); i++)
		getFragmentManager().beginTransaction()
			.remove(this.fragments.get(i)).commit();
	    this.fragments.clear();
	    this.fragments = fragments;
	}

    }

    /** The Constant CAMERA_REQUEST to save camerapicture. */
    public static final int CAMERA_REQUEST = 0;

    /** The m image to the camerapicutre */
    public static Uri mImageCaptureUri;

    /** Flag if a image was addes */
    private Boolean addImg = false;

    /** The id. */
    private String ID;

    /** The images. */
    public Drawable[] imgs;

    /** The img to add. */
    private Drawable imgToAdd;

    /** The adapter. */
    MarkerTabPageAdapter mAdapt;

    /** The view pager. */
    public ViewPager mViewPager;

    /** The next. */
    public ImageButton next;

    /**
     * Instantiates a new marker tab allgemein fragment.
     */
    public MarkerTabAllgemeinFragment() {

    }

    /**
     * Adds the image.
     *
     * @param b
     *            the image as bitmap
     */
    public void addImage(Bitmap b) {
	addImg = true;
	imgToAdd = new BitmapDrawable(getActivity().getResources(),
		Functions.getRoundedCornerBitmap(b, 30));

    }

    /**
     * Inits the drawables.
     */
    private void initDrawables() {
	final ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	if (addImg)
	    fragments.add(new MarkerImageFragment(imgToAdd));

	if (imgs != null)
	    for (final Drawable drawable : imgs)
		fragments.add(new MarkerImageFragment(drawable));
	final MarkerImageFragment button = new MarkerImageFragment(
		(getActivity().getResources()
			.getDrawable(R.drawable.imagebutton)));
	button.setOnClickListener(this);
	fragments.add(button);
	mAdapt.refresh(fragments);

	mViewPager.removeAllViews();
	mViewPager.setAdapter(null);
	mViewPager.setAdapter(mAdapt);
	mViewPager.invalidate();

    }

    /**
     * Inits the old drawables.
     */
    private void initOldDrawables() {
	final ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	if (addImg)
	    fragments.add(new MarkerImageFragment(imgToAdd));

	if (imgs != null)
	    for (final Drawable drawable : imgs)
		fragments.add(new MarkerImageFragment(drawable));

	final MarkerImageFragment button = new MarkerImageFragment(
		(getActivity().getResources()
			.getDrawable(R.drawable.imagebutton)));
	button.setOnClickListener(this);
	fragments.add(button);

	mAdapt = new MarkerTabPageAdapter(getChildFragmentManager(), fragments);
	mViewPager.setAdapter(mAdapt);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
	final CharSequence[] items = { "Take Photo", "Choose from Library",
		"Cancel" };

	final AlertDialog.Builder builder = new AlertDialog.Builder(
		getActivity());
	builder.setTitle("Add Photo!");
	builder.setItems(items, new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int item) {
		if (items[item].equals("Take Photo")) {
		    final Intent intent = new Intent(
			    MediaStore.ACTION_IMAGE_CAPTURE);

		    mImageCaptureUri = Uri.fromFile(new File(Environment
			    .getExternalStorageDirectory(), "tmp_contact_"
			    + String.valueOf(System.currentTimeMillis())
			    + ".jpg"));

		    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
			    mImageCaptureUri);

		    try {
			intent.putExtra("return-data", true);
			getActivity().startActivityForResult(intent,
				CAMERA_REQUEST);
		    } catch (final ActivityNotFoundException e) {
			// Do nothing for now
		    }

		} else if (items[item].equals("Choose from Library")) {
		    final Intent intent = new Intent();
		    intent.setType("image/*");
		    intent.setAction(Intent.ACTION_GET_CONTENT);
		    intent.putExtra("return-data", false);
		    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
		    getActivity()
			    .startActivityForResult(intent, CAMERA_REQUEST);

		} else if (items[item].equals("Cancel"))
		    dialog.dismiss();
	    }
	});
	builder.show();

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

	final View layoutView = inflater.inflate(
		R.layout.fragment_marker_allgemein, container, false);
	final Bundle arguments = getArguments();
	ID = arguments.getString(MarkerOverViewFragment.ARG_ID);
	((TextView) layoutView.findViewById(R.id.Activity_Map_Beschrieb))
		.setText(arguments
			.getString(MarkerOverViewFragment.ARG_BESCHRIEB));
	((TextView) layoutView.findViewById(R.id.Activity_map_distance))
		.setText(arguments
			.getDouble(MarkerOverViewFragment.ARG_DISTANCE) + "km");

	final ProgressBarFragment pbf = new ProgressBarFragment();
	final ArrayList<Fragment> list = new ArrayList<Fragment>();
	list.add(pbf);

	mViewPager = (ViewPager) layoutView
		.findViewById(R.id.Fragment_Marker_ViewPager);
	mViewPager.setOffscreenPageLimit(5);
	if (imgs == null) {
	    if (Functions.isNetworkAvailable(getActivity())) {
		mAdapt = new MarkerTabPageAdapter(getFragmentManager(), list);
		mViewPager.setAdapter(mAdapt);
		new GetImagesFromMarkerTask().execute();
	    }

	} else
	    initOldDrawables();
	return layoutView;
    }
}