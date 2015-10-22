package Fragments;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.skatepedia.android.Functions;
import com.skatepedia.android.MapActivity;
import com.skatepedia.android.R;

// TODO: Auto-generated Javadoc
/**
 * The Class MarkerTabCommentsFragment to display comments.
 */
public class MarkerTabCommentsFragment extends Fragment {

    /**
     * Updates Comments from selected Marker using php
     *
     * @author Rene Zurbrügg
     * 
     * 
     */
    private class updateCommentsTask extends AsyncTask<String, Void, Void> {
	// TYPE;ID;COMMENTS
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
	 */
	@Override
	protected Void doInBackground(String... comment) {

	    final HttpClient httpclient = new DefaultHttpClient();
	    final HttpPost httppost = new HttpPost(
		    "http://www.skateparkwetzikon.ch/Skatepedia/Map/UpdateComments.php");

	    final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    try {
		nameValuePairs.add(new BasicNameValuePair("database",
			MapActivity.DATABASE[type]));

		nameValuePairs.add(new BasicNameValuePair("ID", id));
		nameValuePairs.add(new BasicNameValuePair("language", Functions
			.getLanguage(getActivity())));
		Functions.log("updateCommentsLanguage",
			Functions.getLanguage(getActivity()));
		nameValuePairs.add(new BasicNameValuePair("comments",
			comment[0]));
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		final HttpResponse response = httpclient.execute(httppost);

		final BufferedReader in = new BufferedReader(
			new InputStreamReader(response.getEntity().getContent()));

		String line = "";
		while ((line = in.readLine()) != null)
		    Functions.log("Antwort des Servers:     ", line);
		in.close();

	    } catch (final Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    return null;

	}

    }

    /** The View to input the comment */
    EditText comment;

    /** The comments. */
    String comments;

    /** The ID. */
    String id;

    /** The layout view. */
    View layoutView;

    /** The View to input the name . */
    EditText name;

    /** The type of the marker. */
    int type;

    /**
     * Instantiates a new marker tabcommentsfragment.
     */
    public MarkerTabCommentsFragment() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	final Bundle arguments = getArguments();
	id = arguments.getString(MarkerOverViewFragment.ARG_ID);
	type = arguments.getInt(MarkerOverViewFragment.ARG_TYPE);
	layoutView = inflater.inflate(R.layout.fragment_marker_comments,
		container, false);
	comments = arguments.getString(MarkerOverViewFragment.ARG_COMMENTS);
	showComments(arguments.getString(MarkerOverViewFragment.ARG_COMMENTS));
	name = (EditText) layoutView.findViewById(R.id.InputPersonName);
	comment = (EditText) layoutView.findViewById(R.id.InputBeitrag);

	layoutView.findViewById(R.id.InputButton).setOnClickListener(
		new OnClickListener() {

		    @Override
		    public void onClick(View v) {
			submitComments(name.getText().toString(), comment
				.getText().toString());

		    }
		});
	return layoutView;
    }

    /**
     * Show comments
     *
     * @param commentsAsString
     *            the commentsasstring
     */
    public void showComments(String commentsAsString) {

	final LinearLayout ll = (LinearLayout) layoutView
		.findViewById(R.id.CommentsShowLayout);
	if (ll.getChildCount() > 0)
	    ll.removeAllViews();

	ll.setOrientation(LinearLayout.VERTICAL);
	if (ll.getChildCount() > 0)
	    ll.removeAllViews();
	Functions.log("ShowComments", commentsAsString + " show commentds");
	ll.setPadding(20, 0, 0, 10);
	final String answer = commentsAsString;
	Boolean white = false;
	if (!answer.contains("--")) {
	    final TextView tv = new TextView(getActivity());
	    tv.setText(answer);
	    ll.addView(tv);
	} else {
	    final String[] names_comments = answer.split("--");

	    for (int i = 0; i < names_comments.length; i++)
		try {
		    if (i == 0) {

			final TableRow row = new TableRow(getActivity());

			final TextView tv = new TextView(getActivity());

			final TableRow.LayoutParams lp = new TableRow.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			row.setLayoutParams(lp);
			final TableRow.LayoutParams lp2 = new TableRow.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			tv.setLayoutParams(lp2);
			tv.setText(getResources().getString(
				R.string.Activity_Map_Comments));
			row.setGravity(Gravity.CENTER);
			tv.setGravity(Gravity.CENTER);
			tv.setTextColor(Color.parseColor("#9d0606"));
			tv.setPadding(0, 10, 20, 10);
			row.addView(tv);
			ll.addView(row);

		    } else {
			final TableRow row = new TableRow(getActivity());

			final View v = new View(getActivity());

			final TableRow.LayoutParams lp = new TableRow.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			row.setLayoutParams(lp);
			final TableRow.LayoutParams lp2 = new TableRow.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
			lp2.height = 1;
			v.setLayoutParams(lp2);
			v.setBackgroundColor(Color.parseColor("#666666"));
			row.addView(v);
			ll.addView(row);
		    }

		    final LinearLayout lay = new LinearLayout(getActivity());
		    lay.setLayoutParams(new LinearLayout.LayoutParams(
			    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
			    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

		    Functions.log("ShowComments", "names_comments :"
			    + names_comments[i]);
		    final TableRow row = new TableRow(getActivity());

		    final String name = names_comments[i].split("-")[0];
		    final String comment = names_comments[i].split("-")[1];
		    Functions.log("ShowComments", "name:" + name + " comment  "
			    + comment);

		    final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
			    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
			    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		    row.setLayoutParams(lp);
		    final TextView nameView = new TextView(getActivity());
		    nameView.setText(name + "       ");
		    nameView.setPadding(20, 10, 20, 10);
		    final TextView commentView = new TextView(getActivity());
		    if (!white) {
			lay.setBackgroundColor(Color.parseColor("#dedede"));
			white = true;

		    } else
			white = false;

		    commentView.setText(comment);
		    row.addView(nameView);
		    row.addView(commentView);
		    lay.addView(row);
		    ll.addView(lay);
		} catch (final Exception e) {
		    e.printStackTrace();
		}
	}

    }

    /**
     * Submit comments.
     *
     * @param name
     *            the name
     * @param kommentar
     *            the comment
     */
    public void submitComments(String name, String kommentar) {

	if (kommentar
		.contains(getString(R.string.activityMapNewCommentStringTextBox))) {
	    name = "";
	    kommentar = "";
	}

	if (!name.equals("")
		&& !kommentar.equals("")
		&& !name.equals(getString(R.string.activityMapNewCommentNameStringTextBox))
		&& !kommentar
			.equals(getString(R.string.activityMapNewCommentStringTextBox))) {
	    name.replace(";", ":");
	    name.replace("-", "_");
	    name.replace("//", "/ /");

	    kommentar.replace(";", ":");
	    kommentar.replace("-", "_");
	    kommentar.replace("//", "/ /");
	    final updateCommentsTask uct = new updateCommentsTask();
	    uct.execute(name + "-" + kommentar + "--");
	}

	this.name.setText("");
	comment.setText("");

	if (comments.contains("noch keine Kommentare vorhan")
		|| comments.contains("No comments available"))
	    comments = name + "-" + kommentar + "--";
	else
	    comments = comments + name + "-" + kommentar + "--";

	showComments(comments);

    }

}