package org.champgm.enhancedalarm.alarmui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.champgm.enhancedalarm.R;
import org.champgm.enhancedalarm.common.EditActivity;
import org.champgm.enhancedalarm.util.AlarmTimeHelper;
import org.champgm.enhancedalarm.util.Checks;
import org.champgm.enhancedalarm.util.Days;
import org.champgm.enhancedalarm.util.Period;
import org.champgm.enhancedalarm.util.Toaster;

import com.microsoft.band.notifications.VibrationType;

public class EditAlarmActivity extends EditActivity {
    /**
     * The key used to signify that a timer needs to be edited
     */
    public static final int EDIT_REQUEST = 432422;
    /**
     * The key used to signify that a timer has been successfully edited
     */
    public static final int EDIT_RESULT_SUCCESS = 86162;
    /**
     * the key used to signify that a timer has been successfully deleted
     */
    public static final int DELETE_RESULT_SUCCESS = 86172;

    private static final HashMap<Integer, Boolean> dayBooleanMap;
    private static final HashMap<Integer, Button> dayButtonMap;
    private static HashMap<Integer, Button> periodButtonMap;
    private Period period;
    private TextView alarmText;
    private TextView alarmLabelText;
    private AlarmListItem itemToEdit;

    static {
        dayBooleanMap = new HashMap<Integer, Boolean>(7);
        dayBooleanMap.put(R.id.saturdayButton, false);
        dayBooleanMap.put(R.id.sundayButton, false);
        dayBooleanMap.put(R.id.mondayButton, false);
        dayBooleanMap.put(R.id.tuesdayButton, false);
        dayBooleanMap.put(R.id.wednesdayButton, false);
        dayBooleanMap.put(R.id.thursdayButton, false);
        dayBooleanMap.put(R.id.fridayButton, false);
        dayButtonMap = new HashMap<Integer, Button>(7);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode == AlarmInputActivity.EDIT_ALARM_SUCCESS) {
            if (data != null) {
                final String editedTimestamp = data.getStringExtra(AlarmInputActivity.PUT_EXTRA_ALARM);
                alarmText.setText(editedTimestamp);
            }
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_alarm);
        commonSetup();

        // Get original position and timer item from input
        itemToEdit = new AlarmListItem(VibrationType.NOTIFICATION_ALARM.name(), "", new ArrayList<Days>(), "00:00", Period.TWENTY_FOUR_HOUR, false);
        if (savedInstanceState != null) {
            // Grab saved timer item and original position
            itemToEdit = savedInstanceState.getParcelable(AlarmListItem.PUT_EXTRA_ITEM_KEY);
            originalPosition = savedInstanceState.getInt(AlarmListItem.PUT_EXTRA_POSITION_KEY);
        } else if (getIntent() != null) {
            // Find out if we are adding a new timer, or editing an existing one
            final Intent intent = getIntent();
            boolean addNew = intent.getBooleanExtra(AlarmAdapter.PUT_EXTRA_ADD_ITEM, true);

            if (!addNew) {
                // Grab the one to be edited from the intent
                itemToEdit = intent.getParcelableExtra(AlarmListItem.PUT_EXTRA_ITEM_KEY);
            }

            // Find out where it is in the existing List
            originalPosition = intent.getIntExtra(AlarmListItem.PUT_EXTRA_POSITION_KEY, 999);
        }

        // Parse original position and input timer item
        if (originalPosition == 999) {
            // Hopefully this won't happen
            Toast.makeText(this, "Unknown original position", Toast.LENGTH_LONG).show();
        } else {
            // Fill out all of the relevant fields from the AlarmListItem
            alarmLabelText = (TextView) findViewById(R.id.alarmLabel);
            alarmLabelText.setText(itemToEdit.label);

            alarmText = (Button) findViewById(R.id.alarmTime);
            alarmText.setText(AlarmTimeHelper.simplifyTime(itemToEdit.time));
            alarmText.setOnClickListener(new AlarmTimeClickListener());

            periodButtonMap = new HashMap<Integer, Button>(3);
            periodButtonMap.put(R.id.amButton, (Button) findViewById(R.id.amButton));
            periodButtonMap.put(R.id.pmButton, (Button) findViewById(R.id.pmButton));
            periodButtonMap.put(R.id.twentyFourHourButton, (Button) findViewById(R.id.twentyFourHourButton));
            for (final Map.Entry<Integer, Button> idAndButton : periodButtonMap.entrySet()) {
                idAndButton.getValue().setOnClickListener(new PeriodClickListener(idAndButton.getKey()));
            }
            period = itemToEdit.period;
            refreshPeriodButtons();

            dayButtonMap.put(R.id.saturdayButton, (Button) findViewById(R.id.saturdayButton));
            dayButtonMap.put(R.id.sundayButton, (Button) findViewById(R.id.sundayButton));
            dayButtonMap.put(R.id.mondayButton, (Button) findViewById(R.id.mondayButton));
            dayButtonMap.put(R.id.tuesdayButton, (Button) findViewById(R.id.tuesdayButton));
            dayButtonMap.put(R.id.wednesdayButton, (Button) findViewById(R.id.wednesdayButton));
            dayButtonMap.put(R.id.thursdayButton, (Button) findViewById(R.id.thursdayButton));
            dayButtonMap.put(R.id.fridayButton, (Button) findViewById(R.id.fridayButton));
            for (final Map.Entry<Integer, Button> idAndButton : dayButtonMap.entrySet()) {
                idAndButton.getValue().setOnClickListener(new DayClickListener(idAndButton.getKey()));
            }

            dayBooleanMap.clear();
            for (final Days day : itemToEdit.days) {
                dayBooleanMap.put(day.buttonId, true);
            }
            refreshDayButtons();

            // Select the correct the vibration picker item
            final Spinner vibrationSpinner = (Spinner) findViewById(R.id.vibrationPicker);
            vibrationSpinner.setSelection(vibrationTypes.indexOf(itemToEdit.vibrationTypeName));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up buttonId, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected final void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(AlarmListItem.PUT_EXTRA_ITEM_KEY, getCurrentAlarm());
        outState.putInt(AlarmListItem.PUT_EXTRA_POSITION_KEY, originalPosition);
    }

