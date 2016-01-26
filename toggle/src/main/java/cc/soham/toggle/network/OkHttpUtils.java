package cc.soham.toggle.network;

import java.io.IOException;

import cc.soham.toggle.callbacks.SetConfigCallback;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by sohammondal on 26/01/16.
 */
public class OkHttpUtils {
    private static OkHttpClient client;

    private static OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }

    public static boolean isOkHttpAvailable() {
        try {
            Class cls = Class.forName("okhttp3.OkHttpClient");
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * A static helper method to initiate an OkHttpClient call
     * Gets the latest config and initiates a callback (optional)
     *
     * @param url
     * @param setConfigCallback
     */
    public static void startSetConfig(final String url, final SetConfigCallback setConfigCallback) {
        if (url == null) {
            throw new IllegalStateException("Please pass a valid url");
        }

        Request request = new Request.Builder()
                .url(url)
                .build();

        getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                SimpleConversionAndCallbackAsyncTask.handle(response.body().string(), setConfigCallback);
            }
        });
    }
}
