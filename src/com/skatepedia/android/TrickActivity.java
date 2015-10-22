package com.skatepedia.android;

import java.util.ArrayList;

import Fragments.TrickPicsFragment;
import Fragments.TrickVideoFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.skatepedia.SlidingStrip.SlidingTabLayout;
import com.skatepedia.android.adapter.TrickActivityPageAdapter;

// TODO: Auto-generated Javadoc
/**
 * The Class TrickActivity. Shows two tabs with video and pictures of the trick
 */
public class TrickActivity extends ActionBarActivity {

    /** The action bar. */
    android.support.v7.app.ActionBar actionBar;

    /** The action barprogressbar. */
    private MenuItem actionBarProgressBar;

    /** The language. */
    String language = "en";

    /** The adapter. */
    TrickActivityPageAdapter mAdapter;

    Handler mHandler = new Handler(Looper.getMainLooper()) {
	@Override
	public void handleMessage(Message inputMessage) {
	    actionBarProgressBar.collapseActionView();
	    actionBarProgressBar.setActionView(null);
	}

    };

    /** The view pager. */
    ViewPager mViewPager;

    // private ViewPager mViewPager;
    /** flag if data is savedonphone */
    private Boolean saveOnPhone = false;

    Toolbar toolbar;

    /** The trick. */
    private Skatetrick trick;

    /**
     * Inits the view pager.
     */
    private void initViewPager() {
	mViewPager = (ViewPager) findViewById(R.id.Activity_Basics_Pager);
	mViewPager.setAdapter(mAdapter);
	try {
	    final TrickVideoFragment f = (TrickVideoFragment) ((TrickActivityPageAdapter) mViewPager
		    .getAdapter()).getItem(0);
	    new Thread(new Runnable() {
		@Override
		public void run() {
		    while (!f.isFinished())
			try {
			    Thread.sleep(1000);
			    Log.d("load", "Still loading");
			} catch (final InterruptedException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
		    Log.d("load", "close view");
		    mHandler.sendEmptyMessage(0);
		}
	    }).start();

	} catch (final Exception e) {
	    e.printStackTrace();
	}
	;

	final SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
	tabs.setDistributeEvenly(true);
	tabs.setViewPager(mViewPager);

	/*
	 * 
	 * actionBar.addTab(actionBar .newTab() .setText("Allgemein")
	 * .setTabListener(tabListener)); actionBar.addTab(actionBar .newTab()
	 * .setText("Ausführung") .setTabListener(tabListener));
	 */}

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_tricks_pager);

	final ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	if (Functions.getLanguage(this).equals("de")) {
	    Functions.log("TrickActivity", "Sprache = Deutsch");
	    language = "de";
	}
	toolbar = (Toolbar) findViewById(R.id.toolbar);
	if (toolbar != null) {
	    toolbar.setTitle("Liste");
	    setSupportActionBar(toolbar);

	}

	final SharedPreferences sharedPrefs = getSharedPreferences(
		Functions.SETTING_PREFERENCES, MODE_PRIVATE);
	saveOnPhone = sharedPrefs.getBoolean("saveOnPhone", false);
	Log.d("saveOnPhone", "Bool:   " + saveOnPhone);
	trick = (Skatetrick) getIntent().getSerializableExtra("trick");

	final Bundle args = new Bundle();
	args.putBoolean(TrickVideoFragment.ARG_STREAM, !saveOnPhone);
	args.putSerializable(TrickVideoFragment.ARG_TRICK, trick);
	final TrickVideoFragment tvf = new TrickVideoFragment();
	tvf.setArguments(args);
	final Bundle arg2 = new Bundle();
	arg2.putBoolean(TrickPicsFragment.ARG_SAVEONPHONE, saveOnPhone);
	arg2.putSerializable(TrickPicsFragment.ARG_TRICK, trick);
	arg2.putString(TrickPicsFragment.ARG_LANGUAGE, language);
	fragments.add(tvf);
	final TrickPicsFragment tpf = new TrickPicsFragment();
	tpf.setArguments(arg2);
	fragments.add(tpf);

	mAdapter = new TrickActivityPageAdapter(getSupportFragmentManager(),
		fragments);

	getSupportActionBar().setTitle(trick.toString());

	actionBar = getSupportActionBar();
	actionBar.setDisplayHomeAsUpEnabled(true);
	actionBar.setHomeButtonEnabled(true);
	// actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	initViewPager();

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.trick_menu, menu);
	actionBarProgressBar = menu.findItem(R.id.progress);
	actionBarProgressBar.setActionView(R.layout.navigationrefresh);
	actionBarProgressBar.expandActionView();
	return true;

    }

}
