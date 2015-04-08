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
            Thread.sleep(5000);
            if (!clientParams[0].isConnected()) {
                Log.i("ConnectToBand", "Trying to connect...");
                return clientParams[0].connect().await();
            } else {
                Log.i("ConnectToBand", "Already connected");
                return ConnectionResult.OK;
            }
        } catch (InterruptedException e) {
            Log.i("ConnectToBand", "\n" + e.toString());
            return ConnectionResult.TIMEOUT;
        } catch (BandException e) {
            Log.i("ConnectToBand", "\n" + e.toString());
            return ConnectionResult.INTERNAL_ERROR;
        }
    }
}