package ekta.com.movies_rxjava.controllers.network;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Ekta on 21-11-2017.
 */

public class HeaderInterceptor implements Interceptor {



    @Override
    public Response intercept (Interceptor.Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request normalReq = originalRequest.newBuilder()
                .method(originalRequest.method(), originalRequest.body())
                .build();

        return chain.proceed(normalReq);
    }




}