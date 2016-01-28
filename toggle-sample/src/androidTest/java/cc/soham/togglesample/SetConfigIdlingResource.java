package cc.soham.togglesample;

import android.provider.Settings;
import android.support.test.espresso.IdlingResource;
import android.view.View;

import cc.soham.toggle.objects.Config;

/**
 * Created by sohammondal on 27/01/16.
 */
public class SetConfigIdlingResource implements IdlingResource {
    ResourceCallback resourceCallback;
    SampleNetworkActivity sampleNetworkActivity;

    public SetConfigIdlingResource(SampleNetworkActivity sampleNetworkActivity) {
        this.sampleNetworkActivity = sampleNetworkActivity;
    }

    @Override
    public String getName() {
        return "setConfig";
    }

    @Override
    public boolean isIdleNow() {
        if(sampleNetworkActivity.progressBar.getVisibility() != View.VISIBLE) {
            resourceCallback.onTransitionToIdle();
            return true;
        }
        return false;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }
}
