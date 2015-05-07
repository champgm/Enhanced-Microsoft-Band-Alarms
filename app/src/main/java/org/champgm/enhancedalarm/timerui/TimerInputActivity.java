package org.champgm.enhancedalarm.timerui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.champgm.enhancedalarm.R;
import org.champgm.enhancedalarm.util.Checks;
import org.champgm.enhancedalarm.util.TimestampHelper;
import org.champgm.enhancedalarm.util.Toaster;

import java.util.LinkedList;

public class TimerInputActivity extends Activity {

    public static final String PUT_EXTRA_TIMESTAMP = "2c6fe89e-f1bd-4493-b10b-1406ffb639d5";
    public static final String PUT_EXTRA_REQUEST = "d06ec78a-3fff-4d49-a111-8eedb5902aa7";
    public static final int EDIT_DELAY_REQUEST = 4802;
    public static final int EDIT_INTERVAL_REQUEST = 7547;
    public static final int EDIT_TIMESTAMP_SUCCESS = 9114;
    public static final int EDIT_TIMESTAMP_CANCEL = 1133;

    private final LinkedList<Integer> digits = new LinkedList<>();
    private final TextView[] timestampDisplay = new TextView[7];
    private int timerType;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("TimerInputActivity", "on create called");
        setContentView(R.layout.edit_timer_input);

        // Set the control button listeners
        findViewById(R.id.backspace).setOnClickListener(new BackspaceButtonListener());
        findViewById(R.id.control_done).setOnClickListener(new DoneButtonListener());
        findViewById(R.id.control_cancel).setOnClickListener(new CancelButtonListener());

        // Set the numerical button listeners
        findViewById(R.id.keypad_zero).setOnClickListener(new IntegerButtonListener(0));
        findViewById(R.id.keypad_one).setOnClickListener(new IntegerButtonListener(1));
        findViewById(R.id.keypad_two).setOnClickListener(new IntegerButtonListener(2));
        findViewById(R.id.keypad_three).setOnClickListener(new IntegerButtonListener(3));
        findViewById(R.id.keypad_four).setOnClickListener(new IntegerButtonListener(4));
        findViewById(R.id.keypad_five).setOnClickListener(new IntegerButtonListener(5));
        findViewById(R.id.keypad_six).setOnClickListener(new IntegerButtonListener(6));
        findViewById(R.id.keypad_seven).setOnClickListener(new IntegerButtonListener(7));
        findViewById(R.id.keypad_eight).setOnClickListener(new IntegerButtonListener(8));
        findViewById(R.id.keypad_nine).setOnClickListener(new IntegerButtonListener(9));

        // Store references to all of the timestamp display fields
        timestampDisplay[0] = (TextView) findViewById(R.id.hour_hundreds);
        timestampDisplay[1] = (TextView) findViewById(R.id.hour_tens);
        timestampDisplay[2] = (TextView) findViewById(R.id.hour_ones);
        timestampDisplay[3] = (TextView) findViewById(R.id.minute_tens);
        timestampDisplay[4] = (TextView) findViewById(R.id.minute_ones);
        timestampDisplay[5] = (TextView) findViewById(R.id.second_tens);
        timestampDisplay[6] = (TextView) findViewById(R.id.second_ones);

