package cc.soham.toggle.callbacks;

/**
 * Created by sohammondal on 14/01/16.
 */
public interface ErrorCallback {
    void onError(Exception exception, String feature, boolean enabled, String metadata);
}
