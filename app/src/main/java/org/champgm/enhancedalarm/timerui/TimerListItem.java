package org.champgm.enhancedalarm.timerui;

import android.os.Parcel;
import android.os.Parcelable;

import org.champgm.enhancedalarm.R;

import java.util.UUID;

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
     * Some weird thing needed in order to be able to pass around
     * {@link org.champgm.enhancedalarm.timerui.TimerListItem}s
     * in {@link android.content.Intent}s
     */
    public static final Parcelable.Creator<TimerListItem> CREATOR = new TimerListItemCreator();
    /**
     * The time between event firing
     */
    public final int interval;
    /**
     * The delay before the first event fires
     */
    public final int delay;
    /**
     * The number of times to keep sending the event
     */
    public final int repeat;
    /**
     * The UUID for this thing
     */
    public final UUID uuid;
    /**
     * Flag denoting if this timer is currently running
     */
    public boolean started = false;

    /**
     * This is probably the constructor you want. It will set the input data and a random UUID.
     * 
     * @param interval
     *            The time between event firing
     * @param delay
     *            The delay before the first event fires
     * @param repeat
     *            The number of times to keep sending the event
     */
    public TimerListItem(final int interval, final int delay, final int repeat) {
        this.interval = interval;
        this.delay = delay;
        this.repeat = repeat;
        uuid = UUID.randomUUID();
    }

    /**
     * This constructor will allow you to manually set a UUID. Probably don't use this unless you are cloning an
     * existing item.
     *
     * @param interval
     *            The time between event firing
     * @param delay
     *            The delay before the first event fires
     * @param repeat
     *            The number of times to keep sending the event
     */
    public TimerListItem(final int interval, final int delay, final int repeat, final UUID uuid) {
        this.interval = interval;
        this.delay = delay;
        this.repeat = repeat;
        this.uuid = uuid;
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
        repeat = parcel.readInt();
        started = parcel.readInt() == 1;
        uuid = UUID.fromString(parcel.readString());
    }

    /**
     * Creates an instance with some nonsense values and a random {@link java.util.UUID}. The only reason to use this
     * constructor is to create the add-item item at the end of the list.
     */
    protected TimerListItem() {
        interval = R.string.default_interval;
        delay = R.string.default_delay;
        repeat = R.string.default_repeat;
        uuid = ADD_ITEM_UUID;
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
        destination.writeInt(repeat);
        destination.writeInt(started ? 1 : 0);
        destination.writeString(uuid.toString());
    }

    /**
     * equals method
     * 
     * @param other
     *            the other thing
     * @return does it equals
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TimerListItem)) {
            return false;
        }

        final TimerListItem that = (TimerListItem) other;

        if (delay != that.delay) {
            return false;
        }
        if (interval != that.interval) {
            return false;
        }
        if (repeat != that.repeat) {
            return false;
        }
        if (started != that.started) {
            return false;
        }
        if (!uuid.equals(that.uuid)) {
            return false;
        }

        return true;
    }

    /**
     * hash code
     * 
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result = uuid.hashCode();
        result = 31 * result + interval;
        result = 31 * result + delay;
        result = 31 * result + repeat;
        result = 31 * result + (started ? 1 : 0);
        return result;
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
                ", repeat=" + repeat +
                ", started=" + started +
                '}';
    }

    private static class TimerListItemCreator implements Parcelable.Creator<TimerListItem> {
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
