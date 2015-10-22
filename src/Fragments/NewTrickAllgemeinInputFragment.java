package Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.skatepedia.android.R;

// TODO: Auto-generated Javadoc
/**
 * The Class NewTrickAllgemeinInputFragment to get inputs .
 */
public class NewTrickAllgemeinInputFragment extends Fragment {

    /** The Constant DESCRIPTION to save in Bundle.. */
    public final static String DESCRIPTION = "description";

    /** The Constant DIFFICULTY to save in Bundle.. */
    public final static String DIFFICULTY = "difficulty";

    /** The Constant KIND to save in Bundle.. */
    public final static String KIND = "kind";

    /** The Constant NAME to save in Bundle. */
    public final static String NAME = "name";

    /** The view to input description */
    private EditText descriptView;

    /** The spinner to select difficulty. */
    private Spinner difficultySpinner;

    /** The spinner to sind tricktype. */
    private Spinner kindSpinner;

    /** The view to input name. */
    private EditText nameView;

    /**
     * Instantiates a new new trick allgemein input fragment.
     */
    public NewTrickAllgemeinInputFragment() {

    }

    /**
     * Gets the inputs name, description etc.
     *
     * @return the data in a bundle
     */
    public Bundle getData() {
	final Bundle bund = new Bundle();

	try {
	    bund.putString(NAME, nameView.getText().toString());
	    bund.putString(DESCRIPTION, descriptView.getText().toString());
	    bund.putString(DIFFICULTY, difficultySpinner.getSelectedItem()
		    .toString());
	    bund.putString(KIND, kindSpinner.getSelectedItem().toString());
	} catch (final Exception e) {
	    e.printStackTrace();
	    return null;

	}

	return bund;
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
	final View layoutView = inflater.inflate(
		R.layout.fragment_newtrick_allgemein_input, container, false);

	try {
	    nameView = (EditText) layoutView
		    .findViewById(R.id.Activity_NewTrick_nameView);
	    descriptView = (EditText) layoutView
		    .findViewById(R.id.Activity_NewTrick_Description);
	    difficultySpinner = (Spinner) layoutView
		    .findViewById(R.id.schwierigkeitSpinner);

	    kindSpinner = (Spinner) layoutView.findViewById(R.id.artSpinner);

	} catch (final Exception e) {
	    e.printStackTrace();
	}

	return layoutView;
    }

}
