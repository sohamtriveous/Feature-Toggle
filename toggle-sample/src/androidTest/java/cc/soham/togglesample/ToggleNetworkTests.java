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
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

/**
 * Created by sohammondal on 27/01/16.
 */
@RunWith(AndroidJUnit4.class)
public class ToggleNetworkTests {
    public static Config config = null;

    @Rule
    public ActivityTestRule<SampleNetworkActivity> sampleNetworkActivityActivityTestRule = new ActivityTestRule<>(SampleNetworkActivity.class);

    public SetConfigIdlingResource setConfigIdlingResource;

    @Before
    public void setup() {
        SampleNetworkActivity sampleNetworkActivity = sampleNetworkActivityActivityTestRule.getActivity();
        setConfigIdlingResource = new SetConfigIdlingResource(sampleNetworkActivity);
    }

    @After
    public void tearDown() {
        Espresso.unregisterIdlingResources(setConfigIdlingResource);
    }

    @Test
    @MediumTest
    public void toggle_network_setConfig() {
        Toggle.storeConfigInMem(null);
        onView(withId(R.id.activity_sample_set_config)).perform(click());
        onView(withText("Making a network call to get the config")).inRoot(withDecorView(not(sampleNetworkActivityActivityTestRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
        Espresso.registerIdlingResources(setConfigIdlingResource);
        assertThat(Toggle.getConfig()).isNotNull();
        assertThat(Toggle.getConfig().name).isNotNull();
        assertThat(Toggle.getConfig().features).isNotNull();
    }
}
