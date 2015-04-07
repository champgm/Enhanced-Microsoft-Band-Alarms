package org.champgm.enhancedalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.microsoft.band.BandException;

import org.champgm.enhancedalarm.band.BandHelper;
import org.champgm.enhancedalarm.band.VibrationReceiver;
import org.champgm.enhancedalarm.timer.EditTimerActivity;
import org.champgm.enhancedalarm.timer.TimerAdapter;
import org.champgm.enhancedalarm.timer.TimerListItem;
import org.champgm.enhancedalarm.timer.TimerListItemOnClickListener;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

/**
 * The main activity class, really just a holder for a {@link org.champgm.enhancedalarm.timer.TimerAdapter}.
 */
public class MainActivity extends ActionBarActivity {

    private static final String SHARED_PREFERENCES_KEY = "24e426c8-3d9f-435f-afab-55a03addaba3";
    /**
     * This is the meat of the app. This adapter manages all of the timers.
     */
    private TimerAdapter timerAdapter;
    private BandHelper bandHelper;

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
     *            {@link org.champgm.enhancedalarm.timer.EditTimerActivity#EDIT_RESULT_SUCCESS} or
     *            {@link org.champgm.enhancedalarm.timer.EditTimerActivity#DELETE_RESULT_SUCCESS}
     * @param data
     *            the data returned by the {@link org.champgm.enhancedalarm.timer.EditTimerActivity}
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

        // Create a new TimerAdapter if needed
        if (timerAdapter == null) {
            timerAdapter = new TimerAdapter(this);
        }

        // Create a new band helper if needed
        try {
            bandHelper = new BandHelper(this);
            bandHelper.addTile(this);
        } catch (BandException e) {
            Log.i("MainActivity", "Trouble connecting to band");
        } catch (InterruptedException e) {
            Log.i("MainActivity", "Connection to band interrupted.");
        } catch (TimeoutException e) {
            Log.i("MainActivity", "Timeout connecting to band.");
        }
        if (bandHelper == null) {
            throw new RuntimeException("Cannot connect to band, cannot proceed");
        }

        // Create a ListView and assign it the TimerAdapter.
        final ListView timerList = (ListView) findViewById(R.id.timerList);
        timerList.setAdapter(timerAdapter);

        // Create a thread scheduler
        final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(5);

        // Also, set the on-click listener
        timerList.setOnItemClickListener(new TimerListItemOnClickListener(timerAdapter, this));

//        Log.i("itemClick", "creating alarm manager");
//        // Attempt to start the runnable that will keep vibrating the band
//        final AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//
//        Log.i("itemClick", "New intents");
//        final Intent intent = new Intent(this, VibrationReceiver.class);
//        intent.setAction("org.champgm.enhancedalarm.band.VibrationReceiver");
//        intent.setPackage("org.champgm.enhancedalarm.band");
//        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 4386, intent, 0);
//
//        Log.i("itemClick", "Set repeating alarm");
//        Log.i("itemClick", String.valueOf(AlarmManager.ELAPSED_REALTIME_WAKEUP));
//        Log.i("itemClick", String.valueOf(System.currentTimeMillis()));
//        // alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//        // // System.currentTimeMillis() +
//        // timerListItem.delay * 1000,
//        // timerListItem.interval * 1000, pendingIntent);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 30 * 1000, pendingIntent);

    }

    // @Override
    // protected void onPause() {
    // super.onPause();
    //
    // SharedPreferences.Editor edit = getSharedPreferences(SHARED_PREFERENCES_KEY, 0).edit();
    // edit.put
    //
    // }
}
