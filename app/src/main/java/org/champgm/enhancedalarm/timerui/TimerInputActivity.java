package org.champgm.enhancedalarm.timerui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.champgm.enhancedalarm.R;

public class TimerInputActivity extends ActionBarActivity {
    final int[] timerDigits = new int[] { 0, 0, 0, 0, 0, 0 };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_timer_input);

    }

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
}
