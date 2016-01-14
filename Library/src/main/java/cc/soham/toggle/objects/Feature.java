package cc.soham.toggle.objects;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Feature {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("enabled")
    @Expose
    private Boolean enabled;
    @SerializedName("default")
    @Expose
    private String _default;
    @SerializedName("rules")
    @Expose
    private List<Rule> rules = new ArrayList<Rule>();

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

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
     *     The _default
     */
    public String getDefault() {
        return _default;
    }

    /**
     * 
     * @param _default
     *     The default
     */
    public void setDefault(String _default) {
        this._default = _default;
    }

    /**
     * 
     * @return
     *     The rules
     */
    public List<Rule> getRules() {
        return rules;
    }

    /**
     * 
     * @param rules
     *     The rules
     */
    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

}
