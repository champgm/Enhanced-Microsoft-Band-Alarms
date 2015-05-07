package org.champgm.enhancedalarm.timerui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.band.notifications.VibrationType;

import org.champgm.enhancedalarm.R;
import org.champgm.enhancedalarm.band.BandHelper;
import org.champgm.enhancedalarm.band.VibrationReceiver;
import org.champgm.enhancedalarm.util.Checks;
import org.champgm.enhancedalarm.util.TimestampHelper;

import java.util.ArrayList;

/**
 * The activity spawned when a timer needs to be edited or created.
 */
public class EditTimerActivity extends Activity {

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
        // Fill the types array with existing types
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

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode == TimerInputActivity.EDIT_TIMESTAMP_SUCCESS) {
            if (data != null) {
                final String editedTimestamp = data.getStringExtra(TimerInputActivity.PUT_EXTRA_TIMESTAMP);

                // Figure out if the user was just editing the delay timer or the interval timer
                final int timerType = data.getIntExtra(TimerInputActivity.PUT_EXTRA_REQUEST, 0);
                if (timerType == TimerInputActivity.EDIT_INTERVAL_REQUEST && intervalText != null) {
                    intervalText.setText(editedTimestamp);
                } else if (timerType == TimerInputActivity.EDIT_DELAY_REQUEST && delayText != null) {
                    delayText.setText(editedTimestamp);
                }
            }
        }
    }

    /**
     * Triggered when the "Done" button is clicked
     */
    protected void doneEditing() {
        Log.d("EDITING: ", String.valueOf(delayText != null));
        Log.d("EDITING: ", String.valueOf(delayText.getText() != null));
        Log.d("EDITING: ", String.valueOf(Checks.notEmpty(delayText.getText().toString())));
        Log.d("EDITING: ", String.valueOf(intervalText != null));
        Log.d("EDITING: ", String.valueOf(intervalText.getText() != null));
        Log.d("EDITING: ", String.valueOf(Checks.notEmpty(intervalText.getText().toString())));
        Log.d("EDITING: ", String.valueOf(TimestampHelper.validateTimestamp(delayText.getText().toString())));
        Log.d("EDITING: ", String.valueOf(TimestampHelper.validateTimestamp(intervalText.getText().toString())));

        // Build a new timer and place it into the intent, along with the position of the timer it is meant to
        // replace
        final TimerListItem resultTimer = getCurrentTimer();
        if (resultTimer != null) {
            final Intent resultIntent = new Intent();
            resultIntent.putExtra(TimerListItem.PUT_EXTRA_ITEM_KEY, resultTimer);
            resultIntent.putExtra(TimerListItem.PUT_EXTRA_POSITION_KEY, originalPosition);
            setResult(EDIT_RESULT_SUCCESS, resultIntent);
        }
        finish();
    }

    private TimerListItem getCurrentTimer() {
        if (delayText != null &&
                delayText.getText() != null &&
                Checks.notEmpty(delayText.getText().toString()) &&
                intervalText != null &&
                intervalText.getText() != null &&
                Checks.notEmpty(intervalText.getText().toString()) &&
                TimestampHelper.validateTimestamp(delayText.getText().toString()) &&
                TimestampHelper.validateTimestamp(intervalText.getText().toString())) {
            final Integer delayInt = TimestampHelper.timeStampToSeconds(delayText.getText().toString());
            final Integer intervalInt = TimestampHelper.timeStampToSeconds(intervalText.getText().toString());

            final Spinner vibrationSpinner = (Spinner) findViewById(R.id.vibrationPicker);
            final String vibrationType = String.valueOf(vibrationSpinner.getSelectedItem());

            // Build a new timer and place it into the intent, along with the position of the timer it is meant to
            // replace
            return new TimerListItem(intervalInt, delayInt, vibrationType);
        }
        return null;
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
        if (!BandHelper.anyBandsConnected()) {
            Toast.makeText(this, R.string.no_bands_found, Toast.LENGTH_LONG).show();
        } else {
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
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_timer);

        // Get original position and timer item from input
        TimerListItem itemToEdit = new TimerListItem(0, 0, VibrationType.NOTIFICATION_ALARM.name());
        if (savedInstanceState != null) {
            // Grab saved timer item and original position
            itemToEdit = savedInstanceState.getParcelable(TimerListItem.PUT_EXTRA_ITEM_KEY);
            originalPosition = savedInstanceState.getInt(TimerListItem.PUT_EXTRA_POSITION_KEY);
        } else if (getIntent() != null) {
            // Find out if we are adding a new timer, or editing an existing one
            final Intent intent = getIntent();
            boolean addNew = intent.getBooleanExtra(TimerAdapter.PUT_EXTRA_ADD_ITEM, true);

            if (!addNew) {
                // Grab the one to be edited from the intent
                itemToEdit = intent.getParcelableExtra(TimerListItem.PUT_EXTRA_ITEM_KEY);
            }

            // Find out where it is in the existing List
            originalPosition = intent.getIntExtra(TimerListItem.PUT_EXTRA_POSITION_KEY, 999);
        }

        // Parse original position and input timer item
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

            // Populate the vibration picker
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

    @Override
    protected final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("TimerInputActivity", "on save instance state called");
        outState.putParcelable(TimerListItem.PUT_EXTRA_ITEM_KEY, getCurrentTimer());
        outState.putInt(TimerListItem.PUT_EXTRA_POSITION_KEY, originalPosition);
    }

    @Override
    public final void onRestoreInstanceState(final Bundle savedInstanceState, final PersistableBundle
            persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        onCreate(savedInstanceState);
    }

    public class TimestampClickListener implements Button.OnClickListener {
        private final int timestampToEdit;

        /**
         * Creates an instance
         * 
         * @param timestampToEdit
         *            the number of seconds the previous timestamp represents, used to fill out the display that the
         *            user will edit
         */
        public TimestampClickListener(final int timestampToEdit) {
            this.timestampToEdit = timestampToEdit;
        }

        /**
         * Will start the {@link org.champgm.enhancedalarm.timerui.TimerInputActivity}
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
         * Will send the selected vibration to the band, one time, as a test
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
