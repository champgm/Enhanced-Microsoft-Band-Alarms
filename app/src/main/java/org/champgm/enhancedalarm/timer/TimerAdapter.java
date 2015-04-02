package org.champgm.enhancedalarm.timer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.champgm.enhancedalarm.R;

public class TimerAdapter extends BaseAdapter {
    public static final String PUT_EXTRA_ITEM_KEY = "641b707a-f6f1-4eea-ae88-482c44edd955";
    public static final String PUT_EXTRA_POSITION_KEY = "6f4ca2b5-b84c-4d12-8dc2-52f52ba1ff90";
    public static final String PUT_EXTRA_ADD_ITEM = "481001b8-4992-408e-acfd-637415627725";
    private final LayoutInflater layoutInflater;
    private final ArrayList<TimerListItem> items;
    private final Activity mainActivity;

    public TimerAdapter(final Activity mainActivity) {
        Log.i("TimerAdapter", "creating new timer adapter");
        this.mainActivity = mainActivity;
        this.layoutInflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        items = new ArrayList<>();
        items.add(TimerListItem.ADD_ITEM);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public TimerListItem getItem(final int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        Log.i("creating view", "position: " + position);
        Log.i("creating view", "size: " + items.size());
        logList();

        final TimerListItem timerListItem = items.get(position);
        if (timerListItem.uuid != TimerListItem.ADD_ITEM_UUID) {
            final View timerView = layoutInflater.inflate(R.layout.timer_list_item_layout, parent, false);
            final Button editButton = (Button) timerView.findViewById(R.id.edit_button);
            editButton.setOnClickListener(new TimerListItemEditButtonOnClickListener(position, this, mainActivity, false));

            final TextView intervalText = (TextView) timerView.findViewById(R.id.interval);
            final TextView delayText = (TextView) timerView.findViewById(R.id.delay);
            final TextView repeatText = (TextView) timerView.findViewById(R.id.repeat);

            intervalText.setText(String.valueOf(timerListItem.interval));
            delayText.setText(String.valueOf(timerListItem.delay));
            repeatText.setText(String.valueOf(timerListItem.repeat));

            return timerView;
        } else {
            final View addView = layoutInflater.inflate(R.layout.timer_list_add_item_layout, parent, false);
            final Button addButton = (Button) addView.findViewById(R.id.add_button);
            addButton.setOnClickListener(new TimerListItemEditButtonOnClickListener(position, this, mainActivity, true));
            return addView;
        }
    }

    public int putItem(final TimerListItem newTimerListItem) {
        items.remove(items.size() - 1);
        items.add(newTimerListItem);
        items.add(TimerListItem.ADD_ITEM);
        updateData();
        return items.indexOf(newTimerListItem);
    }

    public void removeItem(final int position) {
        items.remove(position);
        updateData();
    }

    public void replaceItem(final int position, final TimerListItem newTimerListItem) {
        Log.i("replaceItem", "size: " + items.size());
        logList();
        Log.i("replaceItem", "position: " + position);
        Log.i("replaceItem", "replace: " + items.get(position));
        Log.i("replaceItem", "with: " + newTimerListItem);
        items.remove(position);
        items.add(newTimerListItem);
        updateData();
    }

    public void logList() {
        for (final TimerListItem item : items) {
            Log.i("Timer List[" + items.indexOf(item) + "]", item.toString());
        }
    }

    private void updateData() {
        ensureAddItem();
        notifyDataSetChanged();
    }

    private void ensureAddItem() {
        if (!items.contains(TimerListItem.ADD_ITEM)) {
            items.add(TimerListItem.ADD_ITEM);
        }
    }
}
