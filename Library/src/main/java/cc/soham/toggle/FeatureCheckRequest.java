package cc.soham.toggle;

import cc.soham.toggle.callbacks.Callback;
import cc.soham.toggle.callbacks.ErrorCallback;

/**
 * Represents a feature check request
 * Called as a challenge to the config for the given feature name
 */
public class FeatureCheckRequest {
    // Required parameters
    private final Toggle toggle;
    private final String featureName;
    private final  Callback callback;

    // Optional parameters
    private final Toggle.State defaultState;
    private final boolean getLatest;
    private final ErrorCallback errorCallback;

    public static class Builder {
        // Required parameters
        private final Toggle toggle;
        private final String featureName;
        private Callback callback;

        // Optional parameters
        Toggle.State defaultState = Toggle.State.ENABLED;
        boolean getLatest;
        ErrorCallback errorCallback;

        public Builder(Toggle toggle, String featureName) {
            this.toggle = toggle;
            this.featureName = featureName;
        }

        public Builder defaultState(Toggle.State defaultState) {
            this.defaultState = defaultState;
            return this;
        }

        public Builder getLatest() {
            this.getLatest = true;
            return this;
        }

        public Builder onError(ErrorCallback errorCallback) {
            this.errorCallback = errorCallback;
            return this;
        }

        public FeatureCheckRequest launch(Callback callback) {
            this.callback = callback;
            return new FeatureCheckRequest(this);
        }
    }

    private FeatureCheckRequest(Builder builder) {
        this.toggle = builder.toggle;
        this.featureName = builder.featureName;
        this.callback = builder.callback;
        this.defaultState = builder.defaultState;
        this.getLatest = builder.getLatest;
        this.errorCallback = builder.errorCallback;
        toggle.handleFeatureCheckRequest(this);
    }

    public String getFeatureName() {
        return featureName;
    }

    public Callback getCallback() {
        return callback;
    }

    public Toggle.State getDefaultState() {
        return defaultState;
    }

    public boolean shouldGetLatest() {
        return getLatest;
    }

    public ErrorCallback getErrorCallback() {
        return errorCallback;
    }
}