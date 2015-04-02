package org.champgm.enhancedalarm.timer;

import java.util.UUID;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.champgm.enhancedalarm.R;

public class TimerListItem implements Parcelable {
    public static final int ADD_ITEM_ID = 8345;
    public static final UUID ADD_ITEM_UUID = UUID.fromString("e17a30f6-17eb-4750-a673-75d3d1991144");
    public static final TimerListItem ADD_ITEM = new TimerListItem();
    public static final Parcelable.Creator<TimerListItem> CREATOR = new TimerListItemCreator();
    public final int interval;
    public final int delay;
    public final int repeat;
    public boolean started = false;
    protected UUID uuid;

    public TimerListItem() {
        Log.i("TimerListItem", "creating new timer list item");
        Log.i("TimerListItem", UUID.fromString("e17a30f6-17eb-4750-a673-75d3d1991144").toString());

        interval = R.string.default_interval;
        delay = R.string.default_delay;
        repeat = R.string.default_repeat;
        uuid = ADD_ITEM_UUID;
    }

    public TimerListItem(final int interval, final int delay, final int repeat) {
        this.interval = interval;
        this.delay = delay;
        this.repeat = repeat;
        uuid = UUID.randomUUID();
    }

    public TimerListItem(final Parcel parcel) {
        interval = parcel.readInt();
        delay = parcel.readInt();
        repeat = parcel.readInt();
        started = parcel.readInt() == 1;
        uuid = UUID.fromString(parcel.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel destination, final int flags) {
        destination.writeInt(interval);
        destination.writeInt(delay);
        destination.writeInt(repeat);
        destination.writeInt(started ? 1 : 0);
        destination.writeString(uuid.toString());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimerListItem)) {
            return false;
        }

        final TimerListItem that = (TimerListItem) o;

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

    @Override
    public int hashCode() {
        int result = uuid.hashCode();
        result = 31 * result + interval;
        result = 31 * result + delay;
        result = 31 * result + repeat;
        result = 31 * result + (started ? 1 : 0);
        return result;
    }

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
}
