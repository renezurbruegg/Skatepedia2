package com.skatepedia.android;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.skatepedia.android.markers.SkatepediaMarker;
import com.skatepedia.android.receivers.UpdateReceiver;

/**
 * @author Rene Zurbrügg
 * @version 1.0
 * 
 */

public class Functions {

    /** string to load language from preferences */
    private static final String LANGUAGE = "Language";
    /** path to skatepedia folder */
    public static File mPath = new File((Environment
	    .getExternalStorageDirectory().getAbsolutePath() + "/skatepedia"));
    /** string to load language from preferences */
    public static final String PHONELANGUAGE = "phoneLanguage";
    /** string to load settings from prefrences */
    public static final String SETTING_PREFERENCES = "skatepedia_settings";
    /** int to sort ABC */
    public static final int SORT_ABC = 0;
    /** int to sort Difficult */
    public static final int SORT_DIFFICULTY = 2;
    /** int to sort kind */
    public static final int SORT_KIND = 1;
    /** Boolean used to Log data, shows if app is new started */
    private static Boolean started = false;

    /**
     * Convert your ArrayList to an Array
     * 
     * @param al
     *            ArrayList which should be returned as an Array
     * @return Array with the elements of the Arraylist
     */
    private static Skatetrick[] alToArray(ArrayList<Skatetrick> al) {
	System.err.println(al.size() + " al size");
	if (al.size() == 0)
	    return null;
	final Skatetrick[] trick = new Skatetrick[al.size()];
	for (int i = 0; i < trick.length; i++)
	    trick[i] = al.get(i);
	return trick;
    }

    /**
     * Returns language saved on Phone or Preferences
     * 
     * @return language, de if german or en if english
     */
    public static String getLanguage(Context ctx) {
	if (ctx != null)
	    try {
		final SharedPreferences sp = ctx.getSharedPreferences(

		SETTING_PREFERENCES, Context.MODE_PRIVATE);

		if (sp.getBoolean(PHONELANGUAGE, false))
		    return Locale.getDefault().getLanguage();

		if (sp.getString(LANGUAGE, "de").equals("de")) {

		    final Configuration config = new Configuration(ctx
			    .getResources().getConfiguration());
		    config.locale = Locale.GERMAN;

		    ctx.getResources().updateConfiguration(config,
			    ctx.getResources().getDisplayMetrics());
		    return "de";
		}
		final Configuration config = new Configuration(ctx
			.getResources().getConfiguration());
		config.locale = Locale.ENGLISH;

		ctx.getResources().updateConfiguration(config,
			ctx.getResources().getDisplayMetrics());
		return "en";
	    } catch (final Exception e) {
		e.printStackTrace();
	    }
	return "de";
    }

    /**
     * Create a new Bitmap with rounded Corners
     * 
     * @param bitmap
     *            Bitmap to modify
     * @param pixels
     *            pixel to cut uf in edge
     * @return Bitmap with round corners
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
	final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
		bitmap.getHeight(), Config.ARGB_8888);
	final Canvas canvas = new Canvas(output);

	final int color = 0xff424242;
	final Paint paint = new Paint();
	final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	final RectF rectF = new RectF(rect);
	final float roundPx = pixels;

	paint.setAntiAlias(true);
	canvas.drawARGB(0, 0, 0, 0);
	paint.setColor(color);
	canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

	paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	canvas.drawBitmap(bitmap, rect, rect, paint);

	return output;
    }

    /**
     * 
     * Returns the tricks which are saved in you sharedPreferences
     * 
     * @param activity
     *            current Activity which is running
     * @return Tricks saved in sharedPreferences as Array
     */
    static Skatetrick[] getSavedTricks(Activity activity) {
	final SharedPreferences sharedpreferences = activity
		.getSharedPreferences("skatepedia", Context.MODE_PRIVATE);

	final Map<String, ?> hm = sharedpreferences.getAll();
	final Skatetrick[] tricks = new Skatetrick[hm.size()];
	int c = 0;
	try {
	    for (final Entry<String, ?> entry : hm.entrySet()) {
		final String value = (String) entry.getValue();
		tricks[c] = (Skatetrick) Functions.stringToObject(value);
		c++;
	    }
	} catch (final Exception e) {
	    Log.e("ERROR", "fehler bei getSavedTricks()");
	}
	return tricks;
    }

    public static File getSkatepediaDir() {
	// TODO Auto-generated method stub
	return new File(mPath.getAbsolutePath());
    }

