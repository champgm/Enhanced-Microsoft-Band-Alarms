package org.champgm.enhancedalarm.timer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mc023219 on 4/3/15.
 */
public class TimerListItemCreator implements Parcelable.Creator<TimerListItem> {
    @Override
    public TimerListItem createFromParcel(Parcel in) {
        return new TimerListItem(in);
    }

    @Override
    public TimerListItem[] newArray(int size) {
        return new TimerListItem[size];
    }
}
