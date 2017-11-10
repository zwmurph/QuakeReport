package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Earthquake loader task
 */
public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    //Log tag that returns the package name for errors
    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    //Global instance of the String Url, so it can be used in multiple methods in this class
    private String mUrl;

    /**
     * The constructor for this class
     */
    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    /**
     * When the Loader is instructed to load
     */
    @Override
    protected void onStartLoading() {
        //Force the load
        forceLoad();
    }

    /**
     * The background process for the Loader
     *
     * @return
     */
    @Override
    public List<Earthquake> loadInBackground() {
        // Don't perform the request if there are no URLs, or the URL is null.
        if (mUrl == null) {
            return null;
        }

        //Get the data for the URL provided
        List<Earthquake> result = QueryUtils.fetchEarthquakeData(mUrl);

        //Return the result
        return result;
    }
}
