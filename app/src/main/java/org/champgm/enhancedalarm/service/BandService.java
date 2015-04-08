package org.champgm.enhancedalarm.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandDeviceInfo;

import org.champgm.enhancedalarm.band.ConnectToBand;
import org.champgm.enhancedalarm.band.SendVibration;

import java.util.Arrays;

public class BandService extends IntentService {

    private BandClient bandClient = null;

    public BandService() {
        super("BandService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final BandDeviceInfo[] pairedBands = BandClientManager.getInstance().getPairedBands();
        Log.i("BandService", "======Found some bands=========");
        Log.i("BandService", Arrays.toString(pairedBands));

        if (bandClient == null) {
            Log.i("BandService", "No band client yet, creating new one.");
            bandClient = BandClientManager.getInstance().create(this, pairedBands[0]);
        }

        if (!bandClient.isConnected()) {
            Log.i("BandService", "Band client not connected yet, connecting");
            new ConnectToBand().execute(bandClient);
        }

        Log.i("BandService", "Sending vibration to: " + bandClient);
        new SendVibration().execute(bandClient);
    }

}
