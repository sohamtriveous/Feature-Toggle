package cc.soham.toggle.network;

import android.os.AsyncTask;

import cc.soham.toggle.ConversionUtils;
import cc.soham.toggle.FeatureCheckRequest;
import cc.soham.toggle.PersistUtils;
import cc.soham.toggle.Toggle;
import cc.soham.toggle.objects.Config;

/**
 * A simple AsyncTask to
 * - convert the string response of the {@link Config} to a Config
 * - store it in memory and disk
 * - initiate a callback on the UI thread
 */
public class SimpleConversionAndCheckCallbackAsyncTask extends AsyncTask<Void, Void, FeatureCheckResponse> {
    final String configInString;
    final FeatureCheckRequest featureCheckRequest;

    public SimpleConversionAndCheckCallbackAsyncTask(String configInString, FeatureCheckRequest featureCheckRequest) {
        this.configInString = configInString;
        this.featureCheckRequest = featureCheckRequest;
    }

    @Override
    protected FeatureCheckResponse doInBackground(Void... params) {
        // convert string to config
        Config config = ConversionUtils.convertStringToConfig(configInString);
        // store config
        Toggle.storeConfigInMem(config);
        PersistUtils.storeConfig(config);
        // process the resultant config
        FeatureCheckResponse result = featureCheckRequest.getToggle().processConfig(config, featureCheckRequest);
        // disable the cache flag since this is a live request
        result.setCached(false);
        return result;
    }

    @Override
    protected void onPostExecute(FeatureCheckResponse featureCheckResponse) {
        // initiate a callback on the UI thread
        NetworkUtils.initiateCallbackAfterCheck(featureCheckResponse, featureCheckRequest);
    }

    /**
     * Converts, stores and initates a callback for a given OkHttp3 response
     * @param configInString
     * @param featureCheckRequest
     */
    public static void handle(final String configInString, final FeatureCheckRequest featureCheckRequest) {
        new SimpleConversionAndCheckCallbackAsyncTask(configInString, featureCheckRequest).execute();
    }
}
