package com.hayseed.mysunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            startActivity (new Intent (this, SettingsActivity.class));
            return true;
        }

        /*
            Menu option Show Location
         */
        if (id == R.id.action_map)
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (this);
            String location = prefs.getString (getString (R.string.pref_location_key), getString (R.string.pref_location_default));

            //Uri uriLocation = Uri.parse ("geo:0,0?q=30655");
            Uri uriLocation = Uri.parse ("geo:0,0?").buildUpon ()
                    .appendQueryParameter ("q", location)
                    .build ();

            Intent intent = new Intent (Intent.ACTION_VIEW);
            intent.setData (uriLocation);
            if (intent.resolveActivity (getPackageManager ()) != null)
            {
                startActivity (intent);
            }

            return true;
        }

        return super.onOptionsItemSelected (item);

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {
        public PlaceholderFragment ()
        {
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

            List<String> daysList = new ArrayList<String> (Arrays.asList (days));

            View rootView = inflater.inflate (R.layout.fragment_main, container, false);

            ArrayAdapter adapter = new ArrayAdapter (getActivity (), R.layout.list_item_forecast, R.id.list_item_forecast_textview, daysList);

            ListView listView = (ListView) rootView.findViewById (R.id.list_view_forecast);
            listView.setAdapter (adapter);
            return rootView;
        }
    }
}
