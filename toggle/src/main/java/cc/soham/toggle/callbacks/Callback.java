package cc.soham.toggle.callbacks;

import cc.soham.toggle.network.FeatureCheckResponse;

/**
 * Created by sohammondal on 14/01/16.
 */
public interface Callback {
    void onStatusChecked(FeatureCheckResponse featureCheckResponse);
}
