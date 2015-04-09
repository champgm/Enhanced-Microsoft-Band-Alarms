package org.champgm.enhancedalarm.band;

import android.os.AsyncTask;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.microsoft.band.BandClient;
import com.microsoft.band.ConnectionResult;
import com.microsoft.band.notification.VibrationType;

/**
 * Created by mc023219 on 4/7/15.
 */
public class SendVibration extends AsyncTask<BandClient, Void, ConnectionResult> {
    private final VibrationType vibrationType;

    /**
     * Creats an instance
     * 
     * @param vibrationType
     *            the {@link com.microsoft.band.notification.VibrationType} to send to the band
     */
    public SendVibration(final VibrationType vibrationType) {
        this.vibrationType = Preconditions.checkNotNull(vibrationType, "vibrationType may not be null.");
    }

    /**
     * Attempts to
     * NOTE: MAKE SURE THE CLIENT IS CONNECTED BEFORE ATTEMPTING
     * @param bandClients
     * @return
     */
    @Override
    protected ConnectionResult doInBackground(final BandClient... bandClients) {
        Log.d("SendVibration", "Band should be connected... trying to send.");
        try {
            Log.d("SendVibration", "Sending vibration");
            bandClients[0].getNotificationManager().vibrate(vibrationType).await();
        } catch (Exception e) {
            Log.d("SendVibration", "\n" + e.toString());
            return ConnectionResult.INTERNAL_ERROR;
        }

        return ConnectionResult.OK;
    }
}
