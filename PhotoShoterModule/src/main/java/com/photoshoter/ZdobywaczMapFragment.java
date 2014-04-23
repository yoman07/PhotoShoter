package com.photoshoter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;


/**
 * Created by yoman on 03.11.2013.
 * Edited by balu on 22.04.2014
 */

public class ZdobywaczMapFragment extends Fragment {

    public GoogleMap map;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        map.setMyLocationEnabled(true);

        View locationButton = ((View) v.findViewById(1).getParent()).findViewById(2);


        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();

        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        rlp.setMargins(10, 0, 0, 20);
        return v;
    }


}