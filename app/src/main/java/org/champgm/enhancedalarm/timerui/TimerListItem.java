package org.champgm.enhancedalarm.timerui;

import java.util.UUID;

import android.os.Parcel;
import android.os.Parcelable;

import org.champgm.enhancedalarm.R;
import org.champgm.enhancedalarm.util.Checks;

import com.microsoft.band.notifications.VibrationType;

/**
 * This is an object that contains all of the information needed to fill out a timer's {@link android.view.View} as well
 * as some control keys and objects
 */
public class TimerListItem implements Parcelable {

    /**
     * The UUID for the special add-item button that should always be at the bottom of the list
     */
    public static final UUID ADD_ITEM_UUID = UUID.fromString("e17a30f6-17eb-4750-a673-75d3d1991144");
    /**
     * The reference to the special add-item button that should always be at the bottom of the list
     */
    public static final TimerListItem ADD_ITEM = new TimerListItem();
    /**
     * Some weird thing needed in order to be able to pass around {@link TimerListItem}s
     * in {@link android.content.Intent}s
     */
    public static final Creator<TimerListItem> CREATOR = new TimerListItemCreator();
    /**
     * They key used to store a timer list item in a result intent
     */
    public static final String PUT_EXTRA_ITEM_KEY = "641b707a-f6f1-4eea-ae88-482c44edd955";
    /**
     * They key used to store a position (in the TimerAdapter's ArrayList) in a result intent
     */
    public static final String PUT_EXTRA_POSITION_KEY = "6f4ca2b5-b84c-4d12-8dc2-52f52ba1ff90";
    /**
     * The time between event firing
     */
    public final int interval;
    /**
     * The delay before the first event fires
     */
    public final int delay;
    /**
     * The UUID for this thing
     */
    public final UUID uuid;
    /**
     * String corresponding to the chosen {@link com.microsoft.band.notifications.VibrationType}
     */
    public final String vibrationTypeName;
    /**
     * Flag denoting if this timer is currently running
     */
    public boolean started = false;
    private static final String uuidPrefix = "uuid=";
    private static final String intervalPrefix = ", interval=";
    private static final String delayPrefix = ", delay=";
    private static final String startedPrefix = ", started=";
    private static final String vibrationTypeNamePrefix = ", vibrationTypeName=";
    private static final char closeBracket = '}';

    public TimerListItem(final UUID uuid, final int interval, final int delay, final String vibrationTypeName, final boolean started) {
        this.uuid = uuid;
        this.interval = interval;
        this.delay = delay;
        this.vibrationTypeName = vibrationTypeName;
        this.started = started;
    }

    /**
     * This is probably the constructor you want. It will set the input data and a random UUID.
     * 
     * @param interval
     *            The time between event firing
     * @param delay
     *            The delay before the first event fires
     * @param vibrationTypeName
     *            String corresponding to the chosen {@link com.microsoft.band.notifications.VibrationType}
     */
    public TimerListItem(final int interval, final int delay, final String vibrationTypeName) {
        this.interval = interval;
        this.delay = delay;
        if (Checks.isEmpty(vibrationTypeName)) {
            this.vibrationTypeName = vibrationTypeName;
        } else {
            this.vibrationTypeName = VibrationType.NOTIFICATION_ALARM.name();
        }
        uuid = UUID.randomUUID();
    }

    /**
     * Creates an instance. This is part of the {@link android.os.Parcelable} serialization stuff.
     * 
     * @param parcel
     *            the input data to replicate
     */
    public TimerListItem(final Parcel parcel) {
        if (Checks.isNotNull(parcel)) {
            interval = parcel.readInt();
            delay = parcel.readInt();
            started = parcel.readInt() == 1;
            uuid = UUID.fromString(parcel.readString());
            vibrationTypeName = parcel.readString();
        } else {
            throw new RuntimeException("Unable to create TimerListItem from empty parcel");
        }
    }

    /**
     * Creates an instance with some nonsense values and a random {@link java.util.UUID}. The only reason to use this
     * constructor is to create the add-item item at the end of the list.
     */
    protected TimerListItem() {
        interval = R.string.default_interval;
        delay = R.string.default_delay;
        uuid = ADD_ITEM_UUID;
        vibrationTypeName = VibrationType.NOTIFICATION_ALARM.name();
    }

    /**
     * Auto generated, who knows?
     * 
     * @return zero
     */
    @Override
    public int describeContents() {
        return 0;
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
        destination.writeInt(interval);
        destination.writeInt(delay);
        destination.writeInt(started ? 1 : 0);
        destination.writeString(uuid.toString());
        destination.writeString(vibrationTypeName);
    }

    /**
     * yeah it does
     * 
     * @return a bunch of stuff
     */
    @Override
    public String toString() {
        return "TimerListItem{" +
                uuidPrefix + uuid +
                intervalPrefix + interval +
                delayPrefix + delay +
                startedPrefix + started +
                vibrationTypeNamePrefix + vibrationTypeName +
                closeBracket;
    }

    public static TimerListItem fromString(final String stringRepresentation) {
        final UUID uuid = UUID.fromString(stringRepresentation.substring(stringRepresentation.indexOf(uuidPrefix) + uuidPrefix.length(), stringRepresentation.indexOf(intervalPrefix)));
        final int interval = Integer.valueOf(stringRepresentation.substring(stringRepresentation.indexOf(intervalPrefix) + intervalPrefix.length(), stringRepresentation.indexOf(delayPrefix)));
        final int delay = Integer.valueOf(stringRepresentation.substring(stringRepresentation.indexOf(delayPrefix) + delayPrefix.length(), stringRepresentation.indexOf(startedPrefix)));
        final boolean started = Boolean.valueOf(stringRepresentation.substring(stringRepresentation.indexOf(startedPrefix) + startedPrefix.length(), stringRepresentation.indexOf(vibrationTypeNamePrefix)));
        final String vibrationTypeName = stringRepresentation.substring(stringRepresentation.indexOf(vibrationTypeNamePrefix) + vibrationTypeNamePrefix.length(), stringRepresentation.indexOf(closeBracket));
        return new TimerListItem(uuid, interval, delay, vibrationTypeName, started);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TimerListItem))
            return false;

        final TimerListItem that = (TimerListItem) o;

        return delay == that.delay &&
                interval == that.interval &&
                started == that.started &&
                uuid.equals(that.uuid) &&
                vibrationTypeName.equals(that.vibrationTypeName);

    }

    @Override
    public int hashCode() {
        int result = interval;
        result = 31 * result + delay;
        result = 31 * result + uuid.hashCode();
        result = 31 * result + (started ? 1 : 0);
        result = 31 * result + vibrationTypeName.hashCode();
        return result;
    }

    private static class TimerListItemCreator implements Creator<TimerListItem> {
        @Override
        public TimerListItem createFromParcel(final Parcel in) {
            return new TimerListItem(in);
        }

        @Override
        public TimerListItem[] newArray(final int size) {
            return new TimerListItem[size];
        }
    }
}
