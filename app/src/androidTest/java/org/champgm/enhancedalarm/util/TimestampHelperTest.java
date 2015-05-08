package org.champgm.enhancedalarm.util;

import android.test.AndroidTestCase;

import junit.framework.Assert;

public class TimestampHelperTest extends AndroidTestCase {

    public void testValidTimestamps() {
        final String validThreeHour = "012:34:56";
        final String validTwoHour = "01:23:45";
        Assert.assertTrue(TimestampHelper.validateTimestamp(validThreeHour));
        Assert.assertTrue(TimestampHelper.validateTimestamp(validTwoHour));
    }

    public void testInvalidTimestamps() {
        final String tooManyHours = "0123:23:45";
        final String notEnoughHours = "1:23:45";
        final String tooManyMinutes = "01:234:45";
        final String notEnoughMinutes = "01:2:34";
        final String tooManySeconds = "01:23:456";
        final String notEnoughSeconds = "01:23:4";

        Assert.assertFalse(TimestampHelper.validateTimestamp(tooManyHours));
        Assert.assertFalse(TimestampHelper.validateTimestamp(notEnoughHours));
        Assert.assertFalse(TimestampHelper.validateTimestamp(tooManyMinutes));
        Assert.assertFalse(TimestampHelper.validateTimestamp(notEnoughMinutes));
        Assert.assertFalse(TimestampHelper.validateTimestamp(tooManySeconds));
        Assert.assertFalse(TimestampHelper.validateTimestamp(notEnoughSeconds));
    }

    public final void testTimeStampToSeconds() {
        final String ninetyNineSeconds1 = "00:99:00";
        final String ninetyNineSeconds2 = "000:99:00";
        final int expectedSeconds = 5940;

        assertEquals(TimestampHelper.timeStampToSeconds(ninetyNineSeconds1), TimestampHelper.timeStampToSeconds(ninetyNineSeconds2));
        assertEquals(expectedSeconds, TimestampHelper.timeStampToSeconds(ninetyNineSeconds1));
    }

    public final void testSecondsToTimestamp() {
        final String expectedTimestamp = "01:39:00";
        final int seconds = 5940;

        assertEquals(expectedTimestamp, TimestampHelper.secondsToTimestamp(seconds));
    }

    public final void testParsingBothWays() {
        final String expectedTimestamp = "100:40:39";
        final String inputTimestamp = "99:99:99";
        final int expectedSeconds = 362439;
        assertEquals(expectedTimestamp, TimestampHelper.simplifyTimeStamp(inputTimestamp));
        assertEquals(expectedSeconds, TimestampHelper.timeStampToSeconds(inputTimestamp));
        assertEquals(expectedTimestamp, TimestampHelper.secondsToTimestamp(expectedSeconds));
    }
}
