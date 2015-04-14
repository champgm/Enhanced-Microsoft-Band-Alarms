package org.champgm.enhancedalarm.timerui;

import java.util.LinkedList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.champgm.enhancedalarm.R;
import org.champgm.enhancedalarm.util.TimestampHelper;

public class TimerInputActivity extends ActionBarActivity {

    public static final String PUT_EXTRA_TIMESTAMP = "2c6fe89e-f1bd-4493-b10b-1406ffb639d5";
    public static final String PUT_EXTRA_REQUEST = "d06ec78a-3fff-4d49-a111-8eedb5902aa7";
    public static final int EDIT_DELAY_REQUEST = 4802;
    public static final int EDIT_INTERVAL_REQUEST = 7547;
    public static final int EDIT_TIMESTAMP_SUCCESS = 9114;
    public static final int EDIT_TIMESTAMP_CANCEL = 1133;

    private final LinkedList<Integer> digits = new LinkedList<>();
    private final TextView[] timestampDisplay = new TextView[7];
    private int timerType;

    /**
     * Generated, untouched
     *
     * @param menu
     *            ??
     * @return ??
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timer_input, menu);
        return true;
    }

    /**
     * Generated, untouched
     *
     * @param item
     *            ??
     * @return ??
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
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_timer_input);

        // Get the type of timer we will be editing (this isn't really used, just put back into the result later so that
        // the receiving class will know which field to update)
        timerType = getIntent().getIntExtra(PUT_EXTRA_REQUEST, 0);

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

        // Get the timestamp from the intent that started this activity and display it.
        final String timestamp = TimestampHelper.simplifyTimeStamp(getIntent().getStringExtra(PUT_EXTRA_TIMESTAMP));
        pushTimestamp(timestamp);
    }

    /**
     * Take a whole timestamp and replace whatever is displayed with its contents
     * 
     * @param timestamp
     *            the timestamp to fill into the fields
     */
    private void pushTimestamp(final String timestamp) {
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
    }

    /**
     * Push one digit into the digits and display
     * 
     * @param value
     *            the value to push
     */
    private void pushOne(final int value) {
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

    /**
     * Parse the digits and display them
     */
    private void updateTimestampDisplay() {
        // If there is a value in the hour's hundreds place, set the display
        if (digits.get(0) != 0) {
            timestampDisplay[0].setText(String.valueOf(digits.get(0)));
        } else {
            // Otherwise, blank it out so it does not display
            timestampDisplay[0].setText("");
        }

        // Starting at the hour's tens place, fill in each available digit
        for (int i = timestampDisplay.length - 1; i > 0; i--) {
            timestampDisplay[i].setText(String.valueOf(digits.get(i)));
        }
    }

    public class BackspaceButtonListener implements Button.OnClickListener {

        @Override
        public void onClick(final View v) {
            // Remove the item in the seconds place
            digits.removeLast();
            // Push a zero into the hour's hundreds place
            digits.push(0);
            // Update the display
            updateTimestampDisplay();
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
            final Intent resultIntent = new Intent();
            resultIntent.putExtra(PUT_EXTRA_REQUEST, timerType);
            resultIntent.putExtra(PUT_EXTRA_TIMESTAMP, TimestampHelper.linkedListToTimestamp(digits));
            setResult(EDIT_TIMESTAMP_SUCCESS, resultIntent);
            finish();
        }
    }
}