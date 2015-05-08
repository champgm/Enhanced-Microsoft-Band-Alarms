package org.champgm.enhancedalarm.util;

import java.util.LinkedList;

public class AlarmTimeHelper {
    private static final String VALID_TIMESTAMP_PATTERN = "\\d{2}:\\d{2}";

    public static String simplifyTime(final String inputTime) {
        return new ParsedTime(inputTime).toString();
    }

    public static boolean validateTime(final String timestamp) {
        return timestamp != null && timestamp.matches(VALID_TIMESTAMP_PATTERN);
    }

    public static ParsedTime parse(String timestamp){
        return new ParsedTime(timestamp);
    }

    public static String linkedListToAlarmTime(final LinkedList<Integer> digits) {
        final LinkedList<Integer> ourDigits = (LinkedList) digits.clone();

        return simplifyTime(String.valueOf(
                ourDigits.pop()) +
                ourDigits.pop() +
                ":" +
                ourDigits.pop() +
                ourDigits.pop());
    }

    public static boolean makesSense(final String timestamp, final Period period) {
        final ParsedTime parsedTime = new ParsedTime(timestamp);
        if (parsedTime.minutesInt < 0 || parsedTime.minutesInt > 59 || parsedTime.hoursInt < 0) {
            return false;
        }

        switch (period) {
            case AM:
            case PM:
                if (parsedTime.hoursInt > 12) {
                    return false;
                }
                break;
            case TWENTY_FOUR_HOUR:
                if (parsedTime.hoursInt > 24) {
                    return false;
                }
                if (parsedTime.hoursInt == 24 && parsedTime.minutesInt != 0) {
                    return false;
                }
                break;
        }
        return true;
    }

    public static class ParsedTime {
        public final String hoursString;
        public final String minutesString;
        public final int hoursInt;
        public final int minutesInt;
        public final boolean twentyFourHour;

        public ParsedTime(final String timestampToParse) {

            if (validateTime(timestampToParse)) {
                final int firstColonAt = timestampToParse.indexOf(':');

                Integer hoursInteger = Integer.valueOf(timestampToParse.substring(0, firstColonAt));
                Integer minutesInteger = Integer.valueOf(timestampToParse.substring(firstColonAt + 1));

                if (minutesInteger > 59) {
                    hoursInteger++;
                    minutesInteger = minutesInteger - 60;
                }
                if (hoursInteger > 24) {
                    hoursInteger = 24;
                }
                if (hoursInteger == 24 && minutesInteger > 0) {
                    minutesInteger = 0;
                }

                hoursInt = hoursInteger;
                minutesInt = minutesInteger;
            } else {
                hoursInt = 0;
                minutesInt = 0;
            }
            twentyFourHour = hoursInt > 12;
            hoursString = getString(hoursInt);
            minutesString = getString(minutesInt);
        }

        private String getString(final Integer integer) {
            if (integer < 10) {
                return "0" + String.valueOf(integer);
            } else {
                return String.valueOf(integer);
            }
        }

        public String toString() {
            return hoursString + ':' + minutesString;
        }
    }

}
