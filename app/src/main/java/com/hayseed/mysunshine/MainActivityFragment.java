package com.hayseed.mysunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
    private String[]     parsedWeather;
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
        adapter = new ArrayAdapter<String> (getActivity (), R.layout.list_item_forecast, R.id.list_item_forecast_textview, new ArrayList<String> ());

        View rootView = inflater.inflate (R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById (R.id.list_view_forecast);
        listView.setAdapter (adapter);

        listView.setOnItemClickListener (new AdapterView.OnItemClickListener ()
        {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id)
            {
                String forecast = (String) adapter.getItem (position);
                Intent intent   = new Intent (getActivity (), DetailActivity.class);
                intent.putExtra ("data", forecast);
                getActivity ().startActivity (intent);
            }
        });
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
                //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (getContext ());
                //String location = prefs.getString (getString (R.string.pref_location_key), "");
                //new FetchWeatherTask ().execute (location);
                updateWeather ();
                return true;

            default:
                return super.onOptionsItemSelected (item);
        }
    }

    @Override
    public void onStart ()
    {
        super.onStart ();
        updateWeather ();
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

    private void updateWeather ()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (getContext ());
        String location = prefs.getString (getString (R.string.pref_location_key), getString (R.string.pref_location_default));
        String units = prefs.getString (getString (R.string.pref_units_key), getString (R.string.pref_units_default));

        new FetchWeatherTask ().execute (location);
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
                    .appendQueryParameter ("zip", params[0])
                    .appendQueryParameter ("units", "imperial")
                    .appendQueryParameter ("cnt", "7")
                    .appendQueryParameter ("APPID", "ef9bdcc2ae5628da5e466596904253e5");

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
            if (strings == null)
            {
                Toast.makeText (getContext (), "null weather strings", Toast.LENGTH_LONG).show ();
                return;
            }

            super.onPostExecute (strings);

            parsedWeather = strings;
            adapter.clear ();
            adapter.addAll (strings);
            //adapter.notifyDataSetChanged ();
        }

    }
}
