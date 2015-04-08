package org.champgm.enhancedalarm.band;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandDeviceInfo;
import com.microsoft.band.notification.VibrationType;

/**
 * A service class to maintain a connection to the Microsoft Band and send vibration alarms.
 */
public class BandService extends IntentService {
    private BandClient bandClient = null;

    /**
     * Creates an instance, passes "BandService" to the superclass as its name.
     */
    public BandService() {
        super("BandService");
    }

    /**
     * Will attempt to connect to the band if it's not connected already, and then send a vibration alarm.
     * 
     * @param intent
     *            unused
     */
    @Override
    protected void onHandleIntent(final Intent intent) {
        // Create the band client if we haven't already
        if (bandClient == null) {
            final BandDeviceInfo[] pairedBands = BandClientManager.getInstance().getPairedBands();
            Log.i("BandService", "No band client yet, creating new one.");
            bandClient = BandClientManager.getInstance().create(this, pairedBands[0]);
        }

        // Attempt to connect if we aren't already
        if (!bandClient.isConnected()) {
            Log.i("BandService", "Band client not connected yet, connecting");
            new ConnectToBand().execute(bandClient);
        }

        // Send the vibration
        Log.i("BandService", "Sending vibration to: " + bandClient);
        new SendVibration(VibrationType.THREE_TONE_HIGH).execute(bandClient);
    }

    /**
     * Disconnect from the band when this service is destroyed
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bandClient != null && bandClient.isConnected()) {
            bandClient.disconnect();
        }
    }
}
