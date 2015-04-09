package org.champgm.enhancedalarm.timerui;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.common.base.Preconditions;

import org.champgm.enhancedalarm.R;
import org.champgm.enhancedalarm.band.BandService;
import org.champgm.enhancedalarm.band.VibrationReceiver;

/**
 * The on-click listener for each item that is inside of each timer's view. I would really prefer this class not be
 * inside of MainActivity, but other than passing around an instance of MainActivity, I couldn't find a sane way to
 */
public class TimerListItemOnClickListener implements AdapterView.OnItemClickListener {
    private final TimerAdapter timerAdapter;

    /**
     * Creates an instance
     *
     * @param timerAdapter
     *            that timer adapter you've been hearing so much about
     */
    public TimerListItemOnClickListener(final TimerAdapter timerAdapter) {
        this.timerAdapter = Preconditions.checkNotNull(timerAdapter, "timerAdapter may not be null.");
    }

    /**
     * Stuff that happens when you click the list items
     *
     * @param parent
     *            Maybe this is the TimerAdapter? Maybe that can be removed from the constructor
     * @param view
     *            the view to set green/red
     * @param position
     *            the position of the item that was clicked
     * @param id
     *            i don't think this is needed
     */
    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        Log.d("itemClick", "Item clicked");

        // Get the item from the adapter
        final TimerListItem timerListItem = timerAdapter.getItem(position);

        // Ignore the add button item thing, that doesn't need an on-item-click listener.
        if (timerListItem.uuid != TimerListItem.ADD_ITEM_UUID) {
            Log.d("itemClick", "New intents");
            final Intent intent = new Intent(view.getContext(), VibrationReceiver.class);
            intent.putExtra(VibrationReceiver.TIMER_UUID_KEY, timerListItem.uuid.toString());
            final PendingIntent pendingIntent = PendingIntent.getBroadcast(view.getContext(), timerListItem.uuid.hashCode(), intent, 0);

            Log.d("itemClick", "Not add-item");
            if (timerListItem.started) {
                Log.d("itemClick", "Canceling timer for uuid: " + timerListItem.uuid.toString());
                final AlarmManager alarmManager = (AlarmManager) view.getContext().getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();

                final Intent bandServiceIntent = new Intent(view.getContext(), BandService.class);
                view.getContext().stopService(bandServiceIntent);

                Log.d("itemClick", "Clearing green background");
                // Set the background color to clear
                view.setBackgroundColor(view.getContext().getResources().getColor(R.color.invisible));
                // Toggle the timer status

                Log.d("itemClick", "Started - false");
                timerListItem.started = false;
                // Notify the list adapter that something has changed

                Log.d("itemClick", "Data has changed");
                timerAdapter.notifyDataSetChanged();
            } else {
                Log.d("itemClick", "timer not started");
                Log.d("itemClick", "creating alarm manager");
                // Attempt to start the runnable that will keep vibrating the band
                final AlarmManager alarmManager = (AlarmManager) view.getContext().getSystemService(Context.ALARM_SERVICE);

                Log.d("itemClick", "Set repeating alarm");
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + timerListItem.delay * 1000, timerListItem.interval * 1000, pendingIntent);

                // Set the background green
                view.setBackgroundColor(view.getContext().getResources().getColor(R.color.activated_green));
                timerListItem.started = true;
            }
        }
    }
}
