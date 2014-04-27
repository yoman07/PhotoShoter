package com.photoshoter;

import android.location.Location;
import android.util.Log;

import com.photoshoter.events.ImageEvent;
import com.photoshoter.events.MyImageEvent;
import com.photoshoter.events.MyPositionEvent;
import com.photoshoter.events.UserDisconnectEvent;
import com.photoshoter.events.UserPositionEvent;
import com.photoshoter.models.MyUserData;
import com.photoshoter.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;

import de.greenrobot.event.EventBus;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

/**
 * Created by yoman on 23.04.2014.
 */
public class SocketClient {
    private static final String TAG = "SocketClient";
    private static String SERVER_ADRESS = "http://geo-chat.herokuapp.com";
    private static SocketClient ourInstance;
    private static SocketIO socket;

    public static SocketClient getInstance() {
        if (ourInstance == null) {
            ourInstance = new SocketClient();
        }
        return ourInstance;
    }

    private SocketClient() {
        EventBus.getDefault().register(this);
    }


    public void connect() throws MalformedURLException {
        this.socket = new SocketIO(SERVER_ADRESS);

        socket.connect(new IOCallback() {
            @Override
            public void onMessage(JSONObject json, IOAcknowledge ack) {
                try {
                    Log.i(TAG, "Server said:" + json.toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(String data, IOAcknowledge ack) {
                Log.i(TAG, "Server said: " + data);
            }

            @Override
            public void onError(SocketIOException socketIOException) {
                Log.i(TAG, "an Error occured");
                socketIOException.printStackTrace();
                socket.reconnect();
            }

            @Override
            public void onDisconnect() {
                System.out.println("Connection terminated.");
            }

            @Override
            public void onConnect() {
                Log.i(TAG, "Connection established");
            }

            @Override
            public void on(String event, IOAcknowledge ack, Object... args) {
                JSONObject json = (JSONObject) args[0];
                if (event.equals("position_update")) {
                    receivedPositionUpdate(json);
                } else if (event.equals("close")) {
                    receivedUserDisconnect(json);
                } else if(event.equals("photo_received")) {
                    receivedPhoto(json);
                }
                Log.i(TAG, "Server triggered event '" + event + "'" + args.toString());

            }
        });

        JSONObject json = new JSONObject();
        try {
            json.putOpt("user_id", MyUserData.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("connect", json);


    }

    private void receivedPhoto(JSONObject json) {
        try {
            String fbId = json.getString("user_id");
            JSONObject positionJson = json.getJSONObject("position");


            double latitude = positionJson.getInt("latitude");
            double longitude = positionJson.getInt("longitude");

            Location loc = new Location("socketio");
            loc.setLatitude(latitude);
            loc.setLongitude(longitude);

            String base64image = json.getString("image");

            ImageEvent imageEvent = new ImageEvent(fbId,loc,base64image);
            EventBus.getDefault().post(imageEvent);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void receivedUserDisconnect(JSONObject json) {
        try {
            String fbId = json.getString("user_id");
            EventBus.getDefault().post(new UserDisconnectEvent(fbId));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void receivedPositionUpdate(JSONObject json) {
        try {
            String fbId = json.getString("user_id");
            JSONObject positionJson = json.getJSONObject("position");


            double latitude = positionJson.getDouble("lat");
            double longitude = positionJson.getDouble("long");
            Location loc = new Location("socketio");
            loc.setLatitude(latitude);
            loc.setLongitude(longitude);
            EventBus.getDefault().post(new UserPositionEvent(new User(fbId, loc)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void disconnectFromServer() {
        socket.disconnect();
        ourInstance = null;
    }

    public void onEvent(MyPositionEvent myPositionEvent) {
        checkConnectionAndReconnectIfDisconnect();
        Log.i(TAG, myPositionEvent.toString());
        JSONObject json = new JSONObject();
        try {
            json.putOpt("latitude", myPositionEvent.getLocation().getLatitude());
            json.putOpt("longitude", myPositionEvent.getLocation().getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("position_update", json);
    }

    public void onEvent(MyImageEvent myImageEvent) {
        checkConnectionAndReconnectIfDisconnect();
        Log.i(TAG, "Got image event" + myImageEvent.toString());
        JSONObject json = new JSONObject();
        JSONObject locationObject = new JSONObject();
        try {
            locationObject.put("latitude", myImageEvent.getLocation().getLatitude());
            locationObject.put("longitude", myImageEvent.getLocation().getLongitude());
            json.put("position", locationObject);
            json.put("user_id", myImageEvent.getReceiverId());
            json.put("image", myImageEvent.getBase64image());
            this.socket.emit("send_photo", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkConnectionAndReconnectIfDisconnect() {
        if(this.socket != null && !this.socket.isConnected()) {
            this.socket.reconnect();
        }
    }
}
