package org.champgm.enhancedalarm.band;

import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandException;
import com.microsoft.band.ConnectionResult;

/**
 * Created by mc023219 on 3/27/15.
 */
public class ConnectToBand extends AsyncTask<BandClient, Void, ConnectionResult> {
    @Override
    protected ConnectionResult doInBackground(final BandClient... clientParams) {
        try {
            Log.i("====CON", "Trying to connect...");
            return clientParams[0].connect().await();
        } catch (InterruptedException e) {
            Log.i("====CON", "Connection Timeout");
            return ConnectionResult.TIMEOUT;
        } catch (BandException e) {
            Log.i("====CON", "Internal Error");
            return ConnectionResult.INTERNAL_ERROR;
        }
    }

    protected void onPostExecute(final ConnectionResult result) {
        if (result != ConnectionResult.OK) {
            throw new RuntimeException("Failed to connect to band.");
        }
    }
}