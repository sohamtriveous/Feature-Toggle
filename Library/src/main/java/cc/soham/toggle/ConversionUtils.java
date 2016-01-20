package cc.soham.toggle;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import cc.soham.toggle.objects.Product;

/**
 * Created by sohammondal on 20/01/16.
 */
public class ConversionUtils {
    /**
     * Converts String to Product
     *
     * @param productInString
     * @return
     */
    public static Product convertStringToProduct(String productInString) throws JsonSyntaxException {
        return new Gson().fromJson(productInString, Product.class);
    }

    /**
     * Converts JSON to Product
     *
     * @param productInJson
     * @return
     */
    public static Product convertJSONObjectToProduct(JsonElement productInJson) {
        return new Gson().fromJson(productInJson, Product.class);
    }
}
