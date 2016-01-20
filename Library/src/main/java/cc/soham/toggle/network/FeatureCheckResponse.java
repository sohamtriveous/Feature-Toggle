package cc.soham.toggle.network;

/**
 * Created by sohammondal on 20/01/16.
 */
public class FeatureCheckResponse {
    String featureName;
    boolean enabled;
    String metadata;

    public FeatureCheckResponse(String featureName, boolean enabled, String metadata) {
        this.featureName = featureName;
        this.enabled = enabled;
        this.metadata = metadata;
    }

    public String getFeatureName() {
        return featureName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getMetadata() {
        return metadata;
    }
}
