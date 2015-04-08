package org.champgm.enhancedalarm.band;

import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.band.BandClient;
import com.microsoft.band.ConnectionResult;
import com.microsoft.band.notification.VibrationType;

/**
 * Created by mc023219 on 4/7/15.
 */
public class SendVibration extends AsyncTask<BandClient, Void, ConnectionResult> {
    @Override
    protected ConnectionResult doInBackground(final BandClient... bandClients) {
        Log.i("SendVibration", "Band should be connected... trying to send.");
        try {
            Log.i("SendVibration", "Sending vibration");
            bandClients[0].getNotificationManager().vibrate(VibrationType.NOTIFICATION_ALARM).await();
        } catch (Exception e) {
            Log.i("SendVibration", "\n" + e.toString());
            return ConnectionResult.INTERNAL_ERROR;
        }

        return ConnectionResult.OK;
    }
}
