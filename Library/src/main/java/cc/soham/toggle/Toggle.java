package cc.soham.toggle;

import android.content.Context;
import android.support.annotation.NonNull;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import java.net.URL;

import cc.soham.toggle.callbacks.GetConfigCallback;
import cc.soham.toggle.network.CheckLatestAsyncTask;
import cc.soham.toggle.network.FeatureCheckResponse;
import cc.soham.toggle.network.GetConfigAsyncTask;
import cc.soham.toggle.objects.Feature;
import cc.soham.toggle.objects.Product;
import cc.soham.toggle.objects.Rule;

/**
 * Created by sohammondal on 14/01/16.
 */
public class Toggle {
    // TODO: write unit tests for all cases in Toggle (all scenarios that were just tested in sample)
    // TODO: remove the discrepancies in "state":"disabled" and "enabled": true in the json
    // TODO: explore the Toggle.with(context).getConfig(url) API STYLE
    // TODO: explore the Toggle.with(context).check("video").getLatest().defaultState(State.ENABLED).start(new Callback...)
    // TODO: expand the samples to cover different styles of Toggling
    // TODO: improve documentation
    // TODO: check and improve all API calls

    public enum State {
        ENABLED,
        DISABLED
    }

    public enum SourceType {
        STRING,
        JSONOBJECT,
        PRODUCT,
        URL
    }

    private Context context;

