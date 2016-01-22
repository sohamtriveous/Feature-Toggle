package cc.soham.togglesample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import cc.soham.toggle.Toggle;
import cc.soham.toggle.enums.State;
import cc.soham.toggle.objects.Product;
import cc.soham.togglesample.network.MyApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sohammondal on 22/01/16.
 */
public class SampleRetrofitActivity extends AppCompatActivity {
    TextView response;
    TextView video;
    Button featureButton;
    TextView metadataTextView;
    TextView cachedTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_network);

        response = (TextView) findViewById(R.id.activity_sample_network_response);
        video = (TextView) findViewById(R.id.activity_sample_network_feature_video);
        metadataTextView = (TextView) findViewById(R.id.activity_sample_network_feature_metadata);
        cachedTextView = (TextView) findViewById(R.id.activity_sample_network_feature_cached);
        featureButton = (Button) findViewById(R.id.activity_sample_network_feature);

        Call<Product> productCall = MyApi.getApi().getConfig();
        productCall.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Response<Product> response) {
                Product product = response.body();
                Toggle.with(SampleRetrofitActivity.this).getConfig(product);
                Toggle.with(SampleRetrofitActivity.this).check("mixpanel").defaultState(State.ENABLED).start(new cc.soham.toggle.callbacks.Callback() {
                    @Override
                    public void onStatusChecked(String feature, boolean enabled, String metadata, boolean cached) {
                        featureButton.setText(feature + " is " + (enabled ? "enabled" : "disabled"));
                        featureButton.setEnabled(enabled);
                        metadataTextView.setText("Metadata: " + metadata);
                        cachedTextView.setText("Cached: " + cached);
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar.make(findViewById(android.R.id.content), "Error: " + t != null ? t.getMessage() : "Unknown", Snackbar.LENGTH_INDEFINITE).show();
            }
        });
    }
}
