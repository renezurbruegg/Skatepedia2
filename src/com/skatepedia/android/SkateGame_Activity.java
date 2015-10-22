package com.skatepedia.android;

import java.util.ArrayList;
import java.util.TreeMap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class SkateGame_Activity.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SkateGame_Activity extends ActionBarActivity {

    /** The current skater. */
    private Skater currentSkater;

    /** The difficulties. */
    private String[] difficulties;

    Toolbar toolbar;
    /** Flag if easier tricks are OK. */
    private Boolean easyer;

    /** Flag to use just tricks which are learned. */
    private Boolean mastered;

    /** String to show how hard the game should be. */
    private String mode;

    /** The must copy. */
    private Boolean mustCopy = false;

    /** Flag if nollie is OK. */
    private Boolean nollie;

    /** Flag if oldschool tricks are OK. */
    private Boolean oldSchool;

    /** The place. */
    private int place;

    /** ImageView to display S.K.A.T.E letters */
    private ImageView skateImageView;

    /** The View for skater 1. */
    private TextView skater;

    /** The first skater. */
    private Skater skater1;

    /** The second skater. */
    private Skater skater2;

    /** The temporary tricks. */
    private Skatetrick tmpTrick;

    /** The View to display the trick. */
    private TextView trick;

    /** The tricks to perform . */
    private ArrayList<Skatetrick> tricks;

    /**
     * Adds the tricks to the Arraylist.
     *
     * @param tricksMap
     *            the TreeMap with all tricks
     */
    private void addTricks(TreeMap<String, ArrayList<Skatetrick>> tricksMap) {
	tricks = new ArrayList<Skatetrick>();

	if (easyer)
	    for (int i = 0; i <= place; i++) {
		final ArrayList<Skatetrick> tmptrickList = tricksMap
			.get(difficulties[i]);
		for (final Skatetrick skatetrick : tmptrickList)
		    if (mastered) {
			if (skatetrick.mastered())
			    if (oldSchool) {
				tricks.add(new Skatetrick(
					skatetrick.toString(), skatetrick
						.getDescription(), skatetrick
						.getType(), skatetrick
						.getDifficulty(),
					skatetrick.directon));
				tricks.add(new Skatetrick("fakie "
					+ skatetrick.toString(), skatetrick
					.getDescription(),
					skatetrick.getType(), skatetrick
						.getDifficulty(),
					skatetrick.directon));

				if (nollie) {
				    tricks.add(new Skatetrick("switch "
					    + skatetrick.toString(), skatetrick
					    .getDescription(), skatetrick
					    .getType(), skatetrick
					    .getDifficulty(),
					    skatetrick.directon));
				    tricks.add(new Skatetrick("switch "
					    + skatetrick.toString(), skatetrick
					    .getDescription(), skatetrick
					    .getType(), skatetrick
					    .getDifficulty(),
					    skatetrick.directon));
				    tricks.add(new Skatetrick("fakie "
					    + skatetrick.toString(), skatetrick
					    .getDescription(), skatetrick
					    .getType(), skatetrick
					    .getDifficulty(),
					    skatetrick.directon));
				}
			    } else if (skatetrick.getType() != Skatetrick.TRICK_OLD_SCHOOL) {
				tricks.add(new Skatetrick(
					skatetrick.toString(), skatetrick
						.getDescription(), skatetrick
						.getType(), skatetrick
						.getDifficulty(),
					skatetrick.directon));
				tricks.add(new Skatetrick("fakie "
					+ skatetrick.toString(), skatetrick
					.getDescription(),
					skatetrick.getType(), skatetrick
						.getDifficulty(),
					skatetrick.directon));

				if (nollie) {
				    tricks.add(new Skatetrick("switch "
					    + skatetrick.toString(), skatetrick
					    .getDescription(), skatetrick
					    .getType(), skatetrick
					    .getDifficulty(),
					    skatetrick.directon));
				    tricks.add(new Skatetrick("switch "
					    + skatetrick.toString(), skatetrick
					    .getDescription(), skatetrick
					    .getType(), skatetrick
					    .getDifficulty(),
					    skatetrick.directon));
				    tricks.add(new Skatetrick("fakie "
					    + skatetrick.toString(), skatetrick
					    .getDescription(), skatetrick
					    .getType(), skatetrick
					    .getDifficulty(),
					    skatetrick.directon));
				}
			    }
		    } else if (oldSchool) {
			tricks.add(new Skatetrick(skatetrick.toString(),
				skatetrick.getDescription(), skatetrick
					.getType(), skatetrick.getDifficulty(),
				skatetrick.directon));
			tricks.add(new Skatetrick("fakie "
				+ skatetrick.toString(), skatetrick
				.getDescription(), skatetrick.getType(),
				skatetrick.getDifficulty(), skatetrick.directon));

			if (nollie) {
			    tricks.add(new Skatetrick("switch "
				    + skatetrick.toString(), skatetrick
				    .getDescription(), skatetrick.getType(),
				    skatetrick.getDifficulty(),
				    skatetrick.directon));
			    tricks.add(new Skatetrick("switch "
				    + skatetrick.toString(), skatetrick
				    .getDescription(), skatetrick.getType(),
				    skatetrick.getDifficulty(),
				    skatetrick.directon));
			}
		    } else if (skatetrick.getType() != Skatetrick.TRICK_OLD_SCHOOL) {
			tricks.add(new Skatetrick(skatetrick.toString(),
				skatetrick.getDescription(), skatetrick
					.getType(), skatetrick.getDifficulty(),
				skatetrick.directon));
			tricks.add(new Skatetrick("fakie "
				+ skatetrick.toString(), skatetrick
				.getDescription(), skatetrick.getType(),
				skatetrick.getDifficulty(), skatetrick.directon));

			if (nollie) {
			    tricks.add(new Skatetrick("switch "
				    + skatetrick.toString(), skatetrick
				    .getDescription(), skatetrick.getType(),
				    skatetrick.getDifficulty(),
				    skatetrick.directon));
			    tricks.add(new Skatetrick("switch "
				    + skatetrick.toString(), skatetrick
				    .getDescription(), skatetrick.getType(),
				    skatetrick.getDifficulty(),
				    skatetrick.directon));
			    tricks.add(new Skatetrick("fakie "
				    + skatetrick.toString(), skatetrick
				    .getDescription(), skatetrick.getType(),
				    skatetrick.getDifficulty(),
				    skatetrick.directon));
			}
		    }
	    }
	else {
	    final ArrayList<Skatetrick> tmptrickList = tricksMap.get(mode);
	    for (final Skatetrick skatetrick : tmptrickList)
		if (mastered) {
		    if (skatetrick.mastered())
			if (oldSchool) {
			    tricks.add(new Skatetrick(skatetrick.toString(),
				    skatetrick.getDescription(), skatetrick
					    .getType(), skatetrick
					    .getDifficulty(),
				    skatetrick.directon));
			    tricks.add(new Skatetrick("fakie "
				    + skatetrick.toString(), skatetrick
				    .getDescription(), skatetrick.getType(),
				    skatetrick.getDifficulty(),
				    skatetrick.directon));

			    if (nollie) {
				tricks.add(new Skatetrick("switch "
					+ skatetrick.toString(), skatetrick
					.getDescription(),
					skatetrick.getType(), skatetrick
						.getDifficulty(),
					skatetrick.directon));
				tricks.add(new Skatetrick("switch "
					+ skatetrick.toString(), skatetrick
					.getDescription(),
					skatetrick.getType(), skatetrick
						.getDifficulty(),
					skatetrick.directon));
				tricks.add(new Skatetrick("fakie "
					+ skatetrick.toString(), skatetrick
					.getDescription(),
					skatetrick.getType(), skatetrick
						.getDifficulty(),
					skatetrick.directon));
			    }
			} else if (skatetrick.getType() != Skatetrick.TRICK_OLD_SCHOOL) {
			    tricks.add(new Skatetrick(skatetrick.toString(),
				    skatetrick.getDescription(), skatetrick
					    .getType(), skatetrick
					    .getDifficulty(),
				    skatetrick.directon));
			    tricks.add(new Skatetrick("fakie "
				    + skatetrick.toString(), skatetrick
				    .getDescription(), skatetrick.getType(),
				    skatetrick.getDifficulty(),
				    skatetrick.directon));

			    if (nollie) {
				tricks.add(new Skatetrick("switch "
					+ skatetrick.toString(), skatetrick
					.getDescription(),
					skatetrick.getType(), skatetrick
						.getDifficulty(),
					skatetrick.directon));
				tricks.add(new Skatetrick("switch "
					+ skatetrick.toString(), skatetrick
					.getDescription(),
					skatetrick.getType(), skatetrick
						.getDifficulty(),
					skatetrick.directon));
				tricks.add(new Skatetrick("fakie "
					+ skatetrick.toString(), skatetrick
					.getDescription(),
					skatetrick.getType(), skatetrick
						.getDifficulty(),
					skatetrick.directon));
			    }
			}
		} else if (oldSchool) {
		    tricks.add(new Skatetrick(skatetrick.toString(), skatetrick
			    .getDescription(), skatetrick.getType(), skatetrick
			    .getDifficulty(), skatetrick.directon));
		    tricks.add(new Skatetrick("fakie " + skatetrick.toString(),
			    skatetrick.getDescription(), skatetrick.getType(),
			    skatetrick.getDifficulty(), skatetrick.directon));

		    if (nollie) {
			tricks.add(new Skatetrick("switch "
				+ skatetrick.toString(), skatetrick
				.getDescription(), skatetrick.getType(),
				skatetrick.getDifficulty(), skatetrick.directon));
			tricks.add(new Skatetrick("switch "
				+ skatetrick.toString(), skatetrick
				.getDescription(), skatetrick.getType(),
				skatetrick.getDifficulty(), skatetrick.directon));
		    }

		} else if (skatetrick.getType() != Skatetrick.TRICK_OLD_SCHOOL) {
		    tricks.add(new Skatetrick(skatetrick.toString(), skatetrick
			    .getDescription(), skatetrick.getType(), skatetrick
			    .getDifficulty(), skatetrick.directon));
		    tricks.add(new Skatetrick("fakie " + skatetrick.toString(),
			    skatetrick.getDescription(), skatetrick.getType(),
			    skatetrick.getDifficulty(), skatetrick.directon));

		    if (nollie) {
			tricks.add(new Skatetrick("switch "
				+ skatetrick.toString(), skatetrick
				.getDescription(), skatetrick.getType(),
				skatetrick.getDifficulty(), skatetrick.directon));
			tricks.add(new Skatetrick("switch "
				+ skatetrick.toString(), skatetrick
				.getDescription(), skatetrick.getType(),
				skatetrick.getDifficulty(), skatetrick.directon));
			tricks.add(new Skatetrick("fakie "
				+ skatetrick.toString(), skatetrick
				.getDescription(), skatetrick.getType(),
				skatetrick.getDifficulty(), skatetrick.directon));
		    }
		}
	}

    }

    /**
     * Change the kater.
     *
     * @return the boolean if Game should go on
     */
    private Boolean changeSkater() {

	currentSkater = (skater.getText().equals(skater1.getName()) ? skater2
		: skater1);
	switch (currentSkater.getPoints()) {
	case 0:
	    skateImageView.setImageResource(R.drawable.skate0);
	    break;
	case 1:

	    skateImageView.setImageResource(R.drawable.skate1);
	    break;
	case 2:

	    skateImageView.setImageResource(R.drawable.skate2);
	    break;
	case 3:

	    skateImageView.setImageResource(R.drawable.skate3);
	    break;
	case 4:

	    skateImageView.setImageResource(R.drawable.skate4);
	    break;
	default:
	    skateImageView.setImageResource(R.drawable.skate5);
	    return false;
	}

	skater.setText(currentSkater.getName());
	return true;

    }

    /**
     * If Skater failed a Trick.
     *
     * @param v
     *            the v
     */
    public void failed(View v) {
	currentSkater.failed();

	trick.setTextColor(Color.BLACK);
	if (mustCopy) {
	    currentSkater.addSkate();
	    tricks.remove(tmpTrick);
	    tmpTrick = getRandomTrick();
	    trick.setText(tmpTrick.toString());
	    mustCopy = false;

	}

	if (!changeSkater())
	    initloseview();

	tmpTrick = getRandomTrick();
	trick.setText(tmpTrick.toString());
    }

    /**
     * Gets a random trick.
     *
     * @return the random trick
     */
    private Skatetrick getRandomTrick() {
	if (tricks.size() == 0)
	    return (new Skatetrick(getString(R.string.noTricksAvaiable), "", 1,
		    1, 1));
	int high = tricks.size() - 1;
	final int low = 0;
	high++;
	return tricks.get((int) (Math.random() * (high - low) + low));
    }

    /**
     * Init the last View if Game is finished .
     */
    private void initloseview() {
	setContentView(R.layout.skate_finish);
	toolbar = (Toolbar) findViewById(R.id.toolbar);
	setSupportActionBar(toolbar);
	getSupportActionBar().setTitle("S.K.A.T.E");
	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	getSupportActionBar().setHomeButtonEnabled(true);
	((TextView) findViewById(R.id.skater1View)).setText(skater1.getName());
	((TextView) findViewById(R.id.failed1View)).append(skater1.getfails()
		+ "");
	((TextView) findViewById(R.id.succeeded1View)).append(skater1
		.getsucceeds() + "");
	((TextView) findViewById(R.id.SKATE1View)).setText(skater1.getSkate());

	((TextView) findViewById(R.id.skater2View)).setText(skater2.getName());
	((TextView) findViewById(R.id.failed2View)).append(skater2.getfails()
		+ "");
	((TextView) findViewById(R.id.succeeded2View)).append(skater2
		.getsucceeds() + "");
	((TextView) findViewById(R.id.SKATE2View)).setText(skater2.getSkate());

	final Skater winner = (skater1.getPoints() > skater2.getPoints()) ? skater2
		: skater1;
	final Skater looser = (skater1.getPoints() < skater2.getPoints()) ? skater2
		: skater1;
	((TextView) findViewById(R.id.skate1vs2View)).setText(winner.getName()
		+ getString(R.string.haveVs) + looser.getName()
		+ getString(R.string.with) + winner.getSkate()
		+ getString(R.string.to) + looser.getSkate()
		+ getString(R.string.won));

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.skate_game);
	toolbar = (Toolbar) findViewById(R.id.toolbar);
	setSupportActionBar(toolbar);
	getSupportActionBar().setTitle("S.K.A.T.E");
	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	getSupportActionBar().setHomeButtonEnabled(true);
	place = getIntent().getIntExtra("spinner", 1);
	difficulties = getResources().getStringArray(R.array.difficulties);
	mode = difficulties[place];

	String nameSkat1 = getIntent().getStringExtra(SkateActivity.SKATER1);
	if (nameSkat1.equals("") || nameSkat1 == null)
	    nameSkat1 = SkateActivity.SKATER1;
	skater1 = new Skater(nameSkat1);

	final String nameSkat2 = getIntent().getStringExtra(
		SkateActivity.SKATER2);
	if (nameSkat2.equals("") || nameSkat2 == null)
	    nameSkat1 = SkateActivity.SKATER2;
	skater2 = new Skater(nameSkat2);

	skateImageView = (ImageView) findViewById(R.id.skateImageView);
	nollie = getIntent().getBooleanExtra("switch", true);
	easyer = getIntent().getBooleanExtra("easyer", true);
	mastered = getIntent().getBooleanExtra("mastered", true);
	oldSchool = getIntent().getBooleanExtra("oldSchool", true);

	final TreeMap<String, ArrayList<Skatetrick>> tricksMap = Functions
		.sort(Functions.getSavedTricks(this), 2, this);
	addTricks(tricksMap);

	skater = (TextView) findViewById(R.id.skaterView);
	trick = (TextView) findViewById(R.id.TrickView);
	final Typeface type = Typeface.createFromAsset(getAssets(),
		"fonts/dirtyoldtown.ttf");
	trick.setTypeface(type);
	startGame();
    }

    /**
     * Starts the game.
     */
    private void startGame() {
	currentSkater = skater1;
	skater.setText(skater1.getName());
	tmpTrick = getRandomTrick();
	trick.setText(tmpTrick.toString());

    }

    /**
     * if skater performed trick successfull.
     *
     * @param v
     *            the v
     */
    public void succeeded(View v) {

	currentSkater.succeeded();
	if (mustCopy) {
	    tricks.remove(tmpTrick);
	    tmpTrick = getRandomTrick();
	    trick.setText(tmpTrick.toString());
	    mustCopy = false;
	    trick.setTextColor(Color.BLACK);

	} else {

	    mustCopy = true;

	    trick.setTextColor(Color.RED);
	}
	changeSkater();

    }
}
