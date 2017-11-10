package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    //Tag for the log messages
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Query the USGS dataset and return an {@link List<Earthquake>} object to represent a single earthquake.
     */
    public static List<Earthquake> fetchEarthquakeData(String requestUrl) {
        //Create a URL object
        URL url = createUrl(requestUrl);

        //Perform a HTTP request to the URL and receive a JSON response back
        //Initialise variable
        String jsonResponse = null;

        //Try the creation of a JSON response from the URL, if not catch the exception
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            //Log the error
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        //Extract the relevant fields from the JSON response and create an List<Earthquake> object
        List<Earthquake> earthquake = extractFeaturesFromJson(jsonResponse);

        //Return the List<Earthquake>
        return earthquake;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        //Initialise variable
        URL url = null;

        //Try the creation of a URL, if not catch the exception
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            //Log the error
            Log.e(LOG_TAG, "Error with creating URL", e);
        }
        //Return the successful URL
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        //Initialise variable
        String jsonResponse = "";

        //If the URL is null, then return early with an empty String
        if (url == null) {
            return jsonResponse;
        }

        //Initialise variables
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        //Try the connecting to the URL and reading the response, else catch the exception
        try {
            //Open the connection to the URL
            urlConnection = (HttpURLConnection) url.openConnection();

            //Set timeouts so if they expire before there is data available an error is thrown
            //Makes sure the user is never waiting for long periods of time if there is no data
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);

            //Set the request method, as data is wanted to be received
            urlConnection.setRequestMethod("GET");

            //Make the connection
            urlConnection.connect();

            //If the request was successful (response code 200), then read the input and parse the response
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                //Log the error
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            //Log the error
            Log.e(LOG_TAG, "Problem retrieving the JSON results", e);
        } finally {
            //As long there is an open connection
            if (urlConnection != null) {
                //Close the URL connection
                urlConnection.disconnect();
            }
            //As long as the inputStream is open
            if (inputStream != null) {
                //Close the inputStream
                inputStream.close();
            }
        }
        //Return the JSON response
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        //Initialise variable / create a new String Builder (S.B.)
        //S.B. builds one String bit by bit instead of appending more information each time through other variables
        StringBuilder output = new StringBuilder();

        //As long as the inputStream is not empty
        if (inputStream != null) {
            //Create a new InputStreamReader, using the inputStream and "UTF-8" character set
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));

            //Create a buffered reader, which stores information about the characters around each character
            BufferedReader reader = new BufferedReader(inputStreamReader);

            //Get the line of text
            String line = reader.readLine();

            //While the line isn't empty
            while (line != null) {
                //Append the line to the output
                output.append(line);
                //Goto the next line
                line = reader.readLine();
            }
        }
        //Return the completed output in String format
        return output.toString();
    }

    /**
     * Return an {@link List<Earthquake>} item by parsing out information
     * about the earthquake from the input jsonResponse string.
     */
    private static List<Earthquake> extractFeaturesFromJson(String jsonResponse) {
        //If the JSON String is empty or null, then return early
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        //Create an empty ArrayList that we can start adding earthquakes to
        List<Earthquake> earthquakes = new ArrayList<>();

        // Try to parse the jsonResponse, else catch the JSONException
        try {
            //Get the root of the JSON string
            JSONObject root = new JSONObject(jsonResponse);

            //Get the "features" array
            JSONArray features = root.getJSONArray("features");

            //Loop through each element of features
            for (int i = 0; i < features.length(); i++) {
                //Get the relevant element
                JSONObject element = features.getJSONObject(i);

                //Get the "properties" object
                JSONObject properties = element.getJSONObject("properties");

                //Get the data that is needed
                //Declare variable out of try-catch
                double magnitude;

                //Sometimes magnitude is given as null on USGS, try-catch block prevents crashing
                try {
                    magnitude = properties.getDouble("mag");
                } catch (JSONException e) {
                    //If the error is thrown, then set a default value of 0
                    magnitude = 0;
                }

                String location = properties.getString("place");
                long timeInMilliseconds = properties.getLong("time");
                String detailUrl = properties.getString("url");

                //Add the data to a new ArrayList element
                earthquakes.add(new Earthquake(magnitude, location, timeInMilliseconds, detailUrl));
            }
        } catch (JSONException e) {
            //Log the error
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        return earthquakes;
    }
}