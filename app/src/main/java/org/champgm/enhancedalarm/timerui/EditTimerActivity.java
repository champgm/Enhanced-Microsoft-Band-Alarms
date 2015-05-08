package org.champgm.enhancedalarm.timerui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.champgm.enhancedalarm.R;
import org.champgm.enhancedalarm.common.EditActivity;
import org.champgm.enhancedalarm.util.Checks;
import org.champgm.enhancedalarm.util.TimestampHelper;

import com.microsoft.band.notifications.VibrationType;

/**
 * The activity spawned when a timer needs to be edited or created.
 */
public class EditTimerActivity extends EditActivity {

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

    private TextView delayText;
    private TextView intervalText;

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
    @Override
    protected void doneEditing() {
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

            return new TimerListItem(intervalInt, delayInt, vibrationType);
        }
        return null;
    }

    /**
     * Triggered when the "Remove" button is clicked
     */
    @Override
    protected void remove() {
        // Build a new result intent and note the position of the item to be deleted
        final Intent resultIntent = new Intent();
        resultIntent.putExtra(TimerListItem.PUT_EXTRA_POSITION_KEY, originalPosition);
        setResult(DELETE_RESULT_SUCCESS, resultIntent);
        finish();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_timer);
        commonSetup();

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

            // Select the correct the vibration picker item
            final Spinner vibrationSpinner = (Spinner) findViewById(R.id.vibrationPicker);
            vibrationSpinner.setSelection(vibrationTypes.indexOf(itemToEdit.vibrationTypeName));
        }
    }

    @Override
    protected final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
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
        private final int requestCode;

        /**
         * Creates an instance
         * 
         * @param requestCode
         *            should be one of {@link TimerInputActivity#EDIT_INTERVAL_REQUEST} or
         *            {@link TimerInputActivity#EDIT_DELAY_REQUEST}
         */
        public TimestampClickListener(final int requestCode) {
            this.requestCode = requestCode;
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
            if (requestCode == TimerInputActivity.EDIT_INTERVAL_REQUEST) {
                intent.putExtra(TimerInputActivity.PUT_EXTRA_TIMESTAMP, intervalText.getText());
                intent.putExtra(TimerInputActivity.PUT_EXTRA_REQUEST, TimerInputActivity.EDIT_INTERVAL_REQUEST);
            } else {
                intent.putExtra(TimerInputActivity.PUT_EXTRA_TIMESTAMP, delayText.getText());
                intent.putExtra(TimerInputActivity.PUT_EXTRA_REQUEST, TimerInputActivity.EDIT_DELAY_REQUEST);
            }
            startActivityForResult(intent, requestCode);
        }
    }
}
