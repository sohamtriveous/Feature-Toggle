package cc.soham.toggle;

import android.os.Build;

import java.util.List;

import cc.soham.toggle.objects.Device;
import cc.soham.toggle.objects.Rule;
import cc.soham.toggle.objects.Value;

/**
 * Created by sohammondal on 19/01/16.
 */
public class RuleMatcher {
    /**
     * This is after the feature name has been matched between the FeatureCheckRequest and a stored product Feature
     *
     * @param rule
     * @return
     */
    public static boolean matchRule(Rule rule) {
        Value value = rule.getValue();
        // match all the rule values
        if (matchApilevelMin(value.getApilevelMin()) &&
                matchAppversionMax(value.getApilevelMax()) &&
                matchAppversionMax(value.getAppversionMax()) &&
                matchAppversionMin(value.getAppversionMin()) &&
                matchDateMin(value.getDateMin()) &&
                matchDateMax(value.getDateMax()) &&
                matchBuildType(value.getBuildtype()) &&
                matchDevice(value.getDevice()))
            return true;
        else return false;
    }

    /**
     * Match minimum API level
     *
     * @param apilevelMin
     * @return
     */
    public static boolean matchApilevelMin(int apilevelMin) {
        if (apilevelMin == -1)
            return true;
        if (android.os.Build.VERSION.SDK_INT >= apilevelMin) return true;
        else return false;
    }

    /**
     * Match max API level
     *
     * @param apilevelMax
     * @return
     */
    public static boolean matchApilevelMax(int apilevelMax) {
        if (apilevelMax == -1)
            return true;
        if (Build.VERSION.SDK_INT <= apilevelMax) return true;
        else return false;
    }

    public static boolean matchAppversionMin(int appversionMin) {
        if (appversionMin == -1)
            return true;
        if (BuildConfig.VERSION_CODE >= appversionMin) return true;
        else return false;
    }

    public static boolean matchAppversionMax(int appversionMax) {
        if (appversionMax == -1)
            return true;
        if (BuildConfig.VERSION_CODE <= appversionMax) return true;
        else return false;
    }

    public static boolean matchDateMin(int dateMin) {
        if (dateMin == -1)
            return true;
        if (System.currentTimeMillis() >= dateMin) return true;
        else return false;
    }

    public static boolean matchDateMax(int dateMax) {
        if (dateMax == -1)
            return true;
        if (System.currentTimeMillis() <= dateMax) return true;
        else return false;
    }

    public static boolean matchBuildType(String buildType) {
        if (buildType == null)
            return true;
        if (BuildConfig.BUILD_TYPE == buildType) return true;
        else return false;
    }

    /**
     * Match any one of the devices
     * A device match can match a) only the manufacturer or b) only the model or c) both
     * @param devices
     * @return
     */
    public static boolean matchDevice(List<Device> devices) {
        // no device rule specified, return true
        if(devices == null)
            return true;
        boolean matched = false;
        for (Device device : devices) {
            if (device.getManufacturer() == null && device.getModel() == null) {
                continue;
            }
            if (device.getManufacturer() != null) {
                // if the manufacturer does not match, continue
                if (!device.getManufacturer().equals(Build.MANUFACTURER)) {
                    continue;
                }
            }
            if (device.getModel() != null) {
                // if the model does not match, continue
                if (!device.getModel().equals(DeviceNameGenerator.getModel())) {
                    continue;
                }
            }
            // match found
            matched = true;
            break;
        }
        return matched;
    }
}
