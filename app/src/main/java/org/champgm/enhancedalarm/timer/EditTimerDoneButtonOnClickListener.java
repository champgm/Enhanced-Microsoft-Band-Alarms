package org.champgm.enhancedalarm.timer;

import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * On-click listener for the Done button in {@link org.champgm.enhancedalarm.timer.EditTimerActivity} it really just
 * calls a method in the activity
 */
public class EditTimerDoneButtonOnClickListener implements Button.OnClickListener {
    private final EditTimerActivity parent;

    /**
     * Make sure this is the right thing parent.
     * 
     * @param parent
     *            the Activity that this thing should let know to finish
     */
    public EditTimerDoneButtonOnClickListener(final EditTimerActivity parent) {
        this.parent = parent;
    }

    @Override
    public void onClick(final View v) {
        Log.i("DoneButton", "Done clicked");
        parent.doneEditing();
    }
}