    /**
     * Returns the tricks based on the skateList.txt file on your SD-Card
     * 
     * @param activity
     *            current Activity which is running
     * @return Your tricks in an Array
     * 
     */
    static Skatetrick[] getTricks(Activity activity) {

	final File list = new File(mPath + "/skateList.txt");
	final ArrayList<Skatetrick> tricks = new ArrayList<Skatetrick>();
	try {
	    final FileReader fr = new FileReader(list);

	    if (fr != null) {

		final BufferedReader buffreader = new BufferedReader(fr);
		String line = buffreader.readLine();
		Boolean firstLine = true;
		while (line != null) {
		    if (!firstLine) {
			final String[] splitArray = line.split(";");
			final String content = splitArray[1].replaceAll(":en:",
				System.getProperty("line.separator"));

			Skatetrick newTrick = null;
			try {
			    newTrick = new Skatetrick(

			    splitArray[0], content,
				    Integer.parseInt(splitArray[2].replaceAll(
					    " ", "")),
				    Integer.parseInt(splitArray[3].replaceAll(
					    " ", "")),
				    Integer.parseInt(splitArray[4].replaceAll(
					    " ", "")));
			} catch (final Exception e) {
			}
			;
			if (newTrick != null)
			    tricks.add(newTrick);
		    }
		    firstLine = false;
		    line = buffreader.readLine();
		}
		buffreader.close();

	    }

	    fr.close();
	} catch (final FileNotFoundException fe) {
	    final Thread t = new Thread(new Runnable() {
		@Override
		public void run() {

		    try {
			saveTrickFileOnPhone();
		    } catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
	    });
	    t.start();
	    try {
		t.join();
	    } catch (final InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    return getTricks(activity);

	} catch (final IOException e) {
	    e.printStackTrace();
	}
	return alToArray(tricks);

    }

    /**
     * Check if GPS is enabled
     * 
     * @return True if enabled false if not
     */
    public static boolean GPSisEnabled(Context ctx) {

	final LocationManager locationManager = (LocationManager) ctx

	.getSystemService(Context.LOCATION_SERVICE);

	if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))

	    return true;
	else

	    return false;

    }

