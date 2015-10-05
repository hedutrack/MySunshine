package com.hayseed.mysunshine;

import android.net.Uri;
import android.net.Uri.Builder;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment
{
    private String [] parsedWeather;
    private ArrayAdapter adapter;

    public MainActivityFragment ()
    {
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);

        setHasOptionsMenu (true);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState)
    {
        String[] days = {
                "Today",
                "Thursday",
                "Friday",
                "Saturday",
                "Sunday",
                "Monday"
        };

        //String forecast = getData ("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
        new FetchWeatherTask ().execute ("30655");

        List<String> daysList = new ArrayList<String> (Arrays.asList (days));

        View rootView = inflater.inflate (R.layout.fragment_main, container, false);

        adapter = new ArrayAdapter (getActivity (), R.layout.list_item_forecast, R.id.list_item_forecast_textview, daysList);

        ListView listView = (ListView) rootView.findViewById (R.id.list_view_forecast);
        listView.setAdapter (adapter);
        return rootView;
        //return inflater.inflate (R.layout.fragment_main, container, false);
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater)
    {
        inflater.inflate (R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId ())
        {
            case R.id.action_refresh:
                new FetchWeatherTask ().execute ("30655");
                return true;

            default:
                return super.onOptionsItemSelected (item);
        }
    }

    private String getData (String urlString)
    {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader    reader        = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try
        {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL (urlString);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection ();
            urlConnection.setRequestMethod ("GET");
            urlConnection.connect ();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream ();
            StringBuffer buffer = new StringBuffer ();
            if (inputStream == null)
            {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader (new InputStreamReader (inputStream));

            String line;
            while ((line = reader.readLine ()) != null)
            {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append (line + "\n");
            }

            if (buffer.length () == 0)
            {
                // Stream was empty.  No point in parsing.
                return null;
            }
            forecastJsonStr = buffer.toString ();
        }
        catch (IOException e)
        {
            Log.e ("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        }
        finally
        {
            if (urlConnection != null)
            {
                urlConnection.disconnect ();
            }
            if (reader != null)
            {
                try
                {
                    reader.close ();
                }
                catch (final IOException e)
                {
                    Log.e ("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }

        Log.v ("getData", forecastJsonStr);

        return forecastJsonStr;
    }

    // "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7"

    private class FetchWeatherTask extends AsyncTask<String, Void, String[]>
    {
        private String[] weatherData;

        @Override
        protected String[] doInBackground (String... params)
        {

            if (params.length == 0) return null;

            Uri.Builder uri = new Uri.Builder ();
            //uri.scheme ("http").authority ("api.openweathermap.org").appendPath ("data").appendPath ("2.5").appendPath ("forecast").appendPath ("daily?q=94043&mode=json&units=metric&cnt=7");
            uri.scheme ("http").authority ("api.openweathermap.org").appendPath ("data")
                    .appendPath ("2.5")
                    .appendPath ("forecast")
                    .appendPath ("daily")
                    .appendQueryParameter ("q", params[0])
                    .appendQueryParameter ("units", "metric")
                    .appendQueryParameter ("cnt", "7");

            String url = uri.build ().toString ();

            Log.d ("FetchWeatherTask", url);

            String data = getData (url);

            try
            {
                weatherData = WeatherDataParser.getWeatherDataFromJson (data, 7);
                return weatherData;
            }
            catch (JSONException e)
            {
                e.printStackTrace ();
            }

            return null;
        }

        @Override
        protected void onPostExecute (String[] strings)
        {
            super.onPostExecute (strings);

            parsedWeather = strings;
            adapter.clear ();
            adapter.addAll (strings);
            //adapter.notifyDataSetChanged ();
        }

    }
}
