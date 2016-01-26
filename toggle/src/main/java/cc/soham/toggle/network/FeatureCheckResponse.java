package cc.soham.toggle.network;

/**
 * Created by sohammondal on 20/01/16.
 */
public class FeatureCheckResponse {
    public String featureName;
    public String state;
    public String metadata;
    public boolean cached = true;

    public FeatureCheckResponse(String featureName, String state, String metadata) {
        this.featureName = featureName;
        this.state = state;
        this.metadata = metadata;
    }

    public FeatureCheckResponse(String featureName, String state, String metadata, boolean cached) {
        this.featureName = featureName;
        this.state = state;
        this.metadata = metadata;
        this.cached = cached;
    }
}