    /**
     * Check if Network is Avaiable
     * 
     * @return true if avaiable false if not
     */
    public static boolean isNetworkAvailable(Context ctx) {
	final ConnectivityManager connectivityManager = (ConnectivityManager) ctx
		.getSystemService(Context.CONNECTIVITY_SERVICE);
	final NetworkInfo activeNetworkInfo = connectivityManager
		.getActiveNetworkInfo();
	return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void log(String context, String error) {
	final File f = new File(mPath.getAbsolutePath() + "/ErrorLog.txt");
	FileWriter fw;
	try {

	    if (!started) {
		fw = new FileWriter(f, false);
		fw.write("");
		started = true;
	    }
	    fw = new FileWriter(f, true);
	    final BufferedWriter bufferWritter = new BufferedWriter(fw);
	    bufferWritter.write(context + "      -          " + error + '\n');
	    bufferWritter.close();

	} catch (final IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	Log.d(context, context + "   :   " + error);

    }

    /**
     * 
     * 
     * 
     * 
     * @param obj
     *            Skatetrick which should be compiled to a string
     * @return A String which represent the Skatetrick
     */
    static String ObjToString(Object obj) {
	String out = null;
	if (obj != null)
	    try {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(obj);
		out = new String(Base64Coder.encode(baos.toByteArray()));
	    } catch (final IOException e) {
		Log.e("Functions.String to ob meldet:   ", "IOEXCEPTION");
		e.printStackTrace();
		return null;
	    }
	else
	    Log.e("Functions.String to ob meldet:   ", "OBJ = NULL");
	return out;
    }

    /**
     * register an Alarm to check Update
     * 
     * @param time
     *            time until UpdateCheck
     */
    public static void registerAlarm(Context context, Long time) {

	final SharedPreferences settingPreferences = context
		.getSharedPreferences("Skatepedia_Settings",
			Context.MODE_PRIVATE);

	if (settingPreferences
		.getBoolean(SettingActivity.LOOK_FOR_UPDATE, true)
		&& settingPreferences.getBoolean("saveOnPhone", true)) {

	    final Intent intentAlarm = new Intent(context, UpdateReceiver.class);
	    intentAlarm.setAction(UpdateReceiver.CHECK);
	    final AlarmManager alarmManager = (AlarmManager) context
		    .getSystemService(Context.ALARM_SERVICE);

	    alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent
		    .getBroadcast(context, 1, intentAlarm,
			    PendingIntent.FLAG_UPDATE_CURRENT));

	}
    }

    /** saves TrickFile (skateList.txt) on Phone, throws IOException */
    public static void saveTrickFileOnPhone() throws IOException {

	final URL url = new URL(
		"http://www.skateparkwetzikon.ch/Skatepedia/skateList.txt");

	final BufferedReader buffreader = new BufferedReader(
		new InputStreamReader(url.openStream()));

	final File list = new File(mPath.getAbsolutePath() + "/skateList.txt");
	if (!mPath.exists())
	    mPath.mkdirs();
	if (!list.exists())
	    list.createNewFile();

	final OutputStream outputStream = new BufferedOutputStream(
		new FileOutputStream(list));

	if (url != null) {
	    String line = "";

	    while ((line = buffreader.readLine()) != null) {
		outputStream.write(line.getBytes());
		outputStream.write(System.getProperty("line.separator")
			.getBytes());
	    }

	}

	buffreader.close();
	outputStream.close();

    }

    /**
     * @param wordlist
     *            Arraylist witch contains all words from Lexikon
     * @param text
     *            Text to search in ArrayList
     * @return ArrayList which contains all words with pattern text
     */
    static ArrayList<String> search(ArrayList<String> wordlist, String text) {
	if (text.equals(""))
	    return wordlist;
	final ArrayList<String> al = new ArrayList<String>();
	for (int i = 0; i < wordlist.size(); i++)
	    if (wordlist.get(i).split(";")[0].toLowerCase(Locale.GERMAN)
		    .contains(text.toLowerCase(Locale.GERMAN)))
		al.add(wordlist.get(i));
	return al;

    }

    /**
     * 
     * Returns all Skatetricks which contains the searchpattern
     * 
     * @param tricks
     *            Array with all Skatetricks
     * @param searchPattern
     *            Pattern to search in skatetricks
     * @return skatetricks which contains the pattern
     */
    static Skatetrick[] search(Skatetrick[] tricks, String searchPattern) {
	if (searchPattern.equals(""))
	    return tricks;
	final ArrayList<Skatetrick> al = new ArrayList<Skatetrick>();
	for (int i = 0; i < tricks.length; i++)
	    if (tricks[i].toString().toLowerCase(Locale.GERMAN)
		    .contains(searchPattern.toLowerCase(Locale.GERMAN)))
		al.add(tricks[i]);
	return alToArray(al);
    }

    public static ShowcaseView showShowCaseView(Target target,
	    Activity activity, String title, String text, String id) {
	final SharedPreferences sp = activity.getSharedPreferences(
		"skatepedia-ShowCase", Context.MODE_PRIVATE);
	
	Log.d("SP", "SP= " + sp.getAll().toString());

	if (sp.getBoolean(id, true)) {
	    sp.edit().putBoolean(id, false).apply();
	    final RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(
		    ViewGroup.LayoutParams.WRAP_CONTENT,
		    ViewGroup.LayoutParams.WRAP_CONTENT);
	    lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    final int margin = ((Number) (activity.getResources()
		    .getDisplayMetrics().density * 12)).intValue();
	    lps.setMargins(margin, margin, margin, margin);

	    final ShowcaseView sv = new ShowcaseView.Builder(activity, true)
		    .setTarget(target).setContentTitle(title)
		    .setContentText(text).build();
	    sv.setButtonPosition(lps);
	    return sv;
	}
	return null;

    }

    /** Shows A showcaseview over View v with onClickListener */
    public static ShowcaseView showShowCaseView(Target target,
	    Activity activity, String title, String text, String id,
	    OnClickListener okl) {
	final SharedPreferences sp = activity.getSharedPreferences(
		"skatepedia-ShowCase", Context.MODE_PRIVATE);
	Log.d("SP", "SP= " + sp.getAll().toString());

	if (sp.getBoolean(id, true)) {
	    sp.edit().putBoolean(id, false).apply();
	    final RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(
		    ViewGroup.LayoutParams.WRAP_CONTENT,
		    ViewGroup.LayoutParams.WRAP_CONTENT);
	    lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    final int margin = ((Number) (activity.getResources()
		    .getDisplayMetrics().density * 12)).intValue();
	    lps.setMargins(margin, margin, margin, margin);

	    final ShowcaseView sv = new ShowcaseView.Builder(activity, true)
		    .setTarget(target).setContentTitle(title)
		    .setContentText(text).setOnClickListener(okl).build();
	    sv.setButtonPosition(lps);
	    return sv;
	}
	return null;

    }

    /** Shows A showvcaseView over a View v */
    static ShowcaseView showShowCaseView(View v, Activity activity,
	    String title, String text) {
	final SharedPreferences sp = activity.getSharedPreferences(
		"skatepedia-ShowCase ", Context.MODE_PRIVATE);
	if (sp.getBoolean(v.getId() + "View", true)) {
	    sp.edit().putBoolean(v.getId() + "View", false).apply();
	    Log.d("ID", "ID=  " + v.getId() + "");
	    final RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(
		    ViewGroup.LayoutParams.WRAP_CONTENT,
		    ViewGroup.LayoutParams.WRAP_CONTENT);
	    lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    final int margin = ((Number) (activity.getResources()
		    .getDisplayMetrics().density * 12)).intValue();
	    lps.setMargins(margin, margin, margin, margin);

	    final ViewTarget target = new ViewTarget(v);
	    final ShowcaseView sv = new ShowcaseView.Builder(activity, true)
		    .setTarget(target).setContentTitle(title)
		    .setContentText(text).hideOnTouchOutside().build();
	    sv.setButtonPosition(lps);
	    sv.setAlpha((float) 0.1);

	    return sv;
	}
	return null;
    }

    
       public static ShowcaseView showShowCaseView(View v, Activity activity,
	    String title, String text, int posX, int posY) {
	final SharedPreferences sp = activity.getSharedPreferences(
		"skatepedia-ShowCase", Context.MODE_PRIVATE);
	if (sp.getBoolean(v.getId() + "View", true)) {
	    sp.edit().putBoolean(v.getId() + "View", false).apply();
	    final RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(
		    ViewGroup.LayoutParams.WRAP_CONTENT,
		    ViewGroup.LayoutParams.WRAP_CONTENT);
	    lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    final int margin = ((Number) (activity.getResources()
		    .getDisplayMetrics().density)).intValue();
	    lps.setMargins(margin, margin, margin, margin);

	    new ViewTarget(v);
	    final ShowcaseView sv = new ShowcaseView.Builder(activity, true)
		    // .setTarget(target)
		    .setContentTitle("Navigation Drawer")
		    .setContentText("Click here to open the navigation Drawer")
		    .hasManualPosition(true).xPostion(posX).yPostion(posY)
		    .build();
	    sv.setButtonPosition(lps);
	    return sv;
	}
	return null;
    }

    /**
     * Sort your Skatetricks and returns them in a TreeMap
     * 
     * @param tricks
     *            An Array with the Skatetricks
     * @param directon
     *            How to Sort (0 = Alphabet 1=Kind (flip/manual/etc.
     *            2=Difficulty)
     * @return Sorted tricks in a TreeMap ("HeaderName",ArrayList sorted
     *         Tricks")
     */
    static TreeMap<String, ArrayList<Skatetrick>> sort(Skatetrick[] tricks,
	    int directon, Activity mActivity) {

	char currentChar;

	final TreeMap<String, ArrayList<Skatetrick>> map = new TreeMap<String, ArrayList<Skatetrick>>();

	if (tricks != null) {

	    for (int i = 0; i < tricks.length; i++)
		
	    Arrays.sort(tricks);
	    switch (directon) {
	    // Kind(flip etc.)------------------------------------------------

	    case SORT_ABC:

		final ArrayList<Skatetrick> tL1 = new ArrayList<Skatetrick>();
		final ArrayList<Skatetrick> tL2 = new ArrayList<Skatetrick>();
		final ArrayList<Skatetrick> tL3 = new ArrayList<Skatetrick>();
		final ArrayList<Skatetrick> tL4 = new ArrayList<Skatetrick>();
		final ArrayList<Skatetrick> tL5 = new ArrayList<Skatetrick>();
		final ArrayList<Skatetrick> tL6 = new ArrayList<Skatetrick>();
		final ArrayList<Skatetrick> tL7 = new ArrayList<Skatetrick>();
		Arrays.sort(tricks);

		for (int i = 0; i < tricks.length; i++)
		    switch (tricks[i].getType()) {
		    case 0:
			tL1.add(tricks[i]);
			break;
		    case 1:
			tL2.add(tricks[i]);
			break;
		    case 2:
			tL3.add(tricks[i]);
			break;
		    case 3:
			tL4.add(tricks[i]);
			break;
		    case 4:
			tL5.add(tricks[i]);
			break;
		    case 5:
			tL6.add(tricks[i]);
			break;
		    case 6:
			tL7.add(tricks[i]);
			break;

		    }

		map.put(Skatetrick.getTypeAsString(0), tL1);
		map.put(Skatetrick.getTypeAsString(1), tL2);
		map.put(Skatetrick.getTypeAsString(2), tL3);
		map.put(Skatetrick.getTypeAsString(3), tL4);
		map.put(Skatetrick.getTypeAsString(4), tL5);
		map.put(Skatetrick.getTypeAsString(5), tL6);
		return map;
		// alphabetic -------------------------------------------------
	    case SORT_KIND:
		ArrayList<Skatetrick> al = new ArrayList<Skatetrick>();
		currentChar = tricks[0].toString().toLowerCase(Locale.GERMAN)
			.charAt(0);
		for (int i = 0; i < tricks.length; i++) {

		    if (currentChar != tricks[i].toString()
			    .toLowerCase(Locale.GERMAN).charAt(0)) {
			map.put(currentChar + "".toUpperCase(Locale.GERMAN), al);
			currentChar = tricks[i].toString()
				.toLowerCase(Locale.GERMAN).charAt(0);
			al = new ArrayList<Skatetrick>();
		    }
		    al.add(tricks[i]);

		}
		map.put(currentChar + "".toUpperCase(Locale.GERMAN), al);
		return map;

		// Difficult------------------------------------------------
	    case SORT_DIFFICULTY:
		final ArrayList<Skatetrick> tl1 = new ArrayList<Skatetrick>();
		final ArrayList<Skatetrick> tl2 = new ArrayList<Skatetrick>();
		final ArrayList<Skatetrick> tl3 = new ArrayList<Skatetrick>();
		final ArrayList<Skatetrick> tl4 = new ArrayList<Skatetrick>();

		for (int i = 0; i < tricks.length; i++)
		    switch (tricks[i].getDifficulty()) {
		    case 1:
			tl1.add(tricks[i]);
			break;
		    case 2:
			tl2.add(tricks[i]);
			break;
		    case 3:
			tl3.add(tricks[i]);
			break;
		    case 4:
			tl4.add(tricks[i]);
			break;
		    }
		final String[] array = mActivity.getResources().getStringArray(
			R.array.difficulties);
		map.put(array[0], tl1);
		map.put(array[1], tl2);
		map.put(array[2], tl3);
		map.put(array[3], tl4);
		return map;

	    }
	}
	return null;
    }

    /** Converts a String to s SkatepediaMarker */
    static SkatepediaMarker stringToMarker(String s) {
	final String[] splittedString = s.split(";");
	SkatepediaMarker spm = null;
	try {
	    String de_comments = "";
	    String en_comments = "";
	    if (splittedString.length == 8)
		de_comments = splittedString[7];
	    else if (splittedString.length >= 9)
		en_comments = splittedString[8];

	    spm = new SkatepediaMarker(splittedString[0], splittedString[1],
		    Double.parseDouble(splittedString[2].split(":")[0]),
		    Double.parseDouble(splittedString[2].split(":")[1]),
		    Float.parseFloat(splittedString[3]),
		    Integer.parseInt(splittedString[4]), splittedString[5],
		    splittedString[6], de_comments, en_comments);
	} catch (final Exception e) {
	    e.printStackTrace();
	}
	;
	return spm;

    }

    /**
     * @param str
     *            String which represent the skatetrick
     * @return Skatetrick which was saved as String
     */
    public static Object stringToObject(String str) {
	Object out = null;
	if (str != null)
	    try {
		final ByteArrayInputStream bios = new ByteArrayInputStream(
			Base64Coder.decode(str));
		final ObjectInputStream ois = new ObjectInputStream(bios);
		out = ois.readObject();
	    } catch (final IOException e) {
		return null;
	    } catch (final ClassNotFoundException e) {
		return null;
	    }
	return out;

    }

    /**
     * @param trick
     *            Trick which should be updated
     */
    public static void updateTrick(Skatetrick trick, Activity mActivity) {
	final SharedPreferences sharedpreferences = mActivity
		.getSharedPreferences("skatepedia", Context.MODE_PRIVATE);

	final Editor edit = sharedpreferences.edit();
	edit.remove(trick.toString());
	edit.putString(trick.toString(), ObjToString(trick));
	edit.commit();

    }

}
