package ekta.com.movies_rxjava.controllers.network;

import com.google.gson.JsonElement;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Ekta on 14-11-2017.
 */

public interface APIInterface {

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST
    Observable<JsonElement> getPopularMovieList(@Url String serviceName);

    @GET
    Observable<JsonElement> makeGetRequest(@Url String serviceName);
}
