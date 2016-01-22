package cc.soham.togglesample;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

import cc.soham.toggle.Toggle;
import cc.soham.toggle.callbacks.Callback;

/**
 * Created by sohammondal on 20/01/16.
 */
public class SampleNetworkActivity extends AppCompatActivity {
    TextView response;
    TextView video;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sample_network);
        response = (TextView) findViewById(R.id.activity_sample_network_response);
        video = (TextView) findViewById(R.id.activity_sample_network_feature_video);

        try {
            Toggle.with(this).getConfig(new URL(Constants.URL_CONFIG));
            Toggle.with(this).check("video").getLatest().start(new Callback() {
                @Override
                public void onStatusChecked(String feature, boolean enabled, String metadata, boolean cached) {
                    video.setText(feature + " is " + enabled + ", " + metadata + ", " + cached);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
