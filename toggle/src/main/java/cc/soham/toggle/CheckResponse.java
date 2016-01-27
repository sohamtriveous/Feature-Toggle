package cc.soham.toggle;

/**
 * Created by sohammondal on 20/01/16.
 */
public class CheckResponse {
    public String featureName;
    public String state;
    public String featureMetadata;
    public String ruleMetadata;
    public boolean cached = true;

    public CheckResponse(String featureName, String state, String featureMetadata, String ruleMetadata) {
        this.featureName = featureName;
        this.state = state;
        this.featureMetadata = featureMetadata;
        this.ruleMetadata = ruleMetadata;
    }

    public CheckResponse(String featureName, String state, String featureMetadata, String ruleMetadata, boolean cached) {
        this.featureName = featureName;
        this.state = state;
        this.featureMetadata = featureMetadata;
        this.ruleMetadata = ruleMetadata;
        this.cached = cached;
    }
}
