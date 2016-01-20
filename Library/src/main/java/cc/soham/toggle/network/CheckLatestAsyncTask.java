package cc.soham.toggle.network;

import android.os.AsyncTask;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import cc.soham.toggle.FeatureCheckRequest;
import cc.soham.toggle.Toggle;
import cc.soham.toggle.objects.Product;

/**
 * Created by sohammondal on 19/01/16.
 */
public class CheckLatestAsyncTask extends AsyncTask<CheckLatestParams, Void, FeatureCheckResponse> {
    CheckLatestParams checkLatestParams;

    @Override
    protected FeatureCheckResponse doInBackground(CheckLatestParams... params) {
        this.checkLatestParams = params[0];
        String url = Toggle.getSourceUrl(checkLatestParams.featureCheckRequest.getToggle().getContext());
        String response = null;
        Product product = null;
        FeatureCheckResponse result = null;
        try {
            // make network request to receive response
            response = NetworkOperations.downloadUrl(url);
            // convert string to product
            product = Toggle.convertStringToProduct(response);
            // store product
            Toggle.storeProduct(product);
            // process the resultant product
            result = checkLatestParams.featureCheckRequest.getToggle().processProduct(product, checkLatestParams.featureCheckRequest);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(FeatureCheckResponse featureCheckResponse) {
        // make the callback if configured
        if (checkLatestParams != null && featureCheckResponse != null && checkLatestParams.featureCheckRequest.getCallback() != null) {
            checkLatestParams.featureCheckRequest.getCallback().onStatusChecked(featureCheckResponse.getFeatureName(), featureCheckResponse.isEnabled(), featureCheckResponse.getMetadata());
        }
    }

    /**
     * queue a check
     *
     * @param featureCheckRequest
     */
    public static void start(final FeatureCheckRequest featureCheckRequest) {
        new CheckLatestAsyncTask().execute(new CheckLatestParams(featureCheckRequest));
    }
}
