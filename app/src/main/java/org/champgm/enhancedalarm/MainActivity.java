package org.champgm.enhancedalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.champgm.enhancedalarm.timer.EditTimerActivity;
import org.champgm.enhancedalarm.timer.TimerAdapter;
import org.champgm.enhancedalarm.timer.TimerListItem;
import org.champgm.enhancedalarm.timer.TimerListItemOnClickListener;

public class MainActivity extends ActionBarActivity {
    private TimerAdapter timerAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (timerAdapter == null) {
            timerAdapter = new TimerAdapter(this);
        }

        final ListView timerList = (ListView) findViewById(R.id.timerList);
        timerList.setAdapter(timerAdapter);
        timerList.setOnItemClickListener(new TimerListItemOnClickListener(timerAdapter, this));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        Log.i("Main", "Something triggered onResult");
        if (EditTimerActivity.EDIT_RESULT_SUCCESS == resultCode) {

            final boolean addItem = data.getBooleanExtra(TimerAdapter.PUT_EXTRA_ADD_ITEM, true);
            final TimerListItem resultTimer = data.getParcelableExtra(TimerAdapter.PUT_EXTRA_ITEM_KEY);
            final int resultPosition = data.getIntExtra(TimerAdapter.PUT_EXTRA_POSITION_KEY, 999);

            if (resultPosition == 999) {
                Toast.makeText(this, "Unknown position", Toast.LENGTH_LONG).show();
            } else {
//                if (addItem) {
//                    timerAdapter.putItem(resultTimer);
//                } else {
                timerAdapter.replaceItem(resultPosition, resultTimer);
//                }
            }
        } else {
            Log.i("Main", "Something else triggered onResult");
            Log.i("Main", "requestCode: " + requestCode);
            Log.i("Main", "resultCode: " + resultCode);
            Log.i("Main", "data: " + data);
        }
    }

//    private ArrayList<TimerListItem> getDataForListView() {
//        final ArrayList<TimerListItem> timerListItems = new ArrayList<TimerListItem>();
//
//        for (int i = 1; i < 15; i++) {
//            final TimerListItem timerListItem = new TimerListItem();
//            timerListItem.interval = i * 10 + "s";
//            timerListItem.delay = i + "s";
//            timerListItem.repeat = "inf";
//            timerListItems.add(timerListItem);
//        }
//
//        return timerListItems;
//    }
}
