package org.champgm.enhancedalarm.timerui;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.champgm.enhancedalarm.R;
import org.champgm.enhancedalarm.band.VibrationReceiver;
import org.champgm.enhancedalarm.util.TimestampHelper;

import com.microsoft.band.notification.VibrationType;

/**
 * The activity spawned when a timer needs to be edited or created.
 */
public class EditTimerActivity extends ActionBarActivity {

    /**
     * The key used to signify that a timer needs to be edited
     */
    public static final int EDIT_REQUEST = 43242;
    /**
     * The key used to signify that a timer has been successfully edited
     */
    public static final int EDIT_RESULT_SUCCESS = 8616;
    /**
     * the key used to signify that a timer has been successfully deleted
     */
    public static final int DELETE_RESULT_SUCCESS = 8617;

    private static final String testVibrationString = "TEST-VIBRATION";
    private static final ArrayList<String> vibrationTypes;
    private TextView delayText;
    private TextView intervalText;
    private int originalPosition;

    static {
        vibrationTypes = new ArrayList<>(9);
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

    /**
     * auto-generated, not modified
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_timer, menu);
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

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode == TimerInputActivity.EDIT_TIMESTAMP_SUCCESS) {
            final String editedTimestamp = data.getStringExtra(TimerInputActivity.PUT_EXTRA_TIMESTAMP);
            final int timerType = data.getIntExtra(TimerInputActivity.PUT_EXTRA_REQUEST, 0);

            if (timerType == TimerInputActivity.EDIT_INTERVAL_REQUEST) {
                intervalText.setText(editedTimestamp);
            } else if (timerType == TimerInputActivity.EDIT_DELAY_REQUEST) {
                delayText.setText(editedTimestamp);
            }
        }
    }

    /**
     * Triggered when the "Done" button is clicked
     */
    protected void doneEditing() {
        final Integer delayInt = TimestampHelper.timeStampToSeconds(delayText.getText().toString());
        final Integer intervalInt = TimestampHelper.timeStampToSeconds(intervalText.getText().toString());

        final Spinner vibrationSpinner = (Spinner) findViewById(R.id.vibrationPicker);
        final String vibrationType = String.valueOf(vibrationSpinner.getSelectedItem());

        // Build a new timer and place it into the intent, along with the position of the timer it is meant to
        // replace
        final TimerListItem resultTimer = new TimerListItem(intervalInt, delayInt, vibrationType);
        final Intent resultIntent = new Intent();
        resultIntent.putExtra(TimerListItem.PUT_EXTRA_ITEM_KEY, resultTimer);
        resultIntent.putExtra(TimerListItem.PUT_EXTRA_POSITION_KEY, originalPosition);
        setResult(EDIT_RESULT_SUCCESS, resultIntent);
        finish();
    }

    /**
     * Triggered when the "Remove" button is clicked
     */
    protected void remove() {
        // Build a new result intent and note the position of the item to be deleted
        final Intent resultIntent = new Intent();
        resultIntent.putExtra(TimerListItem.PUT_EXTRA_POSITION_KEY, originalPosition);
        setResult(DELETE_RESULT_SUCCESS, resultIntent);
        finish();
    }

    /**
     * Triggered when the "Test" button is clicked
     */
    protected void testVibration() {
        // Grab the current spinner value
        final Spinner vibrationSpinner = (Spinner) findViewById(R.id.vibrationPicker);
        final String vibrationType = String.valueOf(vibrationSpinner.getSelectedItem());

        // Build a pending intent for the alarm manager
        final Intent intent = new Intent(this, VibrationReceiver.class);
        intent.putExtra(VibrationReceiver.TIMER_UUID_KEY, testVibrationString);
        intent.putExtra(VibrationReceiver.VIBRATION_TYPE_KEY, vibrationType);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, vibrationType.hashCode(), intent, 0);

        // Set the alarm manager with a trigger time in the past to trigger the service immediately.
        final AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() - 1, pendingIntent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_timer);

