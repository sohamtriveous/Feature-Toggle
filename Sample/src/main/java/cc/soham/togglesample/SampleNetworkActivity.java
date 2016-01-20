package cc.soham.togglesample;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;
import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;

import cc.soham.toggle.Toggle;
import cc.soham.toggle.callbacks.Callback;
import cc.soham.toggle.callbacks.GetConfigCallback;
import cc.soham.toggle.objects.Product;

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
//                            Toggle.check("mixpanel").launch(new Callback() {
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


            Toggle.init(this, url);
            Toggle.check("mixpanel").getLatest().launch(new Callback() {
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
