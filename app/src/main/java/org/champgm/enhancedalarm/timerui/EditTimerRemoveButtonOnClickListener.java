package org.champgm.enhancedalarm.timerui;

import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * On-click listener for the Remove button in {@link org.champgm.enhancedalarm.timerui.EditTimerActivity} it really just
 * calls a method in the activity
 */
public class EditTimerRemoveButtonOnClickListener implements Button.OnClickListener {
    private final EditTimerActivity parent;

    /**
     * Make sure this has the right parent.
     * 
     * @param parent
     *            the Activity that this thing should let know to finish
     */
    public EditTimerRemoveButtonOnClickListener(final EditTimerActivity parent) {
        this.parent = parent;
    }

    /**
     * Will let the parent know that the user is done editing and wants to remove this item.
     * 
     * @param view
     *            unused
     */
    @Override
    public void onClick(final View view) {
        Log.i("RemoveButton", "Remove clicked");
        parent.remove();
    }
}
