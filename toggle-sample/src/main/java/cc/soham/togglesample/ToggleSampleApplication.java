package cc.soham.togglesample;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by sohammondal on 07/03/16.
 */
public class ToggleSampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
