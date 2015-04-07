package org.champgm.enhancedalarm.timer;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.champgm.enhancedalarm.R;

import java.util.ArrayList;

/**
 * This is the custom adapter for the ListView in {@link org.champgm.enhancedalarm.MainActivity}
 */
public class TimerAdapter extends BaseAdapter {

    /**
     * They key used to store a timer list item in a result intent
     */
    public static final String PUT_EXTRA_ITEM_KEY = "641b707a-f6f1-4eea-ae88-482c44edd955";
    /**
     * They key used to store a position (in the TimerAdapter's ArrayList) in a result intent
     */
    public static final String PUT_EXTRA_POSITION_KEY = "6f4ca2b5-b84c-4d12-8dc2-52f52ba1ff90";
    /**
     * They key used to store a flag representing if a new item needs to be added
     */
    public static final String PUT_EXTRA_ADD_ITEM = "481001b8-4992-408e-acfd-637415627725";
    private final LayoutInflater layoutInflater;
    private final ArrayList<TimerListItem> items;
    private final Activity mainActivity;

    /**
     * Creates an instance
     * 
     * @param mainActivity
     *            a reference back to the {@link org.champgm.enhancedalarm.MainActivity} that this adapter belongs to
     */
    public TimerAdapter(final Activity mainActivity) {
        Log.i("TimerAdapter", "creating new timer adapter");
        this.mainActivity = mainActivity;
        this.layoutInflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        items = new ArrayList<>();
        items.add(new TimerListItem(30, 5, 999));
        items.add(TimerListItem.ADD_ITEM);
    }

    /**
     * Returns the current number of timers
     */
    @Override
    public int getCount() {
        return items.size();
    }

    /**
     * Returns the timer at a position
     * 
     * @param position
     *            position in the current array
     * @return the {@link org.champgm.enhancedalarm.timer.TimerListItem} at that position
     */
    @Override
    public TimerListItem getItem(final int position) {
        return items.get(position);
    }

    /**
     * Just returns the input, really, not sure what this is used for
     * 
     * @param position
     *            position of the item in the ArrayList
     * @return the input
     */
    @Override
    public long getItemId(final int position) {
        return position;
    }

    /**
     * Creates a view for the given list item. This is called for each item every time the list needs to be updated
     * 
     * @param position
     *            the position this item was in
     * @param convertView
     *            the existing view for this item
     * @param parent
     *            *shrug*
     * @return the completed view
     */
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        // Grab the list item corresponding to the view that should be created
        final TimerListItem timerListItem = items.get(position);
        // If this is the special add button, create that, set the button listener, and return it
        if (timerListItem.uuid == TimerListItem.ADD_ITEM_UUID) {
            final View addView = layoutInflater.inflate(R.layout.timer_list_add_item_layout, parent, false);
            final Button addButton = (Button) addView.findViewById(R.id.add_button);
            addButton.setOnClickListener(new TimerListItemEditButtonOnClickListener(position, this, mainActivity, true));
            return addView;
        } else {
            // Create a new view
            final View timerView = layoutInflater.inflate(R.layout.timer_list_item_layout, parent, false);

            // Grab references to all text fields
            final TextView intervalText = (TextView) timerView.findViewById(R.id.interval);
            final TextView delayText = (TextView) timerView.findViewById(R.id.delay);
            final TextView repeatText = (TextView) timerView.findViewById(R.id.repeat);

            // Fill the text fields in
            intervalText.setText(String.valueOf(timerListItem.interval));
            delayText.setText(String.valueOf(timerListItem.delay));
            repeatText.setText(String.valueOf(timerListItem.repeat));

            // Set the listener for the edit button
            final Button editButton = (Button) timerView.findViewById(R.id.edit_button);
            editButton.setOnClickListener(new TimerListItemEditButtonOnClickListener(position, this, mainActivity, false));

            // Set the right color
            if (timerListItem.started) {
                timerView.setBackgroundColor(mainActivity.getResources().getColor(R.color.activated_green));
            } else {
                timerView.setBackgroundColor(mainActivity.getResources().getColor(R.color.invisible));
            }

            return timerView;
        }
    }

    /**
     * Remove an item from the timer list
     * 
     * @param position
     *            location of the item to remove
     */
    public void removeItem(final int position) {
        items.remove(position);

        // Don't forget to update the list and views!
        updateData();
    }

    /**
     * Replaces an item in the list with a new one. Called by the
     * {@link org.champgm.enhancedalarm.timer.EditTimerActivity}
     * 
     * @param position
     *            position of the item to be replaced
     * @param newTimerListItem
     *            the new item
     */
    public void replaceItem(final int position, final TimerListItem newTimerListItem) {
        items.remove(position);
        items.add(newTimerListItem);

        // Don't forget to update the list and views!
        updateData();
    }

    /**
     * Just a debug method, prints the list's contents, probably not needed anymore
     */
    @Deprecated
    public void logList() {
        for (final TimerListItem item : items) {
            Log.i("Timer List[" + items.indexOf(item) + "]", item.toString());
        }
    }

    /**
     * This method will ensure that the add-item menu thing is there and that the list contents are refreshed. Call it
     * any time a list operation is done.
     */
    private void updateData() {
        ensureAddItem();

        // This thing is a superclass method, used to let whatever/whoever is controlling all of this stuff know that
        // some contents have changed and all views need to be regenerated.
        notifyDataSetChanged();
    }

    /**
     * This removes the add-item menu option and re-adds it to make sure that it is at the bottom of the list
     */
    private void ensureAddItem() {
        items.remove(TimerListItem.ADD_ITEM);
        items.add(TimerListItem.ADD_ITEM);
    }

    public ArrayList<TimerListItem> getItems() {
        final ArrayList<TimerListItem> clonedList = new ArrayList<>();
        for (final TimerListItem listItem : items) {
            clonedList.add(listItem.clone());
        }
        return clonedList;
    }
}
