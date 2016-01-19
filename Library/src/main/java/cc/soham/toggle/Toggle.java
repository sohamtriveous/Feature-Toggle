package cc.soham.toggle;

import android.content.Context;
import android.support.annotation.NonNull;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;

import org.json.JSONObject;

import java.net.URL;

import cc.soham.toggle.objects.Feature;
import cc.soham.toggle.objects.Product;
import cc.soham.toggle.objects.Rule;

/**
 * Created by sohammondal on 14/01/16.
 */
public class Toggle {
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

    private static final String ENABLED = "enabled";
    private static final String DISABLED = "disabled";

    static volatile Toggle singleton;

    private static final String PRODUCT_KEY = "productKey";

    public static void init(final Context context) throws Exception {
        Reservoir.init(context, 10000);
        if (singleton == null) {
            synchronized (Toggle.class) {
                singleton = new Toggle();
            }
        }
    }

    public static void init(final Context context, String product) throws Exception {
        init(context);
        getConfig(product);
    }

    public static void init(final Context context, JSONObject product) throws Exception {
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

    public static void getConfig(String productInString) {
        singleton.setSourceType(SourceType.STRING);
        // store source
        storeSourceType(SourceType.STRING);
        // convert from string to product
        Product product = convertStringToProduct(productInString);
        // store product
        storeProduct(product);
    }

    public static void getConfig(JSONObject productInJson) {
        singleton.setSourceType(SourceType.JSONOBJECT);
        // store source
        storeSourceType(SourceType.JSONOBJECT);
        // convert from json to product
        Product product = convertJSONObjectToProduct(productInJson);
        // store product
        storeProduct(product);
    }

    public static void getConfig(Product product) {
        singleton.setSourceType(SourceType.PRODUCT);
        // store source
        storeSourceType(SourceType.PRODUCT);
        // store product
        storeProduct(product);
    }

    public static void getConfig(URL productUrl) {
        singleton.setSourceType(SourceType.URL);
        // store source
        storeSourceType(SourceType.URL);
    }

    public static FeatureCheckRequest.Builder check(String featureName) {
        return new FeatureCheckRequest.Builder(singleton, featureName);
    }

    /**
     * TODO: Stores the source type in disk
     *
     * @param sourceType
     */
    private static void storeSourceType(SourceType sourceType) {

    }

    /**
     * TODO: Converts String to Product
     *
     * @param productInString
     * @return
     */
    private static Product convertStringToProduct(String productInString) {
        return null;
    }

    /**
     * TODO: Converts JSON to Product
     *
     * @param productInJson
     * @return
     */
    private static Product convertJSONObjectToProduct(JSONObject productInJson) {
        return null;
    }

    /**
     * Stores the product in disk
     *
     * @param product
     */
    private static void storeProduct(Product product) {
        Reservoir.putAsync(PRODUCT_KEY, product);
    }

    /**
     * Retrieves the product from disk
     *
     * @param productReservoirGetCallback
     */
    private static void getProduct(ReservoirGetCallback<Product> productReservoirGetCallback) {
        Reservoir.getAsync(PRODUCT_KEY, Product.class, productReservoirGetCallback);
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
        if (!featureCheckRequest.shouldGetLatest() || !sourceType.equals(SourceType.URL)) {
            // get cached or get default
            Reservoir.getAsync(PRODUCT_KEY, Product.class, new ReservoirGetCallback<Product>() {

                @Override
                public void onSuccess(Product object) {
                    for (Feature feature : object.getFeatures()) {
                        if (feature.getName().equals(featureCheckRequest.getFeatureName())) {
                            ResponseDecision responseDecision = handleFeature(featureCheckRequest, feature);
                            if (responseDecision.equals(ResponseDecision.RESPONSE_ENABLED) || responseDecision.equals(ResponseDecision.RESPONSE_DISABLED)) {
                                featureCheckRequest.getCallback().onStatusChecked(featureCheckRequest.getFeatureName(), responseDecision.equals(ResponseDecision.RESPONSE_ENABLED), responseDecision.getMetadata());
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    // couldnt retrieve a stored product, send back the default response
                    featureCheckRequest.getCallback().onStatusChecked(featureCheckRequest.getFeatureName(), featureCheckRequest.getDefaultState() == State.ENABLED, null);
                }
            });
        } else {
            // make network featureCheckRequest
        }
    }

    /**
     * Get the result of handling a given feature
     * (when a match between a requested feature and the store feature is found)
     *
     * @param featureCheckRequest
     * @param feature
     * @return
     */
    private ResponseDecision handleFeature(final FeatureCheckRequest featureCheckRequest, final Feature feature) {
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
        if (feature.getState() == ENABLED) {
            return ResponseDecision.RESPONSE_ENABLED;
        } else {
            return ResponseDecision.RESPONSE_DISABLED;
        }
    }

    @NonNull
    private ResponseDecision getDefaultResponseDecision(Feature feature) {
        if (feature.getDefault().equals(ENABLED)) {
            return ResponseDecision.RESPONSE_ENABLED;
        } else {
            return ResponseDecision.RESPONSE_DISABLED;
        }
    }

}