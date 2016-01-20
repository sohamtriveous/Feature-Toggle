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

        Log.d("Toggle", "M:" + Build.MANUFACTURER + "," + Build.MODEL);

        URL url = null;
        try {
            url = new URL("https://gist.githubusercontent.com/triveous/938d85011cdd5a914f6e/raw/a9c586b9ce052b17ca1f3c0a62f961f49c768d53/toogle.json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
//            Reservoir.init(this, 10000);
//            Reservoir.getAsync(Toggle.PRODUCT_KEY, Product.class, new ReservoirGetCallback<Product>() {
//                        @Override
//                        public void onSuccess(Product object) {
////                            try {
////                                Toggle.init(SampleNetworkActivity.this, object);
////                            } catch (Exception e) {
////                                e.printStackTrace();
////                            }
//
//
////                            try {
////                                Toggle.init(SampleNetworkActivity.this, new Gson().toJson(object, Product.class));
////                            } catch (Exception e) {
////                                e.printStackTrace();
////                            }
//
////                            try {
////                                Toggle.init(SampleNetworkActivity.this, new Gson().toJsonTree(object, Product.class));
////                            } catch (Exception e) {
////                                e.printStackTrace();
////                            }
//
//
//                            Toggle.check("mixpanel").start(new Callback() {
//                                @Override
//                                public void onStatusChecked(String feature, boolean enabled, String metadata, boolean cached) {
//                                    video.setText(feature + " is " + enabled + ", " + metadata + ", " + cached);
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onFailure(Exception e) {
//                            Log.d("Tag", "failure");
//                        }
//                    });

//            Toggle.with(this).getConfig(url);
//            Toggle.with(this).getConfig("{\"product\": \"jsappbasics\", \"features\":\t[\t    {\"name\":\"video\", \"state\":\"disabled\", \"default\": \"enabled\", \"rules\":[\t    \t{\"enabled\": false, \"value\": \t    \t\t{\"apilevel_min\": 21, \"apilevel_max\": 23, \"appversion_min\": 11, \"appversion_max\": 13, \"date_min\": 1452766668000, \"date_max\": 1455566668000, \"buildtype\":\"debug\", \"device\":[{\"manufacturer\":\"xiaomi\",\"model\":\"mi3\"}, {\"manufacturer\":\"samsung\", \"model\":\"s4\"}]}\t\t}, \t    \t{\"enabled\": false, \"value\": {\"appversion_max\": 13}}\t    ]},\t    {\"name\":\"crash_reporting\", \"rules\":[\t    \t   \t{\"enabled\": false, \"value\": \t    \t   \t\t{\"appversion\": 11, \"buildtype\": \"debug\"}\t    \t   \t}\t    \t]\t    },\t    {\"name\":\"mixpanel\",\"state\": \"enabled\"}\t]}");
            Toggle.with(this).check("mixpanel").getLatest().start(new Callback() {
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
