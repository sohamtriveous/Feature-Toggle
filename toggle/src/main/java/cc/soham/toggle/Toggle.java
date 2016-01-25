package cc.soham.toggle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;
import com.google.gson.JsonElement;

import java.net.URL;

import cc.soham.toggle.callbacks.SetConfigCallback;
import cc.soham.toggle.enums.SourceType;
import cc.soham.toggle.network.CheckLatestAsyncTask;
import cc.soham.toggle.network.FeatureCheckResponse;
import cc.soham.toggle.network.SetConfigAsyncTask;
import cc.soham.toggle.objects.Feature;
import cc.soham.toggle.objects.Config;
import cc.soham.toggle.objects.ResponseDecisionMeta;
import cc.soham.toggle.objects.Rule;

/**
 * Created by sohammondal on 14/01/16.
 */
public class Toggle {
    // TODO: return the whole product in check
    // TODO: unit tests for the new memcache
    // TODO: store Config in memcache
    // TODO: add overall metadata
    // TODO: add okhttp implementation
    // TODO: improve documentation
    // TODO: generic check for all features (return Config)
    // TODO: check and improve all API calls
    // TODO: host on jcenter/maven on bintray

    public static final String DEFAULT_STATE = "default";
    public static final String ENABLED = "enabled";
    public static final String DISABLED = "disabled";

    @VisibleForTesting
    static volatile Toggle singleton;

