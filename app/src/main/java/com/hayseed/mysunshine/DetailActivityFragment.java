package com.hayseed.mysunshine;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment
{

    public DetailActivityFragment ()
    {
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState)
    {
        View rootView =  inflater.inflate (R.layout.fragment_detail, container, false);

        Toast.makeText (getActivity (), "DetailActivityFragment", Toast.LENGTH_LONG).show ();

        return rootView;
    }
}
