package ekta.com.movies_rxjava.controllers.network;

import ekta.com.movies_rxjava.BuildConfig;

/**
 * Created by Ekta on 20-11-2017.
 */

public class UrlResolver {

    public static String POPULAR_BASE_URL;

    //Set base urls
    public static void setBaseUrl() {
        POPULAR_BASE_URL = BuildConfig.server_url;
    }

    //Get base urls
    public static String getPopularBaseUrl() {
        return POPULAR_BASE_URL;
    }
}
