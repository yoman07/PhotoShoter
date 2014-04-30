package com.photoshoter.popups;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.IconTextView;
import android.widget.ImageView;

import com.photoshoter.R;
import com.photoshoter.models.UserDataProvider;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by balu on 2014-04-28.
 */
public class ImageViewer extends DialogFragment {

    private int screenHeight;
    private int screenWidth;
    private int bitmapHeight;
    private int bitmapWidth;

    public ImageViewer() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, getActivity().getApplicationInfo().theme);
        setCancelable(false);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.image_viever_layout, container);
        IconTextView txtv = (IconTextView) view.findViewById(R.id.textViewCancel);
        txtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataProvider.getInstance().releaseLock();
                dismiss();
            }
        });
        ImageView imgView = (ImageView) view.findViewById(R.id.imageViewFullScreen);


        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), UserDataProvider.getInstance().getCurrentPhoto());
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmapHeight = bitmap.getHeight();
        bitmapWidth = bitmap.getWidth();
        bitmap = null;

        if (bitmapWidth > bitmapHeight && screenHeight > screenWidth) {
            Picasso.with(getActivity()).load(UserDataProvider.getInstance().getCurrentPhoto()).skipMemoryCache().into(imgView);
        } else if (bitmapWidth > bitmapHeight && screenHeight < screenWidth) {
            Picasso.with(getActivity()).load(UserDataProvider.getInstance().getCurrentPhoto()).fit().centerCrop().skipMemoryCache().into(imgView);
        } else if (bitmapWidth < bitmapHeight && screenHeight > screenWidth) {
            Picasso.with(getActivity()).load(UserDataProvider.getInstance().getCurrentPhoto()).fit().centerCrop().skipMemoryCache().into(imgView);
        } else if (bitmapWidth < bitmapHeight && screenHeight < screenWidth) {
            Picasso.with(getActivity()).load(UserDataProvider.getInstance().getCurrentPhoto()).skipMemoryCache().into(imgView);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}