package cc.soham.toggle.callbacks;

/**
 * Created by sohammondal on 14/01/16.
 */
public interface Callback {
    void onStatusChecked(String feature, String state, String metadata, boolean cached);
}
