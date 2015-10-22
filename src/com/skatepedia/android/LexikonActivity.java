package com.skatepedia.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.skatepedia.android.adapter.DrawerAdapter;

// TODO: Auto-generated Javadoc
/**
 * The Class LexikonActivity.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LexikonActivity extends ActionBarActivity {

    /** Flag if back was pressed. */
    private Boolean backPressed = false;

    /** The language. */
    private String language = "en";
    /** The view which was opened last */
    private View lastView = null;

    /** The drawer layout. */
    private DrawerLayout mDrawerLayout;

    /** The drawer list with elements to display. */
    private ListView mDrawerList;

    /** The drawer toggle to open drawer. */
    private ActionBarDrawerToggle mDrawerToggle;

    Toolbar toolbar;

    /** The wordlist. */
    private ArrayList<String> wordlist;

    /**
     * Fills the layout with the words
     *
     * @param words
     *            the words as ArrayList
     */
    private void fillLayout(ArrayList<String> words) {
	final LinearLayout ln = (LinearLayout) findViewById(R.id.lexikonLayout);
	if (ln.getChildCount() > 0)
	    ln.removeAllViews();

	try {
	    
	    for (int i = 0; i < words.size(); i++) {
		final TextView meaning = new TextView(this);
		meaning.setText(words.get(i).split(";")[1]);
		meaning.setVisibility(View.GONE);

		final TextView word = new TextView(this);
		word.setText(words.get(i).split(";")[0]);

		word.setOnClickListener(new OnClickListener() {

		    @Override
		    public void onClick(View v) {
			if (meaning.isShown())
			    meaning.setVisibility(View.GONE);
			else {
			    if (lastView != null)
				lastView.setVisibility(View.GONE);
			    lastView = meaning;
			    meaning.setVisibility(View.VISIBLE);
			}

		    }
		});
		word.setLayoutParams(new LinearLayout.LayoutParams(
			android.view.ViewGroup.LayoutParams.MATCH_PARENT,
			android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		    // If we're running on Honeycomb or newer, then we can use the Theme's
		    // selectableItemBackground to ensure that the View has a pressed state
		    TypedValue outValue = new TypedValue();
		    getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
		    word.setBackgroundResource(outValue.resourceId);
		}
		word.setPadding(100, 30, 0, 30);
		meaning.setPadding(100, 0, 0, 30);
		View v = new View(this);
		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(new LinearLayout.LayoutParams(
			android.view.ViewGroup.LayoutParams.MATCH_PARENT,
			android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		v.setLayoutParams(new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1));
		v.setBackgroundColor(Color.GRAY);
		ll.setPadding(100, 0, 100, 0);
		ll.addView(v);
		//word.setMargin(10, 0, 10, 0);
		word.setTextSize(22);
		
		meaning.setLayoutParams(new LinearLayout.LayoutParams(
			android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
			android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		ln.addView(word);
		ln.addView(meaning);
		ln.addView(ll);

	//	ln.addView(tr);

	    }
	} catch (final Exception e) {
	    Toast.makeText(getApplicationContext(),
		    "Problem beid er Fomratierung des Lexikons",
		    Toast.LENGTH_SHORT).show();
	    e.printStackTrace();
	}

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
    

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.support.v7.app.ActionBarActivity#onConfigurationChanged(android
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
     * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
     */
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_lexikon);
	toolbar = (Toolbar) findViewById(R.id.toolbar);
	setSupportActionBar(toolbar);

	if (Functions.getLanguage(this).equals("de"))
	    language = "de";

	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	getSupportActionBar().setHomeButtonEnabled(true);

	wordlist = new ArrayList<String>();
	final AssetManager am = getAssets();
	InputStream is;
	try {
	    is = am.open(language + "_lexikon.txt");

	    final BufferedReader br = new BufferedReader(new InputStreamReader(
		    is));
	    String lineHead;
	    while ((lineHead = br.readLine()) != null)
		wordlist.add(lineHead);

	} catch (final IOException e) {

	    e.printStackTrace();
	}
	fillLayout(wordlist);

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
		getApplicationContext(), items, 2);

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
	getSupportActionBar().setTitle("Lexikon");

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

	    if (sw != null)
		try {
		    final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextChange(String newText) {

			    fillLayout(Functions.search(wordlist, newText));

			    return true;
			}

			@Override
			public boolean onQueryTextSubmit(String arg0) {

			    return false;
			}
		    };
		    sw.setOnQueryTextListener(queryTextListener);
		    sw.setOnCloseListener(new OnCloseListener() {

			@Override
			public boolean onClose() {
			    fillLayout(wordlist);
			    return false;
			}
		    });
		} catch (final Exception e) {
		}

	}
	return true;

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

	final boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
	menu.findItem(R.id.search_action).setVisible(!drawerOpen);
	return super.onPrepareOptionsMenu(menu);
    }

}