    public Toggle(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null.");
        }
        this.context = context.getApplicationContext();
    }

    public static final String ENABLED = "enabled";
    public static final String DISABLED = "disabled";

    static volatile Toggle singleton;

    public static final String PRODUCT_KEY = "toggle_productKey";

    public static void init(final Context context) throws Exception {
        if (singleton == null) {
            Reservoir.init(context.getApplicationContext(), 20000);
            synchronized (Toggle.class) {
                singleton = new Toggle(context);
            }
        }
    }

    public static void init(final Context context, String product) throws Exception {
        init(context);
        getConfig(product);
    }

    public static void init(final Context context, JsonElement product) throws Exception {
        init(context);
        getConfig(product);
    }

    public static void init(final Context context, Product product) throws Exception {
        init(context);
        getConfig(product);
    }

    public static void init(final Context context, URL productUrl) throws Exception {
        init(context);
        getConfig(productUrl);
    }

    public static void init(final Context context, URL productUrl, GetConfigCallback getConfigCallback) throws Exception {
        init(context);
        getConfig(productUrl, getConfigCallback);
    }

    public static void getConfig(String productInString) {
        singleton.setSourceType(SourceType.STRING);
        // store source
        PersistUtils.storeSourceType(singleton.getContext(), SourceType.STRING);
        // convert from string to product
        Product product = convertStringToProduct(productInString);
        // store product
        PersistUtils.storeProduct(product);
    }

    public static void getConfig(JsonElement productInJson) {
        singleton.setSourceType(SourceType.JSONOBJECT);
        // store source
        PersistUtils.storeSourceType(singleton.getContext(), SourceType.JSONOBJECT);
        // convert from json to product
        Product product = convertJSONObjectToProduct(productInJson);
        // store product
        PersistUtils.storeProduct(product);
    }

    public static void getConfig(Product product) {
        singleton.setSourceType(SourceType.PRODUCT);
        // store source
        PersistUtils.storeSourceType(singleton.getContext(), SourceType.PRODUCT);
        // store product
        PersistUtils.storeProduct(product);
    }

    public static void getConfig(URL productUrl) {
        getConfig(productUrl, null);
    }

    public static void getConfig(URL productUrl, GetConfigCallback getConfigCallback) {
        singleton.setSourceType(SourceType.URL);
        // store source
        PersistUtils.storeSourceType(singleton.getContext(), SourceType.URL);
        PersistUtils.storeSourceURL(singleton.getContext(), productUrl);
        // make the network request and store the results
        GetConfigAsyncTask.start(productUrl.toExternalForm(), getConfigCallback);
    }

    public static FeatureCheckRequest.Builder check(String featureName) {
        return new FeatureCheckRequest.Builder(singleton, featureName);
    }

    /**
     * Converts String to Product
     *
     * @param productInString
     * @return
     */
    public static Product convertStringToProduct(String productInString) throws JsonSyntaxException {
        return new Gson().fromJson(productInString, Product.class);
    }

    /**
     * Converts JSON to Product
     *
     * @param productInJson
     * @return
     */
    public static Product convertJSONObjectToProduct(JsonElement productInJson) {
        return new Gson().fromJson(productInJson, Product.class);
    }

    // non singleton methods

    private SourceType sourceType;

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    private enum ResponseDecision {
        RESPONSE_UNDECIDED,
        RESPONSE_ENABLED,
        RESPONSE_DISABLED;

        private String metadata;

        public void setMetadata(String metadata) {
            this.metadata = metadata;
        }

        public String getMetadata() {
            return metadata;
        }
    }

    /**
     * Handles a featureCheckRequest
     * Called once a check is initiated for a given feature (name)
     *
     * @param featureCheckRequest
     */
    public void handleFeatureCheckRequest(final FeatureCheckRequest featureCheckRequest) {
        if (!sourceType.equals(SourceType.URL) || !featureCheckRequest.shouldGetLatest()) {
            getAndProcessCachedProduct(featureCheckRequest);
        } else {
            // make network featureCheckRequest
            makeNetworkFeatureCheckRequest(featureCheckRequest);
        }
    }

    public void getAndProcessCachedProduct(final FeatureCheckRequest featureCheckRequest) {
        // get cached or get default
        PersistUtils.getProduct(new ReservoirGetCallback<Product>() {
            @Override
            public void onSuccess(Product product) {
                // process the product
                FeatureCheckResponse featureCheckResponse = processProduct(product, featureCheckRequest);
                // make the callback
                featureCheckRequest.getCallback().onStatusChecked(featureCheckResponse.getFeatureName(), featureCheckResponse.isEnabled(), featureCheckResponse.getMetadata(), true);
            }

            @Override
            public void onFailure(Exception e) {
                // couldnt retrieve a stored product, send back the default response
                e.printStackTrace();
                featureCheckRequest.getCallback().onStatusChecked(featureCheckRequest.getFeatureName(), featureCheckRequest.getDefaultState() == State.ENABLED, PersistUtils.METADATA_DEFAULT, true);
            }
        });
    }

    public FeatureCheckResponse getAndProcessCachedProductSync(final FeatureCheckRequest featureCheckRequest) {
        try {
            Product product = PersistUtils.getProductSync();
            // process the product
            FeatureCheckResponse featureCheckResponse = processProduct(product, featureCheckRequest);
            // make the callback
            featureCheckRequest.getCallback().onStatusChecked(featureCheckResponse.getFeatureName(), featureCheckResponse.isEnabled(), featureCheckResponse.getMetadata(), true);
            return featureCheckResponse;
        } catch (Exception exception) {
            exception.printStackTrace();
            return new FeatureCheckResponse(featureCheckRequest.getFeatureName(), featureCheckRequest.getDefaultState() == State.ENABLED, PersistUtils.METADATA_DEFAULT, true);
        }
    }

    public FeatureCheckResponse processProduct(Product product, FeatureCheckRequest featureCheckRequest) {
        for (Feature feature : product.getFeatures()) {
            // find the given feature in the received Product
            if (feature.getName().equals(featureCheckRequest.getFeatureName())) {
                ResponseDecision responseDecision = handleFeature(feature);
                // if there is a decisive decision (either enabled or disabled) initiate the callback and break
                if (responseDecision.equals(ResponseDecision.RESPONSE_ENABLED) || responseDecision.equals(ResponseDecision.RESPONSE_DISABLED)) {
                    return new FeatureCheckResponse(featureCheckRequest.getFeatureName(), responseDecision.equals(ResponseDecision.RESPONSE_ENABLED), responseDecision.getMetadata());
                }
            }
        }
        return new FeatureCheckResponse(featureCheckRequest.getFeatureName(), featureCheckRequest.getDefaultState() == State.ENABLED, PersistUtils.METADATA_DEFAULT);
    }

    /**
     * makes a network request for the given params
     */
    public void makeNetworkFeatureCheckRequest(final FeatureCheckRequest featureCheckRequest) {
        CheckLatestAsyncTask.start(featureCheckRequest);
    }

    /**
     * Get the result of handling a given feature
     * (when a match between a requested feature and the store feature is found)
     *
     * @param feature
     * @return
     */
    private ResponseDecision handleFeature(final Feature feature) {
        if (feature.getState() == null) {
            // state is null, so we can check the rules
            for (Rule rule : feature.getRules()) {
                if (RuleMatcher.matchRule(rule)) {
                    // if a rule is matched
                    // return an enum which contains
                    // a) whether we should enable/disable the feature
                    // b) the metadata
                    return getRuleMatchedResponseDecision(rule);
                }
            }
            // no rule match, return the default state of the feature
            return getDefaultResponseDecision(feature);
        } else {
            // if state is not null, that means we need to just set this feature to 'that' state and ignore all rules
            return getStatePoweredResponseDecision(feature);
        }
    }

    @NonNull
    private ResponseDecision getRuleMatchedResponseDecision(Rule rule) {
        ResponseDecision responseDecision;
        if (rule.getEnabled()) {
            responseDecision = ResponseDecision.RESPONSE_ENABLED;
        } else {
            responseDecision = ResponseDecision.RESPONSE_DISABLED;
        }
        responseDecision.setMetadata(rule.getMetadata());
        return responseDecision;
    }

    @NonNull
    private ResponseDecision getStatePoweredResponseDecision(Feature feature) {
        if (feature.getState().equalsIgnoreCase(ENABLED)) {
            return ResponseDecision.RESPONSE_ENABLED;
        } else {
            return ResponseDecision.RESPONSE_DISABLED;
        }
    }

    @NonNull
    private ResponseDecision getDefaultResponseDecision(Feature feature) {
        if(feature.getDefault() == null) {
            return ResponseDecision.RESPONSE_ENABLED;
        }
        if (feature.getDefault().equals(ENABLED)) {
            return ResponseDecision.RESPONSE_ENABLED;
        } else {
            return ResponseDecision.RESPONSE_DISABLED;
        }
    }

    public Context getContext() {
        return context;
    }
}