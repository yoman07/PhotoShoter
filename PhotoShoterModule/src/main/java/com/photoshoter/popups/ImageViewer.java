package com.photoshoter.popups;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.photoshoter.R;
import com.photoshoter.models.UserDataProvider;

/**
 * Created by balu on 2014-04-28.
 */
public class ImageViewer extends DialogFragment {

    public ImageViewer() {

    }

    private int screenHeight;
    private int screenWidth;
    private int bitmapHeight;
    private int bitmapWidth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, getActivity().getApplicationInfo().theme);
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
        ImageView imgView = (ImageView) view.findViewById(R.id.imageViewFullScreen);
        bitmapHeight = UserDataProvider.getInstance().getCurrentPhoto().getHeight();
        bitmapWidth = UserDataProvider.getInstance().getCurrentPhoto().getWidth();

        if (bitmapWidth > bitmapHeight && screenHeight > screenWidth) {
            imgView.setImageBitmap(UserDataProvider.getInstance().getCurrentPhoto());
        } else if (bitmapWidth > bitmapHeight && screenHeight < screenWidth) {
            imgView.setImageBitmap(Bitmap.createScaledBitmap(UserDataProvider.getInstance().getCurrentPhoto(), screenWidth, screenHeight, false));
        } else if (bitmapWidth < bitmapHeight && screenHeight > screenWidth) {
            imgView.setImageBitmap(Bitmap.createScaledBitmap(UserDataProvider.getInstance().getCurrentPhoto(), screenWidth, screenHeight, false));
        } else if (bitmapWidth < bitmapHeight && screenHeight < screenWidth) {
            imgView.setImageBitmap(UserDataProvider.getInstance().getCurrentPhoto());
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
        //temporary
        UserDataProvider.getInstance().releaseLock();
    }
}