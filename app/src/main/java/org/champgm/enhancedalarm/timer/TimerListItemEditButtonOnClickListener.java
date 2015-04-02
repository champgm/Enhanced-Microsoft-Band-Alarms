package org.champgm.enhancedalarm.timer;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class TimerListItemEditButtonOnClickListener implements Button.OnClickListener {
    private final TimerAdapter timerAdapter;
    private final int position;
    private final Activity mainActivity;
    private final boolean addNew;

    public TimerListItemEditButtonOnClickListener(
            final int position,
            final TimerAdapter timerAdapter,
            final Activity mainActivity,
            final boolean addNew) {
        this.position = position;
        this.timerAdapter = timerAdapter;
        this.mainActivity = mainActivity;
        this.addNew = addNew;
    }

    @Override
    public void onClick(final View view) {
        final Intent editIntent = new Intent(view.getContext(), EditTimerActivity.class);
        editIntent.putExtra(TimerAdapter.PUT_EXTRA_ITEM_KEY, timerAdapter.getItem(position));
        editIntent.putExtra(TimerAdapter.PUT_EXTRA_POSITION_KEY, position);
        editIntent.putExtra(TimerAdapter.PUT_EXTRA_ADD_ITEM, addNew);

        mainActivity.startActivityForResult(editIntent, EditTimerActivity.EDIT_REQUEST);
    }
}
