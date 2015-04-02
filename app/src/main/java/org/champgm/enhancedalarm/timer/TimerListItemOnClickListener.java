package org.champgm.enhancedalarm.timer;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

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

        final TimerListItem timerListItem = timerAdapter.getItem(position);
        if (timerListItem.started) {
            view.setBackgroundColor(context.getResources().getColor(R.color.invisible));
        } else {
            view.setBackgroundColor(context.getResources().getColor(R.color.activated_green));
        }
        timerListItem.started = !timerListItem.started;


        Log.i("Item Clicked: ", timerListItem.toString());
    }
}
