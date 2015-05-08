package org.champgm.enhancedalarm.alarmui;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.champgm.enhancedalarm.R;
import org.champgm.enhancedalarm.util.AlarmTimeHelper;
import org.champgm.enhancedalarm.util.Checks;
import org.champgm.enhancedalarm.util.Toaster;

public class AlarmInputActivity extends Activity {

    public static final String PUT_EXTRA_ALARM = "5652f4bc-f946-4a8a-adb6-1d8d0ba27448";
    public static final String PUT_EXTRA_REQUEST = "2ce5135a-43ad-412c-9f9b-3ac74d423dd2";
    public static final int EDIT_ALARM_REQUEST = 75472;
    public static final int EDIT_ALARM_SUCCESS = 91142;
    public static final int EDIT_ALARM_CANCEL = 11332;

    private final LinkedList<Integer> digits = new LinkedList<Integer>();
    private final TextView[] alarmTimeDisplay = new TextView[4];
    private int alarmType;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_alarm_input);

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

        // Store references to all of the alarmTime display fields
        alarmTimeDisplay[0] = (TextView) findViewById(R.id.hour_tens);
        alarmTimeDisplay[1] = (TextView) findViewById(R.id.hour_ones);
        alarmTimeDisplay[2] = (TextView) findViewById(R.id.minute_tens);
        alarmTimeDisplay[3] = (TextView) findViewById(R.id.minute_ones);

        if (savedInstanceState != null) {
            restoreStateFromSavedInstance(savedInstanceState);
        } else if (getIntent() != null) {
            final Intent intent = getIntent();
            // Get the type of alarm we will be editing (this isn't really used, just put back into the result later so
            // that the receiving class will know which field to update)
            final int alarmType = intent == null ? 0 : intent.getIntExtra(PUT_EXTRA_REQUEST, 0);

            // Get the alarmTime from the intent that started this activity and display it.
            final String possibleAlarmTime = intent == null ? "" : intent.getStringExtra(PUT_EXTRA_ALARM);

            restoreState(alarmType, possibleAlarmTime);
        }

    }

    /**
     * Get the alarmTime and alarm type, and call restore
     *
     * @param savedInstanceState
     *            the state saved previously
     */
    private void restoreStateFromSavedInstance(final Bundle savedInstanceState) {
        final String possibleAlarmTime = savedInstanceState.getString(PUT_EXTRA_ALARM);
        final int alarmType = savedInstanceState.getInt(PUT_EXTRA_REQUEST);
        restoreState(alarmType, possibleAlarmTime);
    }

    /**
     * Will set the alarmType and
     *
     * @param alarmType
     *            type of alarm being edited
     * @param possibleAlarmTime
     *            alarmTime to attempt to parse and display
     */
    private void restoreState(final int alarmType, final String possibleAlarmTime) {
        this.alarmType = alarmType;

        if (Checks.isEmpty(possibleAlarmTime) || !AlarmTimeHelper.validateTime(possibleAlarmTime)) {
            Toaster.send(this, "Invalid alarmTime: " + possibleAlarmTime);
        }

        final String alarmTime = AlarmTimeHelper.simplifyTime(possibleAlarmTime);
        pushAlarmTime(alarmTime);
    }

    /**
     * Take a whole alarmTime and replace whatever is displayed with its contents
     *
     * @param alarmTime
     *            the alarmTime to fill into the fields
     */
    private void pushAlarmTime(final String alarmTime) {
        if (AlarmTimeHelper.validateTime(alarmTime)) {
            // Clear the old stuff
            digits.clear();

            // Starting at the minutes place, convert each non-colon character into an integer and push it into the list
            for (int i = alarmTime.length() - 1; i > -1; i--) {
                final char character = alarmTime.charAt(i);
                if (character != ':') {
                    digits.push(Character.getNumericValue(character));
                }
            }

            // update the alarmTime display
            updateAlarmTimeDisplay();
        } else {
            Toaster.send(this, "Invalid alarmTime: " + alarmTime);
        }
    }

    /**
     * Push one digit into the digits and display
     *
     * @param value
     *            the value to push
     */
    private void pushOne(final int value) {
        if (Checks.isNotNull(digits) && !digits.isEmpty()) {
            // If the display is not already full...
            if (digits.get(0) == 0) {// && digits.get(1) == 0) {
                // Pop out the leading zero
                digits.pop();
                // Add a new number into the minutes place
                digits.addLast(value);
                // Update the display
                updateAlarmTimeDisplay();
            }
        }
    }

    /**
     * Parse the digits and display them
     */
    private void updateAlarmTimeDisplay() {
        // Starting at the hour's tens place, fill in each available digit
        for (int i = alarmTimeDisplay.length - 1; i > -1; i--) {
            if (Checks.isNotNull(alarmTimeDisplay[i])) {
                alarmTimeDisplay[i].setText(String.valueOf(digits.get(i)));
            }
        }
    }

    @Override
    protected final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PUT_EXTRA_ALARM, AlarmTimeHelper.linkedListToAlarmTime(digits));
        outState.putInt(PUT_EXTRA_REQUEST, alarmType);
    }

    @Override
    public final void onRestoreInstanceState(final Bundle savedInstanceState, final PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        restoreStateFromSavedInstance(savedInstanceState);
    }

    public class BackspaceButtonListener implements Button.OnClickListener {

        @Override
        public void onClick(final View v) {
            if (Checks.isNotNull(digits)) {
                if (!digits.isEmpty()) {
                    // Remove the item in the minutes place
                    digits.removeLast();
                }
                // Push a zero into the hour's tens place
                digits.push(0);
                // Update the display
                updateAlarmTimeDisplay();
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
            setResult(EDIT_ALARM_CANCEL, resultIntent);
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
            if (alarmType > 0) {
                final Intent resultIntent = new Intent();
                resultIntent.putExtra(PUT_EXTRA_REQUEST, alarmType);
                resultIntent.putExtra(PUT_EXTRA_ALARM, AlarmTimeHelper.linkedListToAlarmTime(digits));
                setResult(EDIT_ALARM_SUCCESS, resultIntent);
            }
            finish();
        }
    }
}
