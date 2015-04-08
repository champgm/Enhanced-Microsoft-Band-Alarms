package org.champgm.enhancedalarm.timerui;

import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * On-click listener for the Done button in {@link org.champgm.enhancedalarm.timerui.EditTimerActivity} it really just
 * calls a method in the activity
 */
public class EditTimerDoneButtonOnClickListener implements Button.OnClickListener {
    private final EditTimerActivity parent;

    /**
     * Creates an instance, will call back to parent when done
     * 
     * @param parent
     *            the Activity that this thing should let know to finish
     */
    public EditTimerDoneButtonOnClickListener(final EditTimerActivity parent) {
        this.parent = parent;
    }

    /**
     * Call back to the parent to let it know that the user is done editing
     * 
     * @param view
     *            unused
     */
    @Override
    public void onClick(final View view) {
        Log.i("DoneButton", "Done clicked");
        parent.doneEditing();
    }
}