        if (savedInstanceState != null) {
            restoreStateFromSavedInstance(savedInstanceState);
        } else if (getIntent() != null) {
            final Intent intent = getIntent();
            // Get the type of timer we will be editing (this isn't really used, just put back into the result later so
            // that the receiving class will know which field to update)
            final int timerType = intent == null ? 0 : intent.getIntExtra(PUT_EXTRA_REQUEST, 0);

            // Get the timestamp from the intent that started this activity and display it.
            final String possibleTimestamp = intent == null ? "" : intent.getStringExtra(PUT_EXTRA_TIMESTAMP);
            restoreState(timerType, possibleTimestamp);
        }

    }

    /**
     * Get the timestamp and timer type, and call restore
     * 
     * @param savedInstanceState
     *            the state saved previously
     */
    private void restoreStateFromSavedInstance(final Bundle savedInstanceState) {
        final String possibleTimestamp = savedInstanceState.getString(PUT_EXTRA_TIMESTAMP);
        final int timerType = savedInstanceState.getInt(PUT_EXTRA_REQUEST);
        restoreState(timerType, possibleTimestamp);
    }

    /**
     * Will set the timerType and
     * 
     * @param timerType
     *            type of timer being edited
     * @param possibleTimestamp
     *            timestamp to attempt to parse and display
     */
    private void restoreState(final int timerType, final String possibleTimestamp) {
        Log.i("TimerInputActivity", "restore state called");
        this.timerType = timerType;

        if (Checks.isEmpty(possibleTimestamp) || !TimestampHelper.validateTimestamp(possibleTimestamp)) {
            Toaster.send(this, "Invalid timestamp");
        }

        final String timestamp = TimestampHelper.simplifyTimeStamp(possibleTimestamp);
        pushTimestamp(timestamp);
    }

    /**
     * Take a whole timestamp and replace whatever is displayed with its contents
     * 
     * @param timestamp
     *            the timestamp to fill into the fields
     */
    private void pushTimestamp(final String timestamp) {
        if (TimestampHelper.validateTimestamp(timestamp)) {
            // Clear the old stuff
            digits.clear();

            // Starting at the seconds place, convert each non-colon character into an integer and push it into the list
            for (int i = timestamp.length() - 1; i > -1; i--) {
                final char character = timestamp.charAt(i);
                if (character != ':') {
                    digits.push(Character.getNumericValue(character));
                }
            }

            // If the string doesn't have an hour's hundredth place, stick an extra zero in there
            if (timestamp.length() != 9) {
                digits.push(0);
            }

            // update the timestamp display
            updateTimestampDisplay();
        } else {
            Toaster.send(this, "Invalid timestamp");
        }
    }

    /**
     * Push one digit into the digits and display
     * 
     * @param value
     *            the value to push
     */
    private void pushOne(final int value) {
        if (Checks.notNull(digits) && !digits.isEmpty()) {
            // If the display is not already full...
            if (digits.get(0) == 0 && digits.get(1) == 0) {
                // Pop out the leading zero
                digits.pop();
                // Add a new number into the seconds place
                digits.addLast(value);
                // Update the display
                updateTimestampDisplay();
            }
        }
    }

    /**
     * Parse the digits and display them
     */
    private void updateTimestampDisplay() {
        if (Checks.notNull(timestampDisplay[0]) &&
                Checks.notNull(digits) &&
                !digits.isEmpty()) {
            // If there is a value in the hour's hundreds place, set the display
            if (digits.get(0) != 0) {
                timestampDisplay[0].setText(String.valueOf(digits.get(0)));
            } else {
                // Otherwise, blank it out so it does not display
                timestampDisplay[0].setText("");
            }
        }

        // Starting at the hour's tens place, fill in each available digit
        for (int i = timestampDisplay.length - 1; i > 0; i--) {
            if (Checks.notNull(timestampDisplay[i])) {
                timestampDisplay[i].setText(String.valueOf(digits.get(i)));
            }
        }
    }

    @Override
    protected final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("TimerInputActivity", "on save instance state called");
        outState.putString(PUT_EXTRA_TIMESTAMP, TimestampHelper.linkedListToTimestamp(digits));
        outState.putInt(PUT_EXTRA_REQUEST, timerType);
    }

    @Override
    public final void onRestoreInstanceState(final Bundle savedInstanceState, final PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        restoreStateFromSavedInstance(savedInstanceState);
    }

    public class BackspaceButtonListener implements Button.OnClickListener {

        @Override
        public void onClick(final View v) {
            if (Checks.notNull(digits)) {
                if (!digits.isEmpty()) {
                    // Remove the item in the seconds place
                    digits.removeLast();
                }
                // Push a zero into the hour's hundreds place
                digits.push(0);
                // Update the display
                updateTimestampDisplay();
            }
        }
    }

    public class IntegerButtonListener implements Button.OnClickListener {

        private final int value;

        /**
         * Creates an instance
         *
         * @param value
         *            the value the button should put on the stack
         */
        public IntegerButtonListener(final int value) {
            this.value = value;
        }

        /**
         * Push the numerical value onto the digits stack
         *
         * @param view
         *            unused
         */
        @Override
        public void onClick(final View view) {
            pushOne(this.value);
        }
    }

    public class CancelButtonListener implements Button.OnClickListener {

        /**
         * Push the numerical value onto the digits stack
         *
         * @param view
         *            unused
         */
        @Override
        public void onClick(final View view) {
            final Intent resultIntent = new Intent();
            setResult(EDIT_TIMESTAMP_CANCEL, resultIntent);
            finish();
        }
    }

    public class DoneButtonListener implements Button.OnClickListener {

        /**
         * Push the numerical value onto the digits stack
         *
         * @param view
         *            unused
         */
        @Override
        public void onClick(final View view) {
            if (timerType > 0) {
                final Intent resultIntent = new Intent();
                resultIntent.putExtra(PUT_EXTRA_REQUEST, timerType);
                resultIntent.putExtra(PUT_EXTRA_TIMESTAMP, TimestampHelper.linkedListToTimestamp(digits));
                setResult(EDIT_TIMESTAMP_SUCCESS, resultIntent);
            }
            finish();
        }
    }
}
