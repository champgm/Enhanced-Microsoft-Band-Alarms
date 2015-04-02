package org.champgm.enhancedalarm.timer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.champgm.enhancedalarm.R;

public class EditTimerActivity extends ActionBarActivity {
    public static final int EDIT_REQUEST = 43242;
    public static final int EDIT_RESULT_SUCCESS = 8616;
    public static final int EDIT_RESULT_FAIL = 6168;


    private EditText repeatText;
    private EditText delayText;
    private EditText intervalText;
    private int originalPosition;
    private boolean addNew;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_timer);

        final Intent intent = getIntent();
        addNew = intent.getBooleanExtra(TimerAdapter.PUT_EXTRA_ADD_ITEM, true);

        final TimerListItem itemToEdit;
        if (addNew) {
            itemToEdit = new TimerListItem(0, 0, 0);
        } else {
            itemToEdit = intent.getParcelableExtra(TimerAdapter.PUT_EXTRA_ITEM_KEY);
        }
        originalPosition = intent.getIntExtra(TimerAdapter.PUT_EXTRA_POSITION_KEY, 999);
        if (originalPosition == 999) {
            Toast.makeText(this, "Unknown original position", Toast.LENGTH_LONG).show();
        } else {
            intervalText = (EditText) findViewById(R.id.intervalText);
            delayText = (EditText) findViewById(R.id.delayText);
            repeatText = (EditText) findViewById(R.id.repeatText);

            intervalText.setText(String.valueOf(itemToEdit.interval));
            delayText.setText(String.valueOf(itemToEdit.delay));
            repeatText.setText(String.valueOf(itemToEdit.repeat));

            final Button doneButton = (Button) findViewById(R.id.done_button);
            doneButton.setOnClickListener(new EditTimerDoneButtonOnClickListener(this));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_timer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void doneEditing() {
        Integer repeatInt = null;
        try {
            repeatInt = Integer.parseInt(repeatText.getText().toString());
            Log.i("doneEditing", "set repeat to: " + repeatInt);
        } catch (NumberFormatException nfe) {
            Toast.makeText(this, "Non numerical value for 'Repeat'", Toast.LENGTH_LONG).show();
        }
        Integer delayInt = null;
        try {
            delayInt = Integer.parseInt(delayText.getText().toString());
            Log.i("doneEditing", "set delayInt to: " + delayInt);
        } catch (NumberFormatException nfe) {
            Toast.makeText(this, "Non numerical value for 'Delay'", Toast.LENGTH_LONG).show();
        }
        Integer intervalInt = null;
        try {
            intervalInt = Integer.parseInt(intervalText.getText().toString());
            Log.i("doneEditing", "set intervalInt to: " + intervalInt);
        } catch (NumberFormatException nfe) {
            Toast.makeText(this, "Non numerical value for 'Interval'", Toast.LENGTH_LONG).show();
        }
        if (repeatInt != null && delayInt != null && intervalInt != null &&
                checkSize(repeatInt, "Repeat") && checkSize(delayInt, "Delay") && checkSize(intervalInt, "Interval")) {
            final TimerListItem resultTimer = new TimerListItem(intervalInt, delayInt, repeatInt);
            final Intent resultIntent = new Intent();
            resultIntent.putExtra(TimerAdapter.PUT_EXTRA_ITEM_KEY, resultTimer);
            resultIntent.putExtra(TimerAdapter.PUT_EXTRA_POSITION_KEY, originalPosition);
            resultIntent.putExtra(TimerAdapter.PUT_EXTRA_ADD_ITEM, addNew);
            setResult(EDIT_RESULT_SUCCESS, resultIntent);
            finish();
        } else {
            Log.i("doneEditing", "got null for some value");
        }
    }

    private boolean checkSize(final int value, final String valueType) {
        if (value > 999 || value < 1) {
            Toast.makeText(this, valueType + "cannot be less than 1 or greater than 999.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
