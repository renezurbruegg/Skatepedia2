package com.skatepedia.android;

import java.io.File;

import Fragments.BasicFragment;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.skatepedia.SlidingStrip.SlidingTabLayout;
import com.skatepedia.android.adapter.DrawerAdapter;

/**
 * Set Up a BasicActivity using ViewPager and Fragments.
 * 
 */
public class BasicActivity extends ActionBarActivity {

    /**
     * PageApater for BasicPages. Used to show diffrent HTML-Pages.
     */
    public class BasicActivityPageAdapter extends FragmentPagerAdapter {

	/**
	 * Instantiates a new basicactivitypageadapter.
	 *
	 * @param fm
	 *            the Fragmentmanager
	 */
	public BasicActivityPageAdapter(FragmentManager fm) {
	    super(fm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */@Override
	public int getCount() {
	    return ITEMS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
	 */@Override
	public Fragment getItem(int position) {
	    switch (position) {

	    case 0:
		return BasicFragment.init("Basics/" + lang + "geschichte.html",
			stream);
	    case 1:
		return BasicFragment.init("Basics/" + lang + "basics.html",
			stream);
	    case 2:
		return BasicFragment.init("Basics/" + lang + "firstSteps.html",
			stream);
	    default:
		return BasicFragment.init(
			"http://skateparkwetzikon.ch/index.php", stream);
	    }
	}

	@Override
	public CharSequence getPageTitle(int position) {
	    return (getResources().getStringArray(R.array.BasicTabsHeader)[position]);
	//    return "test";
	}

    }

    /** Number of Items in ViewPager, HTML-Pages. */
    public static final int ITEMS = 3;

    /** The action bar. */
    private android.support.v7.app.ActionBar actionBar;

    /** If back was pressed */
    private Boolean backPressed = false;

    /** The language. */
    private String lang = "en_";

    /** Activity names in Drawer */
    private String[] mAcvitityTitles;

    /** The adapter to display tabs. */
    private BasicActivityPageAdapter mAdapter;
    /** Layout to show Drawer with Activitys */
    private DrawerLayout mDrawerLayout;
    /** The list wioth items to show in Drawer */
    private ListView mDrawerList;

    // --------- End Drawer Stuff------------------------
    // ---------- Start Drawer Stuff----------------------
    /** The drawer toggl to open drawer */
    private ActionBarDrawerToggle mDrawerToggle;
    /** The viewpager to swipe between HTML-PAges. */
    private ViewPager mViewPager;

    /** Boolean to check if data should be streamed or not */
    private Boolean stream = false;

    Toolbar toolbar;

    /**
     * Inits the drawer.
     */
    private void initDrawer() {
	mAcvitityTitles = getResources()
		.getStringArray(R.array.activitysTitles);
	mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
		GravityCompat.START);
	mDrawerList = (ListView) findViewById(R.id.left_drawer);
	mDrawerLayout.setDrawerListener(mDrawerToggle);
	final DrawerItem[] navDrawerItems = new DrawerItem[mAcvitityTitles.length];

	for (int i = 0; i < mAcvitityTitles.length; i++)
	    navDrawerItems[i] = new DrawerItem(mAcvitityTitles[i]);

	final DrawerAdapter adapter = new DrawerAdapter(this,
		getApplicationContext(), navDrawerItems, 1);
	mDrawerList.setAdapter(adapter);
	mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
		R.string.drawer_open, R.string.drawer_close) {

	    @Override
	    public void onDrawerClosed(View drawerView) {
		super.onDrawerClosed(drawerView);

	    }

	    @Override
	    public void onDrawerOpened(View drawerView) {
		super.onDrawerOpened(drawerView);

	    }
	};

	mDrawerLayout.post(new Runnable() {
	    @Override
	    public void run() {
		mDrawerToggle.syncState();
	    }
	});

	mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    /**
     * Inits the view pager.
     */
    private void initViewPager() {
	mViewPager = (ViewPager) findViewById(R.id.Activity_Basics_Pager);
	mViewPager.setAdapter(mAdapter);
	final SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
	tabs.setDistributeEvenly(true);
	tabs.setViewPager(mViewPager);
	/*
	 * tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this
	 * true, This makes the tabs Space Evenly in Available width
	 * 
	 * // Setting Custom Color for the Scroll bar indicator of the Tab View
	 * tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
	 * 
	 * @Override public int getIndicatorColor(int position) { return
	 * getResources().getColor(R.color.tabsScrollColor); } });
	 * 
	 * // Setting the ViewPager For the SlidingTabsLayout
	 * tabs.setViewPager(pager);
	 * 
	 * /* mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
	 * 
	 * @Override public void onPageScrolled(int arg0, float arg1, int arg2)
	 * {/*Do Nothing
	 */
	/*
	 * }
	 * 
	 * @Override public void onPageScrollStateChanged(int arg0) {/*Do
	 * Nothing
	 */
	/*
	 * }
	 * 
	 * @Override public void onPageSelected(int position) {
	 * getActionBar().setSelectedNavigationItem(position); } });
	 * 
	 * final ActionBar.TabListener tabListener = new ActionBar.TabListener()
	 * {
	 * 
	 * @Override public void onTabReselected(Tab tab, FragmentTransaction
	 * ft) {/*Do Nothing
	 */
	/*
	 * }
	 * 
	 * @Override public void onTabSelected(Tab tab, FragmentTransaction ft)
	 * { mViewPager.setCurrentItem(tab.getPosition()); }
	 * 
	 * @Override public void onTabUnselected(Tab tab, FragmentTransaction
	 * ft) {/*Do Nothing
	 */
	/*
	 * }
	 * 
	 * };
	 * 
	 * for (int i = 0; i < ITEMS; i++) actionBar .addTab(actionBar .newTab()
	 * .setText( (getResources()
	 * .getStringArray(R.array.BasicTabsHeader))[i])
	 * .setTabListener(tabListener));
	 */

    }

    /**
     * Checks if is network available.
     *
     * @return true, if is network available
     */
    private boolean isNetworkAvailable() {
	final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	final NetworkInfo activeNetworkInfo = connectivityManager
		.getActiveNetworkInfo();
	return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onBackPressed()
     */@Override
    public void onBackPressed() {

	if (mDrawerLayout.isDrawerOpen(mDrawerList))
	    mDrawerLayout.closeDrawer(mDrawerList);

	else if (backPressed)
	    Toast.makeText(this, "Lange drücken um App zu schliessen",
		    Toast.LENGTH_SHORT).show();

	mDrawerLayout.openDrawer(mDrawerList);
	backPressed = true;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
	super.onConfigurationChanged(newConfig);
	mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */@Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	stream = !getSharedPreferences(Functions.SETTING_PREFERENCES,
		  MODE_PRIVATE).getBoolean("saveOnPhone", true);
	if (Functions.getLanguage(this).equals("de"))
	    lang = ""; // Set Default Language

	setContentView(R.layout.activity_basics);
	toolbar = (Toolbar) findViewById(R.id.toolbar);
	setSupportActionBar(toolbar);

	mAdapter = new BasicActivityPageAdapter(getSupportFragmentManager());
	actionBar = getSupportActionBar();
	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	getSupportActionBar().setHomeButtonEnabled(true);
	initDrawer();
	if (stream) {
	    if (!isNetworkAvailable())
		Toast.makeText(this,
			"Bitte uberprüfen sie Ihre Internetverbindung",
		      Toast.LENGTH_LONG).show();
	    else
		initViewPager();
	} else if (new File(Functions.getSkatepediaDir().getAbsolutePath()
		+ "/Basics/" + "de" + "firstSteps.html").exists()) {
	    final Long time = (long) 1000;
	    Toast.makeText(this, R.string.Err_not_found, Toast.LENGTH_LONG)
		    .show();
	    Functions.registerAlarm(this, time);
	} else
	    initViewPager();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onKeyLongPress(int, android.view.KeyEvent)
     */@Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    finish();
	    return true;
	}
	return super.onKeyLongPress(keyCode, event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */@Override
    public boolean onOptionsItemSelected(MenuItem item) {
	if (mDrawerToggle.onOptionsItemSelected(item))
	    return true;
	return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
	super.onPostCreate(savedInstanceState);
	mDrawerToggle.syncState();
    }
}