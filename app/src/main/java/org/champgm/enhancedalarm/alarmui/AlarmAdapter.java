package org.champgm.enhancedalarm.alarmui;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.champgm.enhancedalarm.R;
import org.champgm.enhancedalarm.util.Checks;
import org.champgm.enhancedalarm.util.Days;
import org.champgm.enhancedalarm.util.Toaster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AlarmAdapter extends BaseAdapter {
    /**
     * They key used to store a flag representing if a new item needs to be added
     */
    public static final String PUT_EXTRA_ADD_ITEM = "481001b8-4992-408e-acfd-637415627725";
    private static final ArrayList<AlarmListItem> contents = new ArrayList<AlarmListItem>();
    public static AlarmAdapter instance;
    private final LayoutInflater layoutInflater;
    private final Fragment alarmsFragment;

    public AlarmAdapter(final Fragment alarmsFragment) {
        this(alarmsFragment, null);
    }

    public AlarmAdapter(final Fragment alarmsFragment, final Collection<AlarmListItem> contents) {
        if (Checks.isNull(alarmsFragment)) {
            throw new RuntimeException("The activity that starts a AlarmAdapter cannot be null. I do not see any other way around this.");
        }
        this.alarmsFragment = alarmsFragment;
        this.layoutInflater = (LayoutInflater) alarmsFragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (contents != null && !contents.isEmpty()) {
            AlarmAdapter.contents.clear();
            AlarmAdapter.contents.addAll(contents);
        }
        updateData();
        instance = this;
    }

    /**
     * Calls fromString() on each item and stores them in a set
     *
     * @return a set of {@link org.champgm.enhancedalarm.alarmui.AlarmListItem}s created from Strings
     */
    public static Set<AlarmListItem> contentsFromString(final Set<String> stringRepresentations) {
        final HashSet<AlarmListItem> alarmListItems = new HashSet<AlarmListItem>(stringRepresentations.size());
        for (final String stringRepresentation : stringRepresentations) {
            alarmListItems.add(AlarmListItem.fromString(stringRepresentation));
        }
        return alarmListItems;
    }

    public void flash(final String uuid) {
        final UUID targetUUID = UUID.fromString(uuid);
        for (final AlarmListItem content : contents) {
            if (content.uuid == targetUUID) {
                content.firing = true;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return contents.size();
    }

    @Override
    public AlarmListItem getItem(final int position) {
        if (Checks.isNotNull(contents) && position <= contents.size() - 1 && position >= 0) {
            return contents.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        if (Checks.isNotNull(parent) &&
                Checks.isNotNull(alarmsFragment) &&
                Checks.isNotNull(layoutInflater) &&
                position >= 0 &&
                position <= contents.size() - 1) {
            // Grab the list item corresponding to the view that should be created
            final AlarmListItem alarmListItem = contents.get(position);
            // If this is the special add button, create that, set the button listener, and return it
            if (alarmListItem.uuid == AlarmListItem.ADD_ITEM_UUID) {
//                Log.i("ALARM_ADAPTER", "UUID is ADD_ITEM UUID.");
                final View addView = layoutInflater.inflate(R.layout.list_add_item, parent, false);
                final Button addButton = (Button) addView.findViewById(R.id.add_button);
                addButton.setOnClickListener(new AlarmListItemEditButtonOnClickListener(position, this, alarmsFragment, true));
                return addView;
            } else {
//                Log.i("ALARM_ADAPTER", "UUID: " + alarmListItem.uuid);
                // Create a new view
                final View alarmView = layoutInflater.inflate(R.layout.alarm_list_item, parent, false);

                // Grab references to all text fields
                final TextView labelText = (TextView) alarmView.findViewById(R.id.alarmLabel);
                final TextView alarmTime = (TextView) alarmView.findViewById(R.id.alarmTime);
                final TextView amPm = (TextView) alarmView.findViewById(R.id.ampm);
                final TextView daysList = (TextView) alarmView.findViewById(R.id.daysList);

                // Fill the text fields in
                labelText.setText(alarmListItem.label);
                alarmTime.setText(alarmListItem.time);
                amPm.setText(alarmListItem.period.stringId);

                if (alarmListItem.days.size() > 0) {
                    final StringBuilder days = new StringBuilder();
                    for (final Days day : Days.orderedDays) {
                        if (alarmListItem.days.contains(day)) {
                            final String abbreviationString = alarmsFragment.getResources().getString(day.abbreviation);
                            days.append(abbreviationString).append(", ");
                        }
                    }
                    days.delete(days.length() - 2, days.length() - 1);
                    daysList.setText(days.toString());
                } else {
                    daysList.setText(R.string.never);
                }

                // Set the listener for the edit button
                final Button editButton = (Button) alarmView.findViewById(R.id.edit_button);
                editButton.setOnClickListener(new AlarmListItemEditButtonOnClickListener(position, this, alarmsFragment, false));

                // Set the right color
                if (alarmListItem.enabled) {
                    // resources MUST be called on main activity view.getResources WILL throw an NPE
                    alarmView.setBackgroundColor(alarmsFragment.getResources().getColor(R.color.activated_green));
                } else {
                    alarmView.setBackgroundColor(alarmsFragment.getResources().getColor(R.color.invisible));
                }

                if (alarmListItem.firing) {
                    final Integer colorFrom = alarmsFragment.getResources().getColor(R.color.activated_green);
                    final Integer colorTo = alarmsFragment.getResources().getColor(R.color.firing_red);
                    final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(final ValueAnimator animator) {
                            alarmView.setBackgroundColor((Integer) animator.getAnimatedValue());
                        }
                    });
                    colorAnimation.start();
                }

                return alarmView;
            }
        } else {
            Toaster.send(convertView.getContext(), "Could not retrieve view for list item in position: " + position);
        }
        return convertView;
    }

    /**
     * Returns the list of {@link org.champgm.enhancedalarm.alarmui.AlarmListItem}s
     *
     * @return the list of alarm items
     */
    public ArrayList<AlarmListItem> getContents() {
        return contents;
    }

    /**
     * Manually sets the contents of this adapter
     *
     * @param newContents the new items which will replace all old items
     */
    public void setContents(final Collection<AlarmListItem> newContents) {
        contents.clear();
        contents.addAll(newContents);
        // Don't forget to update the list and views!
        updateData();
    }

    /**
     * Calls toString() on each {@link org.champgm.enhancedalarm.alarmui.AlarmListItem} and stores them in a set
     *
     * @return the set of contents, stored as Strings
     */
    public Set<String> contentsToString() {
        final HashSet<String> strings = new HashSet<String>(contents.size());
        for (final AlarmListItem alarmListItem : contents) {
            strings.add(alarmListItem.toString());
        }
        return strings;
    }

    /**
     * This method will ensure that the add-item menu thing is there and that the list contents are refreshed. Call it
     * any time a list operation is done.
     */
    private void updateData() {
        // make sure the add button is still there
        if (Checks.isNotNull(contents)) {
            contents.remove(AlarmListItem.ADD_ITEM);
            contents.add(AlarmListItem.ADD_ITEM);
        }

        // This thing is a superclass method, used to let whatever/whoever is controlling all of this stuff know that
        // some contents have changed and all views need to be regenerated.
        notifyDataSetChanged();
    }

    /**
     * Replaces an item in the list with a new one. Called by the {@link EditAlarmActivity}
     *
     * @param position         position of the item to be replaced
     * @param newAlarmListItem the new item
     */
    public void replaceItem(final int position, final AlarmListItem newAlarmListItem) {
        if (Checks.isNotNull(contents) && position <= contents.size() - 1 && position >= 0) {
            contents.remove(position);
            contents.add(newAlarmListItem);
        } else {
            Log.d("AlarmAdapter", "Could not replace item. Contents null or position out of bounds.");
        }
        // Don't forget to update the list and views!
        updateData();
    }

    /**
     * Remove an item from the alarm list
     *
     * @param position location of the item to remove
     */
    public void removeItem(final int position) {
        if (Checks.isNotNull(contents) && position <= contents.size() - 1 && position >= 0) {
            contents.remove(position);
        } else {
            Log.d("AlarmAdapter", "Could not  item. Contents null or position out of bounds.");
        }

        // Don't forget to update the list and views!
        updateData();
    }

}
