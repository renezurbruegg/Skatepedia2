package com.skatepedia.android.adapter;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar.TabListener;

import com.skatepedia.android.R;

@SuppressLint("InflateParams")
public class TrickTabListener implements TabListener {

    Fragment fragment;

    public TrickTabListener(Fragment fragment) {
	this.fragment = fragment;
    }

    @Override
    public void onTabReselected(android.support.v7.app.ActionBar.Tab arg0,
	    android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabSelected(android.support.v7.app.ActionBar.Tab arg0,
	    android.support.v4.app.FragmentTransaction ft) {
	ft.replace(R.id.fragment_container, fragment);

    }

    @Override
    public void onTabUnselected(android.support.v7.app.ActionBar.Tab arg0,
	    android.support.v4.app.FragmentTransaction ft) {
	// ft.remove(fragment);

    }

}