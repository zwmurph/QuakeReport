package com.example.android.quakereport;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.example.android.quakereport.R.id.magnitude;

/**
 * Earthquake adapter class that handles the multiple TextViews for the ListView
 */
public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    /**
     * A custom constructor.
     *
     * @param context     is used to inflate the layout file
     * @param earthquakes is the data we want to populate into the lists
     */
    public EarthquakeAdapter(Activity context, List<Earthquake> earthquakes) {
        // This initialises the ArrayAdapter's internal storage for the context and the list.
        // The second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews the adapter is not
        // going to use this second argument, so it can be any value.
        super(context, 0, earthquakes);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    is the position in the list of data that should be displayed in the list item view.
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Get the data item for this position
        Earthquake currentEarthquake = getItem(position);

        //Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.element_layout, parent, false);
        }

        //Lookup the views for the data population
        TextView magnitudeView = (TextView) convertView.findViewById(magnitude);
        TextView locationView = (TextView) convertView.findViewById(R.id.primaryLocation);
        TextView offsetView = (TextView) convertView.findViewById(R.id.locationOffset);
        TextView dateView = (TextView) convertView.findViewById(R.id.date);
        TextView timeView = (TextView) convertView.findViewById(R.id.time);

        //Populate the data into the template view using the data object
        magnitudeView.setText("" + currentEarthquake.getMagnitude());

        // Set the proper background colour on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeView.getBackground();
        // Get the appropriate background colour based on the current earthquake magnitude
        int magnitudeColour = getMagnitudeColour(currentEarthquake.getMagnitude());
        // Set the colour on the magnitude circle
        magnitudeCircle.setColor(magnitudeColour);

        //Create a date from the milliseconds provided in the time
        Date dateObject = new Date(currentEarthquake.getTimeInMilliseconds());
        //Format the date & time
        String formattedDate = formatDate(dateObject);
        String formattedTime = formatTime(dateObject);
        //Populate that date & time into the respective TextViews
        dateView.setText(formattedDate);
        timeView.setText(formattedTime);

        //Get string of original location
        String ogLocation = currentEarthquake.getLocation();
        //Get index location of "of" in string
        int ofIndex = ogLocation.indexOf("of");

        //If statement to check if the string has an offset or not
        if (ogLocation.contains("of")) {
            //Create the relevant substrings using "of" as a separator location
            String offset = ogLocation.substring(0, ofIndex + 3);
            String location = ogLocation.substring(ofIndex + 3, ogLocation.length());
            //Populate the views with the correct data
            offsetView.setText(offset);
            locationView.setText(location);
        } else {
            //Substitute some text for the offset and set the data to the TextViews
            offsetView.setText("Near to the");
            locationView.setText(ogLocation);
        }

        //Return the completed view to render on-screen
        return convertView;
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return timeFormat.format(dateObject);
    }

    /**
     * @param magnitude - based on the input
     * @return the correct colour ID for the magnitude circle
     */
    private int getMagnitudeColour(double magnitude) {
        int magColourID;
        //Convert double to int, so switch can work
        int magFloor = (int) Math.floor(magnitude);
        switch (magFloor) {
            case 0:
            case 1:
                magColourID = R.color.magnitude1;
                break;
            case 2:
                magColourID = R.color.magnitude2;
                break;
            case 3:
                magColourID = R.color.magnitude3;
                break;
            case 4:
                magColourID = R.color.magnitude4;
                break;
            case 5:
                magColourID = R.color.magnitude5;
                break;
            case 6:
                magColourID = R.color.magnitude6;
                break;
            case 7:
                magColourID = R.color.magnitude7;
                break;
            case 8:
                magColourID = R.color.magnitude8;
                break;
            case 9:
                magColourID = R.color.magnitude9;
                break;
            default:
                magColourID = R.color.magnitude10plus;
                break;
        }
        //Return the colour, instead of the colour ID
        return ContextCompat.getColor(getContext(), magColourID);
    }
}