package cc.soham.toggle.network;

import android.os.AsyncTask;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import cc.soham.toggle.Toggle;
import cc.soham.toggle.callbacks.GetConfigCallback;
import cc.soham.toggle.objects.Product;

/**
 * Created by sohammondal on 19/01/16.
 */
public class GetConfigAsyncTask extends AsyncTask<GetConfigParams, Void, Product> {
    private GetConfigParams getConfigParams;
    boolean cached = false;

    @Override
    protected Product doInBackground(GetConfigParams... params) {
        this.getConfigParams = params[0];
        // get the url
        String url = getConfigParams.getUrl();
        String response = null;
        Product product = null;
        try {
            // make network request to receive response
            response = NetworkOperations.downloadUrl(url);
            // convert string to product
            product = Toggle.convertStringToProduct(response);
            // store product
            Toggle.storeProduct(product);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (product == null) {
            try {
                product = Toggle.getProductSync();
                cached = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return product;
    }

    @Override
    protected void onPostExecute(Product product) {
        // make the callback if configured
        if (getConfigParams != null && getConfigParams.getConfigCallback != null && product != null) {
            getConfigParams.getConfigCallback().onConfigReceived(product, cached);
        }
    }

    public static void start(String url, GetConfigCallback getConfigCallback) {
        new GetConfigAsyncTask().execute(new GetConfigParams(url, getConfigCallback));
    }
}
