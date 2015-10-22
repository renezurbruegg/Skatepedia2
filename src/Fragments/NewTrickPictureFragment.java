package Fragments;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.skatepedia.android.NewTrickActivity;
import com.skatepedia.android.R;

// TODO: Auto-generated Javadoc
/**
 * The Class NewTrickPictureFragment to show a fragment to select pictures from
 * mediastore and send them to server.
 */
public class NewTrickPictureFragment extends Fragment implements
	OnClickListener {

    /** The Constant PICS to get pics from Bundle. */
    public final static String PICS = "picture";

    /** The Constant SELECT_PICTURE to get Action to select image. */
    private static final int SELECT_PICTURE = 1;

    /** The Constant TEXTS to get pics from Bundle.. */
    public final static String TEXTS = "texts";

    /** The current image view. */
    ImageView currentImageView;

    /** The layout to show pics. */
    private LinearLayout layout;

    /** The photoUris. */
    private final ArrayList<Uri> photos = new ArrayList<Uri>();

    /** The EditTexts saved in an ArrayList */
    private final ArrayList<EditText> text = new ArrayList<EditText>();

    /**
     * Instantiates a new new trick picture fragment.
     */
    public NewTrickPictureFragment() {
    }

    /**
     * Adds an image button with camera to layout
     *
     * 
     */
    public void addNewFlipperCameraChild(View v) {
	final double weidth = getActivity().getResources().getDisplayMetrics().xdpi;
	final LayoutParams LLParams = new LayoutParams((int) (weidth),
		android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
	final LinearLayout ll = new LinearLayout(getActivity());
	ll.setOrientation(LinearLayout.VERTICAL);
	ll.setPadding(10, 10, 10, 10);
	ll.setLayoutParams(LLParams);

	final ImageView iv = new ImageView(getActivity());
	iv.setLayoutParams(new LayoutParams(
		android.view.ViewGroup.LayoutParams.MATCH_PARENT,
		android.view.ViewGroup.LayoutParams.MATCH_PARENT));
	ll.addView(iv);
	iv.setImageResource(R.drawable.imagebutton);
	iv.setAdjustViewBounds(true);

	iv.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		final Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(
			Intent.createChooser(intent, "Select Picture"),
			SELECT_PICTURE);
		currentImageView = ((ImageView) v);

	    }
	});

	layout.addView(ll);
	iv.setTag(photos.size());

    }

    /**
     * Adds the new picture to the layout;
     *
     * @param uri
     *            the uri to the image
     */
    public void addNewFlipperChild(Uri uri) {
	Bitmap d = null;
	try {
	    d = MediaStore.Images.Media.getBitmap(getActivity()
		    .getContentResolver(), uri);
	} catch (final FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return;
	} catch (final IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	final double weidth = getActivity().getResources().getDisplayMetrics().xdpi;
	final LayoutParams LLParams = new LayoutParams((int) (weidth),
		android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
	final LinearLayout ll = new LinearLayout(getActivity());
	ll.setOrientation(LinearLayout.VERTICAL);
	ll.setPadding(10, 10, 10, 10);
	ll.setLayoutParams(LLParams);

	final ImageView iv = new ImageView(getActivity());
	iv.setLayoutParams(new LayoutParams(
		android.view.ViewGroup.LayoutParams.MATCH_PARENT,
		android.view.ViewGroup.LayoutParams.MATCH_PARENT));
	ll.addView(iv);

	final int nh = (int) (d.getHeight() * (512.0 / d.getWidth()));
	final Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
	iv.setImageBitmap(scaled);

	iv.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		final Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(
			Intent.createChooser(intent, "Select Picture"),
			SELECT_PICTURE);
		currentImageView = ((ImageView) v);

	    }
	});

	final EditText et = new EditText(getActivity());
	et.setLayoutParams(new LayoutParams(
		android.view.ViewGroup.LayoutParams.MATCH_PARENT,
		android.view.ViewGroup.LayoutParams.MATCH_PARENT));
	et.setInputType(InputType.TYPE_CLASS_TEXT
		| InputType.TYPE_TEXT_FLAG_MULTI_LINE);
	ll.addView(et);

	layout.addView(ll);
	iv.setTag(photos.size());
	photos.add(null);
	text.add(et);
    }

    /**
     * Gets the pics and texts.
     *
     * @return the data in a bundle
     */
    public Bundle getData() {
	final Bundle bund = new Bundle();

	try {

	    bund.putParcelableArrayList(PICS, photos);
	    final ArrayList<String> editTextTexts = new ArrayList<String>();
	    for (final EditText et : text)
		editTextTexts.add(et.getText().toString());
	    bund.putStringArrayList(TEXTS, editTextTexts);

	} catch (final Exception e) {
	    e.printStackTrace();
	    return null;

	}

	return bund;
    }

    /**
     * Gets the real path from uri.
     *
     * @param contentUri
     *            the content uri
     * @return the real path from uri
     */
    public String getRealPathFromURI(Uri contentUri) {
	final String[] proj = { MediaColumns.DATA };

	// This method was deprecated in API level 11
	// Cursor cursor = managedQuery(contentUri, proj, null, null, null);

	final CursorLoader cursorLoader = new CursorLoader(getActivity(),
		contentUri, proj, null, null, null);
	final Cursor cursor = cursorLoader.loadInBackground();

	final int column_index = cursor
		.getColumnIndexOrThrow(MediaColumns.DATA);
	cursor.moveToFirst();
	return cursor.getString(column_index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onActivityResult(int, int,
     * android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
	if (requestCode == SELECT_PICTURE) {
	    final Uri selectedImageUri = data.getData();
	    addNewFlipperChild(selectedImageUri);
	    photos.set(
		    (Integer.parseInt(currentImageView.getTag().toString())),
		    selectedImageUri);
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {

	final Intent intent = new Intent();
	intent.setType("video/*");
	intent.setAction(Intent.ACTION_GET_CONTENT);
	startActivityForResult(Intent.createChooser(intent, "Select Picture"),
		NewTrickActivity.SELECT_VIDEO);
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
		R.layout.fragment_newtrick_pictures, container, false);

	try {
	    layout = (LinearLayout) layoutView
		    .findViewById(R.id.photoTextLayout);

	} catch (final Exception e) {
	    e.printStackTrace();
	}
	addNewFlipperCameraChild(null);

	return layoutView;
    }
}
