package cc.soham.toggle.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Value {

    @SerializedName("appversion")
    @Expose
    private Integer appversion;
    @SerializedName("buildtype")
    @Expose
    private String buildtype;

    /**
     * 
     * @return
     *     The appversion
     */
    public Integer getAppversion() {
        return appversion;
    }

    /**
     * 
     * @param appversion
     *     The appversion
     */
    public void setAppversion(Integer appversion) {
        this.appversion = appversion;
    }

    /**
     * 
     * @return
     *     The buildtype
     */
    public String getBuildtype() {
        return buildtype;
    }

    /**
     * 
     * @param buildtype
     *     The buildtype
     */
    public void setBuildtype(String buildtype) {
        this.buildtype = buildtype;
    }

}
