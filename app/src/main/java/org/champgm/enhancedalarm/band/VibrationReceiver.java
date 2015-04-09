package org.champgm.enhancedalarm.band;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * A {@link android.content.BroadcastReceiver} which will vibrate the Microsoft Band
 */
public class VibrationReceiver extends BroadcastReceiver {
    public static final String TIMER_UUID_KEY = "4fb3b86a-70af-4dba-812e-964f4478a50d";

    /**
     * Creates an instance
     */
    public VibrationReceiver() {
        Log.d("VibrationReceiver", "instantiated");
    }

    /**
     * Starts the {@link BandIntentService}
     * 
     * @param context
     *            used to start the service
     * @param intent
     *            unused
     */
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d("VibrationReceiver", "Received broadcast, starting service.");
        // final Intent bandServiceIntent = new Intent(context, BandIntentService.class);
        // context.startService(bandServiceIntent);

        final Intent bandServiceIntent = new Intent(context, BandService.class);
        bandServiceIntent.putExtra(TIMER_UUID_KEY, intent.getStringExtra(TIMER_UUID_KEY));
        context.startService(bandServiceIntent);
    }
}
