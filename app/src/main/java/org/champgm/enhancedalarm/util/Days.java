package org.champgm.enhancedalarm.util;

import org.champgm.enhancedalarm.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

public enum Days {
    SATURDAY(R.string.saturday, R.string.saturday_abbrev, R.id.saturdayButton, Calendar.SATURDAY),
    SUNDAY(R.string.sunday, R.string.sunday_abbrev, R.id.sundayButton, Calendar.SUNDAY),
    MONDAY(R.string.monday, R.string.monday_abbrev, R.id.mondayButton, Calendar.MONDAY),
    TUESDAY(R.string.tuesday, R.string.tuesday_abbrev, R.id.tuesdayButton, Calendar.TUESDAY),
    WEDNESDAY(R.string.wednesday, R.string.wednesday_abbrev, R.id.wednesdayButton, Calendar.WEDNESDAY),
    THURSDAY(R.string.thursday, R.string.thursday_abbrev, R.id.thursdayButton, Calendar.THURSDAY),
    FRIDAY(R.string.friday, R.string.friday_abbrev, R.id.fridayButton, Calendar.FRIDAY);

    public static final Days[] orderedDays = { SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY };

    public final int abbreviation;
    public final int fullName;
    public final int buttonId;
    public final int calendarDay;

    Days(final int fullName, final int abbreviation, final int buttonId, final int calendarDay) {
        this.fullName = fullName;
        this.abbreviation = abbreviation;
        this.buttonId = buttonId;
        this.calendarDay = calendarDay;
    }

    public static Days fromId(final int resourceId) {
        switch (resourceId) {
            case R.string.saturday:
            case R.id.saturdayButton:
            case R.string.saturday_abbrev:
            case Calendar.SATURDAY:
                return SATURDAY;
            case R.string.sunday:
            case R.id.sundayButton:
            case R.string.sunday_abbrev:
            case Calendar.SUNDAY:
                return SUNDAY;
            case R.string.monday:
            case R.id.mondayButton:
            case R.string.monday_abbrev:
            case Calendar.MONDAY:
                return MONDAY;
            case R.string.tuesday:
            case R.id.tuesdayButton:
            case R.string.tuesday_abbrev:
            case Calendar.TUESDAY:
                return TUESDAY;
            case R.string.wednesday:
            case R.id.wednesdayButton:
            case R.string.wednesday_abbrev:
            case Calendar.WEDNESDAY:
                return WEDNESDAY;
            case R.string.thursday:
            case R.id.thursdayButton:
            case R.string.thursday_abbrev:
            case Calendar.THURSDAY:
                return THURSDAY;
            case R.string.friday:
            case R.id.fridayButton:
            case R.string.friday_abbrev:
            case Calendar.FRIDAY:
                return FRIDAY;
            default:
                return null;
        }
    }

    public static String toString(final Collection<Days> days) {
        if (days.size() < 1) {
            return "";
        }
        final StringBuilder resultBuilder = new StringBuilder();
        for (final Days day : days) {
            resultBuilder.append(day.fullName);
            resultBuilder.append(",");
        }
        resultBuilder.deleteCharAt(resultBuilder.length() - 1);
        return resultBuilder.toString();

    }

    public static Collection<Days> fromString(final String daysList) {
        if (daysList.trim().isEmpty()) {
            return new ArrayList<Days>();
        }
        final String[] split = daysList.split(",");
        final ArrayList<Days> result = new ArrayList<Days>();
        for (final String day : split) {
            result.add(Days.fromId(Integer.valueOf(day)));
        }
        return result;
    }

}
