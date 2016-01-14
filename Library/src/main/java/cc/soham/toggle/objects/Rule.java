package cc.soham.toggle.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rule {

    @SerializedName("enabled")
    @Expose
    private Boolean enabled;
    @SerializedName("metadata")
    @Expose
    private String metadata;
    @SerializedName("value")
    @Expose
    private Value value;

    /**
     * 
     * @return
     *     The enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * 
     * @param enabled
     *     The enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 
     * @return
     *     The metadata
     */
    public String getMetadata() {
        return metadata;
    }

    /**
     * 
     * @param metadata
     *     The metadata
     */
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    /**
     * 
     * @return
     *     The value
     */
    public Value getValue() {
        return value;
    }

    /**
     * 
     * @param value
     *     The value
     */
    public void setValue(Value value) {
        this.value = value;
    }

}
