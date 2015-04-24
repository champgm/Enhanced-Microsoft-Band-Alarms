package org.champgm.enhancedalarm.band;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.champgm.enhancedalarm.util.Toaster;

/**
 * A {@link android.content.BroadcastReceiver} which will vibrate the Microsoft Band
 */
public class VibrationReceiver extends BroadcastReceiver {

    /**
     * The key that should be used to store timer UUIDs in the intent that this receiver will receive
     */
    public static final String TIMER_UUID_KEY = "4fb3b86a-70af-4dba-812e-964f4478a50d";
    /**
     * The key that should be used to store the {@link com.microsoft.band.notification.VibrationType}, that should be
     * sent to the band, in the intent that this receiver will receive
     */
    public static final String VIBRATION_TYPE_KEY = "4fb3b86a-70af-4dba-812e-964f4478a50d";

    /**
     * Starts the {@link BandService} and sends a vibration intent to it
     * 
     * @param context
     *            used to start the service
     * @param intent
     *            unused
     */
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (context == null) {
            return;
        } else if (intent == null) {
            Toaster.send(context, "VibrationReceiver received null Intent. Could not vibrate band.");
            return;
        }
        // Create an intent, put the necessary timer UID and vibration type on it, and use it to start the BandService
        final Intent bandServiceIntent = new Intent(context, BandService.class);
        bandServiceIntent.putExtra(TIMER_UUID_KEY, intent.getStringExtra(TIMER_UUID_KEY));
        bandServiceIntent.putExtra(VIBRATION_TYPE_KEY, intent.getStringExtra(VIBRATION_TYPE_KEY));
        context.startService(bandServiceIntent);
    }
}
