package cc.soham.togglesample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

import cc.soham.toggle.Toggle;
import cc.soham.toggle.callbacks.GetConfigCallback;
import cc.soham.toggle.objects.Product;

/**
 * Created by sohammondal on 20/01/16.
 */
public class SampleNetworkActivity extends AppCompatActivity {
    TextView response;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sample_network);
        response = (TextView) findViewById(R.id.activity_sample_network_response);

        URL url = null;
        try {
            url = new URL("https://gist.githubusercontent.com/triveous/938d85011cdd5a914f6e/raw/df7f225ea69caba7c00895e4b4452c9923d7c6c3/toogle.json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            Toggle.init(this, url, new GetConfigCallback() {
                @Override
                public void onConfigReceived(Product product) {
                    if(product!=null) {
                        response.setText("Product = " + product.getProduct());
                    } else {
                        response.setText("Null product");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
