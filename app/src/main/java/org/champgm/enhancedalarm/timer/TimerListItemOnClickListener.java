package org.champgm.enhancedalarm.timer;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.common.base.Preconditions;

import org.champgm.enhancedalarm.R;
import org.champgm.enhancedalarm.band.BandHelper;
import org.champgm.enhancedalarm.band.Vibrate;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The on-click listener for each item that is inside of each timer's view
 */
public class TimerListItemOnClickListener implements AdapterView.OnItemClickListener {
    private final BandHelper bandHelper;
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private final TimerAdapter timerAdapter;
    private final Context context;
    private ScheduledFuture<?> scheduledFuture;
    private View view;
    private TimerListItem timerListItem;

    /**
     * Creates an instance
     * 
     * @param timerAdapter
     *            that timer adapter you've been hearing so much about
     * @param context
     *            this will be view that represents the list item
     */
    public TimerListItemOnClickListener(final TimerAdapter timerAdapter, final BandHelper bandHelper, final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor, final Context context) {
        this.timerAdapter = Preconditions.checkNotNull(timerAdapter, "timerAdapter may not be null.");
        this.bandHelper = Preconditions.checkNotNull(bandHelper, "bandHelper may not be null.");
        this.context = Preconditions.checkNotNull(context, "context may not be null.");
        this.scheduledThreadPoolExecutor = Preconditions.checkNotNull(scheduledThreadPoolExecutor, "scheduledThreadPoolExecutor may not be null.");
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
        this.view = view;

        // Get the item from the adapter
        timerListItem = timerAdapter.getItem(position);

        // Ignore the add button item thing, that doesn't need an on-item-click listener.
        if (timerListItem.uuid != TimerListItem.ADD_ITEM_UUID) {
            if (timerListItem.started) {
                cancelTimer();
            } else {
                // Attempt to start the runnable that will keep vibrating the band
                try {
                    scheduledFuture = scheduledThreadPoolExecutor.scheduleAtFixedRate(new Vibrate(bandHelper, timerListItem.repeat, this), Long.valueOf(timerListItem.delay), Long.valueOf(timerListItem.interval), TimeUnit.SECONDS);
                } catch (RejectedExecutionException tooMany) {
                    Toast.makeText(context, "Too many timers started. Stop some.", Toast.LENGTH_LONG);
                }

                // Set the background green
                this.view.setBackgroundColor(context.getResources().getColor(R.color.activated_green));
                timerListItem.started = true;
            }
        }
    }

    /**
     * The runnable will also call this when it has iterated the set number of times.
     */
    public void cancelTimer() {
        // Cancel the runnable
        scheduledFuture.cancel(true);
        // Set the background color to clear
        view.setBackgroundColor(context.getResources().getColor(R.color.invisible));
        // Toggle the timer status
        timerListItem.started = false;
        // Notify the list adapter that something has changed
        timerAdapter.notifyDataSetChanged();
    }

}