        // Find out if we are adding a new timer, or editing an existing one
        final Intent intent = getIntent();
        final boolean addNew = intent.getBooleanExtra(TimerAdapter.PUT_EXTRA_ADD_ITEM, true);

        final TimerListItem itemToEdit;
        if (addNew) {
            // Instantiate a new one
            itemToEdit = new TimerListItem(0, 0, VibrationType.NOTIFICATION_ALARM.name());
        } else {
            // Grab the one to be edited from the intent
            itemToEdit = intent.getParcelableExtra(TimerListItem.PUT_EXTRA_ITEM_KEY);
        }

        // Find out where it is in the existing List
        originalPosition = intent.getIntExtra(TimerListItem.PUT_EXTRA_POSITION_KEY, 999);
        if (originalPosition == 999) {
            // Hopefully this won't happen
            Toast.makeText(this, "Unknown original position", Toast.LENGTH_LONG).show();
        } else {
            // Fill out all of the relevant fields from the TimerListItem
            intervalText = (Button) findViewById(R.id.intervalTimestamp);
            delayText = (Button) findViewById(R.id.delayTimestamp);

            intervalText.setText(TimestampHelper.secondsToTimestamp(itemToEdit.interval));
            intervalText.setOnClickListener(new TimestampClickListener(TimerInputActivity.EDIT_INTERVAL_REQUEST));

            delayText.setText(TimestampHelper.secondsToTimestamp(itemToEdit.delay));
            delayText.setOnClickListener(new TimestampClickListener(TimerInputActivity.EDIT_DELAY_REQUEST));

            final Spinner vibrationSpinner = (Spinner) findViewById(R.id.vibrationPicker);
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, vibrationTypes);
            vibrationSpinner.setAdapter(adapter);
            vibrationSpinner.setSelection(vibrationTypes.indexOf(itemToEdit.vibrationTypeName));

            // This will trigger the testVibration() method below
            final Button vibrationTestButton = (Button) findViewById(R.id.vibration_test);
            vibrationTestButton.setOnClickListener(new EditTimerVibrationTestButtonOnClickListener());

            // This will trigger the doneEditing() method below
            final Button doneButton = (Button) findViewById(R.id.done_button);
            doneButton.setOnClickListener(new EditTimerDoneButtonOnClickListener());

            // This will trigger the remove() method below
            final Button removeButton = (Button) findViewById(R.id.remove_button);
            removeButton.setOnClickListener(new EditTimerRemoveButtonOnClickListener());
        }
    }

    public class TimestampClickListener implements Button.OnClickListener {
        private final int timestampToEdit;

        public TimestampClickListener(final int timestampToEdit) {
            this.timestampToEdit = timestampToEdit;
        }

        /**
         * Will let the parent know that the user is done editing and wants to remove this item.
         *
         * @param view
         *            unused
         */
        @Override
        public void onClick(final View view) {
            final Intent intent = new Intent(EditTimerActivity.this, TimerInputActivity.class);
            if (timestampToEdit == TimerInputActivity.EDIT_INTERVAL_REQUEST) {
                intent.putExtra(TimerInputActivity.PUT_EXTRA_TIMESTAMP, intervalText.getText());
                intent.putExtra(TimerInputActivity.PUT_EXTRA_REQUEST, TimerInputActivity.EDIT_INTERVAL_REQUEST);
            } else {
                intent.putExtra(TimerInputActivity.PUT_EXTRA_TIMESTAMP, delayText.getText());
                intent.putExtra(TimerInputActivity.PUT_EXTRA_REQUEST, TimerInputActivity.EDIT_DELAY_REQUEST);
            }
            startActivityForResult(intent, timestampToEdit);
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
            EditTimerActivity.this.remove();
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
            EditTimerActivity.this.doneEditing();
        }
    }

    public class EditTimerVibrationTestButtonOnClickListener implements Button.OnClickListener {
        /**
         * Call back to the parent to let it know that the user is done editing
         *
         * @param view
         *            unused
         */
        @Override
        public void onClick(final View view) {
            EditTimerActivity.this.testVibration();
        }
    }
}
