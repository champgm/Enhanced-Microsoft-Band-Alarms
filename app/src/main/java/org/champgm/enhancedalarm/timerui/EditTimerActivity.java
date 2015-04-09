package org.champgm.enhancedalarm.timerui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.champgm.enhancedalarm.R;

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
    // public static final int EDIT_RESULT_FAIL = 6168;

    private EditText repeatText;
    private EditText delayText;
    private EditText intervalText;
    private int originalPosition;

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

    /**
     * Triggered when the "Done" button is clicked
     */
    protected void doneEditing() {

        // Do some preconditions checks first
        Integer repeatInt = null;
        try {
            repeatInt = Integer.parseInt(repeatText.getText().toString());
            Log.d("doneEditing", "set repeat to: " + repeatInt);
        } catch (NumberFormatException nfe) {
            Toast.makeText(this, "Non numerical value for 'Repeat'", Toast.LENGTH_LONG).show();
        }
        Integer delayInt = null;
        try {
            delayInt = Integer.parseInt(delayText.getText().toString());
            Log.d("doneEditing", "set delayInt to: " + delayInt);
        } catch (NumberFormatException nfe) {
            Toast.makeText(this, "Non numerical value for 'Delay'", Toast.LENGTH_LONG).show();
        }
        Integer intervalInt = null;
        try {
            intervalInt = Integer.parseInt(intervalText.getText().toString());
            Log.d("doneEditing", "set intervalInt to: " + intervalInt);
        } catch (NumberFormatException nfe) {
            Toast.makeText(this, "Non numerical value for 'Interval'", Toast.LENGTH_LONG).show();
        }

        // If everything is okay...
        if (repeatInt != null && delayInt != null && intervalInt != null &&
                checkSize(repeatInt, "Repeat") && checkSize(delayInt, "Delay") && checkSize(intervalInt, "Interval")) {

            // Build a new timer and place it into the intent, along with the position of the timer it is meant to
            // replace
            final TimerListItem resultTimer = new TimerListItem(intervalInt, delayInt, repeatInt);
            final Intent resultIntent = new Intent();
            resultIntent.putExtra(TimerAdapter.PUT_EXTRA_ITEM_KEY, resultTimer);
            resultIntent.putExtra(TimerAdapter.PUT_EXTRA_POSITION_KEY, originalPosition);
            setResult(EDIT_RESULT_SUCCESS, resultIntent);
            finish();
        } else {
            Log.e("doneEditing", "got null for some value");
        }
    }

    /**
     * Triggered when the "Remove" button is clicked
     */
    protected void remove() {
        // Build a new result intent and note the position of the item to be deleted
        final Intent resultIntent = new Intent();
        resultIntent.putExtra(TimerAdapter.PUT_EXTRA_POSITION_KEY, originalPosition);
        setResult(DELETE_RESULT_SUCCESS, resultIntent);
        finish();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_timer);

        // Find out if we are adding a new timer, or editing an existing one
        final Intent intent = getIntent();
        final boolean addNew = intent.getBooleanExtra(TimerAdapter.PUT_EXTRA_ADD_ITEM, true);

        final TimerListItem itemToEdit;
        if (addNew) {
            // Instantiate a new one
            itemToEdit = new TimerListItem(-1, -1, -1);
        } else {
            // Grab the one to be edited from the intent
            itemToEdit = intent.getParcelableExtra(TimerAdapter.PUT_EXTRA_ITEM_KEY);
        }

        // Find out where it is in the existing List
        originalPosition = intent.getIntExtra(TimerAdapter.PUT_EXTRA_POSITION_KEY, 999);
        if (originalPosition == 999) {
            // Hopefully this won't happen
            Toast.makeText(this, "Unknown original position", Toast.LENGTH_LONG).show();
        } else {
            // Fill out all of the relevant fields from the TimerListItem
            intervalText = (EditText) findViewById(R.id.intervalText);
            delayText = (EditText) findViewById(R.id.delayText);
            repeatText = (EditText) findViewById(R.id.repeatText);

            if (itemToEdit.interval >= 0) {
                intervalText.setText(String.valueOf(itemToEdit.interval));
            } else {
                intervalText.setText("");
            }

            if (itemToEdit.delay >= 0) {
                delayText.setText(String.valueOf(itemToEdit.delay));
            } else {
                delayText.setText("");
            }

            if (itemToEdit.repeat >= 0) {
                repeatText.setText(String.valueOf(itemToEdit.repeat));
            } else {
                repeatText.setText("");
            }

            // This will trigger the doneEditing() method below
            final Button doneButton = (Button) findViewById(R.id.done_button);
            doneButton.setOnClickListener(new EditTimerDoneButtonOnClickListener());

            // This will trigger the remove() method below
            final Button removeButton = (Button) findViewById(R.id.remove_button);
            removeButton.setOnClickListener(new EditTimerRemoveButtonOnClickListener());
        }
    }

    /**
     * These ranges are sort of arbitrary, they're mostly dictated by the size of the view space. 3 digits looks pretty
     * good on my phone, but this should probably be changed in the future.
     * 
     * @param value
     *            the numerical value
     * @param valueType
     *            the type of value
     * @return valid or not
     */
    private boolean checkSize(final int value, final String valueType) {
        if (value > 999 || value < 1) {
            Toast.makeText(this, valueType + "cannot be less than 1 or greater than 999.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
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
            Log.d("RemoveButton", "Remove clicked");
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
            Log.d("DoneButton", "Done clicked");
            EditTimerActivity.this.doneEditing();
        }
    }
}
