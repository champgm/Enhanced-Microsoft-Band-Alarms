package org.champgm.enhancedalarm.util;

import android.content.Context;
import android.widget.Toast;

public class Toaster {
    public static void send(final Context context, final String message) {
        final Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void send(final Context context, final int resource) {
        final Toast toast = Toast.makeText(context, resource, Toast.LENGTH_LONG);
        toast.show();
    }
}
