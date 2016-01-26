package cc.soham.toggle.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import cc.soham.toggle.callbacks.SetConfigCallback;

/**
 * Created by sohammondal on 19/01/16.
 */
public class NetworkOperations {
    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    public static String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        String contentAsString;
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            contentAsString = readIt(is);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return contentAsString;
    }

    // Reads an InputStream and converts it to a String.
    private static String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            return total.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Initiate a {@link SetConfigCallback} if needed
     * @param setConfigResponse
     * @param setConfigCallback
     */
    public static void initiateCallback(SetConfigResponse setConfigResponse, SetConfigCallback setConfigCallback) {
        // make the callback if configured
        if (setConfigCallback != null && setConfigResponse != null) {
            setConfigCallback.onConfigReceived(setConfigResponse.config, setConfigResponse.cached);
        }
    }
}
