package cc.soham.toggle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;
import com.google.gson.JsonElement;

import java.net.URL;

import cc.soham.toggle.callbacks.GetConfigCallback;
import cc.soham.toggle.enums.ResponseDecision;
import cc.soham.toggle.enums.SourceType;
import cc.soham.toggle.enums.State;
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
    // TODO: expand the samples to cover different styles of Toggling
    // TODO: improve documentation
    // TODO: check and improve all API calls

    public static final String ENABLED = "enabled";
    public static final String DISABLED = "disabled";

    @VisibleForTesting
    static volatile Toggle singleton;

    public static Toggle with(final Context context) {
        if (singleton == null) {
            try {
                Reservoir.init(context.getApplicationContext(), 20000);
                synchronized (Toggle.class) {
                    singleton = new Toggle(context);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return singleton;
    }

    private Context context;
    private SourceType sourceType;

    public Toggle(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null.");
        }
        this.context = context.getApplicationContext();
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public void getConfig(String productInString) {
        setSourceType(SourceType.STRING);
        // store source
        PersistUtils.storeSourceType(getContext(), SourceType.STRING);
        // convert from string to product
        Product product = ConversionUtils.convertStringToProduct(productInString);
        // store product
        PersistUtils.storeProduct(product);
    }

    public void getConfig(JsonElement productInJson) {
        setSourceType(SourceType.JSONOBJECT);
        // store source
        PersistUtils.storeSourceType(getContext(), SourceType.JSONOBJECT);
        // convert from json to product
        Product product = ConversionUtils.convertJSONObjectToProduct(productInJson);
        // store product
        PersistUtils.storeProduct(product);
    }

    public void getConfig(Product product) {
        setSourceType(SourceType.PRODUCT);
        // store source
        PersistUtils.storeSourceType(getContext(), SourceType.PRODUCT);
        // store product
        PersistUtils.storeProduct(product);
    }

    public void getConfig(URL productUrl) {
        getConfig(productUrl, null);
    }

    public void getConfig(URL productUrl, GetConfigCallback getConfigCallback) {
        setSourceType(SourceType.URL);
        // store source
        PersistUtils.storeSourceType(getContext(), SourceType.URL);
        PersistUtils.storeSourceURL(getContext(), productUrl);
        // make the network request and store the results
        GetConfigAsyncTask.start(productUrl.toExternalForm(), getConfigCallback);
    }

    public FeatureCheckRequest.Builder check(String featureName) {
        return new FeatureCheckRequest.Builder(singleton, featureName);
    }

    /**
     * Handles a featureCheckRequest
     * Called once a check is initiated for a given feature (name)
     *
     * @param featureCheckRequest
     */
    public void handleFeatureCheckRequest(final FeatureCheckRequest featureCheckRequest) {
        if (sourceType == null) {
            sourceType = PersistUtils.getSourceType(getContext());
        }
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
    @VisibleForTesting
    ResponseDecision getRuleMatchedResponseDecision(Rule rule) {
        // an enabled/disabled state is mandatory for a rule
        // (else we wouldn't know what to do once a rule is matched)
        if(rule.getEnabled() == null)
            throw new IllegalStateException("The state in a Rule cannot be empty");
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
    @VisibleForTesting
    ResponseDecision getStatePoweredResponseDecision(Feature feature) {
        // if state is null, we can't take decision on this
        if(feature.getState() == null)
            return ResponseDecision.RESPONSE_UNDECIDED;
        // if state is not null, use the overriding feature
        if (feature.getState().equalsIgnoreCase(DISABLED)) {
            return ResponseDecision.RESPONSE_DISABLED;
        } else {
            return ResponseDecision.RESPONSE_ENABLED;
        }
    }

    @NonNull
    @VisibleForTesting
    ResponseDecision getDefaultResponseDecision(Feature feature) {
        if (feature.getDefault() == null) {
            return ResponseDecision.RESPONSE_ENABLED;
        }
        if (feature.getDefault().equalsIgnoreCase(ENABLED)) {
            return ResponseDecision.RESPONSE_ENABLED;
        } else {
            return ResponseDecision.RESPONSE_DISABLED;
        }
    }

    public Context getContext() {
        return context;
    }
}