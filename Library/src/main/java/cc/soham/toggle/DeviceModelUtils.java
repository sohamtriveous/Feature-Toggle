package cc.soham.toggle;

import android.os.Build;
import android.text.TextUtils;

/**
 * Created by sohammondal on 19/01/16.
 */
public class DeviceModelUtils {
    /**
     * Returns the consumer friendly device name
     */
    public static String getModel(String manufacturer, String model) {
        if (manufacturer == null)
            return model;
        if (model.startsWith(manufacturer)) {
            return model.substring(manufacturer.length());
        }
        return model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }
}
