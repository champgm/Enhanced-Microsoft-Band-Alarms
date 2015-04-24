package org.champgm.enhancedalarm;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.champgm.enhancedalarm.timerui.EditTimerActivity;
import org.champgm.enhancedalarm.timerui.TimerAdapter;
import org.champgm.enhancedalarm.timerui.TimerListItem;
import org.champgm.enhancedalarm.timerui.TimerListItemOnClickListener;
import org.champgm.enhancedalarm.util.Checks;
import org.champgm.enhancedalarm.util.Toaster;

import java.util.ArrayList;

/**
 * The main activity class, really just a holder for a {@link org.champgm.enhancedalarm.timerui.TimerAdapter}.
 */
public class MainActivity extends ActionBarActivity {
    private TimerAdapter timerAdapter;

    /**
     * auto-generated, not modified
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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
            startActivity(new Intent(this, SettingsActivity.class));
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
                if (Checks.notNull(data)) {
                    // The result of editing was actually an edited timer
                    // Grab the resultant timer and its position in the TimerAdapter's ArrayList
                    final TimerListItem resultTimer = data.getParcelableExtra(TimerListItem.PUT_EXTRA_ITEM_KEY);
                    resultPosition = data.getIntExtra(TimerListItem.PUT_EXTRA_POSITION_KEY, 999);

                    // Hopefully this never happens...
                    if (resultPosition == 999) {
                        Toaster.send(this, "Unknown position");
                    } else {
                        // Replace the edited timer with the new one
                        timerAdapter.replaceItem(resultPosition, resultTimer);
                    }
                }
                break;
            case EditTimerActivity.DELETE_RESULT_SUCCESS:
                if (Checks.notNull(data)) {
                    // The result of editing was a removed timer.
                    // Grab the position and remove the item at that position.
                    resultPosition = data.getIntExtra(TimerListItem.PUT_EXTRA_POSITION_KEY, 999);
                    if (resultPosition == 999) {
                        Toaster.send(this, "Unknown position");
                    } else {
                        timerAdapter.removeItem(resultPosition);
                    }
                }
                break;
            default:
                Log.d("MAIN", "Unrecognized code: " + requestCode);
                break;
        }
    }

    @Override
    public final void onRestoreInstanceState(final Bundle savedInstanceState, final PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);

        // Restore or create a new TimerAdapter if needed
        restoreTimerAdapter(savedInstanceState);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Restore or create a new TimerAdapter if needed
        restoreTimerAdapter(savedInstanceState);
    }

    @Override
    protected final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TimerListItem.PUT_EXTRA_ITEM_KEY, timerAdapter.getContents());
    }

    @Override
    protected final void onStop() {
        super.onStop();
    }

    private void restoreTimerAdapter(final Bundle savedInstanceState) {
        // Create a timer adapter with the restored contents, if necessary.
        if (timerAdapter == null) {
            if (savedInstanceState == null || !savedInstanceState.containsKey(TimerListItem.PUT_EXTRA_ITEM_KEY)) {
                timerAdapter = new TimerAdapter(this);
            } else {
                final ArrayList<TimerListItem> listItems = savedInstanceState.getParcelableArrayList(TimerListItem.PUT_EXTRA_ITEM_KEY);
                timerAdapter = new TimerAdapter(this, listItems);
            }
        }

        // Create a ListView and assign it the TimerAdapter.
        final ListView timerList = (ListView) findViewById(R.id.timerList);
        timerList.setAdapter(timerAdapter);

        // Also, set the on-click listener
        timerList.setOnItemClickListener(new TimerListItemOnClickListener(timerAdapter));
    }

}
