package cc.soham.toggle.network;

import android.os.AsyncTask;

import cc.soham.toggle.ConversionUtils;
import cc.soham.toggle.PersistUtils;
import cc.soham.toggle.Toggle;
import cc.soham.toggle.callbacks.SetConfigCallback;
import cc.soham.toggle.objects.Config;

/**
 * Created by sohammondal on 26/01/16.
 */
public class SimpleConversionAndCallbackAsyncTask extends AsyncTask<Void, Void, SetConfigResponse> {
    final String configInString;
    final SetConfigCallback setConfigCallback;

    public SimpleConversionAndCallbackAsyncTask(String configInString, SetConfigCallback setConfigCallback) {
        this.configInString = configInString;
        this.setConfigCallback = setConfigCallback;
    }

    @Override
    protected SetConfigResponse doInBackground(Void... params) {
        Config config = ConversionUtils.convertStringToConfig(configInString);
        Toggle.storeConfigInMem(config);
        PersistUtils.storeConfig(config);
        return new SetConfigResponse(config);
    }

    @Override
    protected void onPostExecute(SetConfigResponse setConfigResponse) {
        NetworkUtils.initiateCallback(setConfigResponse, setConfigCallback);
    }

    public static void handle(final String configInString, final SetConfigCallback setConfigCallback) {
        new SimpleConversionAndCallbackAsyncTask(configInString, setConfigCallback).execute();
    }
}
