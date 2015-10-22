package com.skatepedia.android.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TrickActivityPageAdapter extends FragmentPagerAdapter {

    private final List<Fragment> frames;

    public TrickActivityPageAdapter(FragmentManager fm,
	    ArrayList<Fragment> fragments) {

	super(fm);
	frames = fragments;

    }

    @Override
    public int getCount() {

	return frames.size();

    }

    @Override
    public Fragment getItem(int position) {

	return frames.get(position);

    }

    @Override
    public CharSequence getPageTitle(int position) {
	if (position == 0)
	    return "Video";
	else
	    return "bild";

    }

}
