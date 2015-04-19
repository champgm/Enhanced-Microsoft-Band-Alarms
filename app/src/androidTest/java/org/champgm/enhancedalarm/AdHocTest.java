package org.champgm.enhancedalarm;

import android.test.AndroidTestCase;
import android.util.Log;

import java.util.LinkedList;

/**
 * Created by mc023219 on 4/17/15.
 */
public class AdHocTest extends AndroidTestCase {

    public void testExperimentWithLinkedList() {
        Log.d("TEST", "============================");
        final LinkedList<Integer> digits = new LinkedList<Integer>();
        digits.push(6);
        digits.push(7);
        digits.push(2);
        digits.push(9);
        digits.push(4);

        for (int i = 0; i < digits.size(); i++) {
            Log.d("TEST", "Digit[" + i + "]: " + digits.get(i));
        }

        // the linked list is backwards from what I thought it would be. It pushes items into the left side, or the
        // "bottom" of the array

        // assertEquals(3, digits.size());
    }
}
