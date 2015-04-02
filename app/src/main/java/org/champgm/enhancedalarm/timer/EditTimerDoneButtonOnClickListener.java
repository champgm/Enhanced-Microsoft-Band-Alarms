package org.champgm.enhancedalarm.timer;

import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by mc023219 on 4/3/15.
 */
public class EditTimerDoneButtonOnClickListener implements Button.OnClickListener {
    private final EditTimerActivity parent;

    public EditTimerDoneButtonOnClickListener(final EditTimerActivity parent) {
        this.parent = parent;
    }

    @Override
    public void onClick(final View v) {
        Log.i("DoneButton", "Done clicked");
        parent.doneEditing();
    }
}
