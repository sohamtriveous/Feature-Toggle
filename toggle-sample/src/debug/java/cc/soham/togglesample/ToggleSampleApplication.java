package cc.soham.togglesample;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Toggle-Debug release Application
 */
public class ToggleSampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
