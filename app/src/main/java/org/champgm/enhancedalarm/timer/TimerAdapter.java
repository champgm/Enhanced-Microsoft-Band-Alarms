package org.champgm.enhancedalarm.timer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.champgm.enhancedalarm.R;

import java.util.ArrayList;

public class TimerAdapter extends BaseAdapter {
    private final LayoutInflater layoutInflater;
    private final ArrayList<TimerListItem> items;

    public TimerAdapter(final ArrayList<TimerListItem> items, final LayoutInflater layoutInflater) {
        if (items == null) {
            throw new IllegalArgumentException("items cannot be null.");
        }
        if (layoutInflater == null) {
            throw new IllegalArgumentException("layoutInflater cannot be null.");
        }
        this.layoutInflater = layoutInflater;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(final int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    public TimerListItem getTimerListItem(final int position) {
        return items.get(position);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final View timerView = convertView == null ?
                layoutInflater.inflate(R.layout.timer_list_item_layout, parent, false) :
                convertView;

        final TextView intervalText = (TextView) timerView.findViewById(R.id.interval);
        final TextView delayText = (TextView) timerView.findViewById(R.id.delay);
        final TextView repeatText = (TextView) timerView.findViewById(R.id.repeat);

        final TimerListItem timerListItem = items.get(position);
        intervalText.setText(timerListItem.interval);
        delayText.setText(timerListItem.delay);
        repeatText.setText(timerListItem.repeat);

        return timerView;
    }
}
