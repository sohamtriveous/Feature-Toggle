package cc.soham.toggle.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rule {

    @SerializedName("state")
    @Expose
    public String state;
    @SerializedName("metadata")
    @Expose
    public String metadata;
    @SerializedName("value")
    @Expose
    public Value value;

    public Rule(String state, String metadata, Value value) {
        this.state = state;
        this.metadata = metadata;
        this.value = value;
    }
}
