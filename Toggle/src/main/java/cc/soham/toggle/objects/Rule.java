package cc.soham.toggle.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rule {

    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("metadata")
    @Expose
    private String metadata;
    @SerializedName("value")
    @Expose
    private Value value;

    /**
     * 
     * @return
     *     The state
     */
    public String getState() {
        return state;
    }

    /**
     * 
     * @param state
     *     The state
     */
    public void setState(String state) {
        this.state = state;
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

    public Rule(String state, String metadata, Value value) {
        this.state = state;
        this.metadata = metadata;
        this.value = value;
    }
}
