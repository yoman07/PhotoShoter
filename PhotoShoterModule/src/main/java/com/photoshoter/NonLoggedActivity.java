package com.photoshoter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;

import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class NonLoggedActivity extends ActionBarActivity {
    public Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        printFacebookHash();
        Log.i("MainActivity", "test logowania");
        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        Settings.addLoggingBehavior(LoggingBehavior.REQUESTS);

        Request request = Request.newGraphPathRequest(null, "/4", new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                if (response.getError() != null) {
                    Log.i("MainActivity", String.format("Error making request: %s", response.getError()));
                } else {
                    GraphUser user = response.getGraphObjectAs(GraphUser.class);
                    Log.i("MainActivity", String.format("Name: %s", user.getName()));
                }
            }
        });
        request.executeAsync();




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_logged);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new NonLoggedChooseFragment())
                    .commit();
        }


        // Check if there is a currently logged in user
        // and they are linked to a Facebook account.
        String informationAboutLogged = "non_logged";
        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
            // Go to the user info activity
            Log.d("Log in",
                    "User currently logged.");
            informationAboutLogged = "logged";
            showUserLoggedActivity();
        }

        EasyTracker.getInstance(this).send(MapBuilder
                        .createEvent("ps_actions",     // Event category (required)
                                "application_open",  // Event action (required)
                                informationAboutLogged,   // Event label
                                null)            // Event value
                        .build()
        );


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            // do nothing to prevent NullPointerException caused by this.requestWindowFeature(Window.FEATURE_NO_TITLE)
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void printFacebookHash() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.photoshoter",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("printFacebookHash:", e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Log.e("printFacebookHash:", e.getMessage());
        }
    }

    public void onLoginButtonClicked() {


        EasyTracker.getInstance(this).send(MapBuilder
                        .createEvent("ps_actions",     // Event category (required)
                                "facebook",  // Event action (required)
                                "click_button",   // Event label
                                null)            // Event value
                        .build()
        );

        NonLoggedActivity.this.progressDialog = ProgressDialog.show(
                NonLoggedActivity.this, "", "Logging in...", true);

        List<String> permissions = Arrays.asList("basic_info", "user_about_me",
                "user_relationships", "user_birthday", "user_location");


        ParseFacebookUtils.logIn(permissions, NonLoggedActivity.this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {


                if (user == null) {
                    Log.d("Log in",
                            "Uh oh. The user cancelled the Facebook login.");
                } else {
                    getFacebookIdInBackground();

                    EasyTracker.getInstance(NonLoggedActivity.this).send(MapBuilder
                                    .createEvent("ps_actions",     // Event category (required)
                                            "facebook",  // Event action (required)
                                            "logged_successfull",   // Event label
                                            null)            // Event value
                                    .build()
                    );
                }
            }
        });
    }

    private void getFacebookIdInBackground() {
        Request.executeMeRequestAsync(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                if (user != null) {
                    ParseUser.getCurrentUser().put("fb_id", user.getId());
                    ParseUser.getCurrentUser().saveInBackground();
                    Log.d("Log in",
                            "User logged in through Facebook!");
                    NonLoggedActivity.this.showUserLoggedActivity();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    public void showUserLoggedActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        if(this.progressDialog != null) {
            NonLoggedActivity.this.progressDialog.dismiss();
        }
        finish();
    }


    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }

}
