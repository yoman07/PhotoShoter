package com.photoshoter;

import android.util.Log;

import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.callback.ConnectCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.SocketIOClient;
import com.photoshoter.events.PositionEvent;

import org.json.JSONArray;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by yoman on 23.04.2014.
 */
public class SocketClient {
    private static final String TAG = "SocketClient";
    private static String SERVER_ADRESS = "http://geo-chat.herokuapp.com:80";
    private static SocketClient ourInstance = new SocketClient();

    public static SocketClient getInstance() {
        return ourInstance;
    }

    private SocketClient() {

    }

    public void connect() {
        SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), SERVER_ADRESS, new SocketIOClient.SocketIOConnectCallback() {

            @Override
            public void onConnectCompleted(Exception e, SocketIOClient socketIOClient) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                EventBus.getDefault().register(this);
                Log.i("SocketIOClient", "onConnectCompleted");


                socketIOClient.setEventCallback(new SocketIOClient.EventCallback() {

                    @Override
                    public void onEvent(String s, JSONArray jsonArray) {
                        Log.i("SocketIOClient",s + " json: " + jsonArray.toString());
                    }
                });

                socketIOClient.setStringCallback(new SocketIOClient.StringCallback() {
                    @Override
                    public void onString(String string) {
                        Log.i("SocketIOClient",string);
                    }
                });

                socketIOClient.setJSONCallback(new SocketIOClient.JSONCallback() {
                    @Override
                    public void onJSON(JSONObject json) {
                        Log.i("SocketIOClient","json: " + json.toString());
                    }
                });



                socketIOClient.emit("test");
            }
        });

    }

    public void onEvent(PositionEvent positionEvent) {
        Log.i(TAG, positionEvent.toString());
    }

}
