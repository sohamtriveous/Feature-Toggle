package cc.soham.togglesample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.soham.toggle.Toggle;
import cc.soham.toggle.enums.State;
import cc.soham.toggle.objects.Product;
import cc.soham.togglesample.network.MyApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Sample Retrofit Activity, shows how to
 * - Use {@link retrofit2.Retrofit} to make a network call and manually retrieve a {@link Product} configuration
 * - Once the product is retrieved, use {@link Toggle#getConfig(Product)} to store the config in {@link Toggle}
 * - To check for the feature, use {@link Toggle#check(String)} to check for the status of the feature
 *
 * Note: {@link Toggle#check(String)} can be called before/after {@link Toggle#getConfig(Product)} is called
 * Here {@link Toggle#check(String)} will check for cache and the default value in the Request to check for the feature
 */
public class SampleRetrofitActivity extends AppCompatActivity {
    @Bind(R.id.activity_sample_feature)
    Button featureButton;
    @Bind(R.id.activity_sample_feature_metadata)
    TextView metadataTextView;
    @Bind(R.id.activity_sample_feature_cached)
    TextView cachedTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_base);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Retrofit Sample");
    }

    /**
     * Shows how to manually create a {@link retrofit2.Retrofit} network call to retrieve a {@link Product} config
     * Once the {@link Product} config is received, we manually store it in Toggle using {@link Toggle#getConfig(Product)}
     */
    @OnClick(R.id.activity_sample_get_config)
    public void getConfigButton_onClick() {
        Toast.makeText(SampleRetrofitActivity.this, "Making a retrofit call to get the config", Toast.LENGTH_SHORT).show();
        Call<Product> productCall = MyApi.getApi().getConfig();
        productCall.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(final Response<Product> response) {
                Toast.makeText(SampleRetrofitActivity.this, "Retrofit response received, storing in toggle", Toast.LENGTH_SHORT).show();
                Product product = response.body();
                Toggle.with(SampleRetrofitActivity.this).getConfig(product);
            }

            @Override
            public void onFailure(Throwable t) {
                showMessage( "Error: " + t != null ? t.getMessage() : "Unknown");
            }
        });
    }

    /**
     * Shows how to check for a particular feature, here we check for the 'mixpanel' feature in the config
     * We can pass in additional flags in the {@link Toggle#check(String)} call like default value etc.
     */
    @OnClick(R.id.activity_sample_check)
    public void checkButton_onClick() {
        showMessage("Checking for the feature");
        Toggle.with(SampleRetrofitActivity.this).check("mixpanel").defaultState(State.ENABLED).start(new cc.soham.toggle.callbacks.Callback() {
            @Override
            public void onStatusChecked(String feature, boolean enabled, String metadata, boolean cached) {
                showMessage("Feature checked");
                updateUiAfterResponse(feature, enabled, metadata, cached);
            }
        });
    }

    /**
     * Update the UI as per the feature state
     * @param feature Name of the feature
     * @param enabled The feature-toggle state of the feature: enabled/disabled
     * @param metadata Metadata attached to the feature
     * @param cached Shows whether this is a cached response or not
     */
    private void updateUiAfterResponse(String feature, boolean enabled, String metadata, boolean cached) {
        featureButton.setText(feature + " is " + (enabled ? "enabled" : "disabled"));
        featureButton.setEnabled(enabled);
        metadataTextView.setText("Metadata: " + metadata);
        cachedTextView.setText("Cached: " + cached);
    }

    /**
     * Simple helper method to show Toasts
     * @param message
     */
    private void showMessage(String message) {
        Toast.makeText(SampleRetrofitActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
