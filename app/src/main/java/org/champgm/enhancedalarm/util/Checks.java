package org.champgm.enhancedalarm.util;

/**
 * Created by mc023219 on 4/24/15.
 */
public class Checks {
    /**
     * Checks if a string is null or blank
     * 
     * @param string
     *            to check
     * @return true if string is null or empty
     */
    public static boolean isEmpty(final String string) {
        if (string == null) {
            return true;
        }
        final String trimmed = string.trim();
        if (trimmed.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Checks if a string is not null or blank
     *
     * @param string
     *            to check
     * @return false if string is null or empty
     */
    public static boolean notEmpty(final String string) {
        return !isEmpty(string);
    }

    public static boolean isNull(final Object object) {
        return object == null;
    }

    public static boolean isNotNull(final Object object) {
        return object != null;
    }
}
