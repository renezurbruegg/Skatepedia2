package com.skatepedia.android.adapter;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.idunnololz.widgets.AnimatedExpandableListView.AnimatedExpandableListAdapter;
import com.skatepedia.android.Functions;
import com.skatepedia.android.MainActivity;
import com.skatepedia.android.R;
import com.skatepedia.android.Skatetrick;
import com.skatepedia.android.TrickActivity;

public class ExpandableListAdapter extends AnimatedExpandableListAdapter {

    private final Context _context;
    // child data in format of header title, child title
    private final HashMap<String, List<Skatetrick>> _listDataChild;
    private final List<String> _listDataHeader; // header titles
    private float coordY;
    private boolean init = false;
    private final MainActivity ma;
    private View trickView;;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
	    HashMap<String, List<Skatetrick>> listDataChild, MainActivity ma) {
	_context = context;
	_listDataHeader = listDataHeader;
	_listDataChild = listDataChild;
	this.ma = ma;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
	return _listDataChild.get(_listDataHeader.get(groupPosition)).get(
		childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
	return childPosition;
    }

    @Override
    public Object getGroup(int groupPosition) {
	return _listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
	return _listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
	return groupPosition;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
	    View convertView, ViewGroup parent) {
	final String headerTitle = (String) getGroup(groupPosition);
	if (convertView == null) {
	    final LayoutInflater infalInflater = (LayoutInflater) _context
		    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    convertView = infalInflater.inflate(R.layout.list_group, null);
	}

	final TextView lblListHeader = (TextView) convertView
		.findViewById(R.id.lblListHeader);
	lblListHeader.setTypeface(null, Typeface.BOLD);
	lblListHeader.setText(headerTitle);
	if (!init) {
	    coordY = convertView.getX() + convertView.getHeight();
	    init = true;
	}
	return convertView;
    }

    public Point getPoint() {
	return new Point(
		(ma.getResources().getDisplayMetrics().widthPixels / 2),
		(int) coordY);
    }

    public View getRandTrickView() {
	// TODO Auto-generated method stub
	return trickView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
	return _listDataChild.get(_listDataHeader.get(groupPosition)).size();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getRealChildView(final int groupPosition,
	    final int childPosition, boolean isLastChild, View convertView,
	    ViewGroup parent) {

	final String childText = getChild(groupPosition, childPosition)
		.toString();
	final Skatetrick trick = ((Skatetrick) getChild(groupPosition,
		childPosition));

	if (convertView == null) {
	    final LayoutInflater infalInflater = (LayoutInflater) _context
		    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    convertView = infalInflater.inflate(R.layout.list_item, null);
	}

	final TextView txtListChild = (TextView) convertView
		.findViewById(R.id.lblListItem);

	txtListChild.setText(childText);
	txtListChild.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {

		final Intent intent = new Intent(ma, TrickActivity.class);
		intent.putExtra("trick",
			(Skatetrick) getChild(groupPosition, childPosition));
		ma.startActivity(intent);

	    }
	});
	final TextView tv = (TextView) convertView
		.findViewById(R.id.ActivityMainListItemDifficultTextView);
	tv.setText(((Skatetrick) getChild(groupPosition, childPosition))
		.getDifficultyAsString(ma.getApplicationContext()));
	tv.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View arg0) {
		final Intent intent = new Intent(ma, TrickActivity.class);
		intent.putExtra("trick",
			(Skatetrick) getChild(groupPosition, childPosition));
		ma.startActivity(intent);

	    }
	});
	final CheckBox cb = (CheckBox) convertView
		.findViewById(R.id.checkBoxlistItem);

	cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

	    @Override
	    public void onCheckedChanged(CompoundButton buttonView,
		    boolean isChecked) {
		trick.setMastered(isChecked);
		Functions.updateTrick(trick, ma);
	    }
	});
	try {
	    cb.setChecked(((Skatetrick) getChild(groupPosition, childPosition))
		    .mastered());
	} catch (final Exception e) {
	    Log.e("WTF", getChild(groupPosition, childPosition) + "");
	}
	;
	if (groupPosition == 0 && childPosition == 0)
	    trickView = txtListChild;

	return convertView;
    }

    @Override
    public boolean hasStableIds() {
	return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
	return true;
    }

}
