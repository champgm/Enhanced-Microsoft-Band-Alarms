package org.champgm.enhancedalarm;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.champgm.enhancedalarm.timerui.EditTimerActivity;
import org.champgm.enhancedalarm.timerui.TimerAdapter;
import org.champgm.enhancedalarm.timerui.TimerListItem;
import org.champgm.enhancedalarm.timerui.TimerListItemOnClickListener;

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
    public final void onRestoreInstanceState(final Bundle savedInstanceState, final PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);

        // Restore or create a new TimerAdapter if needed
        restoreTimerAdapter(savedInstanceState);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Create this app's band tile, if it doesn't exist
        // NOTE: it seems like this isn't really necessary. Also, if we don't do this, it will save one or two of those
        // crazy leaked resource exceptions.
        // createTile();

        // Restore or create a new TimerAdapter if needed
        restoreTimerAdapter(savedInstanceState);
    }

    @Override
    protected final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TimerAdapter.PUT_EXTRA_ITEM_KEY, timerAdapter.getContents());
    }

    @Override
    protected final void onStop() {
        super.onStop();
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

    /**
     * Attempts to create a tile for this application on the band if it does not exist.
     * NOTE: it seems like this isn't really necessary. Also, if we don't do this, it will save one or two of those
     * crazy leaked resource exceptions.
     */
    // private void createTile() {
    // // Create a new band client
    // final BandClientManager bandClientManager = BandClientManager.getInstance();
    // final BandDeviceInfo[] pairedBands = bandClientManager.getPairedBands();
    // final BandClient bandClient = bandClientManager.create(this, pairedBands[0]);
    //
    // // Add the Tile
    // try {
    // BandHelper.addTile(bandClient, this);
    // } catch (BandException e) {
    // Log.i("MainActivity", "Trouble connecting to band");
    // } catch (InterruptedException e) {
    // Log.i("MainActivity", "Connection to band interrupted.");
    // } catch (TimeoutException e) {
    // Log.i("MainActivity", "Timeout connecting to band.");
    // }
    //
    // BandHelper.disconnect(bandClient);
    // }
}
