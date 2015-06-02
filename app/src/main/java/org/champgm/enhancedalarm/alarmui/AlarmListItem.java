package org.champgm.enhancedalarm.alarmui;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.microsoft.band.notifications.VibrationType;

import org.champgm.enhancedalarm.util.Checks;
import org.champgm.enhancedalarm.util.Days;
import org.champgm.enhancedalarm.util.Period;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class AlarmListItem implements Parcelable {
    /**
     * Some weird thing needed in order to be able to pass around {@link AlarmListItem}s
     * in {@link android.content.Intent}s
     */
    public static final Creator<AlarmListItem> CREATOR = new AlarmListItemCreator();
    /**
     * The list item which holds the add button will always have this UUID.
     */
    public static final UUID ADD_ITEM_UUID = UUID.fromString("69a7a7a8-510e-48c1-9b27-e5dbe61a3374");
    /**
     * The reference to the special add-item button that should always be at the bottom of the list
     */
    public static final AlarmListItem ADD_ITEM = new AlarmListItem();
    /**
     * They key used to store an alarm list item in a result intent
     */
    public static final String PUT_EXTRA_ITEM_KEY = "ce0532e5-4597-4835-a6b0-f09f6ad09557";
    /**
     * They key used to store a position (in the AlarmAdapter's ArrayList) in a result intent
     */
    public static final String PUT_EXTRA_POSITION_KEY = "db13016e-3a13-4a95-97a0-c99b2dc16bbc";
    /**
     * The UUID for this thing
     */
    public final UUID uuid;
    /**
     * String corresponding to the chosen {@link com.microsoft.band.notifications.VibrationType}
     */
    public final String vibrationTypeName;
    /**
     * The label for this alarm item
     */
    public final String label;
    /**
     * The days this alarm should fire
     */
    public final Collection<Days> days;
    /**
     * The time the alarm should fire
     */
    public final String time;
    /**
     * The period (am, pm, or twenty-four-hour) for the time
     */
    public final Period period;
    /**
     * Denotes if this alarm is currently enabled or disabled.
     */
    public boolean enabled = false;
    /**
     * Denotes if this alarm is currently firing
     */
    public boolean firing = false;
    private static final String uuidPrefix = "uuid=";
    private static final String vibrationTypeNamePrefix = ", vibrationTypeName=";
    private static final String labelPrefix = ", label=";
    private static final String daysPrefix = ", days=";
    private static final String timePrefix = ", time=";
    private static final String periodPrefix = ", period=";
    private static final String enabledPrefix = ", enabled=";
    private static final char closeBracket = '}';
    private static final String blankLabel = "X";
    private static final String blankAlarmTime = "00:01";

    /**
     * Creates an instance with some nonsense values and a random {@link java.util.UUID}. The only reason to use this
     * constructor is to create the add-item item at the end of the list.
     */
    protected AlarmListItem() {
        label = "ADD ITEM";
        days = new ArrayList<Days>(0);
        time = blankAlarmTime;
        period = Period.TWENTY_FOUR_HOUR;
        Log.i("Alarm_List_Item", "Creating ADD_ITEM. UUID will be: " + ADD_ITEM_UUID);
        uuid = ADD_ITEM_UUID;
        vibrationTypeName = VibrationType.NOTIFICATION_ALARM.name();
    }

    public AlarmListItem(
            final String vibrationTypeName,
            final String label,
            final Collection<Days> days,
            final String time,
            final Period period,
            final boolean enabled,
            final boolean firing) {
        this.uuid = UUID.randomUUID();
        if (Checks.isEmpty(vibrationTypeName)) {
            this.vibrationTypeName = vibrationTypeName;
        } else {
            this.vibrationTypeName = VibrationType.NOTIFICATION_ALARM.name();
        }
        if (Checks.isEmpty(label)) {
            this.label = label;
        } else {
            this.label = blankLabel;
        }
        if (Checks.isNotNull(days)) {
            this.days = days;
        } else {
            this.days = new ArrayList<Days>();
        }
        if (Checks.isEmpty(time)) {
            this.time = time;
        } else {
            this.time = blankAlarmTime;
        }
        this.period = period;
        this.enabled = enabled;
        this.firing = firing;
    }

    public AlarmListItem(final UUID uuid,
            final String vibrationTypeName,
            final String label,
            final Collection<Days> days,
            final String time,
            final Period period,
            final boolean enabled,
                         final boolean firing) {
        this.uuid = uuid;
        this.vibrationTypeName = vibrationTypeName;
        this.label = label;
        this.days = days;
        this.time = time;
        this.period = period;
        this.enabled = enabled;
        this.firing=firing;
    }

    /**
     * Creates an instance. This is part of the {@link android.os.Parcelable} serialization stuff.
     *
     * @param parcel
     *            the input data to replicate
     */
    public AlarmListItem(final Parcel parcel) {
        if (Checks.isNotNull(parcel)) {
            uuid = UUID.fromString(parcel.readString());
            vibrationTypeName = parcel.readString();
            label = parcel.readString();
            final int[] days = new int[] { -1, -1, -1, -1, -1, -1, -1 };
            parcel.readIntArray(days);
            this.days = daysFromArray(days);
            time = parcel.readString();
            period = Period.fromId(parcel.readInt());
            enabled = parcel.readInt() == 1;
        } else {
            throw new RuntimeException("Unable to create AlarmListItem from empty parcel");
        }
    }

    /**
     * This is like a serialization message, it writes the fields to the output
     *
     * @param destination
     *            the output for the data
     * @param flags
     *            I don't know what this is
     */
    @Override
    public void writeToParcel(final Parcel destination, final int flags) {
        destination.writeString(uuid.toString());
        destination.writeString(vibrationTypeName);
        destination.writeString(label);
        destination.writeIntArray(daysToList());
        destination.writeString(time);
        destination.writeInt(period.buttonId);
        destination.writeInt(enabled ? 1 : 0);
    }

    private int[] daysToList() {
        final int[] result = new int[7];
        final Iterator<Days> iterator = days.iterator();
        for (int i = 0; i < days.size(); i++) {
            result[i] = iterator.next().fullName;
        }
        return result;
    }

    private List<Days> daysFromArray(final int[] dayInts) {
        final ArrayList<Days> result = new ArrayList<Days>();
        for (final int dayInt : dayInts) {
            final Days dayEnum = Days.fromId(dayInt);
            if (dayEnum != null) {
                result.add(dayEnum);
            }
        }
        return result;
    }

    private static class AlarmListItemCreator implements Creator<AlarmListItem> {
        @Override
        public AlarmListItem createFromParcel(final Parcel in) {
            return new AlarmListItem(in);
        }

        @Override
        public AlarmListItem[] newArray(final int size) {
            return new AlarmListItem[size];
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AlarmListItem)) {
            return false;
        }

        final AlarmListItem that = (AlarmListItem) o;

        return period == that.period &&
                enabled == that.enabled &&
                uuid.equals(that.uuid) &&
                vibrationTypeName.equals(that.vibrationTypeName) &&
                label.equals(that.label) &&
                days.equals(that.days) &&
                time.equals(that.time);

    }

    @Override
    public String toString() {
        for (final Days day : days) {
            Log.i("CONTENTS_OF_DAYS", "Day: " + day);
        }

        return "AlarmListItem{" +
                uuidPrefix + uuid +
                vibrationTypeNamePrefix + vibrationTypeName +
                labelPrefix + label +
                daysPrefix + Days.toString(days) +
                timePrefix + time +
                periodPrefix + period.stringId +
                enabledPrefix + enabled +
                closeBracket;
    }

    public static AlarmListItem fromString(final String stringRepresentation) {
        final UUID uuid = UUID.fromString(stringRepresentation.substring(stringRepresentation.indexOf(uuidPrefix) + uuidPrefix.length(), stringRepresentation.indexOf(vibrationTypeNamePrefix)));
        final String vibrationTypeName = stringRepresentation.substring(stringRepresentation.indexOf(vibrationTypeNamePrefix) + vibrationTypeNamePrefix.length(), stringRepresentation.indexOf(labelPrefix));
        final String label = stringRepresentation.substring(stringRepresentation.indexOf(labelPrefix) + labelPrefix.length(), stringRepresentation.indexOf(daysPrefix));
        final Collection<Days> days = Days.fromString(stringRepresentation.substring(stringRepresentation.indexOf(daysPrefix) + daysPrefix.length(), stringRepresentation.indexOf(timePrefix)));
        final String time = stringRepresentation.substring(stringRepresentation.indexOf(timePrefix) + timePrefix.length(), stringRepresentation.indexOf(periodPrefix));
        final Period period = Period.fromId(Integer.valueOf(stringRepresentation.substring(stringRepresentation.indexOf(periodPrefix) + periodPrefix.length(), stringRepresentation.indexOf(enabledPrefix))));
        final boolean enabled = Boolean.valueOf(stringRepresentation.substring(stringRepresentation.indexOf(enabledPrefix) + enabledPrefix.length(), stringRepresentation.indexOf(closeBracket)));
        Log.i("ALARM_ADAPTER", "Creating alarm list item from string");
        Log.i("ALARM_ADAPTER", "uuid: " + uuid);
        Log.i("ALARM_ADAPTER", "vibrationTypeName: " + vibrationTypeName);
        Log.i("ALARM_ADAPTER", "label: " + label);
        Log.i("ALARM_ADAPTER", "days: " + days);
        Log.i("ALARM_ADAPTER", "time: " + time);
        Log.i("ALARM_ADAPTER", "period: " + period);
        Log.i("ALARM_ADAPTER", "enabled: " + enabled);

        return new AlarmListItem(
                uuid,
                vibrationTypeName,
                label,
                days,
                time,
                period,
                enabled);
    }

    @Override
    public int hashCode() {
        int result = uuid.hashCode();
        result = 31 * result + vibrationTypeName.hashCode();
        result = 31 * result + label.hashCode();
        result = 31 * result + days.hashCode();
        result = 31 * result + time.hashCode();
        result = 31 * result + period.hashCode();
        result = 31 * result + (enabled ? 1 : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
