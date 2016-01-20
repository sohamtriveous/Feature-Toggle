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
        if(checkLatestParams == null)
            return null;
        String url = Toggle.getSourceUrl(checkLatestParams.featureCheckRequest.getToggle().getContext());
        String response = null;
        Product product = null;
        FeatureCheckResponse result = null;
        try {
            // make network request to receive response
            response = NetworkOperations.downloadUrl(url);
            if (response == null)
                return checkLatestParams.getFeatureCheckRequest().getToggle().getAndProcessCachedProductSync(checkLatestParams.getFeatureCheckRequest());
            // convert string to product
            product = Toggle.convertStringToProduct(response);
            // store product
            Toggle.storeProduct(product);
            // process the resultant product
            result = checkLatestParams.featureCheckRequest.getToggle().processProduct(product, checkLatestParams.featureCheckRequest);
            result.setCached(false);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        return checkLatestParams.getFeatureCheckRequest().getToggle().getAndProcessCachedProductSync(checkLatestParams.getFeatureCheckRequest());
    }


    @Override
    protected void onPostExecute(final FeatureCheckResponse featureCheckResponse) {
        // make the callback if configured
        if (checkLatestParams != null && checkLatestParams.featureCheckRequest.getCallback() != null) {
            if(featureCheckResponse != null) {
                checkLatestParams.featureCheckRequest.getCallback().onStatusChecked(featureCheckResponse.getFeatureName(), featureCheckResponse.isEnabled(), featureCheckResponse.getMetadata(), false);
            } else {
                checkLatestParams.featureCheckRequest.getCallback().onStatusChecked(checkLatestParams.featureCheckRequest.getFeatureName(), checkLatestParams.featureCheckRequest.getDefaultState() == Toggle.State.ENABLED, Toggle.METADATA_DEFAULT, true);
            }
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
