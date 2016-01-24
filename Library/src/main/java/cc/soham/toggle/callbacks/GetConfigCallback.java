package cc.soham.toggle.callbacks;

import java.net.URL;

import cc.soham.toggle.objects.Config;

/**
 * Callback triggered after a network getConfig call via {@link cc.soham.toggle.Toggle#getConfig(URL, GetConfigCallback)}
 * is made
 */
public interface GetConfigCallback {
    void onConfigReceived(Config config, boolean cached);
}
