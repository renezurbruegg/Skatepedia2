package com.skatepedia.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.Target;
import com.idunnololz.widgets.AnimatedExpandableListView;
import com.skatepedia.android.adapter.DrawerAdapter;
import com.skatepedia.android.adapter.ExpandableListAdapter;

// TODO: Auto-generated Javadoc
/**
 * Shows diffrent Skatetricks ands groups them by type.
 *
 * @author Rene
 */
public class MainActivity extends ActionBarActivity {

    /** Flag if back was pressed */
    private Boolean backPressed = false;

    /** The View to expand */
    private AnimatedExpandableListView expListView;

    View nav;
    // ------ExpendableListView-------
    /** The list adapter. */
    private ExpandableListAdapter listAdapter;

    /** The childs (tricks). */
    private HashMap<String, List<Skatetrick>> listDataChild;
    // --------------------------------

    /** The Headers (Fliptrick etc.) . */
    private List<String> listDataHeader;
    /** The layout of the drawer */
    private DrawerLayout mDrawerLayout;

    /** The items in the drawer. */
    private ListView mDrawerList;
    // -------------------------------

    // ------ Navigationdrawer --------
    /** The drawer toggle to open drawer . */
    private ActionBarDrawerToggle mDrawerToggle;

    /** Flag if searchmode is enabled */
    private Boolean searchMode = false; // If you are using the searchview

    /** int to show how to sort. */
    private int sortInt = Functions.SORT_ABC; // How to Sort.

    /** The showcaseView to show tutorial. */
    private ShowcaseView sv;

    Toolbar toolbar;

    /** array with tricks to display */
    private Skatetrick[] tricks; // Array with Skatetricks

    /**
     * Fill layout.
     *
     * @param trick
     *            the trick
     */
    private void fillLayout(Skatetrick[] trick) {
	initListView(Functions.sort(trick, sortInt, this));
	if (searchMode) {
	    final int count = listAdapter.getGroupCount();
	    for (int position = 1; position <= count; position++)
		expListView.expandGroup(position - 1);
	}

    }

    /**
     * Inits the drawer.
     */
    private void initDrawer() {
	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	getSupportActionBar().setHomeButtonEnabled(true);

	mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	mDrawerList = (ListView) findViewById(R.id.left_drawer);
	mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
		GravityCompat.START);

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

