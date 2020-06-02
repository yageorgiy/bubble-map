package com.yageorgiy.bubblemap;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.apollographql.apollo.ApolloClient;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BubbleMapApplication extends Application {

    private static final String BASE_URL = "https://bubblemap.prj.yageorgiy.ru/";
    private ApolloClient apolloClient;
    private String currentToken = "";

    @Override
    public void onCreate() {
        super.onCreate();
        rebuildApolloClient();
    }

    public ApolloClient apolloClient() {
        return apolloClient;
    }
    public void rebuildApolloClient() {

        final Boolean isAuthorized = isAuthorized();
        final String authorization = getCurrentToken();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder builder = original.newBuilder().method(original.method(), original.body());

                        if (isAuthorized)
                            builder.header("Authorization", "Bearer " + authorization);

                        return chain.proceed(builder.build());
                    }
                })
                .build();

        ApolloClient.Builder build = ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttpClient);
//                .normalizedCache(normalizedCacheFactory, cacheKeyResolver)
//                .subscriptionTransportFactory(new WebSocketSubscriptionTransport.Factory(SUBSCRIPTION_BASE_URL, okHttpClient))
        apolloClient = build.build();
    }
    public String getCurrentToken() { return currentToken; }
    public void setCurrentToken(String token) {
        currentToken = token;

        rebuildApolloClient();
    }
    public boolean isAuthorized(){
        return !getCurrentToken().equals("");
    }

    private static final Handler mHandler = new Handler();

    public static final void runOnUiThread(Runnable runnable) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
        } else {
            mHandler.post(runnable);
        }
    }

}
