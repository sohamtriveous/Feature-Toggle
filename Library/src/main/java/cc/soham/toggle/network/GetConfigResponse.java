package cc.soham.toggle.network;

import cc.soham.toggle.objects.Config;

/**
 * Response of {@link GetConfigAsyncTask}
 */
public class GetConfigResponse {
    Config config;
    boolean cached = false;

    public GetConfigResponse(Config config) {
        this.config = config;
    }

    public GetConfigResponse(Config config, boolean cached) {
        this.config = config;
        this.cached = cached;
    }
}
