package ekta.com.movies_rxjava.controllers.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.concurrent.TimeUnit;

import ekta.com.movies_rxjava.R;
import ekta.com.movies_rxjava.activities.BaseActivity;
import ekta.com.movies_rxjava.utils.AppLog;
import ekta.com.movies_rxjava.utils.Utils;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Ekta on 15-11-2017.
 */

public class NetworkController {
    private static NetworkController instance;

    private static Context myContext;

    private int REQUEST_CODE_SUCCESS = 200;


    Context mContext;

    static ProgressDialog dialog;

    private BaseActivity activity;
    private static Context context;
    private static NetworkController controller;

    private static Retrofit retrofit;
    public static APIInterface apiInterface;

    private NetworkController() {
    }

    public static NetworkController getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkController(context);
            myContext = context;
        }

        return instance;
    }

    public static NetworkController getInstance() {
        if (controller == null) controller = new NetworkController();
        return controller;
    }

    public static void init(Context mCtx) {
        context = mCtx;

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        retrofit = new Retrofit.Builder()
                .baseUrl(UrlResolver.getPopularBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(httpClient.build())
                .build();
        apiInterface = retrofit.create(APIInterface.class);
    }

    //getting retrofit object for network calls
    public static Retrofit getRetrofit() {
        if (retrofit == null) {

            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            clientBuilder.connectTimeout(60, TimeUnit.SECONDS);
            clientBuilder.readTimeout(60, TimeUnit.SECONDS);


            // Default Header Interceptor
            HeaderInterceptor headerInterceptor = new HeaderInterceptor();
            clientBuilder.addInterceptor(headerInterceptor);

            if (android.support.compat.BuildConfig.DEBUG) {
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                clientBuilder.addInterceptor(loggingInterceptor);
            }

            retrofit = new Retrofit.Builder()
                    .baseUrl(UrlResolver.getPopularBaseUrl())
                    .client(clientBuilder.build())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


        }
        return retrofit;
    }

    public static NetworkController getInstance(BaseActivity activity1) {
        if (instance == null) {
            instance = new NetworkController(activity1);
            myContext = activity1;
        }

        return instance;
    }

    public NetworkController(BaseActivity activity) {
        this.activity = activity;
        mContext = activity;
    }

    public NetworkController(Context context) {
        mContext = context;
    }

    /* Generic  Get Request
      * with Loader to show data is fetching
       * */
    public <T> Observable<T> makeServiceReq(Observable<JsonElement> serviceObserable, final Class<T> serviceResponseType, Boolean isCache) {

        try {
            if (getActivity() == null) {
                AppLog.i(this.getActivity().getLocalClassName(), "Activity is null");
            } else {
                AppLog.e(this.getActivity().getLocalClassName(), "Activity is not null");
            }

            if (isConnectingToInternet(true)) {

                showLoader();


                return serviceObserable
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(new Func1<Throwable, JsonElement>() {
                            @Override
                            public JsonElement call(Throwable throwable) {
                                if (throwable instanceof HttpException) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        public void run() {
                                            Utils.showToast(getContext(), "Something went wrong. Please try again later" +
                                                    ".");
                                        }
                                    });

                                }
                                AppLog.e("Error ", throwable.getMessage());
                                closeLoader();
                                return null;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Func1<JsonElement, T>() {
                            @Override
                            public T call(JsonElement baseGsonBean) {
                                Log.e("Data", "data acf " + baseGsonBean);
                                try {
                                    if (baseGsonBean != null) {
                                        closeLoader();
                                        Gson gson = new Gson();
                                        return gson.fromJson(baseGsonBean, serviceResponseType);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                return null;

                            }

                        });


            } else {
                Utils.showToast(getContext(), "Internet is not available");
                return serviceObserable.map(new Func1<JsonElement, T>() {
                    @Override
                    public T call(JsonElement jsonElement) {

                        return null;
                    }
                });
            }
        } catch (Exception e) {
            AppLog.e("Hi " + this.getClass().getName(), e.getMessage());
            return null;
        }

    }

    /**
     * Checking for all possible internet providers
     **/
    public boolean isConnectingToInternet(boolean isShowMessage) {

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Network[] networks = connectivityManager.getAllNetworks();
                NetworkInfo networkInfo;
                for (Network mNetwork : networks) {
                    networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                    if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                        return true;
                    }
                }
            } else {
                if (connectivityManager != null) {
                    boolean haveConnectedWifi = false;
                    boolean haveConnectedMobile = false;
                    //noinspection deprecation
                    NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                    if (info != null) {
                        for (NetworkInfo ni : info) {
                            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                                if (ni.isConnected())
                                    haveConnectedWifi = true;
                            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                                if (ni.isConnected())
                                    haveConnectedMobile = true;
                        }
                        return haveConnectedWifi || haveConnectedMobile;
                    }
                }
            }
            if (isShowMessage) {
                Utils.showToast(getActivity(), getActivity().getString(R.string.mesg_network_not_available));
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*Generic Post Request
    * no loader because of post request
    * */
    public <T> Observable<T> makeBackgroundServiceReq(Observable<JsonElement> serviceObserable, final Class<T> serviceResponseType, Boolean isCache) {

        try {


            Observable<T> webServiceData1 = serviceObserable
                    .timeout(new Func1<JsonElement, Observable<Object>>() {
                        @Override
                        public Observable<Object> call(JsonElement jsonElement) {

                            return null;
                        }
                    })
                    .onErrorReturn(new Func1<Throwable, JsonElement>() {
                        @Override
                        public JsonElement call(Throwable throwable) {

                            return null;
                        }
                    })
                    .onErrorResumeNext(new Func1<Throwable, Observable<? extends JsonElement>>() {
                        @Override
                        public Observable<? extends JsonElement> call(Throwable throwable) {
                            return Observable.empty();
                        }
                    })
                    .doOnError(new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            AppLog.e("Error", throwable.getMessage());
                            closeLoader();
                        }
                    })
                    .map(new Func1<JsonElement, T>() {
                        @Override
                        public T call(JsonElement baseGsonBean) {
                            Log.e("Data", "data " + baseGsonBean);
                            if (baseGsonBean != null) {
                                Gson gson = new Gson();
                                return gson.fromJson(String.valueOf(baseGsonBean), serviceResponseType);
                            }
                            return null;

                        }

                    })
                    .filter(new Func1<T, Boolean>() {
                        @Override
                        public Boolean call(T baseGsonBean) {


                            return true;

                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io());


            return webServiceData1;
//


        } catch (Exception e) {
            AppLog.e(this.getClass().getName(), e.getMessage());
            return null;
        }

    }


    public BaseActivity getActivity() {
        return activity;
    }

    private void showLoader() {
        if (!(getActivity().isFinishing())) {
            if (dialog != null && dialog.isShowing()) {
                return;
            }
            dialog = ProgressDialog.show(getActivity(), "",
                    getActivity().getString(R.string.mesg_loading), true);

        }
    }

    private void closeLoader() {
        try {
            dialog.dismiss();
            dialog = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Context getContext() {
        return mContext;
    }
}