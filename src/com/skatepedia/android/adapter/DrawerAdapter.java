package com.skatepedia.android.adapter;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.skatepedia.android.BasicActivity;
import com.skatepedia.android.DrawerItem;
import com.skatepedia.android.Functions;
import com.skatepedia.android.LexikonActivity;
import com.skatepedia.android.MainActivity;
import com.skatepedia.android.MapActivity;
import com.skatepedia.android.NewTrickActivity;
import com.skatepedia.android.R;
import com.skatepedia.android.SettingActivity;
import com.skatepedia.android.SkateActivity;

@SuppressLint("InflateParams")
public class DrawerAdapter extends BaseAdapter {

    Activity activity;
    private final Context context;
    int currentPos;
    DrawerItem item[] = null;

    public DrawerAdapter(Activity activity, Context context, DrawerItem[] item,
	    int pos) {

	this.context = context;
	this.item = item;
	currentPos = pos;
	this.activity = activity;
    }

    @Override
    public int getCount() {
	return item.length;
    }

    @Override
    public DrawerItem getItem(int position) {
	return item[position];
    }

    @Override
    public long getItemId(int position) {
	return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	if (convertView == null) {
	    final LayoutInflater mInflater = (LayoutInflater) context
		    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    convertView = mInflater.inflate(R.layout.drawer_list_item, null);
	}

	final TextView placeHolder = (TextView) convertView
		.findViewById(R.id.drawerPlaceHolder);
	final TextView txtTitle = (TextView) convertView
		.findViewById(R.id.drawerText);

	txtTitle.setText(item[position].name);
	if (txtTitle.getLineCount() == 2)
	    txtTitle.setHeight(txtTitle.getHeight() * 2);

	if (position == currentPos) {
	    placeHolder.setBackgroundColor(Color.parseColor("#910f0f"));
	    txtTitle.setBackgroundColor(Color.parseColor("#202020"));

	}
	final int pos = position;
	txtTitle.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		Intent i = null;
		if (pos != currentPos) {
		    Log.d("lang ", Locale.getDefault().getLanguage()
			    + "    lang");
		    if (Functions.getLanguage(context).equals("de"))
			switch (pos) {

			case 0:
			    i = new Intent(activity, MainActivity.class);
			    break;

			case 1:

			    i = new Intent(activity, BasicActivity.class);
			    break;

			case 2:
			    i = new Intent(activity, LexikonActivity.class);
			    break;
			case 3:
			    i = new Intent(activity, SkateActivity.class);
			    break;
			case 4:
			    i = new Intent(activity, NewTrickActivity.class);
			    break;
			case 5:
			    i = new Intent(activity, MapActivity.class);
			    break;
			case 6:
			    i = new Intent(activity, SettingActivity.class);
			    break;
			default:
			    return;
			}
		    else
			switch (pos) {
			case 0:
			    i = new Intent(activity, MainActivity.class);
			    break;
			case 1:
			    i = new Intent(activity, BasicActivity.class);
			    break;

			case 2:
			    i = new Intent(activity, SkateActivity.class);
			    break;
			case 3:
			    i = new Intent(activity, NewTrickActivity.class);
			    break;
			case 4:
			    i = new Intent(activity, MapActivity.class);
			    break;
			case 5:
			    i = new Intent(activity, SettingActivity.class);
			    break;
			default:
			    return;
			}
		    i.setFlags((Intent.FLAG_ACTIVITY_NO_ANIMATION));
		    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		    if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN){
				startWithAnimation(i, activity);
		    } else{
			activity.startActivity(i);
		    }
		    
		    activity.finish();
		    activity.overridePendingTransition(0, 0);
		}
	    }
	});
	return convertView;

    }

    @SuppressLint("NewApi")
    public void startWithAnimation(Intent i, Activity activity)
    {
	Bundle bndlanimation = 
		ActivityOptions.makeCustomAnimation(context, R.anim.animation,R.anim.animation2).toBundle();
	activity.startActivity(i,bndlanimation);
    }
    }
