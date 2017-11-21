package ekta.com.movies_rxjava.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;

import ekta.com.movies_rxjava.MovieApplication;
import ekta.com.movies_rxjava.R;
import ekta.com.movies_rxjava.activities.BaseActivity;
import ekta.com.movies_rxjava.controllers.network.APIInterface;
import ekta.com.movies_rxjava.controllers.network.NetworkController;
import ekta.com.movies_rxjava.controllers.network.UrlResolver;
import ekta.com.movies_rxjava.databinding.ActivityMainBinding;
import ekta.com.movies_rxjava.models.Movie;
import retrofit2.Retrofit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        bindDataToUI();
    }

    private void bindDataToUI() {
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Movie movie = new Movie();
        movie.setImageUrl("https://static.comicvine.com/uploads/original/11127/111271579/5006033-4897645-batman.jpg");
        binding.setMovie(movie);
        
        getDataForPopularMoviesFromApi();

    }

    private void getDataForPopularMoviesFromApi() {

        Retrofit retrofit = NetworkController.getRetrofit();
        APIInterface identityService = retrofit.create(APIInterface.class);


        Observable<JsonElement> jobListData = identityService.makeGetRequest("popular?api_key=74a8c711917fabf892c994dc63136a80");
        NetworkController kala = NetworkController.getInstance(this);
        Observable<JsonElement> jobList = kala.makeServiceReq(jobListData, JsonElement.class, false);
        if (jobList != null) {
            jobList.replay();
            jobList.doOnError(throwable -> {
                Log.e("Error", "Error" + throwable);
            }).subscribeOn(Schedulers.io())

                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(res -> {

                        if (res != null) {
                            try {
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.e("Location  Service Res  ", res.toString());
                        } else {
                            Log.e("Location Service Res", "Data Empty");
                        }

                    });
        }
    }

}