    public static Toggle with(final Context context) {
        if (singleton == null) {
            try {
                Reservoir.init(context.getApplicationContext(), 20000);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            synchronized (Toggle.class) {
                singleton = new Toggle(context);
            }
        }
        return singleton;
    }

    private Context context;
    private SourceType sourceType;
    private Config config;

    public static void storeConfigInMem(Config config) {
        if (singleton != null) {
            singleton.config = config;
        }
    }

    public Toggle(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null.");
        }
        this.context = context.getApplicationContext();
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public void setConfig(String configInString) {
        setSourceType(SourceType.STRING);
        // store source
        PersistUtils.storeSourceType(getContext(), SourceType.STRING);
        // convert from string to config
        Config config = ConversionUtils.convertStringToConfig(configInString);
        // store config
        storeConfigInMem(config);
        PersistUtils.storeConfig(config);
    }

    public void setConfig(JsonElement configInJson) {
        setSourceType(SourceType.JSONOBJECT);
        // store source
        PersistUtils.storeSourceType(getContext(), SourceType.JSONOBJECT);
        // convert from json to config
        Config config = ConversionUtils.convertJSONObjectToConfig(configInJson);
        // store config
        storeConfigInMem(config);
        PersistUtils.storeConfig(config);
    }

    public void setConfig(Config config) {
        setSourceType(SourceType.CONFIG);
        // store source
        PersistUtils.storeSourceType(getContext(), SourceType.CONFIG);
        // store config
        storeConfigInMem(config);
        PersistUtils.storeConfig(config);
    }

    public void setConfig(URL configUrl) {
        setConfig(configUrl, null);
    }

    public void setConfig(URL configUrl, SetConfigCallback setConfigCallback) {
        setSourceType(SourceType.URL);
        // store source
        PersistUtils.storeSourceType(getContext(), SourceType.URL);
        PersistUtils.storeSourceURL(getContext(), configUrl);
        // make the network request and store the results
        SetConfigAsyncTask.start(configUrl.toExternalForm(), setConfigCallback);
    }

    public FeatureCheckRequest.Builder check(String featureName) {
        return new FeatureCheckRequest.Builder(singleton, featureName);
    }

    // core methods

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
            getAndProcessCachedConfig(featureCheckRequest);
        } else {
            // make network featureCheckRequest
            makeNetworkFeatureCheckRequest(featureCheckRequest);
        }
    }

    // TODO: need to unit test this again in light of mem cache
    public void getAndProcessCachedConfig(final FeatureCheckRequest featureCheckRequest) {
        if (config == null) {
            // get cached or get default
            PersistUtils.getConfig(new ReservoirGetCallback<Config>() {
                @Override
                public void onSuccess(Config config) {
                    // store in mem
                    Toggle.storeConfigInMem(config);
                    // call the success method
                    configRetrievedSuccess(config, featureCheckRequest);
                }

                @Override
                public void onFailure(Exception e) {
                    // couldnt retrieve a stored config, send back the default response
                    e.printStackTrace();
                    configRetrievedFailure(featureCheckRequest);
                }
            });
        } else {
            // call the success method
            configRetrievedSuccess(config, featureCheckRequest);
        }
    }

    private void configRetrievedSuccess(Config config, FeatureCheckRequest featureCheckRequest) {
        FeatureCheckResponse featureCheckResponse = processConfig(config, featureCheckRequest);
        // make the callback
        makeFeatureCheckCallback(featureCheckRequest, featureCheckResponse);
    }

    private void configRetrievedFailure(FeatureCheckRequest featureCheckRequest) {
        // send back a default response
        String state = DEFAULT_STATE;
        if (featureCheckRequest.getDefaultState() != null) {
            state = featureCheckRequest.getDefaultState();
        }
        featureCheckRequest.getCallback().onStatusChecked(featureCheckRequest.getFeatureName(), state, null, true);
    }

    public FeatureCheckResponse getAndProcessCachedConfigSync(final FeatureCheckRequest featureCheckRequest) {
        Config config = null;
        try {
            config = PersistUtils.getConfigSync();
        } catch (Exception exception) {
            exception.printStackTrace();
            return getExceptionFeatureCheckResponse(featureCheckRequest);
        }
        // check for null
        if (config == null) {
            if (featureCheckRequest.getDefaultState() == null) {
                throw new IllegalStateException("No configuration found (Config) and no default state configured in the state check");
            }
            return new FeatureCheckResponse(featureCheckRequest.getFeatureName(), featureCheckRequest.getDefaultState(), null, true);
        }
        // process the config
        FeatureCheckResponse featureCheckResponse = processConfig(config, featureCheckRequest);
        return featureCheckResponse;

    }

    @NonNull
    @VisibleForTesting
        // TODO: unit test getExceptionFeatureCheckResponse
    FeatureCheckResponse getExceptionFeatureCheckResponse(FeatureCheckRequest featureCheckRequest) {
        return new FeatureCheckResponse(featureCheckRequest.getFeatureName(), DEFAULT_STATE, null, true);
    }

    @VisibleForTesting
        // TODO: unit test makeFeatureCheckCallback
    void makeFeatureCheckCallback(FeatureCheckRequest featureCheckRequest, FeatureCheckResponse featureCheckResponse) {
        featureCheckRequest.getCallback().onStatusChecked(featureCheckResponse.getFeatureName(), featureCheckResponse.getState(), featureCheckResponse.getMetadata(), true);
    }

    /**
     * Code for enabling/disabling a feature based on a request ({@link FeatureCheckRequest}) and a config ({@link Config})
     *
     * @param config
     * @param featureCheckRequest
     * @return
     */
    public FeatureCheckResponse processConfig(Config config, FeatureCheckRequest featureCheckRequest) {
        for (Feature feature : config.getFeatures()) {
            // find the given feature in the received Config
            if (feature.getName().equals(featureCheckRequest.getFeatureName())) {
                ResponseDecisionMeta responseDecisionMeta = handleFeature(feature, featureCheckRequest);
                // if there is a decisive state (either enabled or disabled) initiate the callback and break
                return new FeatureCheckResponse(featureCheckRequest.getFeatureName(), responseDecisionMeta.state, responseDecisionMeta.metadata);
            }
        }
        // a) feature not found or b) no state could be made based on the config
        // check if there was no default state in the request, send enabled (default toggle)
        String state;
        if (featureCheckRequest.getDefaultState() == null) {
            state = DEFAULT_STATE;
        } else {
            state = featureCheckRequest.getDefaultState();
        }
        return new FeatureCheckResponse(featureCheckRequest.getFeatureName(), state, null, false);
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
     * @param featureCheckRequest
     * @return
     */
    @VisibleForTesting
    ResponseDecisionMeta handleFeature(final Feature feature, final FeatureCheckRequest featureCheckRequest) {
        if (feature.getState() == null) {
            if (feature.getRules() == null) {
                throw new IllegalStateException("You must have rules in case the feature does not have a base state");
            }
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
            return getDefaultResponseDecision(feature, featureCheckRequest);
        } else {
            // if state is not null, that means we need to just set this feature to 'that' state and ignore all rules
            return getStatePoweredResponseDecision(feature);
        }
    }

    @NonNull
    @VisibleForTesting
    ResponseDecisionMeta getRuleMatchedResponseDecision(Rule rule) {
        // an enabled/disabled state is mandatory for a rule
        // (else we wouldn't know what to do once a rule is matched)
        if (rule.getState() == null)
            throw new IllegalStateException("The state in a Rule cannot be empty");
        return new ResponseDecisionMeta(rule);
    }

    @NonNull
    @VisibleForTesting
    ResponseDecisionMeta getStatePoweredResponseDecision(Feature feature) {
        // if state is null, we can't take state on this
        if (feature.getState() == null)
            return new ResponseDecisionMeta(DEFAULT_STATE);
        // if state is not null, use the overriding feature
        return new ResponseDecisionMeta(feature.getState());
    }

    @NonNull
    @VisibleForTesting
    ResponseDecisionMeta getDefaultResponseDecision(final Feature feature, final FeatureCheckRequest featureCheckRequest) {
        // first preference in defaults is given to the local variable (provided while the Toggle.with().check().setDefault().start() call)
        if (featureCheckRequest.getDefaultState() != null) {
            return new ResponseDecisionMeta(featureCheckRequest.getDefaultState());
        }
        // in case no Default is provided in the feature (in the config), return enabled
        if (feature.getDefault() == null) {
            return new ResponseDecisionMeta(DEFAULT_STATE);
        }
        // so no local defaults but some default is provided in the config
        return new ResponseDecisionMeta(feature.getDefault());
    }

    public Context getContext() {
        return context;
    }
}