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
    @Override
    protected ConnectionResult doInBackground(final BandClient... clientParams) {
        try {
            // If we're not connected, try to.
            if (!clientParams[0].isConnected()) {
                Log.i("ConnectToBand", "Trying to connect...");
                return tryToConnect(clientParams[0]);
            } else {
                // Otherwise, that's fine, just return success
                Log.i("ConnectToBand", "Already connected");
                return ConnectionResult.OK;
            }
        } catch (InterruptedException e) {
            // Timeout exception, try again
            Log.i("ConnectToBand", "Connection timed out. Trying again");
            try {
                return tryToConnect(clientParams[0]);
            } catch (InterruptedException e1) {
                // Give up.
                Log.i("ConnectToBand", "Could not connect to band. Try again later?\n" + e1.toString());
                return ConnectionResult.TIMEOUT;
            }
        }

    }

    private ConnectionResult tryToConnect(final BandClient bandClient) throws InterruptedException {
        try {
            // Try to connect
            return bandClient.connect().await();
        } catch (BandException e) {
            // Fail.
            Log.i("ConnectToBand", "Weird error.\n" + e.toString());
            return ConnectionResult.INTERNAL_ERROR;
        }
    }
}