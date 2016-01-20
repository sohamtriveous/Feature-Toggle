package cc.soham.toggle.network;

/**
 * Created by sohammondal on 20/01/16.
 */
public class FeatureCheckResponse {
    String featureName;
    boolean enabled;
    String metadata;
    boolean cached = true;

    public FeatureCheckResponse(String featureName, boolean enabled, String metadata) {
        this.featureName = featureName;
        this.enabled = enabled;
        this.metadata = metadata;
    }

    public FeatureCheckResponse(String featureName, boolean enabled, String metadata, boolean cached) {
        this.featureName = featureName;
        this.enabled = enabled;
        this.metadata = metadata;
        this.cached = cached;
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

    public boolean isCached() {
        return cached;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }
}
