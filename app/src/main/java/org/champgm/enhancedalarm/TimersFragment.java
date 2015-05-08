package org.champgm.enhancedalarm;

import java.util.HashSet;
import java.util.Set;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.champgm.enhancedalarm.timerui.EditTimerActivity;
import org.champgm.enhancedalarm.timerui.TimerAdapter;
import org.champgm.enhancedalarm.timerui.TimerListItem;
import org.champgm.enhancedalarm.timerui.TimerListItemOnClickListener;
import org.champgm.enhancedalarm.util.Checks;
import org.champgm.enhancedalarm.util.Toaster;

public class TimersFragment extends Fragment {
    public static final String SAVED_TIMERS_FRAGMENT_DATA_KEY = "1e735b5e-30cf-4c1f-980d-fa80c289c23b";
    private TimerAdapter timerAdapter;

    /**
     * Sort of like a click-listener for all settings menu items.
     * Currently the only one we care about is {@link org.champgm.enhancedalarm.R.id#action_settings}
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

        // Here is where we launch the settings activity if settings is clicked.
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This will be called after EditTimerActivity finishes.
     *
     * @param requestCode
     *            dunno what this is, it isn't used.
     * @param resultCode
     *            the result of the activity, should be
     *            {@link org.champgm.enhancedalarm.timerui.EditTimerActivity#EDIT_RESULT_SUCCESS} or
     *            {@link org.champgm.enhancedalarm.timerui.EditTimerActivity#DELETE_RESULT_SUCCESS}
     * @param data
     *            the data returned by the {@link org.champgm.enhancedalarm.timerui.EditTimerActivity}
     */
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        int resultPosition;

        switch (resultCode) {
            case EditTimerActivity.EDIT_RESULT_SUCCESS:
                if (Checks.isNotNull(data)) {
                    // The result of editing was actually an edited timer
                    // Grab the resultant timer and its position in the TimerAdapter's ArrayList
                    final TimerListItem resultTimer = data.getParcelableExtra(TimerListItem.PUT_EXTRA_ITEM_KEY);
                    resultPosition = data.getIntExtra(TimerListItem.PUT_EXTRA_POSITION_KEY, 999);

                    // Hopefully this never happens...
                    if (resultPosition == 999) {
                        Toaster.send(getActivity(), "Unknown position");
                    } else {
                        // Replace the edited timer with the new one
                        timerAdapter.replaceItem(resultPosition, resultTimer);
                        saveCurrentTimers();
                    }
                }
                break;
            case EditTimerActivity.DELETE_RESULT_SUCCESS:
                if (Checks.isNotNull(data)) {
                    // The result of editing was a removed timer.
                    // Grab the position and remove the item at that position.
                    resultPosition = data.getIntExtra(TimerListItem.PUT_EXTRA_POSITION_KEY, 999);
                    if (resultPosition == 999) {
                        Toaster.send(getActivity(), "Unknown position");
                    } else {
                        timerAdapter.removeItem(resultPosition);
                        saveCurrentTimers();
                    }
                }
                break;
            default:
                Log.d("MAIN", "Unrecognized code: " + requestCode);
                break;
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_timers, container, false);
        restoreTimerAdapter();
        return rootView;
    }

    @Override
    public final void onPause() {
        super.onPause();
        saveCurrentTimers();
    }

    @Override
    public void onResume() {
        super.onResume();
        restoreTimerAdapter();
    }

    /**
     * Saves the current timers into {@link android.content.SharedPreferences}. This is probably not the ideal way to do
     * this, but it nicely centralizes this persisting logic
     */
    private void saveCurrentTimers() {
        final SharedPreferences settings = getActivity().getSharedPreferences(SAVED_TIMERS_FRAGMENT_DATA_KEY, 0);
        final SharedPreferences.Editor editor = settings.edit();

        // Clear the old and add the current
        editor.remove(TimerListItem.PUT_EXTRA_ITEM_KEY);
        editor.putStringSet(TimerListItem.PUT_EXTRA_ITEM_KEY, new HashSet<String>(timerAdapter.contentsToString()));
        editor.apply();
    }

    /**
     * Grab the saved timer list items and set them into the view.
     */
    private void restoreTimerAdapter() {
        // Grab the list items
        final SharedPreferences settings = getActivity().getSharedPreferences(SAVED_TIMERS_FRAGMENT_DATA_KEY, 0);
        final Set<String> stringSet = settings.getStringSet(TimerListItem.PUT_EXTRA_ITEM_KEY, new HashSet<String>());
        final Set<TimerListItem> timerListItems = TimerAdapter.contentsFromString(stringSet);

        // Create the timerAdapter if it doesn't exist, and set the contents
        if (timerAdapter == null) {
            timerAdapter = new TimerAdapter(this);
        }
        timerAdapter.setContents(timerListItems);

        // bind the adapter to the view and set a click listener
        final View view = getView();
        if (view != null) {
            final ListView timerList = (ListView) view.findViewById(R.id.timerList);
            timerList.setAdapter(timerAdapter);
            timerList.setOnItemClickListener(new TimerListItemOnClickListener(timerAdapter));
        }
    }
}
