package cc.soham.toggle.callbacks;

import java.net.URL;

import cc.soham.toggle.objects.Product;

/**
 * Callback triggered after a network getConfig call via {@link cc.soham.toggle.Toggle#getConfig(URL, GetConfigCallback)}
 * is made
 */
public interface GetConfigCallback {
    void onConfigReceived(Product product, boolean cached);
}
