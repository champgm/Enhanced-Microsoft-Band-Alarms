package org.champgm.enhancedalarm.band;

import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandException;
import com.microsoft.band.ConnectionResult;

/**
 * An {@link android.os.AsyncTask} used to connect to a Microsoft Band
 */
public class ConnectToBand extends AsyncTask<BandClient, Void, ConnectionResult> {
    /**
     * The number of times we will try to connect before we give up.
     */
    public static final int GIVE_UP = 15;

    /**
     * Just in case you want to do this synchronously, this will keep spamming the connection every quarter-second until
     * success or until it gives up. Threshhold is currently set to
     * {@link org.champgm.enhancedalarm.band.ConnectToBand#GIVE_UP}
     * 
     * @param bandClient
     *            the client ot use to make the connection
     * @return hopefully {@link ConnectionResult#OK}
     */
    public static ConnectionResult tryToConnect(final BandClient bandClient) {
        connectOnce(bandClient);
        int connectionAttemptCount = 1;
        while (!bandClient.isConnected() && connectionAttemptCount < GIVE_UP) {
            if (bandClient.isConnected()) {
                return ConnectionResult.OK;
            } else {
                connectOnce(bandClient);
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

    @Override
    protected ConnectionResult doInBackground(final BandClient... clientParams) {
        return tryToConnect(clientParams[0]);
    }

    private static ConnectionResult connectOnce(final BandClient bandClient) {
        if (!bandClient.isConnected()) {
            Log.d("ConnectToBand", "Trying to connect...");
            try {
                // Try to connect
                return bandClient.connect().await();
            } catch (BandException e) {
                // Fail.
                Log.d("ConnectToBand", "Weird error.\n" + e.toString());
                return ConnectionResult.INTERNAL_ERROR;
            } catch (InterruptedException e1) {
                // Give up.
                Log.d("ConnectToBand", "Could not connect to band. Try again later?\n" + e1.toString());
                return ConnectionResult.TIMEOUT;
            }
        } else {
            // Otherwise, that's fine, just return success
            Log.d("ConnectToBand", "Already connected");
            return ConnectionResult.OK;
        }
    }
}