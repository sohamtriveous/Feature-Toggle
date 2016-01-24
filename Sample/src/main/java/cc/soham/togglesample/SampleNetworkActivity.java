package cc.soham.togglesample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.soham.toggle.FeatureCheckRequest;
import cc.soham.toggle.Toggle;
import cc.soham.toggle.callbacks.GetConfigCallback;
import cc.soham.toggle.enums.State;
import cc.soham.toggle.objects.Product;

/**
 * Sample Network Activity, shows how to
 * - Use {@link Toggle#getConfig(Product)} to make a network call and manually retrieve and  astore {@link Product} configuration using {@link }
 * - To check for the feature, use {@link Toggle#check(String)} to check for the status of the feature
 * - To check for the feature with the latest config, use {@link Toggle#check(String)} with the {@link FeatureCheckRequest.Builder#getLatest()} flag to check for the
 * latest status of the feature
 *
 */
public class SampleNetworkActivity extends AppCompatActivity {
    @Bind(R.id.activity_sample_feature)
    Button featureButton;
    @Bind(R.id.activity_sample_feature_metadata)
    TextView metadataTextView;
    @Bind(R.id.activity_sample_feature_cached)
    TextView cachedTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sample_network);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Network Sample");
    }

    /**
     * Shows how to use {@link Toggle#getConfig(Product)} to get the config from
     * Once the {@link Product} config is received, we manually store it in Toggle using {@link Toggle#getConfig(Product)}
     */
    @OnClick(R.id.activity_sample_get_config)
    public void getConfigButton_onClick() {
        Toast.makeText(SampleNetworkActivity.this, "Making a network call to get the config", Toast.LENGTH_SHORT).show();
        try {
            Toggle.with(this).getConfig(new URL(Constants.URL_CONFIG), new GetConfigCallback() {
                @Override
                public void onConfigReceived(Product product, boolean cached) {
                    Toast.makeText(SampleNetworkActivity.this, "Response received, storing in toggle", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows how to check for a particular feature, here we check for the 'mixpanel' feature in the config
     * We can pass in additional flags in the {@link Toggle#check(String)} call like default value etc.
     */
    @OnClick(R.id.activity_sample_check)
    public void checkButton_onClick() {
        showMessage("Checking for the feature");
        Toggle.with(SampleNetworkActivity.this).check("mixpanel").defaultState(State.ENABLED).start(new cc.soham.toggle.callbacks.Callback() {
            @Override
            public void onStatusChecked(String feature, boolean enabled, String metadata, boolean cached) {
                showMessage("Feature checked");
                updateUiAfterResponse(feature, enabled, metadata, cached);
            }
        });
    }

    /*
     * Shows how to check for the latest status of a  particular feature, here we check for the 'mixpanel' feature in the config
     * We can pass in additional flags in the {@link Toggle#check(String)} call like default value etc.
     */
    @OnClick(R.id.activity_sample_check_latest)
    public void checkLatestButton_onClick() {
        showMessage("Checking for the latest feature");
        Toggle.with(SampleNetworkActivity.this).check("mixpanel").getLatest().defaultState(State.ENABLED).start(new cc.soham.toggle.callbacks.Callback() {
            @Override
            public void onStatusChecked(String feature, boolean enabled, String metadata, boolean cached) {
                showMessage("Latest feature checked");
                updateUiAfterResponse(feature, enabled, metadata, cached);
            }
        });
    }

    /**
     * Update the UI as per the feature state
     *
     * @param feature  Name of the feature
     * @param enabled  The feature-toggle state of the feature: enabled/disabled
     * @param metadata Metadata attached to the feature
     * @param cached   Shows whether this is a cached response or not
     */
    private void updateUiAfterResponse(String feature, boolean enabled, String metadata, boolean cached) {
        featureButton.setText(feature + " is " + (enabled ? "enabled" : "disabled"));
        featureButton.setEnabled(enabled);
        metadataTextView.setText("Metadata: " + metadata);
        cachedTextView.setText("Cached: " + cached);
    }

    /**
     * Simple helper method to show Toasts
     *
     * @param message
     */
    private void showMessage(String message) {
        Toast.makeText(SampleNetworkActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
