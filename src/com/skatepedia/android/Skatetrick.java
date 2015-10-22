package com.skatepedia.android;

import java.io.Serializable;

import android.content.Context;

/**
 * This class models a Skatetrick with name and description.
 * 
 * 
 * @author René Zurbrügg
 * @version 1.0
 * 
 */

public class Skatetrick implements Comparable<Object>, Serializable {
    private static final int DIFFICULTY_EASY = 1;
    private static final int DIFFICULTY_HARD = 3;
    private static final int DIFFICULTY_MEDIUM = 2;
    private static final int DIFFICULTY_REALLY_HARD = 4;
    /**0
     * 
     */
    private static final long serialVersionUID = -2684767165201320947L;
    public static final int TRICK_FLIP = 1;
    public static final int TRICK_GRAB = 5;
    public static final int TRICK_GRIND = 4;
    public static final int TRICK_OLD_SCHOOL = 6;
    public static final int TRICK_OLLIE = 0;
    public static final int TRICK_SLIDE = 3;
    public static final int TRICK_SPIN = 2;

    /**
     * @param type
     *            type of trick (Fliptrick etc.)
     * @return type as String (0=Ollie, 1=Fliptrick etc)
     */
    static String getTypeAsString(int type) {
	switch (type) {
	case TRICK_OLLIE:
	    return "Ollies";
	case TRICK_FLIP:
	    return "Flips";
	case TRICK_SPIN:
	    return "Old-Schools";
	case TRICK_SLIDE:
	    return "Slides";
	case TRICK_GRIND:
	    return "Grinds";
	case TRICK_GRAB:
	    return "Grabs";
	case TRICK_OLD_SCHOOL:
	    return "Old-Schools";
	}
	return "FEHLER BEI TYPE";

    }

    private String description = "";
    private int difficulty = 1;
    int directon = 0;
    private boolean mastered = false;
    private String name = "";

    private int type = 0; //

    /**
     * Constructor of Skatetrick
     * 
     * @param name
     *            Name of the trick
     * @param description
     *            DDescription ( How to perform it)
     * @param type
     *            Type of the Trick(0=ollie,1=Fliptrick, 2=Spin-Trick 3=Slide
     *            4=Grind 5=Grab 6=Old-School
     * @param difficulty
     *            Hardness; 1=easy 2=Medium 3=Hard 4=really hard
     */
    Skatetrick(String name, String description, int type, int difficulty) {
	this.name = name;
	this.description = description;
	this.type = type;
	this.difficulty = difficulty;
    }

    /**
     * @param name
     *            Name of the trick
     * @param description
     *            DDescription ( How to perform it)
     * @param type
     *            Type of the Trick(0=ollie,1=Fliptrick, 2=Spin-Trick 3=Slide
     *            4=Grind 5=G
     * @param directon
     *            Direction; 0=normal 1=switch 2=fakie 3=Nollie
     */
    Skatetrick(String name, String description, int type, int difficulty,
	    int directon) {
	this.name = name;
	this.description = description;
	this.type = type;
	this.difficulty = difficulty;
	this.directon = directon;
    }

    /**
     * Change the name of the current Skatetrick and return it
     * 
     * @param name
     *            New name for Skatetrick
     * 
     */
    public void changeName(String name) {
	this.name = name;

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Object o) {
	return name.compareTo(o.toString());
    }

    /**
     * @return the current description
     */
    public String getDescription() {
	return description;
    }

    /**
     * @return return the current difficulty
     */
    public int getDifficulty() {
	return difficulty;
    }

    public String getDifficultyAsString(Context ct) {
	switch (difficulty) {
	case DIFFICULTY_EASY:
	    return ct.getString(R.string.SkateTrickDifficultyStringEasy);
	case DIFFICULTY_MEDIUM:
	    return ct.getString(R.string.SkateTrickDifficultyStringMedium);
	case DIFFICULTY_REALLY_HARD:
	    return ct.getString(R.string.SkateTrickDifficultyStringHard);
	case DIFFICULTY_HARD:
	    return ct.getString(R.string.SkateTrickDifficultyStringReallyHard);
	}

	return "error";
    }

    /**
     * @return return the current Type
     */
    public int getType() {
	return type;
    }

    /**
     * @return return if you have learned the trick
     */
    public boolean mastered() {
	return mastered;
    }

    /**
     * @param b
     *            Boolean if you learned the trick
     */
    public void setMastered(boolean b) {
	mastered = b;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return name;
    }

}
