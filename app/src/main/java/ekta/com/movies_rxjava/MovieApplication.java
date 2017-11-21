package ekta.com.movies_rxjava;

import android.app.Application;
import android.support.compat.*;

import java.util.concurrent.TimeUnit;

import ekta.com.movies_rxjava.controllers.network.HeaderInterceptor;
import ekta.com.movies_rxjava.controllers.network.NetworkController;
import ekta.com.movies_rxjava.controllers.network.UrlResolver;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ekta on 20-11-2017.
 */

public class MovieApplication extends Application {

    static Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        UrlResolver.setBaseUrl();
        NetworkController.init(getApplicationContext());
    }

}
