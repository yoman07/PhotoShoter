package com.photoshoter.location;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.photoshoter.R;
import com.photoshoter.models.User;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by balu on 2014-04-25.
 */
public class CustomMarker {


    private User user;
    private Context ctx;

    public CustomMarker(User user, Context ctx) {
        this.user = user;
        this.ctx = ctx;
    }


    public Bitmap getCustomMarker() {

        //solution based on http://android-crap.blogspot.com/2013/02/create-bitmap-from-layoutview.html

        LayoutInflater mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Inflate the layout into a view and configure it the way you like
        RelativeLayout view = new RelativeLayout(ctx);
        mInflater.inflate(R.layout.marker_layout, view, true);


        ImageView imgView = (ImageView) view.findViewById(R.id.profileImage);
        try {
            URL img_value = new URL("https://graph.facebook.com/" + user.getFbId().toString() + "/?fields=picture");
            Picasso.with(ctx).load(getTrueFacebookPictureUrl(img_value)).error(R.drawable.com_facebook_profile_default_icon).placeholder(R.drawable.com_facebook_profile_default_icon).into(imgView);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //synchronization issue
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Provide it with a layout params. It should necessarily be wrapping the
        //content as we not really going to have a parent for it.
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));

        //Pre-measure the view so that height and width don't remain null.
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        //Assign a size and position to the view and all of its descendants
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        //Create the bitmap
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        //Create a canvas with the specified bitmap to draw into
        Canvas c = new Canvas(bitmap);

        //Render this view (and all of its children) to the given Canvas
        view.draw(c);
        return bitmap;

    }


    /**
     * Method to return link to which fb graph is redirecting
     *
     * @param url
     * @return
     */
    private String getTrueFacebookPictureUrl(URL url) {

        String returnValue = "";
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url.toString());
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e("", "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String responseJSON = builder.toString();
        try {
            JSONObject jsonObject = new JSONObject(responseJSON);
            returnValue = jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");


        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnValue;
    }
}
