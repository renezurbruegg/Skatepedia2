package Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.skatepedia.android.Functions;
import com.skatepedia.android.R;

// TODO: Auto-generated Javadoc
/**
 * Fragment with HTML-Pages to display in viewPager.
 */
public class BasicFragment extends Fragment {

    /** Constant to get flag for stream from Arguments. */
    public static final String ARG_STREAM = "stream";

    /** Constant to get URL from Arguments. */
    public static final String ARG_URL = "url";

    /**
     * Inits the basicFragment.
     *
     * @param text
     *            the filename
     * @param stream
     *            flag if data should be streamed
     * @return the Fragment to display
     */
    public static BasicFragment init(String text, Boolean stream) {
	final BasicFragment bf = new BasicFragment();
	final Bundle args = new Bundle();
	if (stream)
	    text = "http://www.skateparkwetzikon.ch/Skatepedia/" + text;
	else
	    text = Functions.getSkatepediaDir().getAbsolutePath() + "/" + text;
	Log.d("basicFragPath", "Path: " + text);
	args.putString(ARG_URL, text);
	args.putBoolean(ARG_STREAM, stream);
	bf.setArguments(args);

	return bf;

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
	final Bundle args = getArguments();
	final View layoutView = inflater.inflate(R.layout.basic_fragment,
		container, false);
	final WebView webView = (WebView) layoutView
		.findViewById(R.id.fragment_basic_webview);
	webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
	webView.getSettings().setLoadWithOverviewMode(true);
	webView.getSettings().setUseWideViewPort(true);

	if (args.getBoolean(ARG_STREAM)) {
	    webView.clearCache(true);
	    webView.loadUrl(args.getString(ARG_URL));
	} else {
	    webView.setWebViewClient(new WebViewClient() {
		@Override
		public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		    Toast.makeText(getActivity(), R.string.Err_not_found,
			    Toast.LENGTH_LONG).show();
		    Functions.registerAlarm(getActivity(), (long) 1000);
		    webView.destroy();
		}
	    });
	    webView.loadUrl("file://" + args.getString(ARG_URL));
	}
	return layoutView;
    }

}
