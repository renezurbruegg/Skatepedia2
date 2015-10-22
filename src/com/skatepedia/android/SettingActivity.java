package com.skatepedia.android;

import java.io.IOException;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skatepedia.android.adapter.DrawerAdapter;

// TODO: Auto-generated Javadoc
/**
 * The Class SettingActivity to change settings.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingActivity extends ActionBarActivity {

    /** The Constant EXTSTORAGEused in Preferences. */
    public static final String EXTSTORAGE = "extStor";

    /** The Constant LANGUAGEused in Preferences. */
    public static final String LANGUAGE = "Language";
    /** The Constant LOOK_FOR_UPDATEused in Preferences. */
    public static final String LOOK_FOR_UPDATE = "look_for_update";

    /** The Constant PHONELANGUAGE used in Preferences. */
    public static final String PHONELANGUAGE = "phoneLanguage";

    public static Toolbar toolbar;

    /** Flag if back was Pressed */
    private Boolean backPressed = false;

    /** The keep language checkBox. */
    private CheckBox keepLanguageCB;

    /** The language spinner. */
    private Spinner languageSpinner;

    /** The look for updates checkBox. */
    private CheckBox lookForUpdatesCB;

    /** The drawer layout. */
    private DrawerLayout mDrawerLayout;

    /** The drawer list to display items. */
    private ListView mDrawerList;

    /** The drawertoggle to open drawer */
    private ActionBarDrawerToggle mDrawerToggle;

    /** flag if stuff should be saved or streamed */
    private Boolean saveOnPhone;

    /** The save on phone checkBox. */
    private CheckBox saveOnPhoneCB;

    /** The shared prefs. */
    private SharedPreferences sharedPrefs;

    /** Flag if extsave should be used. */
    private Boolean useExtSave;

    /** The use extsave checkBox. */
    private CheckBox useExtSaveCB;

    /**
     * blocks the spinner so you can't choose the language
     */
    public void blockSpinner() {

	try {
	    final TextView tv = (TextView) findViewById(R.id.Activity_settings_language_textView);
	    tv.setTextColor(Color.GRAY);

	    languageSpinner.setEnabled(false);
	    languageSpinner.setClickable(false);
	    if (!Locale.getDefault().getLanguage().equals("de"))
		languageSpinner.setSelection(1);
	    else
		languageSpinner.setSelection(0);
	    ((TextView) languageSpinner.getChildAt(0)).setTextColor(Color.GRAY);
	} catch (final Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /**
     * Blocks the update Checkbox
     *
     * @param block
     *            flag if box is blocked or not
     */
    public void blockUpdateCheckBox(Boolean block) {
	if (block) {
	    ((TextView) findViewById(R.id.activity_settings_lookforUpdate_textView))
		    .setTextColor(Color.GRAY);
	    lookForUpdatesCB.setClickable(false);
	    lookForUpdatesCB.setChecked(false);
	} else {
	    ((TextView) findViewById(R.id.activity_settings_lookforUpdate_textView))
		    .setTextColor(Color.BLACK);
	    lookForUpdatesCB.setClickable(true);
	}
    }

    private void clearApplicationData(Context ct) throws IOException {
	ct.getSharedPreferences("skatepedia", MODE_PRIVATE).edit().clear()
		.apply();
	ct.getSharedPreferences(Functions.SETTING_PREFERENCES, MODE_PRIVATE)
		.edit().clear().apply();
	ct.getSharedPreferences("Skatepedia_Settings", MODE_PRIVATE).edit()
		.clear().commit();
    }

    public void clearData(View v) {
	try {
	    clearApplicationData(getApplicationContext());
	    Toast.makeText(SettingActivity.this, R.string.restart,
		    Toast.LENGTH_LONG).show();
	} catch (final Exception e) {
	}
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
	setContentView(R.layout.activity_settings);
	toolbar = (Toolbar) findViewById(R.id.toolbar);
	setSupportActionBar(toolbar);
	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	getSupportActionBar().setHomeButtonEnabled(true);

	mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
		GravityCompat.START);

	mDrawerList = (ListView) findViewById(R.id.left_drawer);
	
	final String[] names = getResources().getStringArray(
		R.array.activitysTitles);
	final DrawerItem[] items = new DrawerItem[names.length];
	for (int i = 0; i < names.length; i++)
	    items[i] = new DrawerItem(names[i]);
	DrawerAdapter adapt = new DrawerAdapter(this, getApplicationContext(),
		items, 6);
	if (!Functions.getLanguage(this).equals("de"))
	    adapt = new DrawerAdapter(this, getApplicationContext(), items, 6);
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
	getSupportActionBar().setTitle("Einstellungen");
	sharedPrefs = getSharedPreferences(Functions.SETTING_PREFERENCES,
		MODE_PRIVATE);
	saveOnPhone = sharedPrefs.getBoolean("saveOnPhone", true);
	useExtSave = sharedPrefs.getBoolean("useExtSave", true);
	saveOnPhoneCB = (CheckBox) findViewById(R.id.SaveOnPhone);
	useExtSaveCB = (CheckBox) findViewById(R.id.useExtSave);
	keepLanguageCB = (CheckBox) findViewById(R.id.Activity_settings_keep_language);
	lookForUpdatesCB = (CheckBox) findViewById(R.id.activity_settings_lookforUpdate_checkBox);
	lookForUpdatesCB.setChecked(sharedPrefs.getBoolean(LOOK_FOR_UPDATE,
		true));

	blockUpdateCheckBox(!sharedPrefs.getBoolean("saveOnPhone", false));

	lookForUpdatesCB
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {

		    @Override
		    public void onCheckedChanged(CompoundButton arg0,
			    boolean isChecked) {
			final Editor sEditer = sharedPrefs.edit();
			sEditer.remove(LOOK_FOR_UPDATE);
			sEditer.putBoolean(LOOK_FOR_UPDATE, isChecked);
			sEditer.apply();
			Functions.registerAlarm(SettingActivity.this,
				Long.valueOf(1000));

		    }
	});
	languageSpinner = (Spinner) findViewById(R.id.Activity_settings_spinner);

	if (Functions.getLanguage(this).equals("de"))
	    languageSpinner.setSelection(0);
	else
	    languageSpinner.setSelection(1);

	keepLanguageCB.setChecked(sharedPrefs.getBoolean(PHONELANGUAGE, false));
	keepLanguageCB
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {

		    @Override
		    public void onCheckedChanged(CompoundButton buttonView,
			    boolean isChecked) {
			final Editor sEditer = sharedPrefs.edit();
			sEditer.remove(PHONELANGUAGE);
			sEditer.putBoolean(PHONELANGUAGE, isChecked);
			sEditer.apply();

			if (isChecked)
			    blockSpinner();
			else
			    openSpinner();

		    }
	});

	saveOnPhoneCB.setChecked(saveOnPhone);
	useExtSaveCB.setChecked(useExtSave);
	saveOnPhoneCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	    @Override
	    public void onCheckedChanged(CompoundButton buttonView,
		    boolean isChecked) {
		blockUpdateCheckBox(!isChecked);
		if (isChecked)
		    Functions.registerAlarm(getApplicationContext(),
			    (long) 1000);
		final Editor sEditer = sharedPrefs.edit();
		sEditer.remove("saveOnPhone");
		sEditer.putBoolean("saveOnPhone", isChecked);
		sEditer.apply();

	    }
	});

	if (languageSpinner.isEnabled())
	    languageSpinner
		    .setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0,
				View arg1, int position, long arg3) {
			    if (position == 0) {
				final Editor sEditer = sharedPrefs.edit();
				sEditer.remove(PHONELANGUAGE);
				sEditer.putString(LANGUAGE, "de");
				sEditer.apply();
				final Configuration config = new Configuration(
					getResources().getConfiguration());
				config.locale = Locale.GERMAN;
				getResources().updateConfiguration(config,
					getResources().getDisplayMetrics());

			    } else {
				final Editor sEditer = sharedPrefs.edit();
				sEditer.remove(PHONELANGUAGE);
				sEditer.putString(LANGUAGE, "en");
				sEditer.apply();
				final Configuration config = new Configuration(
					getResources().getConfiguration());
				config.locale = Locale.ENGLISH;
				getResources().updateConfiguration(config,
					getResources().getDisplayMetrics());

			    }

			    Toast.makeText(
				    getApplicationContext(),
				    R.string.Activity_settings_Toast_ChangeView,
				    Toast.LENGTH_LONG).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			    // TODO Auto-generated method stub

			}
		    });

	useExtSaveCB = (CheckBox) findViewById(R.id.useExtSave);
	useExtSaveCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

	    @Override
	    public void onCheckedChanged(CompoundButton buttonView,
		    boolean isChecked) {
		final Editor sEditer = sharedPrefs.edit();
		sEditer.remove("useExtSave");
		sEditer.putBoolean("useExtSave", isChecked);
		sEditer.apply();
	    }
	});
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

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onStart()
     */
    @Override
    public void onStart() {
	super.onStart();

	if (sharedPrefs.getBoolean(PHONELANGUAGE, false))
	    blockSpinner();
	else
	    openSpinner();
    }

    /**
     * Open spinner.
     */
    public void openSpinner() {
	languageSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	    @Override
	    public void onItemSelected(AdapterView<?> arg0, View arg1,
		    int position, long arg3) {
		if (position == 0) {
		    final Editor sEditer = sharedPrefs.edit();
		    sEditer.remove(PHONELANGUAGE);
		    sEditer.putString(LANGUAGE, "de");
		    sEditer.apply();
		    final Configuration config = new Configuration(
			    getResources().getConfiguration());
		    config.locale = Locale.GERMAN;
		    getResources().updateConfiguration(config,
			    getResources().getDisplayMetrics());

		} else {
		    final Editor sEditer = sharedPrefs.edit();
		    sEditer.remove(PHONELANGUAGE);
		    sEditer.putString(LANGUAGE, "en");
		    sEditer.apply();
		    final Configuration config = new Configuration(
			    getResources().getConfiguration());
		    config.locale = Locale.ENGLISH;
		    getResources().updateConfiguration(config,
			    getResources().getDisplayMetrics());

		}

		Toast.makeText(getApplicationContext(),
			R.string.Activity_settings_Toast_ChangeView,
			Toast.LENGTH_LONG).show();
	    }

	    @Override
	    public void onNothingSelected(AdapterView<?> arg0) {
		// optionally do something here
	    }
	});
	try {
	    final TextView tv = (TextView) findViewById(R.id.Activity_settings_language_textView);
	    tv.setTextColor(Color.BLACK);
	    ((TextView) languageSpinner.getChildAt(0))
		    .setTextColor(Color.BLACK);
	    languageSpinner.setEnabled(true);
	    languageSpinner.setClickable(true);
	    if (!Locale.getDefault().getLanguage().equals("de"))
		languageSpinner.setSelection(1);
	    else
		languageSpinner.setSelection(0);
	} catch (final Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

}
