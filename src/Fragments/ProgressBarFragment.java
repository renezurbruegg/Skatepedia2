package Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skatepedia.android.R;

// TODO: Auto-generated Javadoc
/**
 * The Class ProgressBarFragment used to show a Fragment with progress bar while
 * loading data.
 */
public class ProgressBarFragment extends Fragment {

    /**
     * Instantiates a new progress bar fragment.
     */
    public ProgressBarFragment() {
	// TODO Auto-generated constructor stub
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
	final View layoutView = inflater.inflate(R.layout.fragment_progressbar,
		container, false);

	return layoutView;
    }

}