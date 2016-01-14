package cc.soham.toggle;

import android.content.Context;

import com.anupcowkur.reservoir.Reservoir;

import org.json.JSONObject;

import java.net.URL;

import cc.soham.toggle.objects.Product;

/**
 * Created by sohammondal on 14/01/16.
 */
public class Toggle {
    public static void init(final Context context) throws Exception {
        Reservoir.init(context, 10000);
    }

    public static void init(final Context context, String productName, String product) throws Exception {
        init(context);
        getConfig(productName, product);
    }

    public static void init(final Context context, String productName, JSONObject product) throws Exception {
        init(context);
        getConfig(productName, product);
    }

    public static void init(final Context context, String productName, Product product) throws Exception {
        init(context);
        getConfig(productName, product);
    }

    public static void init(final Context context, String productName, URL productUrl) throws Exception {
        init(context);
        getConfig(productName, productUrl);
    }

    public static void getConfig(String productName, String product) {

    }

    public static void getConfig(String productName, JSONObject product) {

    }

    public static void getConfig(String productName, Product product) {

    }

    public static void getConfig(String productName, URL productUrl) {

    }
}
