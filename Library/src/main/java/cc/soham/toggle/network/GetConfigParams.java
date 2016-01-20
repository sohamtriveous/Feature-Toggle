package cc.soham.toggle.network;

import cc.soham.toggle.callbacks.GetConfigCallback;

/**
 * Created by sohammondal on 20/01/16.
 */
public class GetConfigParams {
    final String url;
    final GetConfigCallback getConfigCallback;

    public GetConfigParams(String url, GetConfigCallback getConfigCallback) {
        this.url = url;
        this.getConfigCallback = getConfigCallback;
    }

    public String getUrl() {
        return url;
    }

    public GetConfigCallback getConfigCallback() {
        return getConfigCallback;
    }
}
