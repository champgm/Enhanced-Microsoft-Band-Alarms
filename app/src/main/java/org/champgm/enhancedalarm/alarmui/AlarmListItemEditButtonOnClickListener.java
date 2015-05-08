package org.champgm.enhancedalarm.alarmui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import org.champgm.enhancedalarm.timerui.EditTimerActivity;
import org.champgm.enhancedalarm.timerui.TimerAdapter;
import org.champgm.enhancedalarm.timerui.TimerListItem;
import org.champgm.enhancedalarm.util.Checks;

public class AlarmListItemEditButtonOnClickListener  implements Button.OnClickListener {
    private final AlarmAdapter alarmAdapter;
    private final int position;
    private final boolean addNew;
    private final Fragment alarmsFragment;

    /**
     * Creates an instance
     *
     * @param position
     *            the position of the list-item-view-thing that the edit button that this button listener is assigned to
     *            is inside of. Or something. Android is complicated.
     * @param alarmAdapter
     *            the timer adapter from the beginning. Will be used to grab the info for the corresponding item and
     *            send it to the {@link EditTimerActivity}.
     * @param addNew
     *            flag denoting if this is a new item or not
     */
    public AlarmListItemEditButtonOnClickListener(
            final int position,
            final AlarmAdapter alarmAdapter,
            final Fragment alarmsFragment,
            final boolean addNew) {
        if (Checks.isNull(alarmAdapter) || Checks.isNull(alarmsFragment)) {
            throw new RuntimeException("Cannot create an edit button listener if alarmsFragment or timer adapter are null");
        }
        this.alarmAdapter = alarmAdapter;
        this.alarmsFragment = alarmsFragment;
        this.position = position;
        this.addNew = addNew;
    }

    @Override
    public void onClick(final View view) {
        if (Checks.isNull(view)) {
            return;
        }
        // Basically, just put all of the input information into the Intent for the edit alarmsFragment
        final Intent editIntent = new Intent(view.getContext(), EditAlarmActivity.class);
        editIntent.putExtra(AlarmListItem.PUT_EXTRA_ITEM_KEY, alarmAdapter.getItem(position));
        editIntent.putExtra(AlarmListItem.PUT_EXTRA_POSITION_KEY, position);
        editIntent.putExtra(TimerAdapter.PUT_EXTRA_ADD_ITEM, addNew);

        // There MUST be a better way to do this. This is an on-click listener, so you're probably not supposed to start
        // activities from here. So, instead, we take the input reference to the main alarmsFragment and pretend like we are
        // launching the Edit alarmsFragment from there. When the edit alarmsFragment is done, it will send its results there. This
        // is sort of cool because you can have the handler method there that has some cases where it handles different
        // types of results, but it just seems kind of hacky. Hopefully I am doing it wrong.
        alarmsFragment.startActivityForResult(editIntent, EditTimerActivity.EDIT_REQUEST);
    }
}