    @Override
    public final void onRestoreInstanceState(final Bundle savedInstanceState, final PersistableBundle
            persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        onCreate(savedInstanceState);
    }

    @Override
    protected void doneEditing() {
        // Build a new timer and place it into the intent, along with the position of the timer it is meant to
        // replace
        final AlarmListItem resultAlarm = getCurrentAlarm();

        if (resultAlarm != null) {
            if (AlarmTimeHelper.makesSense(resultAlarm.time, resultAlarm.period)) {
                final Intent resultIntent = new Intent();
                resultIntent.putExtra(AlarmListItem.PUT_EXTRA_ITEM_KEY, resultAlarm);
                resultIntent.putExtra(AlarmListItem.PUT_EXTRA_POSITION_KEY, originalPosition);
                setResult(EDIT_RESULT_SUCCESS, resultIntent);
                finish();
            } else {
                Toaster.send(this, resultAlarm.time + " " + getString(resultAlarm.period.stringId) + " does not make sense.");
            }
        }
    }

    @Override
    protected void remove() {
        // Build a new result intent and note the position of the item to be deleted
        final Intent resultIntent = new Intent();
        resultIntent.putExtra(AlarmListItem.PUT_EXTRA_POSITION_KEY, originalPosition);
        setResult(DELETE_RESULT_SUCCESS, resultIntent);
        finish();
    }

    private AlarmListItem getCurrentAlarm() {
        if (alarmText != null &&
                alarmText.getText() != null &&
                Checks.notEmpty(alarmText.getText().toString()) &&
                AlarmTimeHelper.validateTime(alarmText.getText().toString())) {
            final Spinner vibrationSpinner = (Spinner) findViewById(R.id.vibrationPicker);

            return new AlarmListItem(
                    itemToEdit.uuid,
                    String.valueOf(vibrationSpinner.getSelectedItem()),
                    String.valueOf(alarmLabelText.getText()),
                    getSelectedDays(),
                    String.valueOf(alarmText.getText()),
                    period,
                    false);
        }
        return null;
    }

    private void refreshDayButtons() {
        for (final Map.Entry<Integer, Button> idButton : dayButtonMap.entrySet()) {
            final Boolean enabled = dayBooleanMap.get(idButton.getKey());
            if (enabled != null && enabled) {
                dayButtonMap.get(idButton.getKey()).getBackground().setColorFilter(new LightingColorFilter(getResources().getColor(R.color.activated_green), 0x00000000));
            } else {
                dayButtonMap.get(idButton.getKey()).getBackground().clearColorFilter();
            }
        }
    }

    private void toggleButton(final int buttonId) {
        final Boolean selected = dayBooleanMap.get(buttonId);
        if (selected != null) {
            dayBooleanMap.put(buttonId, !dayBooleanMap.get(buttonId));
        } else {
            dayBooleanMap.put(buttonId, true);
        }
        refreshDayButtons();
    }

    private Collection<Days> getSelectedDays() {
        final ArrayList<Days> result = new ArrayList<Days>(7);
        for (final Map.Entry<Integer, Boolean> idEnabled : dayBooleanMap.entrySet()) {
            if (idEnabled.getValue()) {
                result.add(Days.fromId(idEnabled.getKey()));
            }
        }
        return result;
    }

    public class DayClickListener implements Button.OnClickListener {
        private final int buttonId;

        public DayClickListener(final int buttonId) {
            this.buttonId = buttonId;
        }

        @Override
        public void onClick(final View view) {
            toggleButton(buttonId);
        }

    }

    public class PeriodClickListener implements Button.OnClickListener {
        private final int buttonId;

        public PeriodClickListener(final int buttonId) {
            this.buttonId = buttonId;
        }

        @Override
        public void onClick(final View view) {
            period = Period.fromId(buttonId);
            refreshPeriodButtons();
        }
    }

    private void refreshPeriodButtons() {
        for (final Map.Entry<Integer, Button> idAndButton : periodButtonMap.entrySet()) {
            if (idAndButton.getKey() == period.buttonId) {
                idAndButton.getValue().getBackground().setColorFilter(new LightingColorFilter(getResources().getColor(R.color.activated_green), 0x00000000));
            } else {
                idAndButton.getValue().getBackground().clearColorFilter();
            }
        }
    }

    public class AlarmTimeClickListener implements Button.OnClickListener {
        /**
         * Will start the {@link org.champgm.enhancedalarm.alarmui.AlarmInputActivity}
         *
         * @param view
         *            unused
         */
        @Override
        public void onClick(final View view) {
            final Intent intent = new Intent(EditAlarmActivity.this, AlarmInputActivity.class);
            intent.putExtra(AlarmInputActivity.PUT_EXTRA_ALARM, String.valueOf(alarmText.getText()));
            intent.putExtra(AlarmInputActivity.PUT_EXTRA_REQUEST, AlarmInputActivity.EDIT_ALARM_REQUEST);
            startActivityForResult(intent, AlarmInputActivity.EDIT_ALARM_REQUEST);
        }
    }
}
