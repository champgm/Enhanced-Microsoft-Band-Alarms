package org.champgm.enhancedalarm.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by mc023219 on 4/24/15.
 */
public class Toaster {
    public static void send(final Context context, final String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG);
    }
    public static void send(final Context context, int resource) {
        Toast.makeText(context, resource, Toast.LENGTH_LONG);
    }
}
