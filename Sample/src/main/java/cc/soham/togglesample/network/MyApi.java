package cc.soham.togglesample.network;

import cc.soham.toggle.objects.Config;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.GET;

/**
 * Created by sohammondal on 22/01/16.
 */
public class MyApi {
    private static MyApiService myApiService;

    public static MyApiService getApi() {
        if(myApiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://gist.githubusercontent.com/triveous/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            myApiService = retrofit.create(MyApiService.class);
        }
        return myApiService;
    }

    public interface MyApiService {
        @GET("938d85011cdd5a914f6e/raw/a9c586b9ce052b17ca1f3c0a62f961f49c768d53/toogle.json")
        Call<Config> getConfig();
    }
}
