package org.champgm.enhancedalarm.band;

import android.util.Log;

import com.google.common.base.Preconditions;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.notification.BandNotificationException;
import com.microsoft.band.notification.VibrationType;

import org.champgm.enhancedalarm.timer.TimerListItemOnClickListener;

import java.util.concurrent.TimeoutException;

/**
 * Created by mc023219 on 4/6/15.
 */
public class Vibrate implements Runnable {
    private final int maxIterations;
    private final BandHelper bandHelper;
    private final TimerListItemOnClickListener timerListItemOnClickListener;
    private int currentIteration = 0;

    public Vibrate(final BandHelper bandHelper, final int maxIterations, final TimerListItemOnClickListener timerListItemOnClickListener) {
        this.bandHelper = Preconditions.checkNotNull(bandHelper, "bandHelper may not be null.");
        this.timerListItemOnClickListener = Preconditions.checkNotNull(timerListItemOnClickListener, "timerListItemEditButtonOnClickListener may not be null.");
        this.maxIterations = maxIterations;
    }

    @Override
    public void run() {
        if (currentIteration <= maxIterations) {
            try {
                bandHelper.vibrate(VibrationType.NOTIFICATION_ALARM);
            } catch (BandNotificationException e) {
                timerListItemOnClickListener.cancelTimer();
                Log.i("Vibration Runnable", e.toString());
            } catch (BandIOException e) {
                timerListItemOnClickListener.cancelTimer();
                Log.i("Vibration Runnable", e.toString());
            } catch (InterruptedException e) {
                timerListItemOnClickListener.cancelTimer();
                Log.i("Vibration Runnable", e.toString());
            } catch (TimeoutException e) {
                timerListItemOnClickListener.cancelTimer();
                Log.i("Vibration Runnable", e.toString());
            } catch (BandException e) {
                timerListItemOnClickListener.cancelTimer();
                Log.i("Vibration Runnable", e.toString());
            }
            currentIteration++;
        } else {
            timerListItemOnClickListener.cancelTimer();
        }
    }
}
