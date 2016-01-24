package cc.soham.toggle.objects;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Config {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("features")
    @Expose
    private List<Feature> features = new ArrayList<Feature>();

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
     *     The features
     */
    public List<Feature> getFeatures() {
        return features;
    }

    /**
     * 
     * @param features
     *     The features
     */
    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public Config(String name, List<Feature> features) {
        this.name = name;
        this.features = features;
    }
}
