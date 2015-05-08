package org.champgm.enhancedalarm.common;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.champgm.enhancedalarm.R;
import org.champgm.enhancedalarm.band.BandHelper;
import org.champgm.enhancedalarm.band.VibrationReceiver;

import com.microsoft.band.notifications.VibrationType;

public abstract class EditActivity extends Activity {

    protected static final String testVibrationString = "TEST-VIBRATION";
    protected static final ArrayList<String> vibrationTypes;
    protected int originalPosition;

    static {
        // Fill the types array with existing types
        vibrationTypes = new ArrayList<String>(9);
        vibrationTypes.add(VibrationType.NOTIFICATION_ONE_TONE.name());
        vibrationTypes.add(VibrationType.NOTIFICATION_TWO_TONE.name());
        vibrationTypes.add(VibrationType.NOTIFICATION_ALARM.name());
        vibrationTypes.add(VibrationType.NOTIFICATION_TIMER.name());
        vibrationTypes.add(VibrationType.ONE_TONE_HIGH.name());
        vibrationTypes.add(VibrationType.TWO_TONE_HIGH.name());
        vibrationTypes.add(VibrationType.THREE_TONE_HIGH.name());
        vibrationTypes.add(VibrationType.RAMP_UP.name());
        vibrationTypes.add(VibrationType.RAMP_DOWN.name());
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
    }

    protected void commonSetup() {
        // Populate the vibration picker
        final Spinner vibrationSpinner = (Spinner) findViewById(R.id.vibrationPicker);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, vibrationTypes);
        vibrationSpinner.setAdapter(adapter);

        // This will trigger the testVibration() method below
        final Button vibrationTestButton = (Button) findViewById(R.id.vibration_test);
        vibrationTestButton.setOnClickListener(new VibrationTestButtonOnClickListener());

        // This will trigger the doneEditing() method below
        final Button doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new EditTimerDoneButtonOnClickListener());

        // This will trigger the remove() method below
        final Button removeButton = (Button) findViewById(R.id.remove_button);
        removeButton.setOnClickListener(new EditTimerRemoveButtonOnClickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

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
     * Triggered when the "Test" button is clicked
     */
    protected void testVibration() {
        if (!BandHelper.anyBandsConnected()) {
            Toast.makeText(this, R.string.no_bands_found, Toast.LENGTH_LONG).show();
        } else {
            // Grab the current spinner value
            final Spinner vibrationSpinner = (Spinner) findViewById(R.id.vibrationPicker);
            final String vibrationType = String.valueOf(vibrationSpinner.getSelectedItem());

            // Build a pending intent for the alarm manager
            final Intent intent = new Intent(this, VibrationReceiver.class);
            intent.putExtra(VibrationReceiver.UUID_KEY, testVibrationString);
            intent.putExtra(VibrationReceiver.VIBRATION_TYPE_KEY, vibrationType);
            final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, vibrationType.hashCode(), intent, 0);

            // Set the alarm manager with a trigger time in the past to trigger the service immediately.
            final AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() - 1, pendingIntent);
        }
    }

    public class VibrationTestButtonOnClickListener implements Button.OnClickListener {
        /**
         * Will send the selected vibration to the band, one time, as a test
         *
         * @param view
         *            unused
         */
        @Override
        public void onClick(final View view) {
            testVibration();
        }
    }

    public class EditTimerRemoveButtonOnClickListener implements Button.OnClickListener {
        /**
         * Will let the parent know that the user is done editing and wants to remove this item.
         *
         * @param view
         *            unused
         */
        @Override
        public void onClick(final View view) {
            remove();
        }
    }

    public class EditTimerDoneButtonOnClickListener implements Button.OnClickListener {
        /**
         * Call back to the parent to let it know that the user is done editing
         *
         * @param view
         *            unused
         */
        @Override
        public void onClick(final View view) {
            doneEditing();
        }
    }

    protected abstract void doneEditing();

    protected abstract void remove();
}
