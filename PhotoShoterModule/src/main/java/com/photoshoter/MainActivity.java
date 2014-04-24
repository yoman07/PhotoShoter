package com.photoshoter;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.photoshoter.popups.MessagesWindow;

public class MainActivity extends ActionBarActivity {


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);

        setUpMapIfNeeded();
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
        setUpMapIfNeeded();
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
                //TODO: temporary - for tests only
                menu.findItem(R.id.action_example).setIcon(
                        new IconDrawable(this, Iconify.IconValue.fa_bell)
                                .colorRes(R.color.actionbar_bell)
                                .actionBarSize()
                );
                showMessagesWindow();

                return true;
        }
        return super.onOptionsItemSelected(item);
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

        }
    }

}
