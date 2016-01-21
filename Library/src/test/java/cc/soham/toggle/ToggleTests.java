package cc.soham.toggle;

import android.content.Context;
import android.test.suitebuilder.annotation.SmallTest;
import android.text.TextUtils;

import com.anupcowkur.reservoir.Reservoir;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import cc.soham.toggle.enums.ResponseDecision;
import cc.soham.toggle.enums.State;
import cc.soham.toggle.network.FeatureCheckResponse;
import cc.soham.toggle.objects.Feature;
import cc.soham.toggle.objects.Product;
import cc.soham.toggle.objects.ResponseDecisionMeta;
import cc.soham.toggle.objects.Rule;
import cc.soham.toggle.objects.Value;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by sohammondal on 21/01/16.
 */
@SmallTest
@RunWith(PowerMockRunner.class)
@PrepareForTest({Reservoir.class, RuleMatcher.class, System.class})
public class ToggleTests {
    final String metadata = "myMetadata";

    @Mock
    Context context;

    @Before
    public void setup() {
    }

    @After
    public void tearDown() {

    }

    @Test
    public void checkSingletonSetup() {
        Toggle.singleton = null;
        PowerMockito.when(context.getApplicationContext()).thenReturn(context);
        PowerMockito.mockStatic(Reservoir.class);

        Toggle.with(context);

        // verify the Reservoir.init call
        PowerMockito.verifyStatic();
        try {
            Reservoir.init(context, 20000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // make sure that the singleton is not null
        assertThat(Toggle.singleton).isNotNull();
    }

    @Test
    public void getDefaultResponseDecision_noFeatureCheckRequestDefault_null_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", null, null, rules);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, null, false, null);

        ResponseDecisionMeta responseDecisionMeta = toggle.getDefaultResponseDecision(feature, featureCheckRequest);

        assertThat(responseDecisionMeta.responseDecision).isEqualTo(ResponseDecision.RESPONSE_ENABLED);
    }

    private void getApiValue(List<Rule> rules) {
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(true, metadata, value));
    }

    @Test
    public void getDefaultResponseDecision_noFeatureCheckRequestDefault_enabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", null, Toggle.ENABLED, rules);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, null, false, null);

        ResponseDecisionMeta responseDecisionMeta = toggle.getDefaultResponseDecision(feature, featureCheckRequest);

