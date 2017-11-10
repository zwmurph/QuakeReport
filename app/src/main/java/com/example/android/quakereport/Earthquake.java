package com.example.android.quakereport;

/**
 * The Earthquake class that creates and provides methods that return parts of the ArrayList when called
 * by other parts of the app
 */
public class Earthquake {

    //Declare private variables for this class to use
    private double mMagnitude;
    private String mLocation;
    private long mTimeInMilliseconds;
    private String mDetailUrl;

    /**
     * Create the constructor for this class, a constructor creates an instance of a class
     * This constructor will create an instance of a double, long and string
     *
     * @param magnitude
     * @param location
     * @param timeInMilliseconds
     * @param detailUrl
     */
    public Earthquake(double magnitude, String location, long timeInMilliseconds, String detailUrl) {
        mMagnitude = magnitude;
        mLocation = location;
        mTimeInMilliseconds = timeInMilliseconds;
        mDetailUrl = detailUrl;
    }

    /**
     * @return the magnitude of the Earthquake
     */
    public double getMagnitude() {
        return mMagnitude;
    }

    /**
     * @return the location of the Earthquake
     */
    public String getLocation() {
        return mLocation;
    }

    /**
     * @return the date of the Earthquake
     */
    public long getTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }

    /**
     * @return the URL associated with the Earthquake
     */
    public String getDetailUrl() {
        return mDetailUrl;
    }
}