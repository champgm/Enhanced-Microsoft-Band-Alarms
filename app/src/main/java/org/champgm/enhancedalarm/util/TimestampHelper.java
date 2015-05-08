package org.champgm.enhancedalarm.util;

import java.util.LinkedList;

/**
 * Created by mc023219 on 4/16/15.
 */
public class TimestampHelper {

    private static final String VALID_TIMESTAMP_PATTERN = "\\d{2,3}:\\d{2}:\\d{2}";

    public static String linkedListToTimestamp(final LinkedList<Integer> digits) {
        final LinkedList<Integer> ourDigits = (LinkedList) digits.clone();
        final StringBuilder resultBuilder = new StringBuilder();

        if (digits.getFirst() != 0) {
            resultBuilder.append(ourDigits.pop());
        } else {
            ourDigits.removeFirst();
        }

        return simplifyTimeStamp(resultBuilder
                .append(ourDigits.pop())
                .append(ourDigits.pop())
                .append(":")
                .append(ourDigits.pop())
                .append(ourDigits.pop())
                .append(":")
                .append(ourDigits.pop())
                .append(ourDigits.pop())
                .toString());
    }

    public static int timeStampToSeconds(final String timestamp) {
        return new ParsedTimestamp(timestamp).toSeconds();
    }

    public static String secondsToTimestamp(final int seconds) {
        return new ParsedTimestamp(seconds).toString();
    }

    public static String simplifyTimeStamp(final String timestamp) {
        return new ParsedTimestamp(timestamp).toString();
    }

    public static boolean validateTimestamp(final String timestamp) {
        return timestamp != null && timestamp.matches(VALID_TIMESTAMP_PATTERN);
    }

    private static class ParsedTimestamp {
        public final String hoursString;
        public final String minutesString;
        public final String secondsString;

        public final int hoursInt;
        public final int minutesInt;
        public final int secondsInt;

        public ParsedTimestamp(final int totalSeconds) {
            hoursInt = totalSeconds / (60 * 60);
            minutesInt = (totalSeconds % (60 * 60)) / (60);
            secondsInt = (totalSeconds % (60 * 60)) % (60);
            hoursString = getString(hoursInt);
            minutesString = getString(minutesInt);
            secondsString = getString(secondsInt);
        }

        public ParsedTimestamp(final String timestampToParse) {
            if (validateTimestamp(timestampToParse)) {
                final int firstColonAt = timestampToParse.indexOf(':');
                final int secondColonAt = timestampToParse.indexOf(':', firstColonAt + 1);

                Integer hoursInteger = Integer.valueOf(timestampToParse.substring(0, firstColonAt));
                Integer minutesInteger = Integer.valueOf(timestampToParse.substring(firstColonAt + 1, secondColonAt));
                Integer secondsInteger = Integer.valueOf(timestampToParse.substring(secondColonAt + 1, timestampToParse.length()));

                if (secondsInteger > 59) {
                    minutesInteger++;
                    secondsInteger = secondsInteger - 60;
                }

                if (minutesInteger > 59) {
                    hoursInteger++;
                    minutesInteger = minutesInteger - 60;
                }

                hoursInt = hoursInteger;
                minutesInt = minutesInteger;
                secondsInt = secondsInteger;
            } else {
                hoursInt = 0;
                minutesInt = 0;
                secondsInt = 0;
            }
            hoursString = getString(hoursInt);
            minutesString = getString(minutesInt);
            secondsString = getString(secondsInt);
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append(hoursString)
                    .append(":")
                    .append(minutesString)
                    .append(":")
                    .append(secondsString)
                    .toString();
        }

        public int toSeconds() {
            return secondsInt + (minutesInt * 60) + (hoursInt * 60 * 60);
        }

        private String getString(final Integer integer) {
            if (integer < 10) {
                return "0" + String.valueOf(integer);
            } else {
                return String.valueOf(integer);
            }
        }
    }
}
