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
import cc.soham.toggle.objects.Feature;
import cc.soham.toggle.objects.Rule;
import cc.soham.toggle.objects.Value;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by sohammondal on 21/01/16.
 */
@SmallTest
@RunWith(PowerMockRunner.class)
@PrepareForTest({Reservoir.class})
public class ToggleTests {
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
    public void getDefaultResponseDecision_null_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", null, null, rules);

        ResponseDecision responseDecision = toggle.getDefaultResponseDecision(feature);

        assertThat(responseDecision).isEqualTo(ResponseDecision.RESPONSE_ENABLED);
    }

    private void getApiValue(List<Rule> rules) {
        Value value = new Value(14, 16, null, null, null, null, null, null);
        rules.add(new Rule(true, "myMetadata", value));
    }

    @Test
    public void getDefaultResponseDecision_enabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", null, Toggle.ENABLED, rules);

        ResponseDecision responseDecision = toggle.getDefaultResponseDecision(feature);

        assertThat(responseDecision).isEqualTo(ResponseDecision.RESPONSE_ENABLED);
    }

    @Test
    public void getDefaultResponseDecision_disabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", null, Toggle.DISABLED, rules);

        ResponseDecision responseDecision = toggle.getDefaultResponseDecision(feature);

        assertThat(responseDecision).isEqualTo(ResponseDecision.RESPONSE_DISABLED);
    }

    @Test
    public void getStatePoweredResponseDecision_enabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", Toggle.ENABLED, null, rules);

        ResponseDecision responseDecision = toggle.getStatePoweredResponseDecision(feature);

        assertThat(responseDecision).isEqualTo(ResponseDecision.RESPONSE_ENABLED);
    }

    @Test
    public void getStatePoweredResponseDecision_disabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", Toggle.DISABLED, null, rules);

        ResponseDecision responseDecision = toggle.getStatePoweredResponseDecision(feature);

        assertThat(responseDecision).isEqualTo(ResponseDecision.RESPONSE_DISABLED);
    }

    @Test
    public void getStatePoweredResponseDecision_null_returnsUndecided() {
        Toggle toggle = new Toggle(context);
        List<Rule> rules = new ArrayList<>();
        getApiValue(rules);
        Feature feature = new Feature("video", null, null, rules);

        ResponseDecision responseDecision = toggle.getStatePoweredResponseDecision(feature);

        assertThat(responseDecision).isEqualTo(ResponseDecision.RESPONSE_UNDECIDED);
    }

    @Test
    public void getRuleMatchedResponseDecision_enabled_returnsEnabled() {
        Toggle toggle = new Toggle(context);
        Value value = new Value(14, 16, null, null, null, null, null, null);
        String metadata = "myMetadata";
        Rule rule = new Rule(true, metadata, value);

        ResponseDecision responseDecision = toggle.getRuleMatchedResponseDecision(rule);

        // assert the state
        assertThat(responseDecision).isEqualTo(ResponseDecision.RESPONSE_ENABLED);
        // assert the metadata
        assertThat(responseDecision.getMetadata()).isEqualTo(metadata);
    }

    @Test
    public void getRuleMatchedResponseDecision_disabled_returnsDisabled() {
        Toggle toggle = new Toggle(context);
        Value value = new Value(14, 16, null, null, null, null, null, null);
        String metadata = "myMetadata";
        Rule rule = new Rule(false, metadata, value);

        ResponseDecision responseDecision = toggle.getRuleMatchedResponseDecision(rule);

        // assert the state
        assertThat(responseDecision).isEqualTo(ResponseDecision.RESPONSE_DISABLED);
        // assert the metadata
        assertThat(responseDecision.getMetadata()).isEqualTo(metadata);
    }

    @Test(expected = IllegalStateException.class)
    public void getRuleMatchedResponseDecision_null_throwsException() {
        Toggle toggle = new Toggle(context);
        Value value = new Value(14, 16, null, null, null, null, null, null);
        String metadata = "myMetadata";
        Rule rule = new Rule(null, metadata, value);

        toggle.getRuleMatchedResponseDecision(rule);
    }
}
