package org.champgm.enhancedalarm;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.common.base.Preconditions;
import com.microsoft.band.BandDeviceInfo;

import org.champgm.enhancedalarm.band.BandHelper;

import java.util.ArrayList;

/**
 * This {@link android.app.Activity} pops up when you click the "Settings" option from the context menu
 */
public class SettingsActivity extends Activity {
    /**
     * The place where app-wide preferences will be stored
     */
    public static final String PREF_FILE_NAME = "2ad87316-a1e0-4898-82e9-7339317ac71b";
    /**
     * The key where the currently selected band will be stored in the app-wide preferences
     */
    public static final String SELECTED_BAND = "9b1b4574-4822-4a75-ab85-c5f0b7740601";

    private ArrayList<String> spinnerContents = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Show a toast if no bands are connected. This will explain why the spinner is empty
        if (!BandHelper.anyBandsConnected()) {
            Toast.makeText(this, R.string.no_bands_found, Toast.LENGTH_LONG).show();
        }

        // Get the list of connected bands and fill the spinner's contents
        spinnerContents = new ArrayList<>(2);
        final BandDeviceInfo[] connectedBands = BandHelper.getBands();
        for (final BandDeviceInfo connectedBand : connectedBands) {
            spinnerContents.add(connectedBand.getName() + " : " + connectedBand.getMacAddress());
        }

        // Assign the spinner adapter to the spinner layout thing
        final Spinner bandSpinner = (Spinner) findViewById(R.id.bandPicker);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerContents);
        bandSpinner.setAdapter(adapter);

        // Button listeners
        final Button doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new SettingsDoneButtonOnClickListener(bandSpinner));
        final Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new SettingsCancelButtonOnClickListener());

    }

    public class SettingsCancelButtonOnClickListener implements Button.OnClickListener {
        /**
         * Will let the settings activity know that the user is done editing and wants to go back with no changes.
         *
         * @param view
         *            unused
         */
        @Override
        public void onClick(final View view) {
            finish();
        }
    }

    public class SettingsDoneButtonOnClickListener implements Button.OnClickListener {
        private final Spinner bandSpinner;

        /**
         * Creates an instance
         * 
         * @param bandSpinner
         *            the spinner from which to retrieve the selected band
         */
        public SettingsDoneButtonOnClickListener(final Spinner bandSpinner) {
            this.bandSpinner = Preconditions.checkNotNull(bandSpinner, "bandSpinner may not be null.");
        }

        /**
         * Will let the settings activity know that the user is done editing and wants to save changes.
         *
         * @param view
         *            unused
         */
        @Override
        public void onClick(final View view) {
            final int selectedIndex = spinnerContents.indexOf(String.valueOf(bandSpinner.getSelectedItem()));
            final SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferences(SettingsActivity.PREF_FILE_NAME, MODE_PRIVATE).edit();
            sharedPreferencesEditor.putInt(SELECTED_BAND, selectedIndex);
            sharedPreferencesEditor.commit();
            finish();
        }
    }
}
