package cc.soham.toggle.network;

/**
 * Created by sohammondal on 20/01/16.
 */
public class FeatureCheckResponse {
    String featureName;
    String state;
    String metadata;
    boolean cached = true;

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

    public String getFeatureName() {
        return featureName;
    }

    public String getMetadata() {
        return metadata;
    }

    public boolean isCached() {
        return cached;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