	final String[] names = getResources().getStringArray(
		R.array.activitysTitles);
	final DrawerItem[] items = new DrawerItem[names.length];
	for (int i = 0; i < names.length; i++)
	    items[i] = new DrawerItem(names[i]);
	final DrawerAdapter adapt = new DrawerAdapter(this,
		getApplicationContext(), items, 0);
	mDrawerList.setAdapter(adapt);
	mDrawerLayout.post(new Runnable() {
	    @Override
	    public void run() {
		mDrawerToggle.syncState();
	    }
	});
	


    }

    /**
     * Inits the list view.
     *
     * @param tricks
     *            the tricks
     */
    private void initListView(TreeMap<String, ArrayList<Skatetrick>> tricks) {

	if (tricks == null)
	    return;
	;

	final Iterator<Entry<String, ArrayList<Skatetrick>>> it = tricks
		.entrySet().iterator();
	listDataHeader = new ArrayList<String>();
	listDataChild = new HashMap<String, List<Skatetrick>>();
	int count = 0;

	while (it.hasNext()) {

	    final Entry<String, ArrayList<Skatetrick>> en = it.next();
	    final ArrayList<Skatetrick> al = en.getValue();

	    listDataHeader.add(en.getKey().toString());

	    final List<Skatetrick> list = new ArrayList<Skatetrick>();

	    for (int i = 0; i < al.size(); i++)
		list.add(al.get(i));

	    listDataChild.put(listDataHeader.get(count), list); // Header, Child
								// data

	    count++;
	}

	listAdapter = new ExpandableListAdapter(this, listDataHeader,
		listDataChild, this);
	expListView.setAdapter(listAdapter);

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v7.app.ActionBarActivity#onBackPressed()
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
	super.onConfigurationChanged(newConfig);
	mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	
	setContentView(R.layout.activity_main);
	final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

	if (toolbar != null) {
	    toolbar.setTitle("Liste");
	    setSupportActionBar(toolbar);

	}

	initDrawer();
	expListView = (AnimatedExpandableListView) findViewById(R.id.lvExp);
	tricks = Functions.getSavedTricks(this); // Get saved tricks

	final Spinner spinner = (Spinner) findViewById(R.id.spinner_nav);

	// --------------- Start Actionbar Spinner
	// ----------------------------------------------
	final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
		getBaseContext(),
		R.layout.support_simple_spinner_dropdown_item, getResources()
			.getStringArray(R.array.sortier_array)); //
	spinner.setAdapter(adapter);

	final OnItemSelectedListener listener = new OnItemSelectedListener() {
	    @Override
	    public void onItemSelected(AdapterView<?> arg0, View arg1,
		    int itemPosition, long arg3) {
		sortInt = itemPosition; // Sorting = ABC/Kind/Difficulty
		fillLayout(tricks);

	    }

	    @Override
	    public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	    }
	};

	spinner.setOnItemSelectedListener(listener);

	

	fillLayout(tricks);

	expListView.setOnGroupClickListener(new OnGroupClickListener() {

	    @Override
	    public boolean onGroupClick(ExpandableListView parent, View v,
		    int groupPosition, long id) {
		if (expListView.isGroupExpanded(groupPosition))
		    expListView.collapseGroupWithAnimation(groupPosition);
		else
		    expListView.expandGroupWithAnimation(groupPosition);
		return true;
	    }

	});


	 sv = Functions.showShowCaseView(findViewById(R.id.spinner_nav), this,
	 "Sortieren","Bitte klicken sie oben in den hellen Kreis um die Sortierfunktonen zu ändern.");
	if (sv == null ) {
	/*    sv = Functions.showShowCaseView(findViewById(R.id.spinner_nav),
	    this,
	    "Navigieren","Bitte Drücken Sie auf das Logo oder fahren Sie mit dem Finger von Links nach Rechts um die Navigation zu öffnen",0,0);
*/
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.main, menu);
	final MenuItem menuItem = menu.findItem(R.id.search_action);

	if (menuItem != null) {
	    final SearchView sw = (SearchView) menuItem.getActionView();

	    if (sw != null) {
		Log.d("s", "SEARCH VIEW NOT NULL");

		final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
		    @Override
		    public boolean onQueryTextChange(String newText) {
			Log.d("s", "GOT INPUT");
			searchMode = true;
			listDataHeader.clear();
			listDataChild.clear();
			fillLayout(Functions.search(tricks, newText));

			return true;
		    }

		    @Override
		    public boolean onQueryTextSubmit(String newText) {
			sw.setIconified(true);
			fillLayout(Functions.search(tricks, newText));
			return false;
		    }
		};

		sw.setOnQueryTextListener(queryTextListener);
		sw.setOnCloseListener(new OnCloseListener() {

		    @Override
		    public boolean onClose() {
			searchMode = false;
			return false;
		    }
		});
		sw.setOnFocusChangeListener(new OnFocusChangeListener() {

		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus)
			    sw.setIconified(true);

		    }
		});

		final MenuItemCompat.OnActionExpandListener listener = new MenuItemCompat.OnActionExpandListener() {

		    @Override
		    public boolean onMenuItemActionCollapse(MenuItem arg0) {
			fillLayout(Functions.search(tricks, ""));
			return false;
		    }

		    @Override
		    public boolean onMenuItemActionExpand(MenuItem arg0) {
			// TODO Auto-generated method stub
			return false;
		    }
		};

		MenuItemCompat.setOnActionExpandListener(menuItem, listener);
	    }

	}

	return super.onCreateOptionsMenu(menu);
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

    // _________________________________________
    // LISTENERS_____________________________________
    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
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

    /* Called whenever we call invalidateOptionsMenu() */
    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
	// If the nav drawer is open, hide action items related to the content
	// view
	/*
	 * boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
	 * menu.findItem(R.id.search_action).setVisible(!drawerOpen);
	 * if(drawerOpen) findActionBarSpinner().setVisibility(View.GONE); else
	 * findActionBarSpinner().setVisibility(View.VISIBLE);
	 */return super.onPrepareOptionsMenu(menu);

    }

    /**
     * Traverse view children till it finds spinner.
     *
     * @param parent
     *            the parent
     * @return a list with all spinner views
     */
    private List<View> traverseViewChildren(ViewGroup parent) {
	final List<View> spinners = new ArrayList<View>();
	for (int i = 0; i < parent.getChildCount(); i++) {
	    final View child = parent.getChildAt(i);
	    if (child instanceof Spinner)
		spinners.add(child);
	    else if (child instanceof ViewGroup)
		spinners.addAll(traverseViewChildren((ViewGroup) child));
	}
	return spinners;
    }

}
