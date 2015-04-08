package org.champgm.enhancedalarm.timerui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.common.base.Preconditions;

import org.champgm.enhancedalarm.R;
import org.champgm.enhancedalarm.band.VibrationReceiver;

/**
 * The on-click listener for each item that is inside of each timer's view
 */
public class TimerListItemOnClickListener implements AdapterView.OnItemClickListener {
    private final TimerAdapter timerAdapter;
    private final Context context;
    private View view;
    private TimerListItem timerListItem;
    private PendingIntent pendingIntent = null;

    /**
     * Creates an instance
     * 
     * @param timerAdapter
     *            that timer adapter you've been hearing so much about
     * @param context
     *            this will be view that represents the list item
     */
    public TimerListItemOnClickListener(final TimerAdapter timerAdapter, final Context context) {
        this.timerAdapter = Preconditions.checkNotNull(timerAdapter, "timerAdapter may not be null.");
        this.context = Preconditions.checkNotNull(context, "context may not be null.");
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
        Log.i("itemClick", "Item clicked");
        this.view = view;

        // Get the item from the adapter
        timerListItem = timerAdapter.getItem(position);

        // Ignore the add button item thing, that doesn't need an on-item-click listener.
        if (timerListItem.uuid != TimerListItem.ADD_ITEM_UUID) {
            Log.i("itemClick", "Not add-item");
            if (timerListItem.started) {
                Log.i("itemClick", "timer started, canceling");
                cancelTimer();
            } else {
                Log.i("itemClick", "timer not started");
                if (pendingIntent != null) {
                    Log.i("itemClick", "pending intent not null, canceling timer");
                    cancelTimer();
                }

                Log.i("itemClick", "creating alarm manager");
                // Attempt to start the runnable that will keep vibrating the band
                final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                Log.i("itemClick", "New intents");
                final Intent intent = new Intent(context, VibrationReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(context, 4386, intent, 0);

                Log.i("itemClick", "Set repeating alarm");
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, timerListItem.delay * 1000, timerListItem.interval * 1000, pendingIntent);
                // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), timerListItem.interval
                // * 1000, pendingIntent);

                // Set the background green
                this.view.setBackgroundColor(context.getResources().getColor(R.color.activated_green));
                timerListItem.started = true;
            }
        }
    }

    public void cancelTimer() {
        if (pendingIntent != null) {
            Log.i("itemClick", "pending intent not null, creating alarm manager");
            final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Log.i("itemClick", "canceling");
            alarmManager.cancel(pendingIntent);
            Log.i("itemClick", "setting intent to null");
            pendingIntent = null;
        }

        Log.i("itemClick", "Clearing green background");
        // Set the background color to clear
        view.setBackgroundColor(context.getResources().getColor(R.color.invisible));
        // Toggle the timer status

        Log.i("itemClick", "Started - false");
        timerListItem.started = false;
        // Notify the list adapter that something has changed

        Log.i("itemClick", "Data has changed");
        timerAdapter.notifyDataSetChanged();
    }

}
