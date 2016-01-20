package cc.soham.toggle.network;

import cc.soham.toggle.FeatureCheckRequest;

/**
 * Created by sohammondal on 20/01/16.
 */
public class CheckLatestParams {
    final FeatureCheckRequest featureCheckRequest;

    public CheckLatestParams(FeatureCheckRequest featureCheckRequest) {
        this.featureCheckRequest = featureCheckRequest;
    }
}
