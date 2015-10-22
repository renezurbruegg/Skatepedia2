package com.skatepedia.android;

import java.util.ArrayList;

import Fragments.NewTrickAllgemeinInputFragment;
import Fragments.NewTrickPictureFragment;
import Fragments.NewTrickVideoFragment;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.skatepedia.SlidingStrip.SlidingTabLayout;
import com.skatepedia.android.adapter.DrawerAdapter;
import com.skatepedia.android.services.NewTrickService;

// TODO: Auto-generated Javadoc
/**
 * The Class NewTrickActivity.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class NewTrickActivity extends ActionBarActivity implements
	OnClickListener {

    /**
     * The Class NewTrickActivityAdapter with fragments to display.
     * 
     */
    public class NewTrickActivityAdapter extends FragmentPagerAdapter {

	/** List with fragments in Adapter (Video, Infos, Pictures). */
	ArrayList<Fragment> list = new ArrayList<Fragment>();

	/**
	 * Instantiates a new new trick activity adapter.
	 *
	 * @param fm
	 *            the fm
	 */
	public NewTrickActivityAdapter(FragmentManager fm) {
	    super(fm);
	    list.add(new NewTrickVideoFragment());
	    list.add(new NewTrickAllgemeinInputFragment());
	    list.add(new NewTrickPictureFragment());

	}

	@Override
	public CharSequence getPageTitle(int position) {
	    return (getResources().getStringArray(R.array.newTrickArr)[position]);

	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
	    return list.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
	 */
	@Override
	public Fragment getItem(int position) {

	    if (position < list.size())
		return list.get(position);
	    else
		return new NewTrickPictureFragment();
	}

    }

    /** The Constant DESCRIPTION. */
    public static final String DESCRIPTION = "description";

    /** The Constant DIFFICULTY. */
    public static final String DIFFICULTY = "difficulty";

    /** The Constant KIND. */
    public static final String KIND = "kind";

    Toolbar toolbar;
    /** The Constant NAME. */
    public static final String NAME = "name";

    /** The Constant PHOTO. */
    public static final String PHOTO = "photo";

    /** The Constant SELECT_VIDEO. */
    public static final int SELECT_VIDEO = 2;

    /** The Constant TEXT. */
    public static final String TEXT = "text";

    /** The Constant URI. */
    public static final String URI = "uri";

    /** The action bar. */
    private android.support.v7.app.ActionBar actionBar;

    /** flag if back was pressed. */
    private Boolean backPressed = false;

    /** The context. */
    private final Context ct = this;

    /** The descriptio of the new trickn. */
    private String description;

    /** The difficulty of the new trick. */
    private String difficulty;

    /** The kind of the new trick. */
    private String kind;

    /** The adapter with all fragments. */
    private NewTrickActivityAdapter mAdapter;

    /** The drawer layout. */
    private DrawerLayout mDrawerLayout;

    /** The drawer list with items to display. */

    private ListView mDrawerList;

    /** The drawer toggle to open drawer. */
    private ActionBarDrawerToggle mDrawerToggle;

    ViewPager mViewPager;

    /** The name of the new trick. */
    private String name;

    /** The pic texts of the new trick. */
    private ArrayList<String> picTexts;

    /** The pic uris of the new trick. */
    private ArrayList<Uri> picUris;

    /** The vid uri of the new trick. */
    private Uri vidUri;

    /**
     * Inits the view pager.
     */
    private void initViewPager() {
	mViewPager = (ViewPager) findViewById(R.id.Activity_newTrick_Pager);
	mViewPager.setOffscreenPageLimit(3);
	mViewPager.setAdapter(mAdapter);
	final SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
	//tabs.setDistributeEvenly(true);
	tabs.setViewPager(mViewPager);
	;

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onBackPressed()
     */
    @Override
    public void onBackPressed() {

	if (mDrawerLayout.isDrawerOpen(mDrawerList))
	    mDrawerLayout.closeDrawer(mDrawerList);

	else if (backPressed)
	    Toast.makeText(this, "Lange drücken um App zu schliessen",
		    Toast.LENGTH_SHORT).show();

	mDrawerLayout.openDrawer(mDrawerList);
	backPressed = true;

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.support.v4.app.FragmentActivity#onConfigurationChanged(android
     * .content.res.Configuration)
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
	super.onConfigurationChanged(newConfig);
	// Pass any configuration change to the drawer toggls
	mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_newtrick);
	toolbar = (Toolbar) findViewById(R.id.toolbar);
	setSupportActionBar(toolbar);
	actionBar = getSupportActionBar();
	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	getSupportActionBar().setHomeButtonEnabled(true);
//	getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	mAdapter = new NewTrickActivityAdapter(getSupportFragmentManager());

	mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
		GravityCompat.START);

	mDrawerList = (ListView) findViewById(R.id.left_drawer);
	
	final String[] names = getResources().getStringArray(
		R.array.activitysTitles);
	final DrawerItem[] items = new DrawerItem[names.length];
	for (int i = 0; i < names.length; i++)
	    items[i] = new DrawerItem(names[i]);
	final DrawerAdapter adapt = new DrawerAdapter(this,
		getApplicationContext(), items, 4);

	mDrawerList.setAdapter(adapt);
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
	mDrawerLayout.setDrawerListener(mDrawerToggle);

	getSupportActionBar().setTitle("Neuer Trick hinzufügen");
	initViewPager();

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onKeyLongPress(int, android.view.KeyEvent)
     */
    @Override
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
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// The action bar home/up action should open or close the drawer.
	// ActionBarDrawerToggle will take care of this.
	if (mDrawerToggle.onOptionsItemSelected(item))
	    return true;
	return super.onOptionsItemSelected(item);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onPostCreate(android.os.Bundle)
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
	super.onPostCreate(savedInstanceState);
	// Sync the toggle state after onRestoreInstanceState has occurred.
	mDrawerToggle.syncState();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
	mDrawerLayout.isDrawerOpen(mDrawerList);
	return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Upload the trick to the server.
     *
     * @param v
     *            the v
     */
    public void uploadTrick(View v) {

	final NewTrickVideoFragment video = (NewTrickVideoFragment) mAdapter
		.getItem(0);
	if (video.getData() != null)
	    vidUri = (Uri) video.getData().getParcelable(
		    NewTrickVideoFragment.VIDEO);

	final NewTrickAllgemeinInputFragment allgemein = (NewTrickAllgemeinInputFragment) mAdapter
		.getItem(1);
	if (allgemein.getData() != null) {
	    final Bundle data = allgemein.getData();
	    name = data.getString(NewTrickAllgemeinInputFragment.NAME);
	    description = data
		    .getString(NewTrickAllgemeinInputFragment.DESCRIPTION);
	    difficulty = data
		    .getString(NewTrickAllgemeinInputFragment.DIFFICULTY);
	    kind = data.getString(NewTrickAllgemeinInputFragment.KIND);
	}

	final NewTrickPictureFragment frag = (NewTrickPictureFragment) mAdapter
		.getItem(2);
	if (frag.getData() != null) {
	    picUris = frag.getData().getParcelableArrayList(
		    NewTrickPictureFragment.PICS);
	    picTexts = frag.getData().getStringArrayList(
		    NewTrickPictureFragment.TEXTS);
	}
	if (Functions.isNetworkAvailable(this))
	    if (name == null || description == null || difficulty == null
		    || kind == null || vidUri == null || picUris == null
		    || picTexts == null)
		Toast.makeText(this, "sie haben nicht alles ausgefüllt",
			Toast.LENGTH_LONG).show();
	    else {

		final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
			    final Intent newTrickIntent = new Intent(ct,
				    NewTrickService.class);
			    newTrickIntent.putExtra(URI, vidUri);
			    newTrickIntent.putParcelableArrayListExtra(PHOTO,
				    picUris);
			    newTrickIntent.putStringArrayListExtra(TEXT,
				    picTexts);
			    newTrickIntent.putExtra(NAME, name);
			    newTrickIntent.putExtra(KIND, kind);
			    newTrickIntent.putExtra(DESCRIPTION, description);
			    newTrickIntent.putExtra(DIFFICULTY, difficulty);
			    startService(newTrickIntent);
			    Toast.makeText(NewTrickActivity.this,
				    "Daten werden hochgeladen",
				    Toast.LENGTH_LONG).show();
			    final Intent i = new Intent(NewTrickActivity.this,
				    NewTrickActivity.class);
			    startActivity(i);
			    finish();
			    break;

			case DialogInterface.BUTTON_NEGATIVE:
			    // No button clicked
			    break;
			}
		    }
		};

		final AlertDialog.Builder builder = new AlertDialog.Builder(
			NewTrickActivity.this);
		builder.setMessage("Sollen die Daten hochgeladen werden?")
			.setPositiveButton("Ja", dialogClickListener)
			.setNegativeButton("Nein", dialogClickListener).show();

	    }

    }

}
