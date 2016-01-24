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
     * This is after the feature name has been matched between the FeatureCheckRequest and a stored
     * Config Feature
     *
     * @param rule
     * @return
     */
    public static boolean matchRule(Rule rule) {
        Value value = rule.getValue();
        // match all the rule values
        if (matchApilevelMin(value.getApilevelMin()) &&
                matchApilevelMax(value.getApilevelMax()) &&
                matchAppversionMin(value.getAppversionMin()) &&
                matchAppversionMax(value.getAppversionMax()) &&
                matchDateMin(value.getDateMin()) &&
                matchDateMax(value.getDateMax()) &&
                matchBuildType(value.getBuildtype()) &&
                matchDevice(value.getDevice()))
            return true;
        else return false;
    }

    public static int getBuildVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    public static int getVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    public static String getBuildType() {
        return BuildConfig.BUILD_TYPE;
    }

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * Match minimum API level
     *
     * @param apilevelMin
     * @return
     */
    public static boolean matchApilevelMin(Integer apilevelMin) {
        if (apilevelMin == null)
            return true;
        if (getBuildVersion() >= apilevelMin) return true;
        else return false;
    }

    /**
     * Match max API level
     *
     * @param apilevelMax
     * @return
     */
    public static boolean matchApilevelMax(Integer apilevelMax) {
        if (apilevelMax == null)
            return true;
        if (getBuildVersion() <= apilevelMax) return true;
        else return false;
    }

    public static boolean matchAppversionMin(Integer appversionMin) {
        if (appversionMin == null)
            return true;
        if (getVersionCode() >= appversionMin) return true;
        else return false;
    }

    public static boolean matchAppversionMax(Integer appversionMax) {
        if (appversionMax == null)
            return true;
        if (getVersionCode() <= appversionMax) return true;
        else return false;
    }

    public static boolean matchDateMin(Long dateMin) {
        if (dateMin == null)
            return true;
        if (System.currentTimeMillis() >= dateMin) return true;
        else return false;
    }

    public static boolean matchDateMax(Long dateMax) {
        if (dateMax == null)
            return true;
        if (System.currentTimeMillis() <= dateMax) return true;
        else return false;
    }

    public static boolean matchBuildType(String buildType) {
        if (buildType == null)
            return true;
        if (getBuildType() == buildType) return true;
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
                if (!device.getManufacturer().equalsIgnoreCase(getManufacturer())) {
                    continue;
                }
            }
            if (device.getModel() != null) {
                // if the model does not match, continue
                if (!device.getModel().equalsIgnoreCase(DeviceModelUtils.getModel(getManufacturer(), getModel()))) {
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
