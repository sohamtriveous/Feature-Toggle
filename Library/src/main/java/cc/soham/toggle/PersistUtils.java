package cc.soham.toggle;

import android.content.Context;
import android.preference.PreferenceManager;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;

import java.net.URL;

import cc.soham.toggle.enums.SourceType;
import cc.soham.toggle.objects.Product;

/**
 * Created by sohammondal on 20/01/16.
 */
public class PersistUtils {
    public static final String KEY_SOURCE_TYPE = "toggle_source_type";
    public static final String KEY_SOURCE_URL = "toggle_source_url";
    public static final String METADATA_DEFAULT = "toggle_metadata_default";
    public static final String PRODUCT_KEY = "toggle_productKey";

    /**
     * Stores the product in disk
     *
     * @param product
     */
    public static void storeProduct(Product product) {
        Reservoir.putAsync(PRODUCT_KEY, product, null);
    }

    /**
     * Retrieves the product from disk
     *
     * @param productReservoirGetCallback
     */
    public static void getProduct(ReservoirGetCallback<Product> productReservoirGetCallback) {
        Reservoir.getAsync(PRODUCT_KEY, Product.class, productReservoirGetCallback);
    }

    public static Product getProductSync() throws Exception {
        return Reservoir.get(PRODUCT_KEY, Product.class);
    }

    /**
     * Store the source type
     *
     * @param sourceType
     */
    public static void storeSourceType(final Context context, SourceType sourceType) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_SOURCE_TYPE, sourceType.name()).apply();
    }

    public static SourceType getSourceType(final Context context) {
        String source = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_SOURCE_TYPE, SourceType.NONE.name());
        return SourceType.valueOf(source);
    }

    /**
     * Store the source url
     *
     * @param url
     */
    public static void storeSourceURL(final Context context, URL url) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_SOURCE_URL, url.toExternalForm()).apply();
    }

    public static String getSourceUrl(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_SOURCE_URL, null);
    }
}
