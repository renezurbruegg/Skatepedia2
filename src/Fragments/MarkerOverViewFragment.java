package Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabWidget;
import android.widget.TextView;

import com.skatepedia.android.R;
import com.skatepedia.android.markers.SkatepediaMarker;

// TODO: Auto-generated Javadoc
/**
 * The Class MarkerOverViewFragment which contains two tabs allgemein and
 * comments.
 */
public class MarkerOverViewFragment extends Fragment {

    /**
     * Used to show fragments in Tabs
     */
    public class DummyTabContent implements TabContentFactory {

	/** The context. */
	private final Context mContext;

	/**
	 * Instantiates a new dummy tab content.
	 *
	 * @param context
	 *            the context
	 */
	public DummyTabContent(Context context) {
	    mContext = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.TabHost.TabContentFactory#createTabContent(java.lang
	 * .String)
	 */
	@Override
	public View createTabContent(String tag) {
	    final View v = new View(mContext);
	    return v;
	}
    }

    /** The Constant ARG_BESCHRIEB used to getArguments. */
    public static final String ARG_BESCHRIEB = "beschrieb";

    /** The Constant ARG_COMMENTS used to getArguments. */
    public static final String ARG_COMMENTS = "comments";

    /** The Constant ARG_DISTANCE used to getArguments. */
    public static final String ARG_DISTANCE = "dist";

    /** The Constant ARG_ID used to getArguments. */
    public static final String ARG_ID = "id";

    /** The Constant ARG_TITLE used to getArguments. */
    public static final String ARG_TITLE = "title";

    /** The Constant ARG_TYPE used to getArguments. */
    public static final String ARG_TYPE = "TYPE";

    /** The Constant ARG_VOTE used to getArguments. */
    public static final String ARG_VOTE = "vote";

    /** Shows marker infos and image in a fragment. */
    MarkerTabAllgemeinFragment allgemeinFragment;

    /** The comments fragment to show comments. */
    MarkerTabCommentsFragment commentsFragment;

    /** The marker . */
    private SkatepediaMarker marker;

    /** The tab host to show tabs */
    private TabHost mTabHost;

    /**
     * Instantiates a new marker fragment.
     */
    public MarkerOverViewFragment() {

    }

    /**
     * Instantiates a new marker fragment.
     *
     * @param marker
     *            the marker
     */
    public MarkerOverViewFragment(SkatepediaMarker marker) {
	this.marker = marker;
    }

    /**
     * Gets the allgemeinfragment.
     *
     * @return the allgemeinfragment
     */
    public MarkerTabAllgemeinFragment getAllgemeinFragment() {
	return allgemeinFragment;
    }

    /**
     * Gets the commentsfragment.
     *
     * @return the commentsfragment
     */
    public MarkerTabCommentsFragment getCommentsFragment() {
	return commentsFragment;
    }

    /**
     * Inits the tab host.
     *
     * @param v
     *            the view which contains the tabhost
     */
    private void initTabHost(View v) {
	mTabHost = (TabHost) v.findViewById(android.R.id.tabhost);
	mTabHost.setup();

	final Bundle markerViewArgs = new Bundle();
	markerViewArgs.putString(ARG_ID, marker.getID());
	markerViewArgs.putString(ARG_TITLE, marker.getTitle());
	markerViewArgs.putString(ARG_BESCHRIEB, marker.getSnippet());
	markerViewArgs.putInt(ARG_VOTE, marker.getVote());
	markerViewArgs.putDouble(ARG_DISTANCE,
		(Math.round(100 * marker.getDistance()) / 100));

	final Bundle markerCommentsArgs = new Bundle();
	markerCommentsArgs.putString(ARG_COMMENTS,
		marker.getCommentsAsString(getActivity()));
	markerCommentsArgs.putString(ARG_ID, marker.getID());
	markerCommentsArgs.putInt(ARG_TYPE, marker.getType());
	/**
	 * Defining Tab Change Listener event. This is invoked when tab is
	 * changed
	 */
	final TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {

	    @Override
	    public void onTabChanged(String tabId) {
		final android.support.v4.app.FragmentManager fm = getChildFragmentManager();
		final MarkerTabAllgemeinFragment allgemeinFragment = (MarkerTabAllgemeinFragment) fm
			.findFragmentByTag("allgFrag");
		final MarkerTabCommentsFragment commentFragment = (MarkerTabCommentsFragment) fm
			.findFragmentByTag("commFrag");
		final android.support.v4.app.FragmentTransaction ft = fm
			.beginTransaction();

		/** Detaches the allgemeinFragment if exists */
		if (allgemeinFragment != null)
		    ft.detach(allgemeinFragment);

		/** Detaches the commentFragment if exists */
		if (commentFragment != null)
		    ft.detach(commentFragment);

		/** If current tab is android */
		if (tabId.equalsIgnoreCase("allgFrag")) {

		    if (allgemeinFragment == null) {
			/**
			 * Create allgemeinFragment and adding to
			 * fragmenttransaction
			 */
			final MarkerTabAllgemeinFragment allgFrag = new MarkerTabAllgemeinFragment();
			allgFrag.setArguments(markerViewArgs);
			MarkerOverViewFragment.this.allgemeinFragment = allgFrag;
			ft.add(R.id.realtabcontent, allgFrag, "allgFrag");
		    } else {
			/**
			 * Bring to the front, if already exists in the
			 * fragmenttransaction
			 */

			MarkerOverViewFragment.this.allgemeinFragment = allgemeinFragment;
			ft.attach(allgemeinFragment);
		    }

		} else if (commentFragment == null) {
		    /** Create commentFragment and adding to fragmenttransaction */
		    final MarkerTabCommentsFragment commFrag = new MarkerTabCommentsFragment();
		    commFrag.setArguments(markerCommentsArgs);

		    commentsFragment = commFrag;
		    ft.add(R.id.realtabcontent, commFrag, "commFrag");
		} else {

		    commentsFragment = commentFragment;
		    /**
		     * Bring to the front, if already exists in the
		     * fragmenttransaction
		     */
		    ft.attach(commentFragment);
		}
		ft.commit();
	    }
	};

	/** Setting tabchangelistener for the tab */
	mTabHost.setOnTabChangedListener(tabChangeListener);

	/** Defining tab builder for Andriod tab */
	final TabHost.TabSpec tSpecAndroid = mTabHost.newTabSpec("allgFrag");
	tSpecAndroid.setIndicator(getActivity().getString(R.string.general));
	tSpecAndroid.setContent(new DummyTabContent(getActivity()
		.getBaseContext()));
	mTabHost.addTab(tSpecAndroid);

	/** Defining tab builder for Apple tab */
	final TabHost.TabSpec tSpecComments = mTabHost.newTabSpec("commFrag");
	tSpecComments.setIndicator(getActivity().getString(R.string.comments));
	tSpecComments.setContent(new DummyTabContent(getActivity()
		.getBaseContext()));
	mTabHost.addTab(tSpecComments);

	mTabHost.setBackgroundColor(Color.parseColor("#ffffff"));
	final TabWidget widget = mTabHost.getTabWidget();
	for (int i = 0; i < widget.getChildCount(); i++) {
	    final View view = widget.getChildAt(i);

	    // Look for the title view to ensure this is an indicator and not a
	    // divider.
	    final TextView tv = (TextView) view
		    .findViewById(android.R.id.title);
	    if (tv == null)
		continue;
	    view.setBackgroundResource(R.drawable.tab_indicator_ab_example);
	}
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
		R.layout.fragment_marker_container, container, false);

	initTabHost(layoutView);
	return layoutView;
    }

}