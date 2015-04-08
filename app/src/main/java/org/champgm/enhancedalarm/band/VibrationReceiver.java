package org.champgm.enhancedalarm.band;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * A {@link android.content.BroadcastReceiver} which will vibrate the Microsoft Band
 */
public class VibrationReceiver extends BroadcastReceiver {

    /**
     * Creates an instance
     */
    public VibrationReceiver() {
        Log.i("VibrationReceiver", "instantiated");
    }

    /**
     * Starts the {@link org.champgm.enhancedalarm.band.BandService}
     * @param context used to start the service
     * @param intent unused
     */
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.i("VibrationReceiver", "Received broadcast, starting service");
        final Intent bandServiceIntent = new Intent(context, BandService.class);
        context.startService(bandServiceIntent);
    }
}
