package cc.soham.toggle.network;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import cc.soham.toggle.ConversionUtils;
import cc.soham.toggle.PersistUtils;
import cc.soham.toggle.Toggle;
import cc.soham.toggle.callbacks.SetConfigCallback;
import cc.soham.toggle.objects.Config;

/**
 * An {@link AsyncTask} that gets the latest config from the network
 * TODO: use OkHTTP (provided) in the future
 */
public class SetConfigAsyncTask extends AsyncTask<Void, Void, SetConfigResponse> {
    final String url;
    final SetConfigCallback setConfigCallback;

    public SetConfigAsyncTask(String url, SetConfigCallback setConfigCallback) {
        this.url = url;
        this.setConfigCallback = setConfigCallback;
    }

    /**
     * Attept to download and parse the latest config file
     * Also returns cached response wherever the network is not available
     * @param params
     * @return
     */
    @Override
    protected SetConfigResponse doInBackground(Void... params) {
        return getSetConfigResponse(url);
    }

    /**
     * Iniate a {@link SetConfigCallback} for the given config {@link SetConfigResponse} received from the server
     * @param setConfigResponse
     */
    @Override
    protected void onPostExecute(SetConfigResponse setConfigResponse) {
        NetworkOperations.initiateCallback(setConfigResponse, setConfigCallback);
    }

    /**
     * Generates a {@link SetConfigResponse} by downloading and parsing a config file
     * @param url The url for the config file
     * @return the {@link SetConfigResponse} for the given url
     */
    @Nullable
    private static SetConfigResponse getSetConfigResponse(String url) {
        try {
            // make network request to receive response
            String response = NetworkOperations.downloadUrl(url);
            // convert string to config
            Config config = ConversionUtils.convertStringToConfig(response);
            // store config
            Toggle.storeConfigInMem(config);
            PersistUtils.storeConfig(config);
            return new SetConfigResponse(config);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        SetConfigResponse setConfigResponse = null;
        // in case of any error, get the Config locally if possible
        try {
            return new SetConfigResponse(PersistUtils.getConfigSync(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return setConfigResponse;
    }

    /**
     * A static helper method to initiate a {@link SetConfigAsyncTask} call
     * Gets the latest config and initiates a callback (optional)
     * @param url
     * @param setConfigCallback
     */
    public static void start(String url, SetConfigCallback setConfigCallback) {
        if (url == null) {
            throw new IllegalStateException("Please pass a valid url");
        }
        new SetConfigAsyncTask(url, setConfigCallback).execute();
    }
}
