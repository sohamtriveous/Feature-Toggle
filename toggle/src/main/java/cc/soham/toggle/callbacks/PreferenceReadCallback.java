package cc.soham.toggle.callbacks;

import cc.soham.toggle.objects.Config;

/**
 * Created by sohammondal on 27/01/16.
 */
public interface PreferenceReadCallback {
    void onSuccess(Config config);
    void onFailure(Exception exception);
}
