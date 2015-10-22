package Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.skatepedia.android.NewTrickActivity;
import com.skatepedia.android.R;

// TODO: Auto-generated Javadoc
/**
 * The Class NewTrickVideoFragment to make a fragment to upload video.
 */
public class NewTrickVideoFragment extends Fragment implements OnClickListener {

    /** The Constant to trim Video */
    private final static int TRIM_VIDEO = 2;

    /** The Constant VIDEO to get Video from arguments. */
    public final static String VIDEO = "video";

    /** The uri to the video. */
    private Uri uri;

    /** The videoView to display video. */
    private VideoView video;

    /**
     * Instantiates a new new trick video fragment.
     */
    public NewTrickVideoFragment() {
    }

    /**
     * Gets the video Uri.
     *
     * @return the uri in a bundle
     */
    public Bundle getData() {
	final Bundle bund = new Bundle();

	try {
	    bund.putParcelable(VIDEO, uri);
	} catch (final Exception e) {
	    e.printStackTrace();
	    return null;

	}

	return bund;
    }

    /**
     * Gets the file path from video uri.
     *
     * @param context
     *            the context
     * @param contentUri
     *            the content uri
     * @return the file path from video uri
     */
    public String getFilePathFromVideoURI(Context context, Uri contentUri) {
	Cursor cursor = null;
	try {
	    final String[] proj = { MediaColumns.DATA };
	    cursor = context.getContentResolver().query(contentUri, proj, null,
		    null, null);
	    final int column_index = cursor
		    .getColumnIndexOrThrow(MediaColumns.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	} finally {
	    if (cursor != null)
		cursor.close();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onActivityResult(int, int,
     * android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO Auto-generated method stub
	super.onActivityResult(requestCode, resultCode, data);
	Log.d("TAAAG", "Habe result in frag erhalten. ");
	getActivity();
	if (requestCode == NewTrickActivity.SELECT_VIDEO) {

	    uri = data.getData();
	    if (MediaPlayer.create(getActivity(), uri).getDuration() > 10000)
		/*
		 * Intent trimVideoIntent = new
		 * Intent("com.android.camera.action.TRIM");
		 * 
		 * // The key for the extra has been discovered from
		 * com.android.gallery3d.app.PhotoPage.KEY_MEDIA_ITEM_PATH
		 * trimVideoIntent.putExtra("media-item-path",
		 * getFilePathFromVideoURI(getActivity(), uri));
		 * trimVideoIntent.setData(uri);
		 * 
		 * // Check if the device can handle the Intent
		 * List<ResolveInfo> list =
		 * getActivity().getPackageManager().queryIntentActivities
		 * (trimVideoIntent, 0); if (null != list && list.size() > 0) {
		 * startActivity(trimVideoIntent); // Fires TrimVideo activity
		 * into being active } else{
		 */
		Toast.makeText(getActivity(),
			getActivity().getString(R.string.ErrVideoToLong),
			Toast.LENGTH_LONG).show();
	    // }
	    else {

		video.setMediaController(new MediaController(getActivity()));
		video.setVideoURI(uri);
		video.requestFocus();
		video.start();
	    }

	} else if (requestCode == TRIM_VIDEO) {
	    Log.d("trimmed vid", "trimmed Video erhalten");
	    video.setMediaController(new MediaController(getActivity()));
	    video.setVideoURI(uri);
	    video.requestFocus();
	    video.start();
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
		R.layout.fragment_newtrick_video, container, false);

	try {
	    video = (VideoView) layoutView
		    .findViewById(R.id.Activity_newTrick_videoView);
	    ((Button) layoutView
		    .findViewById(R.id.Activity_newTrick_videoButton))
		    .setOnClickListener(this);
	    ;

	} catch (final Exception e) {
	    e.printStackTrace();
	}

	return layoutView;
    }

}
