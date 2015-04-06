package org.champgm.enhancedalarm.timer;

import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * On-click listener for the Remove button in {@link org.champgm.enhancedalarm.timer.EditTimerActivity} it really just
 * calls a method in the activity
 */
public class EditTimerRemoveButtonOnClickListener implements Button.OnClickListener {
    private final EditTimerActivity parent;

    /**
     * Make sure this is the right thing parent.
     * 
     * @param parent
     *            the Activity that this thing should let know to finish
     */
    public EditTimerRemoveButtonOnClickListener(final EditTimerActivity parent) {
        this.parent = parent;
    }

    @Override
    public void onClick(final View v) {
        Log.i("RemoveButton", "Remove clicked");
        parent.remove();
    }
}
