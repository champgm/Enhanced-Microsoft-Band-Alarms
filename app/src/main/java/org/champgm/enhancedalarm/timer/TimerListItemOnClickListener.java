package org.champgm.enhancedalarm.timer;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import org.champgm.enhancedalarm.R;

/**
 * The on-click listener for each item that is inside of each timer's view
 */
public class TimerListItemOnClickListener implements AdapterView.OnItemClickListener {
    private final TimerAdapter timerAdapter;
    private final Context context;

    /**
     * Creates an instance
     * 
     * @param timerAdapter
     *            that timer adapter you've been hearing so much about
     * @param context
     *            this will be view that represents the list item
     */
    public TimerListItemOnClickListener(final TimerAdapter timerAdapter, final Context context) {
        this.timerAdapter = timerAdapter;
        this.context = context;
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
        // Get the item from the adapter
        final TimerListItem timerListItem = timerAdapter.getItem(position);

        // Set the background color
        // (Also, in the future... near future hopefully, this is where the timer controlling will happen.
        if (timerListItem.started) {
            view.setBackgroundColor(context.getResources().getColor(R.color.invisible));
        } else {
            view.setBackgroundColor(context.getResources().getColor(R.color.activated_green));
        }
        timerListItem.started = !timerListItem.started;
    }
}
