package com.skatepedia.android.adapter;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.idunnololz.widgets.AnimatedExpandableListView.AnimatedExpandableListAdapter;
import com.skatepedia.android.MapActivity;
import com.skatepedia.android.R;
import com.skatepedia.android.markers.SkatepediaMarker;

@SuppressLint("InflateParams")
public class MapExpListAdapter extends AnimatedExpandableListAdapter {

    private final Context _context;
    // child data in format of header title, child title
    private final HashMap<String, List<SkatepediaMarker>> _listDataChild;
    private final List<String> _listDataHeader; // header titles
    View lastView; // Used For tutorial focusing
    MapActivity mapActivity;

    public MapExpListAdapter(Context context, List<String> listDataHeader,
	    HashMap<String, List<SkatepediaMarker>> listDataChild,
	    MapActivity mapActivity) {
	_context = context;
	_listDataHeader = listDataHeader;
	_listDataChild = listDataChild;
	this.mapActivity = mapActivity;
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
    public int getRealChildrenCount(int groupPosition) {
	return _listDataChild.get(_listDataHeader.get(groupPosition)).size();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getRealChildView(final int groupPosition, final int childPosition,
	    boolean isLastChild, View convertView, ViewGroup parent) {

	final String childText = ((SkatepediaMarker) getChild(groupPosition,
		childPosition)).getTitle();
	final SkatepediaMarker marker = ((SkatepediaMarker) getChild(
		groupPosition, childPosition));

	if (convertView == null) {
	    final LayoutInflater infalInflater = (LayoutInflater) _context
		    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    convertView = infalInflater.inflate(R.layout.mapexplist_item, null);
	}

	final TextView txtListChild = (TextView) convertView
		.findViewById(R.id.lblListItem);

	if (marker.clickable) {
	    txtListChild.setText(childText + "  "
		    + Math.round(marker.getDistance() * Math.pow(10, 2))
		    / Math.pow(10, 2) + "km");
	    txtListChild.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
		    marker.zoomMap();
		    marker.getActivity().markerClick(marker);

		}
	    });
	    final RatingBar rb = (RatingBar) convertView
		    .findViewById(R.id.ratingBar);
	    rb.setRating(marker.getRating());
	    final ImageView iv = (ImageView) convertView
		    .findViewById(R.id.lbllistIcon);

	    switch (marker.getType()) {

	    case SkatepediaMarker.MARKER_PARK:
		iv.setImageResource(R.drawable.park_marker);

		break;
	    case SkatepediaMarker.MARKER_SHOP:
		iv.setImageResource(R.drawable.shop_marker);
		break;
	    default:
		if (isLastChild)
		    lastView = convertView;
		break;
	    }
	}
	return convertView;
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

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
	    View convertView, ViewGroup parent) {
	final String headerTitle = (String) getGroup(groupPosition);
	if (convertView == null) {
	    final LayoutInflater infalInflater = (LayoutInflater) _context
		    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    convertView = infalInflater.inflate(R.layout.list_group_map, null);
	}

	final TextView lblListHeader = (TextView) convertView
		.findViewById(R.id.lblListHeader);
	lblListHeader.setTypeface(null, Typeface.BOLD);
	lblListHeader.setText(headerTitle);
	return convertView;
    }

    public View getLastView() {
	return lastView;

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
