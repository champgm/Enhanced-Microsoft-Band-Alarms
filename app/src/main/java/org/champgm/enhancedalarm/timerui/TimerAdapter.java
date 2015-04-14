package org.champgm.enhancedalarm.timerui;

import java.util.ArrayList;

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

import com.google.common.base.Preconditions;
import com.microsoft.band.notification.VibrationType;

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
    private final ArrayList<TimerListItem> contents;
    private final LayoutInflater layoutInflater;
    private final Activity mainActivity;

    /**
     * Creates an instance with one sample timer item
     * 
     * @param mainActivity
     *            a reference back to the {@link org.champgm.enhancedalarm.MainActivity} that this adapter belongs to
     */
    public TimerAdapter(final Activity mainActivity) {
        this(mainActivity, newList());
    }

    /**
     * Creates an instance with a blank list of contents
     *
     * @param mainActivity
     *            a reference back to the {@link org.champgm.enhancedalarm.MainActivity} that this adapter belongs to
     */
    public TimerAdapter(final Activity mainActivity, final ArrayList<TimerListItem> contents) {
        this.mainActivity = Preconditions.checkNotNull(mainActivity, "mainActivity may not be null.");
        this.contents = Preconditions.checkNotNull(contents, "contents may not be null.");
        ensureAddItem();

        Log.d("TimerAdapter", "creating new timer adapter");
        this.layoutInflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Returns the current number of timers
     */
    @Override
    public int getCount() {
        return contents.size();
    }

    /**
     * Returns the timer at a position
     * 
     * @param position
     *            position in the current array
     * @return the {@link TimerListItem} at that position
     */
    @Override
    public TimerListItem getItem(final int position) {
        return contents.get(position);
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
        final TimerListItem timerListItem = contents.get(position);
        // If this is the special add button, create that, set the button listener, and return it
        if (timerListItem.uuid == TimerListItem.ADD_ITEM_UUID) {
            final View addView = layoutInflater.inflate(R.layout.timer_list_add, parent, false);
            final Button addButton = (Button) addView.findViewById(R.id.add_button);
            addButton.setOnClickListener(new TimerListItemEditButtonOnClickListener(position, this, mainActivity, true));
            return addView;
        } else {
            // Create a new view
            final View timerView = layoutInflater.inflate(R.layout.timer_list_item, parent, false);

            // Grab references to all text fields
            final TextView intervalText = (TextView) timerView.findViewById(R.id.interval);
            final TextView delayText = (TextView) timerView.findViewById(R.id.delay);

            // Fill the text fields in
            intervalText.setText(String.valueOf(timerListItem.interval));
            delayText.setText(String.valueOf(timerListItem.delay));

            // Set the listener for the edit button
            final Button editButton = (Button) timerView.findViewById(R.id.edit_button);
            editButton.setOnClickListener(new TimerListItemEditButtonOnClickListener(position, this, mainActivity, false));

            // Set the right color
            if (timerListItem.started) {
                // resources MUST be called on main activity view.getResources WILL throw an NPE
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
        contents.remove(position);

        // Don't forget to update the list and views!
        updateData();
    }

    /**
     * Replaces an item in the list with a new one. Called by the {@link EditTimerActivity}
     * 
     * @param position
     *            position of the item to be replaced
     * @param newTimerListItem
     *            the new item
     */
    public void replaceItem(final int position, final TimerListItem newTimerListItem) {
        contents.remove(position);
        contents.add(newTimerListItem);

        // Don't forget to update the list and views!
        updateData();
    }

    /**
     * Returns the list of timer items
     * 
     * @return the list of timer items
     */
    public ArrayList<TimerListItem> getContents() {
        return contents;
    }

    private static ArrayList<TimerListItem> newList() {
        final ArrayList<TimerListItem> timerListItems = new ArrayList<>();
        timerListItems.add(new TimerListItem(30, 5, VibrationType.THREE_TONE_HIGH.name()));
        return timerListItems;
    }

    /**
     * This method will ensure that the add-item menu thing is there and that the list contents are refreshed. Call it
     * any time a list operation is done.
     */
    private void updateData() {
        // make sure the add button is still there
        ensureAddItem();

        // This thing is a superclass method, used to let whatever/whoever is controlling all of this stuff know that
        // some contents have changed and all views need to be regenerated.
        notifyDataSetChanged();
    }

    /**
     * This removes the add-item menu option and re-adds it to make sure that it is at the bottom of the list
     */
    private void ensureAddItem() {
        contents.remove(TimerListItem.ADD_ITEM);
        contents.add(TimerListItem.ADD_ITEM);
    }
}
