package org.champgm.enhancedalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.base.Preconditions;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandDeviceInfo;
import com.microsoft.band.BandException;

import org.champgm.enhancedalarm.band.BandHelper;
import org.champgm.enhancedalarm.band.BandService;
import org.champgm.enhancedalarm.band.VibrationReceiver;
import org.champgm.enhancedalarm.timerui.EditTimerActivity;
import org.champgm.enhancedalarm.timerui.TimerAdapter;
import org.champgm.enhancedalarm.timerui.TimerListItem;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/**
 * The main activity class, really just a holder for a {@link org.champgm.enhancedalarm.timerui.TimerAdapter}.
 */
public class MainActivity extends ActionBarActivity {

    private static final String SHARED_PREFERENCES_KEY = "24e426c8-3d9f-435f-afab-55a03addaba3";
    /**
     * This is the meat of the app. This adapter manages all of the timers.
     */
    private TimerAdapter timerAdapter;

    // private static final String ="3035d9fe-2135-42e7-a027-b507d1f6c369";

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
     * auto-generated, not modified
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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

        // The result of editing was actually an edited timer
        if (EditTimerActivity.EDIT_RESULT_SUCCESS == resultCode) {
            // Grab the resultant timer and its position in the TimerAdapter's ArrayList
            final TimerListItem resultTimer = data.getParcelableExtra(TimerAdapter.PUT_EXTRA_ITEM_KEY);
            final int resultPosition = data.getIntExtra(TimerAdapter.PUT_EXTRA_POSITION_KEY, 999);

            // Hopefully this never happens...
            if (resultPosition == 999) {
                Toast.makeText(this, "Unknown position", Toast.LENGTH_LONG).show();
            } else {
                // Replace the edited timer with the new one
                timerAdapter.replaceItem(resultPosition, resultTimer);
            }
        } else if (EditTimerActivity.DELETE_RESULT_SUCCESS == resultCode) {
            // The result of editing was a removed timer.
            // Grab the position and remove the item at that position.
            final int resultPosition = data.getIntExtra(TimerAdapter.PUT_EXTRA_POSITION_KEY, 999);
            timerAdapter.removeItem(resultPosition);
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create this app's band tile, if it doesn't exist
        createTile();

        // Restore or create a new TimerAdapter if needed
        restoreTimerAdapter(savedInstanceState);
    }

    private void restoreTimerAdapter(final Bundle savedInstanceState) {
        // Create a timer adapter with the restored contents, if necessary.
        if (timerAdapter == null) {
            if (savedInstanceState == null || !savedInstanceState.containsKey(TimerAdapter.PUT_EXTRA_ITEM_KEY)) {
                timerAdapter = new TimerAdapter(this);
            } else {
                final ArrayList<TimerListItem> listItems = savedInstanceState.getParcelableArrayList(TimerAdapter.PUT_EXTRA_ITEM_KEY);
                timerAdapter = new TimerAdapter(this, listItems);
            }
        }

        // Create a ListView and assign it the TimerAdapter.
        final ListView timerList = (ListView) findViewById(R.id.timerList);
        timerList.setAdapter(timerAdapter);

        // Also, set the on-click listener
        timerList.setOnItemClickListener(new TimerListItemOnClickListener(timerAdapter));
    }

    @Override
    protected final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TimerAdapter.PUT_EXTRA_ITEM_KEY, timerAdapter.getContents());
    }

    @Override
    public final void onRestoreInstanceState(final Bundle savedInstanceState, final PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);

        // Restore or create a new TimerAdapter if needed
        restoreTimerAdapter(savedInstanceState);
    }

    @Override
    protected final void onStop() {
        super.onStop();
    }

    // @Override
    // protected void onPause() {
    // super.onPause();
    //
    // SharedPreferences.Editor edit = getSharedPreferences(SHARED_PREFERENCES_KEY, 0).edit();
    // edit.put
    //
    // }

    /**
     * Attempts to create a tile for this application on the band if it does not exist.
     */
    private void createTile() {
        // Create a new band client
        final BandDeviceInfo[] pairedBands = BandClientManager.getInstance().getPairedBands();
        final BandClient bandClient = BandClientManager.getInstance().create(this, pairedBands[0]);

        try {
            BandHelper.addTile(bandClient, this);
        } catch (BandException e) {
            Log.i("MainActivity", "Trouble connecting to band");
        } catch (InterruptedException e) {
            Log.i("MainActivity", "Connection to band interrupted.");
        } catch (TimeoutException e) {
            Log.i("MainActivity", "Timeout connecting to band.");
        }

        BandHelper.disconnect(bandClient);
    }

    /**
     * The on-click listener for each item that is inside of each timer's view
     */
    public class TimerListItemOnClickListener implements AdapterView.OnItemClickListener {
        private final TimerAdapter timerAdapter;

        // private PendingIntent pendingIntent = null;

        /**
         * Creates an instance
         *
         * @param timerAdapter
         *            that timer adapter you've been hearing so much about
         */
        public TimerListItemOnClickListener(final TimerAdapter timerAdapter) {
            this.timerAdapter = Preconditions.checkNotNull(timerAdapter, "timerAdapter may not be null.");
        }

        /**
         * Stuff that happens when you click the list items
         *
         * @param parent
         *            Maybe this is the TimerAdapter? Maybe that can be removed from the constructor
         * @param view
         *            the view to set green/red
         * @param position
         *            the position of the item that was clicked
         * @param id
         *            i don't think this is needed
         */
        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            Log.d("itemClick", "Item clicked");

            // Get the item from the adapter
            final TimerListItem timerListItem = timerAdapter.getItem(position);

            // Ignore the add button item thing, that doesn't need an on-item-click listener.
            if (timerListItem.uuid != TimerListItem.ADD_ITEM_UUID) {
                Log.d("itemClick", "New intents");
                final Intent intent = new Intent(view.getContext(), VibrationReceiver.class);
                intent.putExtra(VibrationReceiver.TIMER_UUID_KEY, timerListItem.uuid.toString());
                final PendingIntent pendingIntent = PendingIntent.getBroadcast(view.getContext(), timerListItem.uuid.hashCode(), intent, 0);

                Log.d("itemClick", "Not add-item");
                if (timerListItem.started) {
                    Log.d("itemClick", "Canceling timer for uuid: " + timerListItem.uuid.toString());
                    final AlarmManager alarmManager = (AlarmManager) view.getContext().getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    pendingIntent.cancel();

                    final Intent bandServiceIntent = new Intent(MainActivity.this, BandService.class);
                    MainActivity.this.stopService(bandServiceIntent);

                    Log.d("itemClick", "Clearing green background");
                    // Set the background color to clear
                    view.setBackgroundColor(view.getContext().getResources().getColor(R.color.invisible));
                    // Toggle the timer status

                    Log.d("itemClick", "Started - false");
                    timerListItem.started = false;
                    // Notify the list adapter that something has changed

                    Log.d("itemClick", "Data has changed");
                    timerAdapter.notifyDataSetChanged();
                } else {
                    Log.d("itemClick", "timer not started");
                    Log.d("itemClick", "creating alarm manager");
                    // Attempt to start the runnable that will keep vibrating the band
                    final AlarmManager alarmManager = (AlarmManager) view.getContext().getSystemService(Context.ALARM_SERVICE);

                    Log.d("itemClick", "Set repeating alarm");
                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, timerListItem.delay * 1000, timerListItem.interval * 1000, pendingIntent);

                    // Set the background green
                    view.setBackgroundColor(view.getContext().getResources().getColor(R.color.activated_green));
                    timerListItem.started = true;
                }
            }
        }
    }
}
