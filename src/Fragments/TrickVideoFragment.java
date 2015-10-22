package Fragments;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
// TODO: Auto-generated Javadoc

import com.skatepedia.android.Functions;
import com.skatepedia.android.R;
import com.skatepedia.android.Skatetrick;

/**
 * The Class TrickVideoFragment. Shows the Video and description of a skatetrick
 */
public class TrickVideoFragment extends Fragment {

    /** The Constant ARG_STREAM to get infos from Arguments. */
    public static final String ARG_STREAM = "stream";

    /** The Constant ARG_TRICK to get the trick from Arguments. */
    public static final String ARG_TRICK = "trick";

    Boolean finished = false;

    /** Flag if data is saved on Server or Phone */
    Boolean stream;

    /** The trick. */
    Skatetrick trick;

    /**
     * Instantiates a new trick video fragment.
     */
    public TrickVideoFragment() {
	// TODO Auto-generated constructor stub
    }

    /**
     * Inits the Video- and TextView
     *
     * @param videoView
     *            the video view
     * @param beschrieb
     *            the text to add to the textview
     */
    private void initvv(final VideoView videoView, TextView beschrieb) {

	Uri video = null;
	final File STORAGE = Environment.getExternalStorageDirectory();

	if (!stream) {
	    final String path = (STORAGE.getAbsolutePath() + "/skatepedia/"
		    + trick.toString().replace(" ", "_") + "/"
		    + trick.toString().replace(" ", "_") + ".mp4");
	    video = Uri.parse(path);
	} else {
	    final String url = "http://skateparkwetzikon.ch/Skatepedia/"
		    + trick.toString().replace(" ", "_") + "/"
		    + trick.toString().replace(" ", "_") + ".mp4";
	    video = Uri.parse(url);
	}

	if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
	    final ActionBar actionBar = ((ActionBarActivity) getActivity())
		    .getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);

	    beschrieb.setText(trick.getDescription());
	    ((View) beschrieb.getParent().getParent()).invalidate();
	}

	final MediaController mediaController = new MediaController(
		getActivity());
	mediaController.setAnchorView(videoView);
	videoView.setMediaController(mediaController);
	videoView.setVideoURI(video);
	videoView.setOnPreparedListener(new OnPreparedListener() {

	    @Override
	    public void onPrepared(MediaPlayer mp) {
		videoView.start();
		finished = true;

	    }
	});

    }

    public Boolean isFinished() {
	return finished;
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

	stream = getArguments().getBoolean(ARG_STREAM);
	trick = (Skatetrick) getArguments().getSerializable(ARG_TRICK);
	final View layoutView = inflater.inflate(R.layout.skate_vid_fragment,
		container, false);
	final VideoView videoView = (VideoView) layoutView
		.findViewById(R.id.videoView1);

	initvv(videoView, (TextView) layoutView.findViewById(R.id.beschreibung));
	if (!Functions.getLanguage(getActivity()).equals("de")) {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(
		    getActivity());
	    builder.setMessage(R.string.langErrDialog)
		    .setCancelable(false)
		    .setPositiveButton("OK",
			    new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
					int id) {

				}
			    });
	    final AlertDialog alert = builder.create();
	    alert.show();
	}
	return layoutView;
    }

}