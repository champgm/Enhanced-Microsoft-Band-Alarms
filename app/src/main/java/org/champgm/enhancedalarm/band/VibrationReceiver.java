package org.champgm.enhancedalarm.band;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.microsoft.band.notification.VibrationType;public class VibrationReceiver extends BroadcastReceiver {

    public VibrationReceiver() {
        Log.i("VibrationReceiver", "instantiated");
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.i("VibrationReceiver", "got a broadcast");
        Toast.makeText(context, "vibrating", Toast.LENGTH_LONG);
        BandHelper bandHelper = null;
        try {
            Log.i("VibrationReceiver", "trying to make a new band helper");
            bandHelper = new BandHelper(context);
            Log.i("VibrationReceiver", "trying to vibrate");
            bandHelper.vibrate(VibrationType.NOTIFICATION_ALARM);
        } catch (Exception e) {
            Log.i("VibrationReceiver", "failed.\n" + e.toString());
            e.printStackTrace();
        }
    }
}


