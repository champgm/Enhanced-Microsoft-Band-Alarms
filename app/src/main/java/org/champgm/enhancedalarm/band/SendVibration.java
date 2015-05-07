package org.champgm.enhancedalarm.band;

import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.band.BandClient;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.notifications.VibrationType;

/**
 * A task for sending vibration to the band.
 */
public class SendVibration extends AsyncTask<BandClient, Void, ConnectionState> {
    /**
     * The number of times we will try to send the vibration before we give up.
     */
    public static final int GIVE_UP = 15;
    private final VibrationType vibrationType;

    /**
     * Creats an instance
     * 
     * @param vibrationType
     *            the {@link com.microsoft.band.notifications.VibrationType} to send to the band
     */
    public SendVibration(final VibrationType vibrationType) {
        this.vibrationType = vibrationType;
    }

    /**
     * Just in case you want to do this synchronously. This will keep trying to vibrate the band until it gives up.
     *
     * @param bandClient
     *            the client to use to send the vibration
     * @return the {@link com.microsoft.band.ConnectionState}
     */
    public static ConnectionState sendVibration(final BandClient bandClient, final VibrationType vibrationType) {
        if (bandClient != null && vibrationType != null) {
            int vibrationAttemptCount = 1;
            ConnectionState connectionState = sendVibrationOnce(bandClient, vibrationType);
            while (ConnectionState.BOUND != connectionState && vibrationAttemptCount < GIVE_UP) {
                connectionState = sendVibrationOnce(bandClient, vibrationType);
                if (ConnectionState.BOUND == connectionState) {
                    return ConnectionState.BOUND;
                } else {
                    vibrationAttemptCount++;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Log.i("ConnectToBand", "Problem while delaying: \n" + e.toString());
                    }
                }
            }
            return ConnectionState.UNBOUND;
        }
        return ConnectionState.UNBOUND;
    }

    /**
     * Attempts to send a vibration type one time and eats any exception that might occur
     *
     * @param bandClient
     *            the client to use to send the vibration
     * @param vibrationType
     *            the type of vibration to send
     * @return hopefully {@link com.microsoft.band.ConnectionState#BOUND}
     */
    private static ConnectionState sendVibrationOnce(final BandClient bandClient, final VibrationType vibrationType) {
        if (bandClient != null && vibrationType != null) {
            try {
                if (!bandClient.isConnected()) {
                    BandHelper.connectToBand(bandClient);
                }

                bandClient.getNotificationManager().vibrate(vibrationType).await();
                return ConnectionState.BOUND;
            } catch (Exception e) {
                return ConnectionState.UNBOUND;
            }
        }
        return ConnectionState.UNBOUND;
    }

    /**
     * Attempts to connect to a band
     *
     * @param bandClients
     *            band clients, the first of which will be connected
     * @return the {@link com.microsoft.band.ConnectionState}
     */
    @Override
    protected ConnectionState doInBackground(final BandClient... bandClients) {
        if (bandClients != null && bandClients.length > 0) {
            return sendVibration(bandClients[0], vibrationType);
        }
        return ConnectionState.UNBOUND;
    }
}
