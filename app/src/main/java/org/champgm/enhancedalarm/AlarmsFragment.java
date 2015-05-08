package org.champgm.enhancedalarm;

import java.util.HashSet;
import java.util.Set;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.champgm.enhancedalarm.alarmui.AlarmAdapter;
import org.champgm.enhancedalarm.alarmui.AlarmListItem;
import org.champgm.enhancedalarm.alarmui.AlarmListItemOnClickListener;
import org.champgm.enhancedalarm.alarmui.EditAlarmActivity;
import org.champgm.enhancedalarm.util.Checks;
import org.champgm.enhancedalarm.util.Toaster;

public class AlarmsFragment extends Fragment {
    public static final String SAVED_ALARMS_FRAGMENT_DATA_KEY = "31175951-97cb-4dd1-b570-492cf7690883";
    private AlarmAdapter alarmAdapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_alarms, container, false);
        restoreAlarmAdapter();
        return rootView;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        int resultPosition;

        switch (resultCode) {
            case EditAlarmActivity.EDIT_RESULT_SUCCESS:
                if (Checks.isNotNull(data)) {
                    // The result of editing was actually an edited alarm
                    // Grab the resultant alarm and its position in the AlarmAdapter's ArrayList
                    final AlarmListItem resultAlarm = data.getParcelableExtra(AlarmListItem.PUT_EXTRA_ITEM_KEY);
                    resultPosition = data.getIntExtra(AlarmListItem.PUT_EXTRA_POSITION_KEY, 999);

                    // Hopefully this never happens...
                    if (resultPosition == 999) {
                        Toaster.send(getActivity(), "Unknown position");
                    } else {
                        // Replace the edited alarm with the new one
                        alarmAdapter.replaceItem(resultPosition, resultAlarm);
                        saveCurrentAlarms();
                    }
                }
                break;
            case EditAlarmActivity.DELETE_RESULT_SUCCESS:
                if (Checks.isNotNull(data)) {
                    // The result of editing was a removed alarm.
                    // Grab the position and remove the item at that position.
                    resultPosition = data.getIntExtra(AlarmListItem.PUT_EXTRA_POSITION_KEY, 999);
                    if (resultPosition == 999) {
                        Toaster.send(getActivity(), "Unknown position");
                    } else {
                        alarmAdapter.removeItem(resultPosition);
                        saveCurrentAlarms();
                    }
                }
                break;
            default:
                Log.d("MAIN", "Unrecognized code: " + requestCode);
                break;
        }
    }
    
    
    @Override
    public final void onPause() {
        super.onPause();
        saveCurrentAlarms();
    }

    @Override
    public void onResume() {
        super.onResume();
        restoreAlarmAdapter();
    }

    /**
     * Saves the current alarms into {@link android.content.SharedPreferences}. This is probably not the ideal way to do
     * this, but it nicely centralizes this persisting logic
     */
    private void saveCurrentAlarms() {
        final SharedPreferences settings = getActivity().getSharedPreferences(SAVED_ALARMS_FRAGMENT_DATA_KEY, 0);
        final SharedPreferences.Editor editor = settings.edit();

        // Clear the old and add the current
        editor.remove(AlarmListItem.PUT_EXTRA_ITEM_KEY);
        editor.putStringSet(AlarmListItem.PUT_EXTRA_ITEM_KEY, new HashSet<String>(alarmAdapter.contentsToString()));
        editor.apply();
    }

    /**
     * Grab the saved alarm list items and set them into the view.
     */
    private void restoreAlarmAdapter() {
        // Grab the list items
        final SharedPreferences settings = getActivity().getSharedPreferences(SAVED_ALARMS_FRAGMENT_DATA_KEY, 0);
        final Set<String> stringSet = settings.getStringSet(AlarmListItem.PUT_EXTRA_ITEM_KEY, new HashSet<String>());
        final Set<AlarmListItem> alarmListItems = AlarmAdapter.contentsFromString(stringSet);

        // Create the alarmAdapter if it doesn't exist, and set the contents
        if (alarmAdapter == null) {
            alarmAdapter = new AlarmAdapter(this);
        }
        alarmAdapter.setContents(alarmListItems);

        // bind the adapter to the view and set a click listener
        final View view = getView();
        if (view != null) {
            final ListView alarmList = (ListView) view.findViewById(R.id.alarmList);
            alarmList.setAdapter(alarmAdapter);
            alarmList.setOnItemClickListener(new AlarmListItemOnClickListener(alarmAdapter));
        }
    }

}
