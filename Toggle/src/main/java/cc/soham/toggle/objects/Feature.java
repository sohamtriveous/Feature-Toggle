package cc.soham.toggle.objects;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Feature {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("state")
    @Expose
    private String state;
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

    public Feature(String name, String state, String _default, List<Rule> rules) {
        this.name = name;
        this.state = state;
        this._default = _default;
        this.rules = rules;
    }
}