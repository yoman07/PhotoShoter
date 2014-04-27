package com.photoshoter;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.parse.ParseUser;
import com.photoshoter.events.ImageEvent;
import com.photoshoter.events.MyImageEvent;
import com.photoshoter.events.MyPositionEvent;
import com.photoshoter.events.UserDisconnectEvent;
import com.photoshoter.events.UserPositionEvent;
import com.photoshoter.helpers.ImageHelper;
import com.photoshoter.location.CustomMarker;
import com.photoshoter.location.GeolocationService;
import com.photoshoter.location.LocationUtils;
import com.photoshoter.models.MyUserData;
import com.photoshoter.models.User;
import com.photoshoter.models.UserDataProvider;
import com.photoshoter.popups.MessagesWindow;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class MainActivity extends ActionBarActivity implements GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MainActivity";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private Menu menu;

    private boolean gpsChecked;

    private Handler handler;

    private String myFbId;

    UserDataProvider usrDP;

    private boolean isSocketOpen;

    private Map<String, Marker> markerMap = new HashMap<String, Marker>();
    private Map<String, Bitmap> markerIcon = new HashMap<String, Bitmap>();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);

        usrDP = UserDataProvider.getInstance();

        if (savedInstanceState != null) {
            gpsChecked = savedInstanceState.getBoolean("gpsChecked");
            myFbId = savedInstanceState.getString("myFbId");
            isSocketOpen = savedInstanceState.getBoolean("isSocketOpen");
        } else {
            gpsChecked = false;
            ParseUser currentUser = ParseUser.getCurrentUser();
            myFbId = currentUser.get("fb_id").toString();
            isSocketOpen = false;
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

            if (!usrDP.isMarkerMapEmpty()) {
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
        menu.findItem(R.id.action_example).setIcon(
                new IconDrawable(this, Iconify.IconValue.fa_bell_o)
                        .colorRes(R.color.navigation_drawer_text)
                        .actionBarSize()
        );
        restoreActionBar();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_example:
                showMessagesWindow();
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
    }


    /**
     * Opens Dialog with received messages
     */
    private void showMessagesWindow() {
        FragmentManager fm = getSupportFragmentManager();
        MessagesWindow messagesWindowDialog = new MessagesWindow();
        messagesWindowDialog.show(fm, "fragment_messages_window");
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

    @Override
    public void onBackPressed() {
        if (isMyServiceRunning())
            stopService(new Intent(MainActivity.this, GeolocationService.class));
        usrDP.clearMarkerIconMap();
        usrDP.clearMarkerMap();
        SocketClient.getInstance().disconnectFromServer();
        isSocketOpen = false;

        finish();
    }


    @Override
    protected void onDestroy() {
        if (isMyServiceRunning())
            stopService(new Intent(MainActivity.this, GeolocationService.class));
        EventBus.getDefault().unregister(this);
        usrDP.clearMarkerIconMap();
        usrDP.clearMarkerMap();
        usrDP.markerIconMapAddItems(markerIcon);
        usrDP.markerMapAddItems(markerMap);
        super.onDestroy();
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
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(settingsIntent);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
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

                // If any other request code was received
            default:
                // Report that this Activity received an unknown requestCode
                Log.d("Google Play Services", "Unknown request Code");

                break;
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
        System.out.println("Marker cliked");
        return false;
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
                        Marker newMarker = mMap.addMarker(new MarkerOptions()
                                .position(latlng)
                                .icon(BitmapDescriptorFactory.fromBitmap(markerIcon.get(userId))));
                        markerMap.put(userId, newMarker);
                    }
                }
            }
        });
    }

    private void recreateMarkers() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<String, Bitmap> entry : usrDP.returnMarkerIconMap().entrySet()) {
                    String key = entry.getKey();
                    Bitmap value = entry.getValue();
                    markerIcon.put(key, value);
                }
                usrDP.clearMarkerIconMap();
                for (Map.Entry<String, Marker> entry : usrDP.returnMarkerMap().entrySet()) {
                    String key = entry.getKey();
                    Marker value = entry.getValue();
                    Marker newMarker = mMap.addMarker(new MarkerOptions()
                            .position(value.getPosition())
                            .icon(BitmapDescriptorFactory.fromBitmap(markerIcon.get(key))));
                    markerMap.put(key, newMarker);
                }
                usrDP.clearMarkerMap();
            }
        });
    }

    public void onEvent(MyPositionEvent myPositionEvent) {
        moveMarker(myFbId, myPositionEvent.getLatLng(), false);
    }


    private void sendImageEvent(Bitmap bm, Location location, String receiverId) {
            MyImageEvent imageEvent = new MyImageEvent(ImageHelper.base64FormatFromBitmap(bm),location, receiverId);
            EventBus.getDefault().post(imageEvent);
    }

    public void onEvent(UserPositionEvent userPositionEvent) {
        Log.i(TAG, userPositionEvent.toString());
        moveMarker(userPositionEvent.getUser().getFbId(), userPositionEvent.getUser().getLatLng(), false);
    }

    public void onEvent(UserDisconnectEvent userDisconnectEvent) {
        String userId = userDisconnectEvent.getFbId();
        moveMarker(userId, null, true);
    }

    public  void onEvent(ImageEvent imageEvent) {
        Log.i(TAG, "Got imageEvent with data" + imageEvent.toString());
        Bitmap bitmap = ImageHelper.bitmapFromBase64Format(imageEvent.getBase64image());
        Log.i(TAG, "Bitmap " + bitmap.toString());
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
                    .icon(BitmapDescriptorFactory.fromBitmap(result)));
            markerIcon.put(userId, result);
            markerMap.put(userId, newMarker);
        }
    }

}
