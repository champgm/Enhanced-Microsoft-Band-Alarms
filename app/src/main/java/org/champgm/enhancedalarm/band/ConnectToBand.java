package org.champgm.enhancedalarm.band;

import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandException;
import com.microsoft.band.ConnectionState;

/**
 * An {@link android.os.AsyncTask} used to connect to a Microsoft Band
 */
public class ConnectToBand extends AsyncTask<BandClient, Void, ConnectionState> {
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
     * @return hopefully {@link ConnectionState#BOUND}
     */
    public static ConnectionState tryToConnect(final BandClient bandClient) {
        if (bandClient == null) {
            return ConnectionState.UNBOUND;
        }

        connectOnce(bandClient);

        int connectionAttemptCount = 1;
        while (!bandClient.isConnected() && connectionAttemptCount < GIVE_UP) {
            if (bandClient.isConnected()) {
                return ConnectionState.BOUND;
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
        return ConnectionState.UNBOUND;
    }

    /**
     * Attempts to connect to the band one time and eats any exceptions that might occur.
     * 
     * @param bandClient
     *            the client to use to connect
     * @return hopefully {@link com.microsoft.band.ConnectionState#BOUND}
     */
    private static ConnectionState connectOnce(final BandClient bandClient) {
        if (bandClient == null) {
            return ConnectionState.UNBOUND;
        }

        if (!bandClient.isConnected()) {
            try {
                // Try to connect
                return bandClient.connect().await();
            } catch (BandException e) {
                // Fail.
                return ConnectionState.UNBOUND;
            } catch (InterruptedException e1) {
                // Give up.
                return ConnectionState.UNBOUND;
            }
        } else {
            // Otherwise, that's fine, just return success
            return ConnectionState.BOUND;
        }
    }

    /**
     * Asynchronous call to connect to the band.
     *
     * @param bandClients
     *            a vararg of {@link com.microsoft.band.BandClient}s
     * @return hopefully {@link ConnectionState#BOUND}
     */
    @Override
    protected ConnectionState doInBackground(final BandClient... bandClients) {
        if (bandClients != null && bandClients.length > 0) {
            return tryToConnect(bandClients[0]);
        }
        return ConnectionState.UNBOUND;
    }
}