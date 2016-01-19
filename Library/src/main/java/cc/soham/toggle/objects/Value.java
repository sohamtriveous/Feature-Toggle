package cc.soham.toggle.objects;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Value {

    @SerializedName("apilevel_min")
    @Expose
    private Integer apilevelMin = -1;
    @SerializedName("apilevel_max")
    @Expose
    private Integer apilevelMax = -1;
    @SerializedName("appversion_min")
    @Expose
    private Integer appversionMin = -1;
    @SerializedName("appversion_max")
    @Expose
    private Integer appversionMax = -1;
    @SerializedName("date_min")
    @Expose
    private Long dateMin = -1L;
    @SerializedName("date_max")
    @Expose
    private Long dateMax = -1L;
    @SerializedName("buildtype")
    @Expose
    private String buildtype = null;
    @SerializedName("device")
    @Expose
    private List<Device> device = new ArrayList<>();

    /**
     * @return The apilevelMin
     */
    public Integer getApilevelMin() {
        return apilevelMin;
    }

    /**
     * @param apilevelMin The apilevel_min
     */
    public void setApilevelMin(Integer apilevelMin) {
        this.apilevelMin = apilevelMin;
    }

    /**
     * @return The apilevelMax
     */
    public Integer getApilevelMax() {
        return apilevelMax;
    }

    /**
     * @param apilevelMax The apilevel_max
     */
    public void setApilevelMax(Integer apilevelMax) {
        this.apilevelMax = apilevelMax;
    }

    /**
     * @return The appversionMin
     */
    public Integer getAppversionMin() {
        return appversionMin;
    }

    /**
     * @param appversionMin The appversion_min
     */
    public void setAppversionMin(Integer appversionMin) {
        this.appversionMin = appversionMin;
    }

    /**
     * @return The appversionMax
     */
    public Integer getAppversionMax() {
        return appversionMax;
    }

    /**
     * @param appversionMax The appversion_max
     */
    public void setAppversionMax(Integer appversionMax) {
        this.appversionMax = appversionMax;
    }

    /**
     * @return The dateMin
     */
    public Long getDateMin() {
        return dateMin;
    }

    /**
     * @param dateMin The date_min
     */
    public void setDateMin(Long dateMin) {
        this.dateMin = dateMin;
    }

    /**
     * @return The dateMax
     */
    public Long getDateMax() {
        return dateMax;
    }

    /**
     * @param dateMax The date_max
     */
    public void setDateMax(Long dateMax) {
        this.dateMax = dateMax;
    }

    /**
     * @return The buildtype
     */
    public String getBuildtype() {
        return buildtype;
    }

    /**
     * @param buildtype The buildtype
     */
    public void setBuildtype(String buildtype) {
        this.buildtype = buildtype;
    }

    /**
     * @return The device
     */
    public List<Device> getDevice() {
        return device;
    }

    /**
     * @param device The device
     */
    public void setDevice(List<Device> device) {
        this.device = device;
    }

    public Value(Integer apilevelMin, Integer apilevelMax, Integer appversionMin, Integer appversionMax, Long dateMin, Long dateMax, String buildtype, List<Device> device) {
        this.apilevelMin = apilevelMin;
        this.apilevelMax = apilevelMax;
        this.appversionMin = appversionMin;
        this.appversionMax = appversionMax;
        this.dateMin = dateMin;
        this.dateMax = dateMax;
        this.buildtype = buildtype;
        this.device = device;
    }
}
