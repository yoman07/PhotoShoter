package com.photoshoter.popups;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.photoshoter.R;
import com.photoshoter.models.Message;

/**
 * Created by balu on 2014-04-22.
 */
public class MessagesWindow extends DialogFragment {

    public MessagesWindow(){

    }

    private int width;
    private int height;

    private ListView mListView;
    private MessagesListAdapter mListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);

        View view = inflater.inflate(R.layout.messages_window, container);

        mListAdapter = new MessagesListAdapter(getActivity());
        mListView = (ListView)view.findViewById(R.id.messageslistView);
        mListView.setAdapter(mListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            dismiss();
            }
        });

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        width = width - width/12;
        height = height - height/8;

        mListAdapter.addItem(new Message());
        mListAdapter.addItem(new Message());
        mListAdapter.addItem(new Message());
        mListAdapter.addItem(new Message());
        mListAdapter.addItem(new Message());
        mListAdapter.addItem(new Message());
        mListAdapter.addItem(new Message());
        mListAdapter.addItem(new Message());

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Window window = getDialog().getWindow();
        window.setLayout(width, height);
        window.setGravity(Gravity.CENTER);
      }
}
