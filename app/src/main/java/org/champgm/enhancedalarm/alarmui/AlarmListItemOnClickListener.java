package org.champgm.enhancedalarm.alarmui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import org.champgm.enhancedalarm.R;
import org.champgm.enhancedalarm.band.BandHelper;
import org.champgm.enhancedalarm.band.BandService;
import org.champgm.enhancedalarm.band.VibrationReceiver;
import org.champgm.enhancedalarm.util.AlarmTimeHelper;
import org.champgm.enhancedalarm.util.Checks;
import org.champgm.enhancedalarm.util.Days;
import org.champgm.enhancedalarm.util.Period;

import java.util.Calendar;
import java.util.HashMap;

public class AlarmListItemOnClickListener implements AdapterView.OnItemClickListener {
    private final AlarmAdapter alarmAdapter;

    /**
     * Creates an instance
     *
     * @param alarmAdapter
     *            the alarm adapter
     */
    public AlarmListItemOnClickListener(final AlarmAdapter alarmAdapter) {
        if (Checks.isNull(alarmAdapter)) {
            throw new RuntimeException("Cannot create an item click listener if alarmAdapter is null.");
        }
        this.alarmAdapter = alarmAdapter;
    }

    /**
     * Stuff that happens when you click the list items
     *
     * @param parent
     *            Maybe this is the AlarmAdapter? Maybe that can be removed from the constructor
     * @param view
     *            the view to set green/red
     * @param position
     *            the position of the item that was clicked
     * @param id
     *            i don't think this is needed
     */
    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        // Get the item from the adapter
        final AlarmListItem alarmListItem = alarmAdapter.getItem(position);
        if (Checks.isNotNull(alarmListItem) &&
                Checks.isNotNull(view)) {
            // Ignore the add button item thing, that doesn't need an on-item-click listener.
            if (alarmListItem.uuid != AlarmListItem.ADD_ITEM_UUID) {
                if (!BandHelper.anyBandsConnected()) {
                    Toast.makeText(view.getContext(), R.string.no_bands_found, Toast.LENGTH_LONG).show();
                } else {

                    final HashMap<Days, PendingIntent> pendingIntents = new HashMap<Days, PendingIntent>(7);

                    final Intent intent = new Intent(view.getContext(), VibrationReceiver.class);
                    final String uuidString = alarmListItem.uuid.toString();
                    intent.putExtra(VibrationReceiver.UUID_KEY, uuidString);
                    intent.putExtra(VibrationReceiver.VIBRATION_TYPE_KEY, alarmListItem.vibrationTypeName);

                    for (final Days day : alarmListItem.days) {
                        pendingIntents.put(day, buildIntent(view.getContext(), intent, uuidString, day));
                    }

                    final AlarmManager alarmManager = (AlarmManager)
                            view.getContext().getSystemService(Context.ALARM_SERVICE);
                    if (alarmListItem.enabled) {
                        for (final PendingIntent pendingIntent : pendingIntents.values()) {
                            alarmManager.cancel(pendingIntent);
                            pendingIntent.cancel();
                        }

                        final Intent bandServiceIntent = new Intent(view.getContext(), BandService.class);
                        view.getContext().stopService(bandServiceIntent);

                        // Set the background color to clear
                        view.setBackgroundColor(view.getContext().getResources().getColor(R.color.invisible));
                        // Toggle the alarm status
                        alarmListItem.enabled = false;
                    } else {

                        for (final Days day : alarmListItem.days) {
                            final Calendar calendar = Calendar.getInstance();
                            Log.i("No Modification-", calendar.toString());
                            switch (alarmListItem.period) {
                                case AM:
                                    calendar.set(Calendar.AM_PM, Calendar.AM);
                                    break;
                                case PM:
                                    calendar.set(Calendar.AM_PM, Calendar.PM);
                                    break;
                                case TWENTY_FOUR_HOUR:
                                    calendar.clear(Calendar.AM_PM);
                                    break;
                            }
                            Log.i("Set the AMPM   -", calendar.toString());
                            final AlarmTimeHelper.ParsedTime parsedTime = AlarmTimeHelper.parse(alarmListItem.time);
                            final int hoursInt = alarmListItem.period == Period.PM ? parsedTime.hoursInt + 12 : parsedTime.hoursInt;

                            calendar.set(Calendar.HOUR_OF_DAY, hoursInt);
                            Log.i("Set the Hour   -", calendar.toString());
                            calendar.set(Calendar.MINUTE, parsedTime.minutesInt);
                            Log.i("Set the Minute -", calendar.toString());
                            final int today = calendar.get(Calendar.DAY_OF_WEEK);
                            if (today < day.calendarDay) {
                                calendar.add(Calendar.DAY_OF_WEEK, today + (7 - day.calendarDay));
                            } else {
                                calendar.add(Calendar.DAY_OF_WEEK, today - day.calendarDay);
                            }
                            Log.i("Set the Day    -", calendar.toString());
                            Log.i("Final Calendar -", calendar.toString());
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 604800000, pendingIntents.get(day));
                        }

                        // Set the background green
                        view.setBackgroundColor(view.getContext().getResources().getColor(R.color.activated_green));
                        alarmListItem.enabled = true;
                    }
                    alarmAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private Calendar addDay(final Calendar calendar, final Days day) {
        switch (day) {
            case SATURDAY:
                calendar.add(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                break;
            case SUNDAY:
                calendar.add(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                break;
            case MONDAY:
                calendar.add(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                break;
            case TUESDAY:
                calendar.add(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                break;
            case WEDNESDAY:
                calendar.add(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                break;
            case THURSDAY:
                calendar.add(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                break;
            case FRIDAY:
                calendar.add(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                break;
        }
        return calendar;
    }

    private PendingIntent buildIntent(final Context context, final Intent intent, final String uuidString, final Days day) {
        return PendingIntent.getBroadcast(context, (uuidString + ":" + day).hashCode(), intent, 0);
    }
}