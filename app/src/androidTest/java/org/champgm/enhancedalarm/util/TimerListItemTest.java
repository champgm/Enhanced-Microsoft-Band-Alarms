package org.champgm.enhancedalarm.util;

import java.util.UUID;

import android.test.AndroidTestCase;

import org.champgm.enhancedalarm.timerui.TimerListItem;

import com.microsoft.band.notifications.VibrationType;

public class TimerListItemTest extends AndroidTestCase {
    public void testFromString() {
        final UUID uuid = UUID.randomUUID();
        final int interval = 100;
        final int delay = 200;
        final String vibrationTypeName = VibrationType.RAMP_DOWN.name();
        final boolean started = true;

        final TimerListItem expectedListItem = new TimerListItem(uuid, interval, delay, vibrationTypeName, started);
        String stringRepresentation = expectedListItem.toString();
        System.out.println(stringRepresentation);
        final TimerListItem fromString = TimerListItem.fromString(stringRepresentation);
        assertEquals(expectedListItem, fromString);

    }
}
