package cc.soham.togglesample;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;

import org.junit.After;
import org.junit.Before;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import cc.soham.toggle.Toggle;
import cc.soham.toggle.objects.Config;

import static android.support.test.espresso.Espresso.*;
import static org.assertj.core.api.Assertions.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by sohammondal on 27/01/16.
 */
@RunWith(AndroidJUnit4.class)
public class ToggleNetworkTests {
    public static Config config = null;

    @Rule
    public ActivityTestRule<SampleNetworkActivity> sampleNetworkActivityActivityTestRule = new ActivityTestRule<>(SampleNetworkActivity.class);

    public ProgressBarIdlingResource progressBarIdlingResource;

    @Before
    public void setup() {
        SampleNetworkActivity sampleNetworkActivity = sampleNetworkActivityActivityTestRule.getActivity();
        progressBarIdlingResource = new ProgressBarIdlingResource(sampleNetworkActivity);
    }

    @After
    public void tearDown() {
        Espresso.unregisterIdlingResources(progressBarIdlingResource);
    }

    @Test
    @MediumTest
    public void toggle_network_setConfig() {
        // make sure the config is not loaded from memory
        Toggle.storeConfigInMem(null);
        // perform the button click
        onView(withId(R.id.activity_sample_set_config)).perform(click());
        // register the idling resource so that we can know when the config is done
        Espresso.registerIdlingResources(progressBarIdlingResource);
        assertThat(Toggle.getConfig()).isNotNull();
        assertThat(Toggle.getConfig().name).isNotNull();
        assertThat(Toggle.getConfig().features).isNotNull();
    }
}
