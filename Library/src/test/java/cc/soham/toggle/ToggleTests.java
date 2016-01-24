package cc.soham.toggle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.test.suitebuilder.annotation.SmallTest;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import cc.soham.toggle.callbacks.Callback;
import cc.soham.toggle.enums.SourceType;
import cc.soham.toggle.network.FeatureCheckResponse;
import cc.soham.toggle.objects.Config;
import cc.soham.toggle.objects.Feature;
import cc.soham.toggle.objects.ResponseDecisionMeta;
import cc.soham.toggle.objects.Rule;
import cc.soham.toggle.objects.Value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by sohammondal on 21/01/16.
 */
@SmallTest
@RunWith(PowerMockRunner.class)
@PrepareForTest({Reservoir.class, RuleMatcher.class, System.class, Toggle.class, PersistUtils.class})
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

        assertThat(responseDecisionMeta.state).isEqualTo(Toggle.DEFAULT_STATE);
    }

    private void getApiValue(List<Rule> rules) {
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(Toggle.ENABLED, metadata, value));
    }

    @Test
    public void getDefaultResponseDecision_noFeatureCheckRequestDefault_enabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", null, Toggle.ENABLED, rules);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, null, false, null);

        ResponseDecisionMeta responseDecisionMeta = toggle.getDefaultResponseDecision(feature, featureCheckRequest);

        assertThat(responseDecisionMeta.state).isEqualTo(Toggle.ENABLED);
    }

    @Test
    public void getDefaultResponseDecision_noFeatureCheckRequestDefault_disabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", null, Toggle.DISABLED, rules);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, null, false, null);

        ResponseDecisionMeta responseDecisionMeta = toggle.getDefaultResponseDecision(feature, featureCheckRequest);

        assertThat(responseDecisionMeta.state).isEqualTo(Toggle.DISABLED);
    }

    @Test
    public void getDefaultResponseDecision_FeatureCheckRequestDefault_disabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", null, Toggle.ENABLED, rules);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, Toggle.DISABLED, false, null);

        ResponseDecisionMeta responseDecisionMeta = toggle.getDefaultResponseDecision(feature, featureCheckRequest);

        assertThat(responseDecisionMeta.state).isEqualTo(Toggle.DISABLED);
    }

    @Test
    public void getDefaultResponseDecision_FeatureCheckRequestDefault_enabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", null, Toggle.DISABLED, rules);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, Toggle.ENABLED, false, null);

        ResponseDecisionMeta responseDecisionMeta = toggle.getDefaultResponseDecision(feature, featureCheckRequest);

        assertThat(responseDecisionMeta.state).isEqualTo(Toggle.ENABLED);
    }

    @Test
    public void getStatePoweredResponseDecision_enabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", Toggle.ENABLED, null, rules);

        ResponseDecisionMeta responseDecisionMeta = toggle.getStatePoweredResponseDecision(feature);

        assertThat(responseDecisionMeta.state).isEqualTo(Toggle.ENABLED);
    }

    @Test
    public void getStatePoweredResponseDecision_disabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", Toggle.DISABLED, null, rules);

        ResponseDecisionMeta responseDecisionMeta = toggle.getStatePoweredResponseDecision(feature);

        assertThat(responseDecisionMeta.state).isEqualTo(Toggle.DISABLED);
    }

    @Test
    public void getStatePoweredResponseDecision_null_returnsUndecided() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", null, null, rules);

        ResponseDecisionMeta responseDecisionMeta = toggle.getStatePoweredResponseDecision(feature);

        assertThat(responseDecisionMeta.state).isEqualTo(Toggle.DEFAULT_STATE);
    }

    @Test
    public void getRuleMatchedResponseDecision_enabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        Value value = new Value(14, 16, null, null, null, null, null, null);
        Rule rule = new Rule(Toggle.ENABLED, metadata, value);

        ResponseDecisionMeta responseDecisionMeta = toggle.getRuleMatchedResponseDecision(rule);

        // assert the state
        assertThat(responseDecisionMeta.state).isEqualTo(Toggle.ENABLED);
        // assert the metadata
        assertThat(responseDecisionMeta.metadata).isEqualTo(metadata);
    }

    @Test
    public void getRuleMatchedResponseDecision_disabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        Value value = new Value(14, 16, null, null, null, null, null, null);
        Rule rule = new Rule(Toggle.DISABLED, metadata, value);

        ResponseDecisionMeta responseDecisionMeta = toggle.getRuleMatchedResponseDecision(rule);

        // assert the state
        assertThat(responseDecisionMeta.state).isEqualTo(Toggle.DISABLED);
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
     * Handle Feature tests, a state to : couple of things can happen
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
        assertThat(responseDecisionMeta.state).isEqualTo(Toggle.ENABLED);
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
        assertThat(responseDecisionMeta.state).isEqualTo(Toggle.DISABLED);
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
        assertThat(responseDecisionMeta.state).isEqualTo(Toggle.ENABLED);
        assertThat(responseDecisionMeta.metadata).isEqualTo(metadata);
    }

    @Test
    public void handleFeature_stateNull_ruleMatched_disabledForRightAPILevel_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(Toggle.DISABLED, metadata, value));
        Feature feature = new Feature("video", null, Toggle.ENABLED, rules);
        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, null, false, null);

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(16);

        ResponseDecisionMeta responseDecisionMeta = toggle.handleFeature(feature, featureCheckRequest);
        // assert the state and metadata
        assertThat(responseDecisionMeta.state).isEqualTo(Toggle.DISABLED);
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
        rules.add(new Rule(Toggle.DISABLED, metadata, value));
        Feature feature = new Feature("video", null, Toggle.ENABLED, rules);
        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, null, false, null);

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        ResponseDecisionMeta responseDecisionMeta = toggle.handleFeature(feature, featureCheckRequest);
        // assert the state and metadata
        assertThat(responseDecisionMeta.state).isEqualTo(Toggle.ENABLED);
        assertThat(responseDecisionMeta.metadata).isNull();
    }

    @Test
    public void handleFeature_stateNull_ruleNotMatched_defaultDisabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(Toggle.ENABLED, metadata, value));
        Feature feature = new Feature("video", null, Toggle.DISABLED, rules);
        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, null, false, null);

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        ResponseDecisionMeta responseDecisionMeta = toggle.handleFeature(feature, featureCheckRequest);
        // assert the state and metadata
        assertThat(responseDecisionMeta.state).isEqualTo(Toggle.DISABLED);
        assertThat(responseDecisionMeta.metadata).isNull();
    }

    @Test
    public void handleFeature_stateNull_ruleNotMatched_defaultNull_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(Toggle.DISABLED, metadata, value));
        Feature feature = new Feature("video", null, null, rules);
        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, null, false, null);

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        ResponseDecisionMeta responseDecisionMeta = toggle.handleFeature(feature, featureCheckRequest);
        // assert the state and metadata
        assertThat(responseDecisionMeta.state).isEqualTo(Toggle.DEFAULT_STATE);
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
        rules.add(new Rule(Toggle.DISABLED, metadata, value));
        Feature feature = new Feature("video", null, null, rules);
        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, Toggle.ENABLED, false, null);

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        ResponseDecisionMeta responseDecisionMeta = toggle.handleFeature(feature, featureCheckRequest);
        // assert the state and metadata
        assertThat(responseDecisionMeta.state).isEqualTo(Toggle.ENABLED);
        assertThat(responseDecisionMeta.metadata).isNull();
    }

    @Test
    public void handleFeature_stateNull_ruleNotMatched_defaultNull_featureCheckRequestDefault_disabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(Toggle.DISABLED, metadata, value));
        Feature feature = new Feature("video", null, null, rules);
        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, "video", null, Toggle.DISABLED, false, null);

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        ResponseDecisionMeta responseDecisionMeta = toggle.handleFeature(feature, featureCheckRequest);
        // assert the state and metadata
        assertThat(responseDecisionMeta.state).isEqualTo(Toggle.DISABLED);
        assertThat(responseDecisionMeta.metadata).isNull();
    }

    /**
     * Process config tests
     */

    // set 1: feature not present

    @Test
    public void processConfig_featureNotPresent_defaultDisabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(Toggle.DISABLED, metadata, value));
        Feature featureVideo = new Feature("video", null, null, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Config config = new Config("myapp", features);

        String featureToBeSearched = "share";
        String defaultStateInRequest = Toggle.DISABLED;

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processConfig(config, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(null);
        assertThat(featureCheckResponse.isCached()).isFalse();
        assertThat(featureCheckResponse.getState()).isEqualTo(defaultStateInRequest);
    }

    @Test
    public void processConfig_featureNotPresent_defaultEnabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(Toggle.DISABLED, metadata, value));
        Feature featureVideo = new Feature("video", null, null, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Config config = new Config("myapp", features);

        String featureToBeSearched = "share";
        String defaultStateInRequest = Toggle.ENABLED;

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processConfig(config, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(null);
        assertThat(featureCheckResponse.isCached()).isFalse();
        assertThat(featureCheckResponse.getState()).isEqualTo(defaultStateInRequest);
    }

    @Test
    public void processConfig_featureNotPresent_null_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(Toggle.DISABLED, metadata, value));
        Feature featureVideo = new Feature("video", null, null, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Config config = new Config("myapp", features);

        String featureToBeSearched = "share";
        String defaultStateInRequest = null;

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processConfig(config, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(null);
        assertThat(featureCheckResponse.isCached()).isFalse();
        assertThat(featureCheckResponse.getState()).isEqualTo(Toggle.DEFAULT_STATE);
    }

    // feature present but no conclusion could not be made

    @Test
    public void processConfig_featurePresent_butNoDefaultOrMatchInConfig_defaultInRequestEnabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(Toggle.DISABLED, metadata, value));
        Feature featureVideo = new Feature("video", null, null, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Config config = new Config("myapp", features);

        String featureToBeSearched = "video";
        String defaultStateInRequest = Toggle.ENABLED;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processConfig(config, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(null);
        assertThat(featureCheckResponse.isCached()).isTrue();
        assertThat(featureCheckResponse.getState()).isEqualTo(defaultStateInRequest);
    }

    @Test
    public void processConfig_featurePresent_butNoDefaultOrMatchInConfig_defaultInRequestDisabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(Toggle.DISABLED, metadata, value));
        Feature featureVideo = new Feature("video", null, null, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Config config = new Config("myapp", features);

        String featureToBeSearched = "video";
        String defaultStateInRequest = Toggle.DISABLED;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processConfig(config, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(null);
        assertThat(featureCheckResponse.isCached()).isTrue();
        assertThat(featureCheckResponse.getState()).isEqualTo(defaultStateInRequest);
    }

    @Test
    public void processConfig_featurePresent_butNoDefaultOrMatchInConfig_defaultInRequestAbsent_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(Toggle.DISABLED, metadata, value));
        Feature featureVideo = new Feature("video", null, null, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Config config = new Config("myapp", features);

        String featureToBeSearched = "video";
        String defaultStateInRequest = null;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processConfig(config, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(null);
        assertThat(featureCheckResponse.isCached()).isTrue();
        assertThat(featureCheckResponse.getState()).isEqualTo(Toggle.DEFAULT_STATE);
    }

    // default present in config and request (conflict management)
    // make sure the request has the override

    @Test
    public void processConfig_featurePresent_defaultPresentInConfigDisabled_defaultInRequestEnabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(Toggle.DISABLED, metadata, value));
        Feature featureVideo = new Feature("video", null, Toggle.DISABLED, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Config config = new Config("myapp", features);

        String featureToBeSearched = "video";
        String defaultStateInRequest = Toggle.ENABLED;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processConfig(config, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(null);
        assertThat(featureCheckResponse.isCached()).isTrue();
        assertThat(featureCheckResponse.getState()).isEqualTo(defaultStateInRequest);
    }

    @Test
    public void processConfig_featurePresent_defaultPresentMatchInConfigEnabled_defaultInRequestDisabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(Toggle.DISABLED, metadata, value));
        Feature featureVideo = new Feature("video", null, Toggle.ENABLED, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Config config = new Config("myapp", features);

        String featureToBeSearched = "video";
        String defaultStateInRequest = Toggle.DISABLED;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processConfig(config, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(null);
        assertThat(featureCheckResponse.isCached()).isTrue();
        assertThat(featureCheckResponse.getState()).isEqualTo(defaultStateInRequest);
    }

    @Test
    public void processConfig_featurePresent_defaultPresentMatchInConfigDisabled_defaultInRequestAbsent_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(Toggle.DISABLED, metadata, value));
        Feature featureVideo = new Feature("video", null, Toggle.DISABLED, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Config config = new Config("myapp", features);

        String featureToBeSearched = "video";
        String defaultStateInRequest = null;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processConfig(config, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(null);
        assertThat(featureCheckResponse.isCached()).isTrue();
        assertThat(featureCheckResponse.getState()).isEqualTo(Toggle.DISABLED);
    }

    @Test
    public void processConfig_featurePresent_defaultPresentMatchInConfigEnabled_defaultInRequestAbsent_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(Toggle.DISABLED, metadata, value));
        Feature featureVideo = new Feature("video", null, Toggle.ENABLED, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Config config = new Config("myapp", features);

        String featureToBeSearched = "video";
        String defaultStateInRequest = null;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(20);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processConfig(config, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(null);
        assertThat(featureCheckResponse.isCached()).isTrue();
        assertThat(featureCheckResponse.getState()).isEqualTo(Toggle.ENABLED);
    }

    // feature present and rules are matched
    @Test
    public void processConfig_featurePresent_apiLevel_matchEnabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(Toggle.ENABLED, metadata, value));
        Feature featureVideo = new Feature("video", null, Toggle.DISABLED, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Config config = new Config("myapp", features);

        String featureToBeSearched = "video";
        String defaultStateInRequest = Toggle.DISABLED;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(16);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processConfig(config, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(metadata);
        assertThat(featureCheckResponse.isCached()).isTrue();
        assertThat(featureCheckResponse.getState()).isEqualTo(Toggle.ENABLED);
    }

    @Test
    public void processConfig_featurePresent_apiLevel_matchDisabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(Toggle.DISABLED, metadata, value));
        Feature featureVideo = new Feature("video", null, Toggle.ENABLED, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Config config = new Config("myapp", features);

        String featureToBeSearched = "video";
        String defaultStateInRequest = Toggle.ENABLED;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(16);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processConfig(config, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(metadata);
        assertThat(featureCheckResponse.isCached()).isTrue();
        assertThat(featureCheckResponse.getState()).isEqualTo(Toggle.DISABLED);
    }

    @Test
    public void processConfig_featurePresent_apiLevel_stateEnabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        Value value = new Value(14, 18, null, null, null, null, null, null);
        rules.add(new Rule(Toggle.DISABLED, metadata, value));
        Feature featureVideo = new Feature("video", Toggle.ENABLED, null, null);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        Config config = new Config("myapp", features);

        String featureToBeSearched = "video";
        String defaultStateInRequest = Toggle.DISABLED;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(16);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processConfig(config, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getMetadata()).isNull();
        assertThat(featureCheckResponse.isCached()).isTrue();
        assertThat(featureCheckResponse.getState()).isEqualTo(Toggle.ENABLED);
    }

    @Test
    public void processConfig_featurePresent_apiLevelDoesNotMatch_dateMinMatches_stateEnabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        PowerMockito.spy(System.class);
        Mockito.when(System.currentTimeMillis()).thenReturn(1453196889999L);

        Config config = getStandardConfig(metadata);

        String featureToBeSearched = "video";
        String defaultStateInRequest = Toggle.ENABLED;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(16);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, null, defaultStateInRequest, false, null);

        FeatureCheckResponse featureCheckResponse = toggle.processConfig(config, featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureToBeSearched);
        assertThat(featureCheckResponse.getState()).isEqualTo(Toggle.DISABLED);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(metadata);
        assertThat(featureCheckResponse.isCached()).isTrue();
    }

    @NonNull
    private static Config getStandardConfig(String metadata) {
        List<Rule> rules = new ArrayList<>();
        Value value1 = new Value(14, 18, null, null, null, null, null, null);
        Value value2 = new Value(null, null, null, null, 1453196880000L, null, null, null);

        rules.add(new Rule(Toggle.DISABLED, metadata, value1));
        rules.add(new Rule(Toggle.DISABLED, metadata, value2));

        Feature featureVideo = new Feature("video", null, Toggle.ENABLED, rules);
        Feature featureAudio = new Feature("audio", Toggle.ENABLED, null, rules);
        Feature featureSpeech = new Feature("speech", null, Toggle.DISABLED, rules);

        List<Feature> features = new ArrayList<>();
        features.add(featureVideo);
        features.add(featureAudio);
        features.add(featureSpeech);

        return new Config("myapp", features);
    }

    // getAndProcessCachedConfigSync
    @Test
    public void getAndProcessCachedConfigSync() {
        Toggle toggle = new Toggle(context);
        PowerMockito.mockStatic(PersistUtils.class);

        Callback callback = mock(Callback.class);

        String featureToBeSearched = "video";
        String defaultStateInRequest = Toggle.ENABLED;

        PowerMockito.spy(RuleMatcher.class);
        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(16);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, callback, defaultStateInRequest, false, null);

        try {
            PowerMockito.when(PersistUtils.getConfigSync()).thenReturn(getStandardConfig(metadata));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Mockito.when(RuleMatcher.getBuildVersion()).thenReturn(16);

        FeatureCheckResponse featureCheckResponse = toggle.getAndProcessCachedConfigSync(featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureCheckRequest.getFeatureName());
        assertThat(featureCheckResponse.getState()).isEqualTo(Toggle.DISABLED);
        assertThat(featureCheckResponse.getMetadata()).isEqualTo(metadata);
        assertThat(featureCheckResponse.isCached()).isTrue();
    }

    @Test
    public void getAndProcessCachedConfigSync_nullConfig_defaultStateDisabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        PowerMockito.mockStatic(PersistUtils.class);

        Callback callback = mock(Callback.class);

        String featureToBeSearched = "video";
        String defaultStateInRequest = Toggle.DISABLED;

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, callback, defaultStateInRequest, false, null);

        try {
            PowerMockito.when(PersistUtils.getConfigSync()).thenReturn(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        PowerMockito.spy(RuleMatcher.class);
        PowerMockito.when(RuleMatcher.getBuildVersion()).thenReturn(16);

        FeatureCheckResponse featureCheckResponse = toggle.getAndProcessCachedConfigSync(featureCheckRequest);

        assertThat(featureCheckResponse.getFeatureName()).isEqualTo(featureCheckRequest.getFeatureName());
        assertThat(featureCheckResponse.getState()).isEqualTo(Toggle.DISABLED);
        assertThat(featureCheckResponse.getMetadata()).isNull();
        assertThat(featureCheckResponse.isCached()).isTrue();
    }

    @Test(expected = IllegalStateException.class)
    public void getAndProcessCachedConfigSync_nullConfig_defaultStateNull_throwsException() {
        Toggle toggle = new Toggle(context);
        PowerMockito.mockStatic(PersistUtils.class);

        Callback callback = mock(Callback.class);

        String featureToBeSearched = "video";
        String defaultStateInRequest = null;

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, callback, defaultStateInRequest, false, null);

        try {
            PowerMockito.when(PersistUtils.getConfigSync()).thenReturn(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        PowerMockito.spy(RuleMatcher.class);
        PowerMockito.when(RuleMatcher.getBuildVersion()).thenReturn(16);

        toggle.getAndProcessCachedConfigSync(featureCheckRequest);
    }

    // handleFeatureCheckRequest
    // TODO: test this
    public void handleFeatureCheckRequest_checkCallWithoutGetConfig() {
        Toggle toggle = new Toggle(context);
        toggle.setSourceType(null);

        PowerMockito.mockStatic(PersistUtils.class);
        PowerMockito.when(PersistUtils.getSourceType(context)).thenReturn(SourceType.JSONOBJECT);

        Callback callback = mock(Callback.class);

        String featureToBeSearched = "video";
        String defaultStateInRequest = Toggle.DISABLED;

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, callback, defaultStateInRequest, false, null);

        PowerMockito.spy(RuleMatcher.class);
        PowerMockito.when(RuleMatcher.getBuildVersion()).thenReturn(16);

        Config config = getStandardConfig(metadata);

        toggle.handleFeatureCheckRequest(featureCheckRequest);


    }

    // example of using ArgumentCaptor to test callbacks

    @Captor
    private ArgumentCaptor<ReservoirGetCallback<Config>> reservoirGetCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Captor
    private ArgumentCaptor<Class> classCaptor;

    /**
     * The idea here is to invoke {@link Toggle#getAndProcessCachedConfig(FeatureCheckRequest)}
     * This in turn makes a call to {@link PersistUtils#getConfig(ReservoirGetCallback)} which in turn
     * calls {@link Reservoir#getAsync(String, Class, ReservoirGetCallback)}
     *
     * The challenge here is to unit test this properly, here we face the following challenges:
     * 1. We need to intercept the {@link ReservoirGetCallback} (among other things) passed to {@link Reservoir#getAsync(String, Class, ReservoirGetCallback)}
     * 2. Then we need to test success and failure scenarios manually by taking that intercept and then call the success/failure callbacks manually
     * 3. Then we need to check if the final output, which in this case is a callback (passed in {@link FeatureCheckRequest}
     * to the callee with the right parameters. So basically, the rule enable/disable needs to be properly passed back to the callee.
     *
     * Remember, we are not unit testing {@link Reservoir} here, we just assume that it will work!
     * Our job is to simulate all the response conditions from {@link Reservoir} and see if we are handling it properly
     */

    @Test
    public void getAndProcessCachedConfig_featureCheckMatches_reservoirSuccess_enabled_returnsEnabled() {
        // prepare
        PowerMockito.mockStatic(Reservoir.class);

        Toggle toggle = new Toggle(context);
        String featureToBeSearched = "video";
        String defaultStateInRequest = Toggle.DISABLED;
        Callback callback = mock(Callback.class);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, callback, defaultStateInRequest, false, null);
        Config config = getStandardConfig(metadata);

        // call the getAndProcessCachedConfig is called
        toggle.getAndProcessCachedConfig(featureCheckRequest);

        // verify that a) a call to Reservoir.getAsync is made
        // b) capture the callback argument so that we can call it ourselves
        PowerMockito.verifyStatic();
        Reservoir.getAsync(stringCaptor.capture(), classCaptor.capture(), reservoirGetCallbackArgumentCaptor.capture());

        // setup the environment to match
        PowerMockito.spy(RuleMatcher.class);
        PowerMockito.when(RuleMatcher.getBuildVersion()).thenReturn(16);

        // call the success callback ourselves
        reservoirGetCallbackArgumentCaptor.getValue().onSuccess(config);

        // verify that the final customer callback was made with the right parameters
        verify(callback).onStatusChecked("video", Toggle.DISABLED, metadata, true);
    }

    @Test
    public void getAndProcessCachedConfig_featureCheckDoesNotMatch_reservoirSuccess_defaultDisabled_returnsDisabled() {
        // prepare
        PowerMockito.mockStatic(Reservoir.class);

        Toggle toggle = new Toggle(context);
        String featureToBeSearched = "video";
        String defaultStateInRequest = Toggle.DISABLED;
        Callback callback = mock(Callback.class);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, callback, defaultStateInRequest, false, null);
        Config config = getStandardConfig(metadata);

        // call the getAndProcessCachedConfig is called
        toggle.getAndProcessCachedConfig(featureCheckRequest);

        // verify that a) a call to Reservoir.getAsync is made
        // b) capture the callback argument so that we can call it ourselves
        PowerMockito.verifyStatic();
        Reservoir.getAsync(stringCaptor.capture(), classCaptor.capture(), reservoirGetCallbackArgumentCaptor.capture());

        // setup the environment to match
        PowerMockito.spy(RuleMatcher.class);
        PowerMockito.when(RuleMatcher.getBuildVersion()).thenReturn(23);

        // call the success callback ourselves
        reservoirGetCallbackArgumentCaptor.getValue().onSuccess(config);

        // verify that the final customer callback was made with the right parameters
        verify(callback).onStatusChecked("video", Toggle.DISABLED, metadata, true);
    }

    @Test
    public void getAndProcessCachedConfig_featureCheckDoesNotMatch_reservoirFailure_defaultDisabled_returnsDisabled() {
        // prepare
        PowerMockito.mockStatic(Reservoir.class);

        Toggle toggle = new Toggle(context);
        String featureToBeSearched = "video";
        String defaultStateInRequest = Toggle.DISABLED;
        Callback callback = mock(Callback.class);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, callback, defaultStateInRequest, false, null);
        Config config = getStandardConfig(metadata);

        // call the getAndProcessCachedConfig is called
        toggle.getAndProcessCachedConfig(featureCheckRequest);

        // verify that a) a call to Reservoir.getAsync is made
        // b) capture the callback argument so that we can call it ourselves
        PowerMockito.verifyStatic();
        Reservoir.getAsync(stringCaptor.capture(), classCaptor.capture(), reservoirGetCallbackArgumentCaptor.capture());

        // setup the environment to match
        PowerMockito.spy(RuleMatcher.class);
        PowerMockito.when(RuleMatcher.getBuildVersion()).thenReturn(23);

        // simulate a failure to retrieve from Reservoir
        reservoirGetCallbackArgumentCaptor.getValue().onFailure(new Exception("Dammit Reservoir didn't work"));

        // verify that the final customer callback was made with the right parameters
        verify(callback).onStatusChecked("video", Toggle.DISABLED, null, true);
    }

    @Test
    public void getAndProcessCachedConfig_featureCheckDoesNotMatch_reservoirFailure_defaultEnabled_returnsEnabled() {
        // prepare
        PowerMockito.mockStatic(Reservoir.class);

        Toggle toggle = new Toggle(context);
        String featureToBeSearched = "video";
        String defaultStateInRequest = Toggle.ENABLED;
        Callback callback = mock(Callback.class);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, callback, defaultStateInRequest, false, null);
        Config config = getStandardConfig(metadata);

        // call the getAndProcessCachedConfig is called
        toggle.getAndProcessCachedConfig(featureCheckRequest);

        // verify that a) a call to Reservoir.getAsync is made
        // b) capture the callback argument so that we can call it ourselves
        PowerMockito.verifyStatic();
        Reservoir.getAsync(stringCaptor.capture(), classCaptor.capture(), reservoirGetCallbackArgumentCaptor.capture());

        // setup the environment to match
        PowerMockito.spy(RuleMatcher.class);
        PowerMockito.when(RuleMatcher.getBuildVersion()).thenReturn(23);

        // simulate a failure to retrieve from Reservoir
        reservoirGetCallbackArgumentCaptor.getValue().onFailure(new Exception("Dammit Reservoir didn't work"));

        // verify that the final customer callback was made with the right parameters
        verify(callback).onStatusChecked("video", Toggle.ENABLED, null, true);
    }

    @Test
    public void getAndProcessCachedConfig_featureCheckDoesNotMatch_reservoirFailure_defaultAbsent_returnsEnabled() {
        // prepare
        PowerMockito.mockStatic(Reservoir.class);

        Toggle toggle = new Toggle(context);
        String featureToBeSearched = "video";
        String defaultStateInRequest = null;
        Callback callback = mock(Callback.class);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, callback, defaultStateInRequest, false, null);
        Config config = getStandardConfig(metadata);

        // call the getAndProcessCachedConfig is called
        toggle.getAndProcessCachedConfig(featureCheckRequest);

        // verify that a) a call to Reservoir.getAsync is made
        // b) capture the callback argument so that we can call it ourselves
        PowerMockito.verifyStatic();
        Reservoir.getAsync(stringCaptor.capture(), classCaptor.capture(), reservoirGetCallbackArgumentCaptor.capture());

        // setup the environment to match
        PowerMockito.spy(RuleMatcher.class);
        PowerMockito.when(RuleMatcher.getBuildVersion()).thenReturn(23);

        // simulate a failure to retrieve from Reservoir
        reservoirGetCallbackArgumentCaptor.getValue().onFailure(new Exception("Dammit Reservoir didn't work"));

        // verify that the final customer callback was made with the right parameters
        verify(callback).onStatusChecked("video", Toggle.DEFAULT_STATE, null, true);
    }

    // handleFeatureCheckRequest

    @Test
    public void handleFeatureCheckRequest_sourceTypeJson_getAndProcessCachedConfigIsCalled() {
        PowerMockito.mockStatic(PersistUtils.class);
        Toggle toggle = Mockito.mock(Toggle.class);

        try {
            PowerMockito.when(toggle.getContext()).thenReturn(context);
            PowerMockito.when(PersistUtils.getSourceType(context)).thenReturn(SourceType.JSONOBJECT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String featureToBeSearched = "video";
        String defaultStateInRequest = null;
        Callback callback = mock(Callback.class);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, callback, defaultStateInRequest, false, null);

        Mockito.doCallRealMethod().when(toggle).handleFeatureCheckRequest(featureCheckRequest);
        Mockito.doCallRealMethod().when(toggle).setSourceType(null);

        toggle.setSourceType(null);
        toggle.handleFeatureCheckRequest(featureCheckRequest);

        verify(toggle).getAndProcessCachedConfig(featureCheckRequest);
    }

    @Test
    public void handleFeatureCheckRequest_sourceTypeJson_videoEnabled_returnsEnabled() {
        PowerMockito.mockStatic(Reservoir.class);

        Toggle toggle = new Toggle(context);
        String featureToBeSearched = "video";
        String defaultStateInRequest = Toggle.ENABLED;
        Callback callback = mock(Callback.class);

        FeatureCheckRequest featureCheckRequest = new FeatureCheckRequest(toggle, featureToBeSearched, callback, defaultStateInRequest, false, null);
        Config config = getStandardConfig(metadata);

        // setup the environment to match
        PowerMockito.spy(RuleMatcher.class);
        PowerMockito.when(RuleMatcher.getBuildVersion()).thenReturn(16);

        toggle.setSourceType(SourceType.JSONOBJECT);

        toggle.handleFeatureCheckRequest(featureCheckRequest);

        // verify that a) a call to Reservoir.getAsync is made
        // b) capture the callback argument so that we can call it ourselves
        PowerMockito.verifyStatic();
        Reservoir.getAsync(stringCaptor.capture(), classCaptor.capture(), reservoirGetCallbackArgumentCaptor.capture());

        // call the success callback ourselves
        reservoirGetCallbackArgumentCaptor.getValue().onSuccess(config);

        verify(callback).onStatusChecked(featureToBeSearched, Toggle.DISABLED, metadata, true);
    }
}
