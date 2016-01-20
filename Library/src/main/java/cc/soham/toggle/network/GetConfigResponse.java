package cc.soham.toggle.network;

import cc.soham.toggle.objects.Product;

/**
 * Response of {@link GetConfigAsyncTask}
 */
public class GetConfigResponse {
    Product product;
    boolean cached = false;

    public GetConfigResponse(Product product) {
        this.product = product;
    }

    public GetConfigResponse(Product product, boolean cached) {
        this.product = product;
        this.cached = cached;
    }
}
