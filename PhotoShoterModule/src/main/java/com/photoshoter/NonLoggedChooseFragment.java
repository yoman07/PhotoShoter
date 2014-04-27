package com.photoshoter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yoman on 06.11.2013.
 */
public class NonLoggedChooseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_choose_login, container, false);

        rootView.findViewById(R.id.facebook_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoginButtonClicked();
            }
        });

        return rootView;
    }


    private void onLoginButtonClicked() {
        final NonLoggedActivity activity = (NonLoggedActivity) getActivity();
        activity.onLoginButtonClicked();
    }

}