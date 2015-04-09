package org.champgm.enhancedalarm.band;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.microsoft.band.BandClient;
import com.microsoft.band.notification.VibrationType;

/**
 * A service class to maintain a connection to the Microsoft Band and send vibration alarms.
 */
public class BandService extends Service {
    final BandServiceBinder binder = new BandServiceBinder();
    private BandClient bandClient = null;

    /**
     * Creates an instance, passes "BandService" to the superclass as its name.
     */
    public BandService() {
        super();
        Log.d("BandService", "Service instantiated.");
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("BandService", "Received command to vibrate for timer uuid: " + intent.getStringExtra(VibrationReceiver.TIMER_UUID_KEY));

        // Create the band client if we haven't already
        if (bandClient == null) {
            Log.d("BandService", "No band client yet, creating new one.");
            bandClient = BandHelper.getBandClient(this, 0);
            BandHelper.connectToBand(bandClient);
        }

        // Send the vibration
        Log.d("BandService", "Sending vibration to: " + bandClient);
        BandHelper.sendVibration(VibrationType.THREE_TONE_HIGH, bandClient);

        return START_STICKY;
    }

    /**
     * Will attempt to connect to the band if it's not connected already, and then send a vibration alarm.
     *
     * @param intent
     *            unused
     */
    @Override
    public IBinder onBind(final Intent intent) {
        return binder;
    }

    /**
     * Disconnect from the band when this service is destroyed
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("BandService", "Service will be destroyed.");
        BandHelper.disconnect(bandClient);
    }

    public class BandServiceBinder extends Binder {
        BandService getService() {
            return BandService.this;
        }
    }
}
