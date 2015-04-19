package org.champgm.enhancedalarm.timerui;

import java.util.UUID;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.StringUtils;
import org.champgm.enhancedalarm.R;

import com.google.common.base.Preconditions;
import com.microsoft.band.notification.VibrationType;

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
     * Flag denoting if this timer is currently running
     */
    public boolean started = false;
    /**
     * String corresponding to the chosen {@link com.microsoft.band.notification.VibrationType}
     */
    public final String vibrationTypeName;

    /**
     * This is probably the constructor you want. It will set the input data and a random UUID.
     * 
     * @param interval
     *            The time between event firing
     * @param delay
     *            The delay before the first event fires
     * @param vibrationTypeName
     *            String corresponding to the chosen {@link com.microsoft.band.notification.VibrationType}
     */
    public TimerListItem(final int interval, final int delay, final String vibrationTypeName) {
        Preconditions.checkArgument(StringUtils.isNotBlank(vibrationTypeName), "vibrationTypeName may not be null or empty.");
        this.interval = interval;
        this.delay = delay;
        this.vibrationTypeName = vibrationTypeName;
        uuid = UUID.randomUUID();
    }

    /**
     * Creates an instance. This is part of the {@link android.os.Parcelable} serialization stuff.
     * 
     * @param parcel
     *            the input data to replicate
     */
    public TimerListItem(final Parcel parcel) {
        interval = parcel.readInt();
        delay = parcel.readInt();
        started = parcel.readInt() == 1;
        uuid = UUID.fromString(parcel.readString());
        vibrationTypeName = parcel.readString();
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
                "uuid=" + uuid +
                ", interval=" + interval +
                ", delay=" + delay +
                ", started=" + started +
                ", vibrationTypeName=" + vibrationTypeName +
                '}';
    }

    @Override
    public int hashCode() {
        return new org.apache.commons.lang3.builder.HashCodeBuilder()
                .append(interval)
                .append(delay)
                .append(uuid)
                .append(started)
                .append(vibrationTypeName)
                .toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof TimerListItem) {
            final TimerListItem other = (TimerListItem) obj;
            return new org.apache.commons.lang3.builder.EqualsBuilder()
                    .append(interval, other.interval)
                    .append(delay, other.delay)
                    .append(uuid, other.uuid)
                    .append(started, other.started)
                    .append(vibrationTypeName, other.vibrationTypeName)
                    .isEquals();
        }
        return false;
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
