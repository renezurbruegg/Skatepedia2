package Fragments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.skatepedia.android.Functions;
import com.skatepedia.android.R;
import com.skatepedia.android.SkatepediaFileClient;
import com.skatepedia.android.Skatetrick;
import com.skatepedia.android.adapter.TrickActivityPageAdapter;

// TODO: Auto-generated Javadoc
/**
 * The Class TrickPicsFragment Shows a Viewpager with Trickfragments.
 */
public class TrickPicsFragment extends Fragment {

    /**
     * The Class getFramesTask. Gets the number of images in folder.
     */
    private class getFramesTask extends AsyncTask<Void, Void, Integer> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
	 */
	@Override
	protected Integer doInBackground(Void... params) {
	    try {

		final SkatepediaFileClient mFTPClient = new SkatepediaFileClient();
		mFTPClient.changeWorkingDirectory("/"
			+ trick.toString().replace(" ", "_") + "/de/");
		frames = (mFTPClient.listFiles().length - 2);
		Functions.log("GFT", "/" + trick.toString().replace(" ", "")
			+ "/de/");
		Functions.log("GFT", frames + "");

	    } catch (final Exception e) {
		e.printStackTrace();
	    }
	    ;
	    return frames;

	}

    }

    /**
     * The Class initViewFlipperTask to download the images and Text.
     */
    private class initViewFlipperTask extends AsyncTask<Void, Void, Object[]> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
	 */
	@Override
	protected Object[] doInBackground(Void... params) {
	    Log.d("initViewFlippi gesterte", "gestartet");
	    final Object[] answer = new Object[frames * 2];
	    try {

		for (int i = 0; i < frames; i++) {
		    final String ans = getFileContent("img_" + (i + 1) + ".txt");
		    if (ans != "")
			answer[i] = ans;
		    else {
			if (language.equals("de"))
			    language = "en";
			else
			    language = "de";
			answer[i] = getFileContent("img_" + (i + 1) + ".txt");
		    }
		}

		if (saveOnPhone)
		    for (int i = frames; i < 2 * frames; i++)
			answer[i] = BitmapFactory.decodeFile((STORAGE
				.getAbsolutePath()
				+ "/skatepedia/"
				+ trick.toString()
				+ "/"
				+ "img_"
				+ (i + 1 - frames) + ".JPG").replace(" ", "_"));
		else
		    for (int i = frames; i < 2 * frames; i++) {
			answer[i] = getBitmapFromUrl("img_" + (i + 1 - frames)
				+ ".JPG");

			Functions.log("DEBUG", "URL:" + "img_"
				+ (i + 1 - frames) + ".JPG" + " FRAMES "
				+ frames + "I " + i);
		    }
	    } catch (final IOException e) {
		e.printStackTrace();
	    }
	    return answer;

	}

	/**
	 * Gets the bitmap from an url.
	 * 
	 * @param image
	 *            the image
	 * @return the bitmap
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private Bitmap getBitmapFromUrl(String image) throws IOException {

	    final String pfad = "http://skateparkwetzikon.ch/Skatepedia/"
		    + trick.toString().replace(" ", "_") + "/";

	    Functions.log("bitmaap", pfad + image);
	    InputStream img;
	    try {
		img = new URL(pfad + image).openConnection().getInputStream();

	    } catch (final Exception e) {
		img = new URL(pfad + image.replace("JPG", "jpg"))
			.openConnection().getInputStream();

	    }
	    return BitmapFactory.decodeStream(img);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Object[] answer) {
	    initObjects(answer);
	    /*
	     * if(language_err) { AlertDialog.Builder builder = new
	     * AlertDialog.Builder(getActivity());
	     * builder.setMessage(R.string.langErrDialog) .setCancelable(false)
	     * .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	     * public void onClick(DialogInterface dialog, int id) {
	     * 
	     * } }); AlertDialog alert = builder.create(); alert.show(); }
	     */
	}

    }

    /** The Constant ARG_LANGUAGE to get the language from arguments.. */
    public static final String ARG_LANGUAGE = "language";

    /** The Constant ARG_SAVEONPHONE to get the savestate from arguments. */
    public static final String ARG_SAVEONPHONE = "save";

    /** The Constant ARG_TRICK to get the Trick from arguments. */
    public static final String ARG_TRICK = "trick";

    /** The fragments to display. */
    private final ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    private int frames = 4;

    /** The iamges to display */
    public Object[] imgs;

    /** The language. */
    String language;

    /** The page adapter. */
    private TrickActivityPageAdapter mPageAdapter;

    /** The viewPager. */
    public ViewPager mViewPager;

    /** flag if data is saved on Phone */
    Boolean saveOnPhone;

    /** The Link to the storage. */
    File STORAGE = null;

    /** The trick. */
    Skatetrick trick;

    /**
     * Instantiates a new trick pics fragment.
     */
    public TrickPicsFragment() {
	STORAGE = Environment.getExternalStorageDirectory();

    }

    /**
     * Gets the text savend in img_x.txt files
     * 
     * @param path
     *            the path to the file
     * @return the file content as string
     */
    private String getFileContent(String path) {
	String full = "";
	String input = "";

	if (saveOnPhone) {
	    final File STORAGE = Environment.getExternalStorageDirectory();

	    final String pfad = (STORAGE.getAbsolutePath() + "/skatepedia/"
		    + trick.toString().replace(" ", "_") + "/" + language + "/" + path);
	    try {
		final BufferedReader br = new BufferedReader(new FileReader(
			pfad.replace(" ", "_")));

		while ((input = br.readLine()) != null)
		    full += input + "\n";
		br.close();
	    } catch (final IOException e) {
		e.printStackTrace();
	    }
	} else {
	    URL myConnection;
	    try {
		myConnection = new URL(
			("http://skateparkwetzikon.ch/Skatepedia/"
				+ trick.toString().replace(" ", "_") + "/"
				+ language + "/" + path));

		final URLConnection connectMe = myConnection.openConnection();

		final InputStreamReader lineReader = new InputStreamReader(
			connectMe.getInputStream());
		final BufferedReader buffer = new BufferedReader(lineReader);
		while ((input = buffer.readLine()) != null)
		    full += input + "\n";
		buffer.close();
	    } catch (final Exception e) {
		e.printStackTrace();

	    }

	}
	return full;

    }

    /**
     * Inits the ViewPager
     */
    private void initFlipper() {
	if (saveOnPhone) {
	    final File STORAGE = Environment.getExternalStorageDirectory();
	    final File dir = new File(STORAGE.getAbsolutePath()
		    + "/skatepedia/" + trick.toString().replace(" ", "_") + "/"
		    + language + "/");
	    if (dir.isDirectory())
		frames = dir.listFiles().length - 1;

	} else {
	    final getFramesTask gFT = new getFramesTask();
	    gFT.execute();
	    try {
		gFT.get();
	    } catch (final InterruptedException e) {
		e.printStackTrace();
	    } catch (final ExecutionException e) {
		e.printStackTrace();
	    }

	}

    }

    /**
     * Inits the TrickFragments and adds them to the viewPager.
     * 
     * @param answer
     *            Objects to init
     */
    private void initObjects(Object[] answer) {
	imgs = answer;
	fragments.clear();
	try {
	    for (int i = 0; i < frames; i++)
		fragments.add(new TrickFragment().init(answer[i].toString(),
			(Bitmap) answer[i + frames]));

	    mViewPager.removeAllViews();
	    mPageAdapter = new TrickActivityPageAdapter(getFragmentManager(),
		    fragments);
	    mViewPager.setAdapter(mPageAdapter);
	    mViewPager.invalidate();
	} catch (final Exception e) {
	    Toast.makeText(getActivity(),
		    "Fehler beim initiieren der images/Texte",
		    Toast.LENGTH_LONG).show();
	}

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
	saveOnPhone = getArguments().getBoolean(ARG_SAVEONPHONE);
	trick = (Skatetrick) getArguments().getSerializable(ARG_TRICK);
	language = Functions.getLanguage(getActivity());
	final View layoutView = inflater.inflate(R.layout.skate_pics_fragment,
		container, false);
	Log.d("In oncreate", "in OncreateView Fragment skate pics ------------");

	final ViewPager viewPager = (ViewPager) layoutView
		.findViewById(R.id.Activity_Tricks_ViewPager);
	mViewPager = viewPager;
	mViewPager.setOffscreenPageLimit(10);
	mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
	    private static final float MIN_SCALE = 0.75f;

	    @Override
	    public void transformPage(View view, float position) {
		final int pageWidth = view.getWidth();

		if (position < -1)
		    // This page is way off-screen to the left.
		    view.setAlpha(0);
		else if (position <= 0) { // [-1,0]
		    // Use the default slide transition when moving to the left
		    // page
		    view.setAlpha(1);
		    view.setTranslationX(0);
		    view.setScaleX(1);
		    view.setScaleY(1);

		} else if (position <= 1) { // (0,1]
		    // Fade the page out.
		    view.setAlpha(1 - position);

		    // Counteract the default slide transition
		    view.setTranslationX(pageWidth * -position);

		    // Scale the page down (between MIN_SCALE and 1)
		    final float scaleFactor = MIN_SCALE + (1 - MIN_SCALE)
			    * (1 - Math.abs(position));
		    view.setScaleX(scaleFactor);
		    view.setScaleY(scaleFactor);

		} else
		    // This page is way off-screen to the right.
		    view.setAlpha(0);
	    }

	});
	if (imgs != null)
	    initObjects(imgs);
	else {
	    initFlipper();
	    final initViewFlipperTask ivft = new initViewFlipperTask();
	    ivft.execute();
	}
	return layoutView;
    }

}