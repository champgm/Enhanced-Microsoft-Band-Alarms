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
    /**
     * The number of times we will try to send the vibration before we give up.
     */
    public static final int GIVE_UP = 15;
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
     * Just in case you want to do this synchronously. This will keep trying to vibrate the band until it gives up.
     *
     * @param bandClient
     *            the client to use to send the vibration
     * @return
     */
    public static ConnectionResult sendVibration(final BandClient bandClient, final VibrationType vibrationType) {
        int connectionAttemptCount = 1;
        ConnectionResult connectionResult = sendVibrationOnce(bandClient, vibrationType);
        while (ConnectionResult.OK != connectionResult && connectionAttemptCount < GIVE_UP) {
            connectionResult = sendVibrationOnce(bandClient, vibrationType);
            if (ConnectionResult.OK == connectionResult) {
                return ConnectionResult.OK;
            } else {
                connectionAttemptCount++;
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Log.i("ConnectToBand", "Problem while delaying: \n" + e.toString());
                }
            }
        }
        return ConnectionResult.TIMEOUT;
    }

    private static ConnectionResult sendVibrationOnce(final BandClient bandClient, final VibrationType vibrationType) {
        try {
            bandClient.getNotificationManager().vibrate(vibrationType).await();
            return ConnectionResult.OK;
        } catch (Exception e) {
            return ConnectionResult.INTERNAL_ERROR;
        }
    }

    /**
     * Attempts to
     * NOTE: MAKE SURE THE CLIENT IS CONNECTED BEFORE ATTEMPTING
     *
     * @param bandClients
     * @return
     */
    @Override
    protected ConnectionResult doInBackground(final BandClient... bandClients) {
        return sendVibration(bandClients[0], vibrationType);
    }
}