        assertThat(responseDecisionMeta.responseDecision).isEqualTo(ResponseDecision.RESPONSE_ENABLED);
    }

    @Test
    public void getDefaultResponseDecision_noFeatureCheckRequestDefault_disabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", null, Toggle.DISABLED, rules);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, null, false, null);

        ResponseDecisionMeta responseDecisionMeta = toggle.getDefaultResponseDecision(feature, featureCheckRequest);

        assertThat(responseDecisionMeta.responseDecision).isEqualTo(ResponseDecision.RESPONSE_DISABLED);
    }

    @Test
    public void getDefaultResponseDecision_FeatureCheckRequestDefault_disabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", null, Toggle.ENABLED, rules);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, State.DISABLED, false, null);

        ResponseDecisionMeta responseDecisionMeta = toggle.getDefaultResponseDecision(feature, featureCheckRequest);

        assertThat(responseDecisionMeta.responseDecision).isEqualTo(ResponseDecision.RESPONSE_DISABLED);
    }

    @Test
    public void getDefaultResponseDecision_FeatureCheckRequestDefault_enabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", null, Toggle.DISABLED, rules);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, State.ENABLED, false, null);

        ResponseDecisionMeta responseDecisionMeta = toggle.getDefaultResponseDecision(feature, featureCheckRequest);

        assertThat(responseDecisionMeta.responseDecision).isEqualTo(ResponseDecision.RESPONSE_ENABLED);
    }

    @Test
    public void getStatePoweredResponseDecision_enabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", Toggle.ENABLED, null, rules);

        ResponseDecisionMeta responseDecisionMeta = toggle.getStatePoweredResponseDecision(feature);

        assertThat(responseDecisionMeta.responseDecision).isEqualTo(ResponseDecision.RESPONSE_ENABLED);
    }

    @Test
    public void getStatePoweredResponseDecision_disabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", Toggle.DISABLED, null, rules);

        ResponseDecisionMeta responseDecisionMeta = toggle.getStatePoweredResponseDecision(feature);

        assertThat(responseDecisionMeta.responseDecision).isEqualTo(ResponseDecision.RESPONSE_DISABLED);
    }

    @Test
    public void getStatePoweredResponseDecision_null_returnsUndecided() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", null, null, rules);

        ResponseDecisionMeta responseDecisionMeta = toggle.getStatePoweredResponseDecision(feature);

        assertThat(responseDecisionMeta.responseDecision).isEqualTo(ResponseDecision.RESPONSE_UNDECIDED);
    }

    @Test
    public void getRuleMatchedResponseDecision_enabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        Value value = new Value(14, 16, null, null, null, null, null, null);
        Rule rule = new Rule(true, metadata, value);

        ResponseDecisionMeta responseDecisionMeta = toggle.getRuleMatchedResponseDecision(rule);

        // assert the state
        assertThat(responseDecisionMeta.responseDecision).isEqualTo(ResponseDecision.RESPONSE_ENABLED);
        // assert the metadata
        assertThat(responseDecisionMeta.metadata).isEqualTo(metadata);
    }

    @Test
    public void getRuleMatchedResponseDecision_disabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        Value value = new Value(14, 16, null, null, null, null, null, null);
        Rule rule = new Rule(false, metadata, value);

        ResponseDecisionMeta responseDecisionMeta = toggle.getRuleMatchedResponseDecision(rule);

        // assert the state
        assertThat(responseDecisionMeta.responseDecision).isEqualTo(ResponseDecision.RESPONSE_DISABLED);
        // assert the metadata
        assertThat(responseDecisionMeta.metadata).isEqualTo(metadata);
    }

    @Test(expected = IllegalStateException.class)
    public void getRuleMatchedResponseDecision_null_throwsException() {
        Toggle toggle = new Toggle(context);
        Value value = new Value(14, 16, null, null, null, null, null, null);
        Rule rule = new Rule(null, metadata, value);

        toggle.getRuleMatchedResponseDecision(rule);
    }

    /**
     * Handle Feature tests, a decision to : couple of things can happen
     * - State variable is found
     * - No state variable, rule matched and the correct response (enabled/disabled) is propagated properly
     * - No state variable, rule matched but the state in the rule is null, exception is thrown
     * - No rule is matched, but there is a default value
     * - No rule is matched, and there is no default value and there is no featureCheckRequest default value
     * - No rule is matched, and there is no default value and there is a featureCheckRequest default value
     */

    @Test
    public void handleFeature_stateNotNull_enabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", Toggle.ENABLED, null, rules);
        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, null, false, null);

        ResponseDecisionMeta responseDecisionMeta = toggle.handleFeature(feature, featureCheckRequest);
        // assert the state
        assertThat(responseDecisionMeta.responseDecision).isEqualTo(ResponseDecision.RESPONSE_ENABLED);
    }

    @Test
    public void handleFeature_stateNotNull_disabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", Toggle.DISABLED, null, rules);
        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, null, false, null);

        ResponseDecisionMeta responseDecisionMeta = toggle.handleFeature(feature, featureCheckRequest);
        // assert the state
        assertThat(responseDecisionMeta.responseDecision).isEqualTo(ResponseDecision.RESPONSE_DISABLED);
    }

    @Test
    public void handleFeature_stateNull_ruleMatched_enabledForRightAPILevel_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", null, Toggle.DISABLED, rules);
        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, null, false, null);

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(16);

        ResponseDecisionMeta responseDecisionMeta = toggle.handleFeature(feature, featureCheckRequest);
        // assert the state and metadata
        assertThat(responseDecisionMeta.responseDecision).isEqualTo(ResponseDecision.RESPONSE_ENABLED);
        assertThat(responseDecisionMeta.metadata).isEqualTo(metadata);
    }

    @Test
    public void handleFeature_stateNull_ruleMatched_disabledForRightAPILevel_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(false, metadata, value));
        Feature feature = new Feature("video", null, Toggle.ENABLED, rules);
        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, null, false, null);

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(16);

        ResponseDecisionMeta responseDecisionMeta = toggle.handleFeature(feature, featureCheckRequest);
        // assert the state and metadata
        assertThat(responseDecisionMeta.responseDecision).isEqualTo(ResponseDecision.RESPONSE_DISABLED);
        assertThat(responseDecisionMeta.metadata).isEqualTo(metadata);
    }

    @Test(expected = IllegalStateException.class)
    public void handleFeature_stateNull_ruleMatched_nullForRightAPILevel_throwsException() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(null, metadata, value));
        Feature feature = new Feature("video", null, Toggle.ENABLED, rules);
        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, null, false, null);

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(16);

        toggle.handleFeature(feature, featureCheckRequest);
    }

    @Test
    public void handleFeature_stateNull_ruleNotMatched_defaultEnabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(false, metadata, value));
        Feature feature = new Feature("video", null, Toggle.ENABLED, rules);
        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, null, false, null);

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        ResponseDecisionMeta responseDecisionMeta = toggle.handleFeature(feature, featureCheckRequest);
        // assert the state and metadata
        assertThat(responseDecisionMeta.responseDecision).isEqualTo(ResponseDecision.RESPONSE_ENABLED);
        assertThat(responseDecisionMeta.metadata).isNull();
    }

    @Test
    public void handleFeature_stateNull_ruleNotMatched_defaultDisabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(true, metadata, value));
        Feature feature = new Feature("video", null, Toggle.DISABLED, rules);
        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, null, false, null);

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        ResponseDecisionMeta responseDecisionMeta = toggle.handleFeature(feature, featureCheckRequest);
        // assert the state and metadata
        assertThat(responseDecisionMeta.responseDecision).isEqualTo(ResponseDecision.RESPONSE_DISABLED);
        assertThat(responseDecisionMeta.metadata).isNull();
    }

    @Test
    public void handleFeature_stateNull_ruleNotMatched_defaultNull_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(false, metadata, value));
        Feature feature = new Feature("video", null, null, rules);
        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, null, false, null);

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        ResponseDecisionMeta responseDecisionMeta = toggle.handleFeature(feature, featureCheckRequest);
        // assert the state and metadata
        assertThat(responseDecisionMeta.responseDecision).isEqualTo(ResponseDecision.RESPONSE_ENABLED);
        assertThat(responseDecisionMeta.metadata).isNull();
    }

    // check for no rules without a base state
    @Test(expected = IllegalStateException.class)
    public void handleFeature_stateNull_ruleNull_defaultNull_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        Feature feature = new Feature("video", null, null, null);
        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, null, false, null);

        toggle.handleFeature(feature, featureCheckRequest);
    }

    @Test
    public void handleFeature_stateNull_ruleNotMatched_defaultNull_featureCheckRequestDefault_enabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(false, metadata, value));
        Feature feature = new Feature("video", null, null, rules);
        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, State.ENABLED, false, null);

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        ResponseDecisionMeta responseDecisionMeta = toggle.handleFeature(feature, featureCheckRequest);
        // assert the state and metadata
        assertThat(responseDecisionMeta.responseDecision).isEqualTo(ResponseDecision.RESPONSE_ENABLED);
        assertThat(responseDecisionMeta.metadata).isNull();
    }

    @Test
    public void handleFeature_stateNull_ruleNotMatched_defaultNull_featureCheckRequestDefault_disabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(false, metadata, value));
        Feature feature = new Feature("video", null, null, rules);
        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, State.DISABLED, false, null);

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        ResponseDecisionMeta responseDecisionMeta = toggle.handleFeature(feature, featureCheckRequest);
        // assert the state and metadata
        assertThat(responseDecisionMeta.responseDecision).isEqualTo(ResponseDecision.RESPONSE_DISABLED);
        assertThat(responseDecisionMeta.metadata).isNull();
    }

    /**
     * Process product tests
     */

    // set 1: feature not present

    @Test
    public void processProduct_featureNotPresent_defaultDisabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(false, metadata, value));
        Feature featureVideo = new Feature("video", null, null, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Product product = new Product("myapp", features);

        String featureToBeSearched = "share";
        State defaultStateInRequest = State.DISABLED;

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processProduct(product, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(null);
        assertThat(featureCheckResponse.isCached()).isFalse();
        assertThat(featureCheckResponse.isEnabled()).isEqualTo(defaultStateInRequest == State.ENABLED);
    }

    @Test
    public void processProduct_featureNotPresent_defaultEnabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(false, metadata, value));
        Feature featureVideo = new Feature("video", null, null, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Product product = new Product("myapp", features);

        String featureToBeSearched = "share";
        State defaultStateInRequest = State.ENABLED;

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processProduct(product, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(null);
        assertThat(featureCheckResponse.isCached()).isFalse();
        assertThat(featureCheckResponse.isEnabled()).isEqualTo(defaultStateInRequest == State.ENABLED);
    }

    @Test
    public void processProduct_featureNotPresent_null_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(false, metadata, value));
        Feature featureVideo = new Feature("video", null, null, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Product product = new Product("myapp", features);

        String featureToBeSearched = "share";
        State defaultStateInRequest = null;

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processProduct(product, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(null);
        assertThat(featureCheckResponse.isCached()).isFalse();
        assertThat(featureCheckResponse.isEnabled()).isTrue();
    }

    // feature present but no conclusion could not be made

    @Test
    public void processProduct_featurePresent_butNoDefaultOrMatchInConfig_defaultInRequestEnabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(false, metadata, value));
        Feature featureVideo = new Feature("video", null, null, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Product product = new Product("myapp", features);

        String featureToBeSearched = "video";
        State defaultStateInRequest = State.ENABLED;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processProduct(product, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(null);
        assertThat(featureCheckResponse.isCached()).isTrue();
        assertThat(featureCheckResponse.isEnabled()).isEqualTo(defaultStateInRequest == State.ENABLED);
    }

    @Test
    public void processProduct_featurePresent_butNoDefaultOrMatchInConfig_defaultInRequestDisabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(false, metadata, value));
        Feature featureVideo = new Feature("video", null, null, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Product product = new Product("myapp", features);

        String featureToBeSearched = "video";
        State defaultStateInRequest = State.DISABLED;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processProduct(product, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(null);
        assertThat(featureCheckResponse.isCached()).isTrue();
        assertThat(featureCheckResponse.isEnabled()).isEqualTo(defaultStateInRequest == State.ENABLED);
    }

    @Test
    public void processProduct_featurePresent_butNoDefaultOrMatchInConfig_defaultInRequestAbsent_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(false, metadata, value));
        Feature featureVideo = new Feature("video", null, null, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Product product = new Product("myapp", features);

        String featureToBeSearched = "video";
        State defaultStateInRequest = null;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processProduct(product, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(null);
        assertThat(featureCheckResponse.isCached()).isTrue();
        assertThat(featureCheckResponse.isEnabled()).isTrue();
    }

    // default present in config and request (conflict management)
    // make sure the request has the override

    @Test
    public void processProduct_featurePresent_defaultPresentInConfigDisabled_defaultInRequestEnabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(false, metadata, value));
        Feature featureVideo = new Feature("video", null, Toggle.DISABLED, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Product product = new Product("myapp", features);

        String featureToBeSearched = "video";
        State defaultStateInRequest = State.ENABLED;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processProduct(product, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(null);
        assertThat(featureCheckResponse.isCached()).isTrue();
        assertThat(featureCheckResponse.isEnabled()).isEqualTo(defaultStateInRequest == State.ENABLED);
    }

    @Test
    public void processProduct_featurePresent_defaultPresentMatchInConfigEnabled_defaultInRequestDisabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(false, metadata, value));
        Feature featureVideo = new Feature("video", null, Toggle.ENABLED, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Product product = new Product("myapp", features);

        String featureToBeSearched = "video";
        State defaultStateInRequest = State.DISABLED;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processProduct(product, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(null);
        assertThat(featureCheckResponse.isCached()).isTrue();
        assertThat(featureCheckResponse.isEnabled()).isEqualTo(defaultStateInRequest == State.ENABLED);
    }

    @Test
    public void processProduct_featurePresent_defaultPresentMatchInConfigDisabled_defaultInRequestAbsent_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(false, metadata, value));
        Feature featureVideo = new Feature("video", null, Toggle.DISABLED, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Product product = new Product("myapp", features);

        String featureToBeSearched = "video";
        State defaultStateInRequest = null;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processProduct(product, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(null);
        assertThat(featureCheckResponse.isCached()).isTrue();
        assertThat(featureCheckResponse.isEnabled()).isFalse();
    }

    @Test
    public void processProduct_featurePresent_defaultPresentMatchInConfigEnabled_defaultInRequestAbsent_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(false, metadata, value));
        Feature featureVideo = new Feature("video", null, Toggle.ENABLED, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Product product = new Product("myapp", features);

        String featureToBeSearched = "video";
        State defaultStateInRequest = null;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processProduct(product, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(null);
        assertThat(featureCheckResponse.isCached()).isTrue();
        assertThat(featureCheckResponse.isEnabled()).isTrue();
    }

    // feature present and rules are matched
    @Test
    public void processProduct_featurePresent_apiLevel_matchEnabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(true, metadata, value));
        Feature featureVideo = new Feature("video", null, Toggle.DISABLED, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Product product = new Product("myapp", features);

        String featureToBeSearched = "video";
        State defaultStateInRequest = State.DISABLED;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(16);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processProduct(product, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(metadata);
        assertThat(featureCheckResponse.isCached()).isTrue();
        assertThat(featureCheckResponse.isEnabled()).isTrue();
    }

    @Test
    public void processProduct_featurePresent_apiLevel_matchDisabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(false, metadata, value));
        Feature featureVideo = new Feature("video", null, Toggle.ENABLED, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Product product = new Product("myapp", features);

        String featureToBeSearched = "video";
        State defaultStateInRequest = State.ENABLED;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(16);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processProduct(product, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(metadata);
        assertThat(featureCheckResponse.isCached()).isTrue();
        assertThat(featureCheckResponse.isEnabled()).isFalse();
    }

    @Test
    public void processProduct_featurePresent_apiLevel_stateEnabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(false, metadata, value));
        Feature featureVideo = new Feature("video", Toggle.ENABLED, null, null);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Product product = new Product("myapp", features);

        String featureToBeSearched = "video";
        State defaultStateInRequest = State.DISABLED;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(16);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processProduct(product, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isNull();
        assertThat(featureCheckResponse.isCached()).isTrue();
        assertThat(featureCheckResponse.isEnabled()).isTrue();
    }

    @Test
    public void processProduct_featurePresent_apiLevelDoesNotMatch_dateMinMatches_stateEnabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value1 = new Value(14, 18, null, null, null, null, null, null);

        PowerMockito.spy(System.class);
        Mockito.when(System.currentTimeMillis()).thenReturn(1453196889999L);
        Value value2 = new Value(null, null, null, null, 1453196880000L, null, null, null);

        rules.add(new Rule(false, metadata, value1));
        rules.add(new Rule(false, metadata, value2));

        Feature featureVideo = new Feature("video", null, Toggle.ENABLED, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Product product = new Product("myapp", features);

        String featureToBeSearched = "video";
        State defaultStateInRequest = State.ENABLED;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(16);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processProduct(product, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.isEnabled()).isFalse();
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(metadata);
        assertThat(featureCheckResponse.isCached()).isTrue();
    }
}
