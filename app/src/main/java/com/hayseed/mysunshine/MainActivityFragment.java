package com.hayseed.mysunshine;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment
{

    public MainActivityFragment ()
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
        //return inflater.inflate (R.layout.fragment_main, container, false);
    }
}
