package org.champgm.enhancedalarm.timer;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.StringUtils;

public class TimerListItem {
    public String interval;
    public String delay;
    public String repeat;
    public boolean started = false;

    public TimerListItem() {
        interval = "Xs";
        delay = "Xs";
        repeat = "Xx";
    }

    public TimerListItem(final String interval, final String delay, final String repeat) {
        Preconditions.checkArgument(!StringUtils.isBlank(interval), "interval may not be null or empty.");
        Preconditions.checkArgument(!StringUtils.isBlank(delay), "delay may not be null or empty.");
        Preconditions.checkArgument(!StringUtils.isBlank(repeat), "repeat may not be null or empty.");

        this.interval = interval;
        this.delay = delay;
        this.repeat = repeat;
    }
}
