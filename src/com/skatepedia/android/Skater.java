package com.skatepedia.android;

// TODO: Auto-generated Javadoc
/**
 * Models a skater Object with name and points.
 */
public class Skater {

    /** how many tricks he failed. */
    private int failed = 0;

    /** The name of the skater. */
    private final String name;

    private int points = 0;

    private String skate = "";

    /** Array with S.K.A.T.E Chars */
    private final char[] skateArr = { 'S', 'K', 'A', 'T', 'E' };

    /** how many tricks suceeded. */
    private int succeeded = 0;

    /**
     * Instantiates a new skater.
     *
     * @param name
     *            the name
     */
    Skater(String name) {
	this.name = name;
    }

    /**
     * Adds a letter to skate
     */
    void addSkate() {
	if (points < 5)
	    skate = skate + skateArr[points];
	points = points + 1;
    }

    /**
     * if he failed a tricks
     */
    void failed() {
	failed++;
    }

    /**
     * Gets the number of fails.
     *
     * @return the fails
     */
    int getfails() {
	return failed;
    }

    /**
     * Gets the name of the skater .
     *
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * Gets the points.
     *
     * @return the points
     */
    public int getPoints() {
	// TODO Auto-generated method stub
	return points;
    }

    /**
     * Gets the chars he has in S.K.A.T.E
     *
     * @return chars he has in S.K.A.T.E
     */
    public String getSkate() {
	// TODO Auto-generated method stub
	return skate;
    }

    /**
     * Gets the number of tricks he landed
     *
     * @return the succeeds
     */
    int getsucceeds() {
	return succeeded;
    }

    /**
     * if he landed a trick
     */
    void succeeded() {
	succeeded++;
    }
}
