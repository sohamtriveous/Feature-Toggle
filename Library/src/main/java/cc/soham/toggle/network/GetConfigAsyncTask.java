package cc.soham.toggle.network;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import cc.soham.toggle.Toggle;
import cc.soham.toggle.callbacks.GetConfigCallback;
import cc.soham.toggle.objects.Product;

/**
 * An {@link AsyncTask} that gets the latest config from the network
 * TODO: use OkHTTP (provided) in the future
 */
public class GetConfigAsyncTask extends AsyncTask<Void, Void, GetConfigResponse> {
    final String url;
    final GetConfigCallback getConfigCallback;

    public GetConfigAsyncTask(String url, GetConfigCallback getConfigCallback) {
        this.url = url;
        this.getConfigCallback = getConfigCallback;
    }

    /**
     * Attept to download and parse the latest config file
     * Also returns cached response wherever the network is not available
     * @param params
     * @return
     */
    @Override
    protected GetConfigResponse doInBackground(Void... params) {
        return getGetConfigResponse(url);
    }

    /**
     * Iniate a {@link GetConfigCallback} for the given config {@link GetConfigResponse} received from the server
     * @param getConfigResponse
     */
    @Override
    protected void onPostExecute(GetConfigResponse getConfigResponse) {
        // make the callback if configured
        if (getConfigCallback != null && getConfigResponse != null) {
            getConfigCallback.onConfigReceived(getConfigResponse.product, getConfigResponse.cached);
        }
    }

    /**
     * Generates a {@link GetConfigResponse} by downloading and parsing a config file
     * @param url The url for the config file
     * @return the {@link GetConfigResponse} for the given url
     */
    @Nullable
    private static GetConfigResponse getGetConfigResponse(String url) {
        try {
            // make network request to receive response
            String response = NetworkOperations.downloadUrl(url);
            // convert string to product
            Product product = Toggle.convertStringToProduct(response);
            // store product
            Toggle.storeProduct(product);
            return new GetConfigResponse(product);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        GetConfigResponse getConfigResponse = null;
        // in case of any error, get the Product locally if possible
        try {
            return new GetConfigResponse(Toggle.getProductSync(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getConfigResponse;
    }

    /**
     * A static helper method to initiate a {@link GetConfigAsyncTask} call
     * Gets the latest config and initiates a callback (optional)
     * @param url
     * @param getConfigCallback
     */
    public static void start(String url, GetConfigCallback getConfigCallback) {
        if (url == null) {
            throw new IllegalStateException("Please pass a valid url");
        }
        new GetConfigAsyncTask(url, getConfigCallback).execute();
    }
}
