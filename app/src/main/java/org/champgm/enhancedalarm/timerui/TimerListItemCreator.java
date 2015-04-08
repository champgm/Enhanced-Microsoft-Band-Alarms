package org.champgm.enhancedalarm.timerui;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is just some utility object for {@link android.os.Parcelable}. There's nothing here, you don't want this class.
 * I think it's normally created in-line, I think the term is "anonymous", but I think that's garbage, so I gave it its
 * own file.
 */
public class TimerListItemCreator implements Parcelable.Creator<TimerListItem> {
    @Override
    public TimerListItem createFromParcel(final Parcel in) {
        return new TimerListItem(in);
    }

    @Override
    public TimerListItem[] newArray(final int size) {
        return new TimerListItem[size];
    }
}
