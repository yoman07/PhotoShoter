package com.photoshoter;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.photoshoter.events.ImageEvent;
import com.photoshoter.events.MyImageEvent;
import com.photoshoter.events.MyPositionEvent;
import com.photoshoter.events.UserDisconnectEvent;
import com.photoshoter.events.UserPositionEvent;
import com.photoshoter.helpers.ImageHelper;
import com.photoshoter.location.CustomMarker;
import com.photoshoter.location.GeolocationService;
import com.photoshoter.location.LocationUtils;
import com.photoshoter.location.MarkerHolder;
import com.photoshoter.models.MyUserData;
import com.photoshoter.models.User;
import com.photoshoter.models.UserDataProvider;
import com.photoshoter.popups.ImageViewer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class MainActivity extends ActionBarActivity implements GoogleMap.OnMarkerClickListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = "MainActivity";
    public boolean notyficationFlag;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Menu menu;
    private boolean gpsChecked;
    private boolean isSocketOpen;
    private boolean firstLaunch;
    private Handler handler;

    private String myFbId;

    private String receiverId;

    private MarkerHolder markerHolder;

    private Map<String, Marker> markerMap = new HashMap<String, Marker>();
    private Map<String, Bitmap> markerIcon = new HashMap<String, Bitmap>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);

        markerHolder = MarkerHolder.getInstance();

        if (savedInstanceState != null) {
            gpsChecked = savedInstanceState.getBoolean("gpsChecked");
            myFbId = savedInstanceState.getString("myFbId");
            isSocketOpen = savedInstanceState.getBoolean("isSocketOpen");
            firstLaunch = savedInstanceState.getBoolean("firstLaunch");
            notyficationFlag = savedInstanceState.getBoolean("notyficationFlag");
        } else {
            gpsChecked = false;
            myFbId = MyUserData.getUserId();
            isSocketOpen = false;
            firstLaunch = true;
            notyficationFlag = false;
        }

        if (!gpsChecked) {
            //check for gps provider
            LocationManager locationManager =
                    (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            gpsChecked = true;
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                showGpsAlertDialog().show();

        }

        handler = new Handler(Looper.getMainLooper());

        //Check for Google Play Services apk
        if (servicesConnected()) {
            setUpMapIfNeeded();

            if (!markerHolder.isMarkerMapEmpty()) {
                recreateMarkers();
            }
            if (!isMyServiceRunning())
                startService(new Intent(MainActivity.this, GeolocationService.class));
        }

        if (!isSocketOpen) {
            try {
                SocketClient.getInstance().connect();
                isSocketOpen = true;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        EventBus.getDefault().register(this);

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.app_name);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        if (!notyficationFlag) {
            menu.findItem(R.id.action_example).setIcon(
                    new IconDrawable(this, Iconify.IconValue.fa_bell_o)
                            .colorRes(R.color.navigation_drawer_text)
                            .actionBarSize()
            );
        } else {
            menu.findItem(R.id.action_example).setIcon(
                    new IconDrawable(getApplicationContext(), Iconify.IconValue.fa_bell)
                            .colorRes(R.color.actionbar_bell)
                            .actionBarSize()
            );
        }
        restoreActionBar();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_example:
                item.setIcon(
                        new IconDrawable(this, Iconify.IconValue.fa_bell_o)
                                .colorRes(R.color.navigation_drawer_text)
                                .actionBarSize()
                );

                if (markerMap.containsKey(UserDataProvider.getInstance().getIdOfPhotoSender())) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerMap.get(UserDataProvider.getInstance().getIdOfPhotoSender()).getPosition(), 12.0f));
                }
                notyficationFlag = false;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("gpsChecked", gpsChecked);
        outState.putString("myFbId", myFbId);
        outState.putBoolean("isSocketOpen", isSocketOpen);
        outState.putBoolean("firstLaunch", firstLaunch);
    }

    @Override
    public void onBackPressed() {

        if (isMyServiceRunning())
            stopService(new Intent(MainActivity.this, GeolocationService.class));
        markerHolder.clearMarkerIconMap();
        markerHolder.clearMarkerMap();
        SocketClient.getInstance().disconnectFromServer();
        isSocketOpen = false;
        notyficationFlag = false;
        deleteImage();
        UserDataProvider.getInstance().removeCurrenPhoto();
        finish();
    }

    @Override
    protected void onDestroy() {
        if (isMyServiceRunning())
            stopService(new Intent(MainActivity.this, GeolocationService.class));
        EventBus.getDefault().unregister(this);
        markerHolder.clearMarkerIconMap();
        markerHolder.clearMarkerMap();
        markerHolder.markerIconMapAddItems(markerIcon);
        markerHolder.markerMapAddItems(markerMap);
        super.onDestroy();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            //Set up and align My Location Button
            mMap.setMyLocationEnabled(true);
            View vw = getWindow().getDecorView().findViewById(android.R.id.content);
            View locationButton = ((View) vw.findViewById(1).getParent()).findViewById(2);
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            rlp.setMargins(10, 0, 0, 20);

            mMap.setOnMarkerClickListener(this);
        }

    }

    /**
     * Dialog window to inform about GPS provider
     *
     * @return
     */
    private Dialog showGpsAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.gps_provider_title)
                .setMessage(R.string.gps_provider_body)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(settingsIntent);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }

    /*
             * Handle results returned to the FragmentActivity
             * by Google Play services
             */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {

            case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST:
                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        // Log the result
                        Log.d("Google Play Services", "Problem Resolved");

                        break;

                    // If any other result was returned by Google Play services
                    default:
                        // Log the result
                        Log.d("Google Play Services", "Unknown Problem");
                        break;
                }
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    //TODO: work on quality improvement
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    if (imageBitmap != null && markerMap.containsKey(receiverId)) {
                        sendImageEvent(imageBitmap, MarkerHolder.getInstance().getMyLocation(), receiverId);
                    }

                }

                // If any other request code was received
            default:
                // Report that this Activity received an unknown requestCode
                Log.d("Google Play Services", "Unknown request Code");

                break;
        }

    }

    private void dispatchTakePictureIntent(String receiverId) {
        this.receiverId = receiverId;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    this,
                    LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(
                        getSupportFragmentManager(),
                        "Location Updates");
            }
        }
        return false;
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (GeolocationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTitle().equals(myFbId)) {
            Toast.makeText(this, R.string.only_you, Toast.LENGTH_SHORT).show();
        } else {
            chooseMarkerAction(marker.getTitle());
        }
        return true;
    }

    private void chooseMarkerAction(final String fb_id) {
        CharSequence actionOptions[] = new CharSequence[]{getString(R.string.take_picture), getString(R.string.open_picture)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(UserDataProvider.getInstance().getUserName(fb_id));
        builder.setItems(actionOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        dispatchTakePictureIntent(fb_id);
                        break;
                    case 1:
                        if (fb_id.equals(UserDataProvider.getInstance().getIdOfPhotoSender())) {
                            UserDataProvider.getInstance().setLock();
                            menu.findItem(R.id.action_example).setIcon(
                                    new IconDrawable(getApplicationContext(), Iconify.IconValue.fa_bell_o)
                                            .colorRes(R.color.navigation_drawer_text)
                                            .actionBarSize()
                            );
                            notyficationFlag = false;
                            FragmentManager fm = getSupportFragmentManager();
                            ImageViewer imageViewerWindow = new ImageViewer();
                            imageViewerWindow.show(fm, "fragment_image_window");

                        } else {
                            Toast.makeText(getApplicationContext(), R.string.nothing, Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }


        });
        builder.show();
    }

    private void moveMarker(final String userId, final LatLng latlng, final boolean delete) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (delete) {
                    try {
                        markerMap.get(userId).remove();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    markerMap.remove(userId);
                } else {
                    if (markerMap.containsKey(userId) && markerIcon.containsKey(userId)) {
                        markerMap.get(userId).remove();
                        Marker newMarker = mMap.addMarker(new MarkerOptions()
                                .position(latlng)
                                .title(userId)
                                .icon(BitmapDescriptorFactory.fromBitmap(markerIcon.get(userId))));
                        markerMap.put(userId, newMarker);


                    } else if (!markerMap.containsKey(userId) && !markerIcon.containsKey(userId)) {
                        markerIcon.put(userId, null);
                        DownloadMarkerImage task = new DownloadMarkerImage();
                        String lat = String.valueOf(latlng.latitude);
                        String lng = String.valueOf(latlng.longitude);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (new String[]{userId, lat, lng}));
                        } else {
                            task.execute(new String[]{userId, lat, lng});
                        }
                    } else if (!markerMap.containsKey(userId) && markerIcon.containsKey(userId)) {
                        if (markerIcon.get(userId) != null) {
                            Marker newMarker = mMap.addMarker(new MarkerOptions()
                                    .position(latlng)
                                    .title(userId)
                                    .icon(BitmapDescriptorFactory.fromBitmap(markerIcon.get(userId))));
                            markerMap.put(userId, newMarker);
                        }
                    }
                }
            }
        });
    }

    private void recreateMarkers() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                markerIcon.putAll(markerHolder.returnMarkerIconMap());
                markerHolder.clearMarkerIconMap();
                for (Map.Entry<String, Marker> entry : markerHolder.returnMarkerMap().entrySet()) {
                    Marker newMarker = mMap.addMarker(new MarkerOptions()
                            .position(entry.getValue().getPosition())
                            .title(entry.getValue().getTitle())
                            .icon(BitmapDescriptorFactory.fromBitmap(markerIcon.get(entry.getKey()))));
                    markerMap.put(entry.getKey(), newMarker);
                }
                markerHolder.clearMarkerMap();
            }
        });
    }

    public void onEvent(MyPositionEvent myPositionEvent) {
        if (firstLaunch) {
            firstLaunch = false;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPositionEvent.getLatLng(),
                    12.0f));
        }
        if (!UserDataProvider.getInstance().isUserInUserMap(myFbId)) {
            UserDataProvider.getInstance().addUser(myFbId, null);
            FetchUserNameFromFb task = new FetchUserNameFromFb();
            task.execute(new String[]{myFbId});
        }
        MarkerHolder.getInstance().setMyLocation(myPositionEvent.getLocation());
        moveMarker(myFbId, myPositionEvent.getLatLng(), false);
    }

    private void sendImageEvent(Bitmap bm, Location location, String receiverId) {
        MyImageEvent imageEvent = new MyImageEvent(ImageHelper.base64FormatFromBitmap(bm), location, receiverId);
        EventBus.getDefault().post(imageEvent);
    }

    public void onEvent(UserPositionEvent userPositionEvent) {
        Log.i(TAG, userPositionEvent.toString());
        if (!UserDataProvider.getInstance().isUserInUserMap(userPositionEvent.getUser().getFbId())) {
            UserDataProvider.getInstance().addUser(userPositionEvent.getUser().getFbId(), null);
            //Temporary solution - server should send 'user is online' event with user data
            FetchUserNameFromFb task = new FetchUserNameFromFb();
            task.execute(new String[]{userPositionEvent.getUser().getFbId()});
        }
        moveMarker(userPositionEvent.getUser().getFbId(), userPositionEvent.getUser().getLatLng(), false);
    }

    public void onEvent(UserDisconnectEvent userDisconnectEvent) {
        String userId = userDisconnectEvent.getFbId();
        moveMarker(userId, null, true);
    }

    public void onEvent(ImageEvent imageEvent) {
        Log.i(TAG, "Got imageEvent with data" + imageEvent.toString());
        if (!UserDataProvider.getInstance().isLock()) {
            UserDataProvider.getInstance().holdReceivedBitmap(imageEvent.getSenderId(), getPictureUri(ImageHelper.bitmapFromBase64Format(imageEvent.getBase64image())));
            if (!notyficationFlag && markerMap.containsKey(imageEvent.getSenderId())) {
                notifyUserAboutNewMessage();
            }
        }
    }

    private void notifyUserAboutNewMessage() {

        handler.post(new Runnable() {
            @Override
            public void run() {

                menu.findItem(R.id.action_example).setIcon(
                        new IconDrawable(getApplicationContext(), Iconify.IconValue.fa_bell)
                                .colorRes(R.color.actionbar_bell)
                                .actionBarSize()
                );
                notyficationFlag = true;

                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private synchronized Uri getPictureUri(Bitmap bmp) {

        File APP_FILE_PATH = new File(Environment.getExternalStorageDirectory()
                .getPath() + "/" + getString(R.string.app_name) + "/");
        if (!APP_FILE_PATH.exists()) {
            APP_FILE_PATH.mkdirs();
        }

        File file = new File(APP_FILE_PATH, "image.png");
        Uri imageFileUri = Uri.fromFile(file);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file, false);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Throwable ignore) {
            }
        }
        return imageFileUri;
    }

    private void deleteImage() {

        try {
            File file = new File(UserDataProvider.getInstance().getCurrentPhoto().getPath());
            if (file.exists()) {
                if (file.delete()) {
                    Log.i(TAG, "file deleted");
                    try {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                                Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i(TAG, "file not deleted");
                }
            }
        } catch (NullPointerException e) {
            Log.i(TAG, "file not found");
        }
    }

    /**
     * Dialog window to inform about Google Play services apk
     */
    public static class ErrorDialogFragment extends DialogFragment {

        private Dialog mDialog;

        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    private class DownloadMarkerImage extends AsyncTask<String, Void, Bitmap> {

        String userId;
        Double lat;
        Double lng;

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bm = null;
            userId = params[0];
            lat = Double.parseDouble(params[1]);
            lng = Double.parseDouble(params[2]);
            bm = new CustomMarker(new User(userId), getApplicationContext()).getCustomMarker();
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            LatLng latLng = new LatLng(lat, lng);
            Marker newMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(userId)
                    .icon(BitmapDescriptorFactory.fromBitmap(result)));
            markerIcon.put(userId, result);
            markerMap.put(userId, newMarker);
        }
    }

    private class FetchUserNameFromFb extends AsyncTask<String, Void, String> {

        String fb_id;

        @Override
        protected String doInBackground(String... params) {
            fb_id = params[0];
            String returnValue = "";
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("https://graph.facebook.com/" + fb_id + "?fields=name");
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
                    Log.e("", "Failed to check");
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String responseJSON = builder.toString();
            try {
                JSONObject jsonObject = new JSONObject(responseJSON);
                returnValue = jsonObject.get("name").toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return returnValue;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                String[] splited = s.split("\\s+");
                UserDataProvider.getInstance().addUser(fb_id, splited[0]);
            }

        }
    }

}
