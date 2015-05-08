package org.champgm.enhancedalarm.timerui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import org.champgm.enhancedalarm.util.Checks;

/**
 * The on-click listener for the edit button that is inside of each timer's view
 */
public class TimerListItemEditButtonOnClickListener implements Button.OnClickListener {
    private final TimerAdapter timerAdapter;
    private final int position;
    private final boolean addNew;
    private final Fragment timersFragment;

    /**
     * Creates an instance
     *
     * @param position
     *            the position of the list-item-view-thing that the edit button that this button listener is assigned to
     *            is inside of. Or something. Android is complicated.
     * @param timerAdapter
     *            the timer adapter from the beginning. Will be used to grab the info for the corresponding item and
     *            send it to the {@link EditTimerActivity}.
     * @param addNew
     *            flag denoting if this is a new item or not
     */
    public TimerListItemEditButtonOnClickListener(
            final int position,
            final TimerAdapter timerAdapter,
            final Fragment timersFragment,
            final boolean addNew) {
        if (Checks.isNull(timerAdapter) || Checks.isNull(timersFragment)) {
            throw new RuntimeException("Cannot create an edit button listener if timersFragment or timer adapter are null");
        }
        this.timerAdapter = timerAdapter;
        this.timersFragment = timersFragment;
        this.position = position;
        this.addNew = addNew;
    }

    @Override
    public void onClick(final View view) {
        if (Checks.isNull(view)) {
            return;
        }
        // Basically, just put all of the input information into the Intent for the edit timersFragment
        final Intent editIntent = new Intent(view.getContext(), EditTimerActivity.class);
        editIntent.putExtra(TimerListItem.PUT_EXTRA_ITEM_KEY, timerAdapter.getItem(position));
        editIntent.putExtra(TimerListItem.PUT_EXTRA_POSITION_KEY, position);
        editIntent.putExtra(TimerAdapter.PUT_EXTRA_ADD_ITEM, addNew);

        // There MUST be a better way to do this. This is an on-click listener, so you're probably not supposed to start
        // activities from here. So, instead, we take the input reference to the main timersFragment and pretend like we are
        // launching the Edit timersFragment from there. When the edit timersFragment is done, it will send its results there. This
        // is sort of cool because you can have the handler method there that has some cases where it handles different
        // types of results, but it just seems kind of hacky. Hopefully I am doing it wrong.
        timersFragment.startActivityForResult(editIntent, EditTimerActivity.EDIT_REQUEST);
    }
}