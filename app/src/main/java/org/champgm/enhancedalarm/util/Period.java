package org.champgm.enhancedalarm.util;

import org.champgm.enhancedalarm.R;

public enum Period {
    AM(R.string.am, R.id.amButton),
    PM(R.string.pm, R.id.pmButton),
    TWENTY_FOUR_HOUR(R.string.twentyFourHour, R.id.twentyFourHourButton);
    public final int stringId;
    public final int buttonId;

    Period(final int stringId, final int buttonId) {
        this.stringId = stringId;
        this.buttonId = buttonId;
    }

    public static Period fromId(final int resourceId) {
        switch (resourceId) {
            case R.string.am:
            case R.id.amButton:
                return AM;
            case R.string.pm:
            case R.id.pmButton:
                return PM;
            case R.string.twentyFourHour:
            case R.id.twentyFourHourButton:
                return TWENTY_FOUR_HOUR;
            default:
                throw new RuntimeException("Enum not found for resourceId: " + resourceId);
        }
    }
}
