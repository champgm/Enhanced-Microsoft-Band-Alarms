package org.champgm.enhancedalarm.timer;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import org.champgm.enhancedalarm.R;

/**
 * Created by mc023219 on 4/2/15.
 */
public class TimerListItemOnClickListener implements AdapterView.OnItemClickListener {
    private final TimerAdapter timerAdapter;
    private final Context context;

    public TimerListItemOnClickListener(final TimerAdapter timerAdapter, final Context context) {
        this.timerAdapter = timerAdapter;
        this.context = context;
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {

        final TimerListItem timerListItem = timerAdapter.getTimerListItem(position);
        if (timerListItem.started) {
            view.setBackgroundColor(context.getResources().getColor(R.color.invisible));
        } else {
            view.setBackgroundColor(context.getResources().getColor(R.color.activated_green));
        }
        timerListItem.started = !timerListItem.started;

        final StringBuilder toastContent = new StringBuilder()
                .append("Interval: ")
                .append(timerListItem.interval)
                .append("\n")
                .append("Delay: ")
                .append(timerListItem.delay)
                .append("\n")
                .append("Repeat: ")
                .append(timerListItem.repeat);

        Toast.makeText(context, toastContent.toString(), Toast.LENGTH_LONG).show();
    }
}
