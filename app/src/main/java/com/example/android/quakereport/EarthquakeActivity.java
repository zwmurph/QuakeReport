package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Implement the Loader Manager, so that that background tasks can be completed in a resource efficient way
 */
public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    //Log tag that returns the package name for errors
    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    //URL that provides the JSON response from the USGS site
    private static final String REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=3&limit=1000";

    //Constant value for the earthquake loader ID. This really only comes into play if you're using multiple loaders.
    private static final int EARTHQUAKE_LOADER_ID = 1;

    //Global instance of the EarthquakeAdapter, so it can be used in multiple methods in this class
    private EarthquakeAdapter mAdapter;

    //Global instance of the TextView that is displayed when the list is empty
    private TextView mEmptyStateTextView;

    //Global instance of the ProgressBar, so it can be used in multiple methods in this class
    private ProgressBar mProgressBar;

    /**
     * OnCreate method for this activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        //Check if the user is connected to the Internet
        if (!checkInternetConnectivity()) {
            //Hide the loading spinner
            mProgressBar = (ProgressBar) findViewById(R.id.loading_spinner);
            mProgressBar.setVisibility(View.GONE);

            // Set empty state text to display "No earthquakes found."
            mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
            mEmptyStateTextView.setText(R.string.no_connectivity);
        } else {
            //Creates a object constructor for the ListView
            ListView earthquakeListView = (ListView) findViewById(R.id.list);

            //Find and set the empty text view as the empty view for the layout
            mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
            earthquakeListView.setEmptyView(mEmptyStateTextView);

            //Creates an adapter for the words to use, appends the array of words to the adapter,
            //the adapter is responsible for making a View for each item in the data set
            mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());

            //Sets the adapter method on the ListView
            //so the list can be populated in the user interface
            earthquakeListView.setAdapter(mAdapter);

            //On click listener for each item
            earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    //Find the current earthquake that was clicked on
                    Earthquake currentEarthquake = mAdapter.getItem(position);

                    //Convert the String URL into a URI object (to pass into the Intent constructor)
                    Uri earthquakeUri = Uri.parse(currentEarthquake.getDetailUrl());

                    //Load the webpage
                    openWebPage(earthquakeUri);
                }
            });

            //Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            //Initialize the loader. Pass in the int ID constant defined above and pass in null for
            //the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            //because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        }
    }

    /**
     * Method that checks if the user is connected to the Internet
     *
     * @return true or false
     */
    private boolean checkInternetConnectivity() {
        //Create a connectivity manager
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get the active network's network info, and return a boolean value
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Method for the creation of the loader, pass in:
     * The Loader {@param id} to be used
     * The {@param args} (arguments) the Loader should take
     *
     * @return a new instance of the Loader
     */
    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {
        return new EarthquakeLoader(this, REQUEST_URL);
    }

    /**
     * Once the loader has finished, execute this method with:
     * The {@param loader} to be used
     * The {@param result} from the Loader creation
     */
    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> result) {
        //Hide the loading spinner
        mProgressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        mProgressBar.setVisibility(View.GONE);

        // Set empty state text to display "No earthquakes found."
        mEmptyStateTextView.setText(R.string.no_earthquakes);

        //Clear the adapter of previous data
        mAdapter.clear();

        //If there is a valid list of {@Link Earthquake}'s, then add them to the adapter's dataset
        //This triggers the ListView to update
        if (result != null && !result.isEmpty()) {
            mAdapter.addAll(result);
        }
    }

    /**
     * If the Loader is reset (i.e. through orientation change), handle that in this method
     * The {@param loader} to be used
     */
    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    /**
     * Opens an intent using the URL passed in a parameter
     * The {@param url} to be used
     */
    public void openWebPage(Uri url) {
        //Create an intent
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        if (intent.resolveActivity(getPackageManager()) != null) {
            //If possible, open the intent
            startActivity(intent);
        }
    }
}