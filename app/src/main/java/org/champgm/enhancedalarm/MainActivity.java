package org.champgm.enhancedalarm;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.champgm.enhancedalarm.timer.TimerAdapter;
import org.champgm.enhancedalarm.timer.TimerListItem;
import org.champgm.enhancedalarm.timer.TimerListItemOnClickListener;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    private TimerAdapter timerAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (timerAdapter == null) {
            final ArrayList<TimerListItem> initalArray = new ArrayList<TimerListItem>();
            initalArray.add(new TimerListItem("30s", "5s", "Inf"));
            timerAdapter = new TimerAdapter(initalArray, (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
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
