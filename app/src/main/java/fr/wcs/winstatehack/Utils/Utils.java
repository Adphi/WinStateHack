package fr.wcs.winstatehack.Utils;

import java.util.ArrayList;

/**
 * Created by adphi on 21/12/17.
 */

public class Utils {
    public static String TAG = getTAG(Utils.class);
    public static String getTAG(Object o) {
        return String.format("WWW %s", o.getClass().getSimpleName());
    }

    public static ArrayList<Integer> getLogTable() {
        ArrayList<Integer> out = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            int y = (int) (((i*i)/200)*Math.log10(i));
            out.add(y);
        }
        return out;
    }
}
