package cc.soham.toggle.network;

/**
 * Created by sohammondal on 20/01/16.
 */
public class FeatureCheckResponse {
    public String featureName;
    public String state;
    public String featureMetadata;
    public String ruleMetadata;
    public boolean cached = true;

    public FeatureCheckResponse(String featureName, String state, String featureMetadata, String ruleMetadata) {
        this.featureName = featureName;
        this.state = state;
        this.featureMetadata = featureMetadata;
        this.ruleMetadata = ruleMetadata;
    }

    public FeatureCheckResponse(String featureName, String state, String featureMetadata, String ruleMetadata, boolean cached) {
        this.featureName = featureName;
        this.state = state;
        this.featureMetadata = featureMetadata;
        this.ruleMetadata = ruleMetadata;
        this.cached = cached;
    }
}
