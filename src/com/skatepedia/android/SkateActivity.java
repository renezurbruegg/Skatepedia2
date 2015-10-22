package com.skatepedia.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.skatepedia.android.adapter.DrawerAdapter;

// TODO: Auto-generated Javadoc
/**
 * Gets Inputs to start the SkateGame.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SkateActivity extends ActionBarActivity {

    /** The constant to get the name of skater 1. */
    static final String SKATER1 = "skater1";

    /** The constant to get the name of skater 2. */
    static final String SKATER2 = "skater2";

    Toolbar toolbar;
    /** flag if back was pressed. */
    private Boolean backPressed = false;

    /** flag if easier tricks are allowed. */
    private Boolean easyer = false;

    /** flag if just tricks you can are allowed. */
    private Boolean mastered = false;

    /** The m drawer layout. */
    private DrawerLayout mDrawerLayout;

    /** The m drawer list. */
    private ListView mDrawerList;

    /** The m drawer toggle. */
    private ActionBarDrawerToggle mDrawerToggle;

    /** flag if nollie tricks are allowed. */
    private Boolean nollie = false;

    /** flag if oldschool tricks are allowed. */
    private Boolean oldSchool = false;

    /** The spinner. */
    private Spinner spinner;

    /** The textview for skater 1. */
    private AutoCompleteTextView textViewskater1;

    /** The textview for skater 2. */
    private AutoCompleteTextView textViewskater2;

    /**
     * Gets the array for textview with last imputs.
     *
     * @param string
     *            the string to input
     * @return the array with last inputs
     */

    private String[] getArrayForAutoCompTextView(String string) {
	final SharedPreferences sp = getSharedPreferences("skatepedia-"
		+ string, MODE_PRIVATE);
	final String[] answer = new String[sp.getAll().size()];
	int i = 0;
	final Map<String, String> map = new TreeMap<String, String>(
		(Map<? extends String, ? extends String>) sp.getAll());
	for (final Map.Entry<String, String> entry : map.entrySet()) {

	    Functions.log("getArrayForAutoComTextView Place:   [" + i + "]",
		    entry.getKey());
	    answer[i] = (entry.getKey());
	    i++;
	}
	return answer;
    }

    /**
     * Gets the map with inputs before.
     *
     * @param c
     *            the first char
     * @param sp
     *            the sharedoreferences to look in
     * @return the map
     */
    public TreeMap<String, Integer> getMap(char c, SharedPreferences sp) {
	final Map<String, ?> map = sp.getAll();

	final Map<String, Integer> shortMap = new TreeMap<String, Integer>();

	for (final Entry<String, ?> entry : map.entrySet())
	    if (entry.getKey().charAt(0) == c)
		shortMap.put(entry.getKey(),
			Integer.parseInt(entry.getValue().toString()));

	return sortMap(shortMap);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onBackPressed()
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
     * @see
     * android.app.Activity#onConfigurationChanged(android.content.res.Configuration
     * )
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
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_skate);
	toolbar = (Toolbar) findViewById(R.id.toolbar);
	setSupportActionBar(toolbar);
	getSupportActionBar().setTitle("S.K.A.T.E");
	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	getSupportActionBar().setHomeButtonEnabled(true);

	textViewskater1 = (AutoCompleteTextView) findViewById(R.id.editSkater1);
	textViewskater2 = (AutoCompleteTextView) findViewById(R.id.editSkater2);
	final ArrayAdapter<String> adapterSkater = new ArrayAdapter<String>(
		this, android.R.layout.simple_dropdown_item_1line,
		getArrayForAutoCompTextView(SKATER1));
	textViewskater1.setAdapter(adapterSkater);
	textViewskater2.setAdapter(adapterSkater);

	((CheckBox) findViewById(R.id.switchBox))
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {

		    @Override
		    public void onCheckedChanged(CompoundButton buttonView,
			    boolean isChecked) {
			nollie = isChecked;

		    }
		});

	((CheckBox) findViewById(R.id.easyerTricks))
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {

		    @Override
		    public void onCheckedChanged(CompoundButton buttonView,
			    boolean isChecked) {
			easyer = isChecked;

		    }
		});

	((CheckBox) findViewById(R.id.masteredBox))
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {

		    @Override
		    public void onCheckedChanged(CompoundButton buttonView,
			    boolean isChecked) {
			mastered = isChecked;

		    }
		});

	((CheckBox) findViewById(R.id.OldSchoolBox))
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {

		    @Override
		    public void onCheckedChanged(CompoundButton buttonView,
			    boolean isChecked) {
			oldSchool = isChecked;

		    }
		});

	spinner = (Spinner) findViewById(R.id.diff_Spinner);
	// Create an ArrayAdapter using the string array and a default spinner
	// layout
	final ArrayAdapter<CharSequence> adapter = ArrayAdapter
		.createFromResource(SkateActivity.this, R.array.difficulties,
			android.R.layout.simple_spinner_item);
	// Specify the layout to use when the list of choices appears
	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	// Apply the adapter to the spinner
	spinner.setAdapter(adapter);

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
		getApplicationContext(), items, 3);

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
	// If the nav drawer is open, hide action items related to the content
	// view
	return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Save.
     *
     * @param skater1
     *            the first skater
     * @param dataBaseName
     *            the databasename
     */
    private void save(String skater1, String dataBaseName) {
	final SharedPreferences sp1 = getSharedPreferences("skatepedia-"
		+ dataBaseName, MODE_PRIVATE);

	if (skater1 == null || skater1.equals(""))
	    return;
	final TreeMap<String, Integer> map = getMap(skater1.charAt(0), sp1);
	if (!sp1.getAll().containsKey(skater1)) {
	    final SharedPreferences.Editor editor1 = sp1.edit();
	    if (map.size() < 10)
		editor1.putInt(skater1, 1);
	    else {

		editor1.remove(((NavigableMap<String, Integer>) map)
			.firstEntry().getKey());
		editor1.putInt(skater1, 1);
	    }
	    editor1.apply();

	} else {

	    for (final Entry<String, Integer> entry : map.entrySet()) {
		if (entry.getKey().equals(skater1))
		    try {
			int key = entry.getValue();
			map.remove(key + "");
			key += 1;
			map.put(skater1, key);
		    } catch (final NumberFormatException e) {
			Toast.makeText(getApplicationContext(),
				"Problem beim auslesen der Skater",
				Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		    }
	    }
	    final Editor editor1 = sp1.edit();
	    for (final Entry<String, Integer> entry : map.entrySet())
		editor1.putInt(entry.getKey(), entry.getValue());
	    editor1.apply();
	}

    }

    /**
     * Sort map.
     *
     * @param shortMap
     *            the map to sort
     * @return the sorted map
     */
    public TreeMap<String, Integer> sortMap(Map<String, Integer> shortMap) {
	final Comparator<Map.Entry<String, Integer>> byMapValues = new Comparator<Map.Entry<String, Integer>>() {
	    @Override
	    public int compare(Map.Entry<String, Integer> left,
		    Map.Entry<String, Integer> right) {
		return left.getValue().compareTo(right.getValue());
	    }
	};

	// create a list of map entries
	final List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>();

	// add all candy bars
	list.addAll(shortMap.entrySet());

	// sort the collection
	Collections.sort(list, byMapValues);

	final TreeMap<String, Integer> tm = new TreeMap<String, Integer>();
	for (final Entry<String, Integer> entry : list)
	    tm.put(entry.getKey(), entry.getValue());
	return tm;

    }

    /**
     * Start skate.
     *
     * @param v
     *            the v
     */
    public void startSkate(View v) {

	save(textViewskater1.getText().toString(), SKATER1);
	final Intent intent = new Intent(this, SkateGame_Activity.class);
	intent.putExtra("spinner", spinner.getSelectedItemPosition());
	intent.putExtra(SKATER1, textViewskater1.getText().toString());
	intent.putExtra(SKATER2, textViewskater2.getText().toString());
	intent.putExtra("switch", (nollie));

	Functions.log("easyer", (easyer) + "");
	intent.putExtra("easyer", easyer);
	intent.putExtra("oldSchool", oldSchool);
	intent.putExtra("mastered", mastered);
	startActivity(intent);

    }

}
