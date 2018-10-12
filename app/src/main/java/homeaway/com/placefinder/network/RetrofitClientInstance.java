package homeaway.com.placefinder.network;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import homeaway.com.placefinder.util.Config;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClientInstance {

    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(){
        if(retrofit == null){

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            // Builds Retrofit and FoursquareService objects for calling the Foursquare API and parsing with GSON
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(Config.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create()) //converter factory for serialization and deserialization of objects
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //Adapter factory for supporting service method return types
                    .build();
        }
        return retrofit;
    }
}
