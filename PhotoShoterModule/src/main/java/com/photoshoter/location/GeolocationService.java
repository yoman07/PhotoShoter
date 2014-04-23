package com.photoshoter.location;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by balu on 22-04-2014.
 */


public class GeolocationService extends Service {
    public GeolocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
