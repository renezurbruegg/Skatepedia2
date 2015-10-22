package Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.skatepedia.android.R;

// TODO: Auto-generated Javadoc
/**
 * The Class TrickFragment. Shows a Picture and an Image
 */
public class TrickFragment extends Fragment implements OnClickListener {

    /** The Constant EXTRA_MESSAGE to get Text. */
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    /** The Constant EXTRA_PARCELABLE to get Image. */
    public static final String EXTRA_PARCELABLE = "PARCELABLE";

    /** The imageview to show the image. */
    ImageView iv;

    /** The textView to show the text. */
    TextView tv;

    /**
     * Change image and text.
     *
     * @param message
     *            the text
     * @param bmp
     *            the image
     */
    public void changeImageAndText(String message, Bitmap bmp) {
	tv.setText(message);
	iv.setImageBitmap(bmp);
	final android.view.ViewGroup.LayoutParams lp = iv.getLayoutParams();

	final WindowManager wm = (WindowManager) getActivity()
		.getSystemService(Context.WINDOW_SERVICE);
	final Display display = wm.getDefaultDisplay();
	final Point size = new Point();
	display.getSize(size);
	Log.d("Debug",
		size.x + " ; " + bmp.getHeight() + " ; " + bmp.getWidth());

	final int width = lp.width = (int) ((size.x) * (0.833));
	Log.d("Debug",
		size.x + " ; " + bmp.getHeight() + " ; " + bmp.getWidth()
			+ " ; " + width);
	final double constant = ((width) / bmp.getWidth());
	Log.d("Debug", "cons  " + constant);

	lp.height = (int) (constant * bmp.getHeight());
	Log.d("context w, h", lp.width + "  " + lp.height);
	iv.setLayoutParams(lp);
	iv.invalidate();

	tv.invalidate();
    }

    /**
     * Inits the Fragment.
     *
     * @param string
     *            the text to display
     * @param b
     *            the image to display
     * @return the fragment with iamge and text
     */
    public Fragment init(String string, Bitmap b) {
	final TrickFragment f = new TrickFragment();

	final Bundle bdl = new Bundle();
	bdl.putString(EXTRA_MESSAGE, string);
	bdl.putParcelable(EXTRA_PARCELABLE, b);
	f.setArguments(bdl);

	return f;

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
	if (tv != null)
	    tv.setVisibility(tv.isShown() ? View.GONE : View.VISIBLE);
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

	final String message = getArguments().getString(EXTRA_MESSAGE);
	final Bitmap bitmap = getArguments().getParcelable(EXTRA_PARCELABLE);
	final View v = inflater.inflate(R.layout.trick_fragment_layout,
		container, false);

	tv = (TextView) v.findViewById(R.id.trick_fragment_textView_1);

	tv.setOnClickListener(this);
	iv = (ImageView) v.findViewById(R.id.trick_fragment_imageView_1);

	iv.setOnClickListener(this);

	changeImageAndText(message, bitmap);
	return v;

    }

}
