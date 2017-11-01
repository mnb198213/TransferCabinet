package com.baselib.http;

import android.content.Context;
import android.text.TextUtils;

import com.baselib.http.interceptor.HttpLoggerInterceptor;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Spirit on 2017/4/7 16:40.
 */

public class RetrofitClient {

    private static boolean isDebug;

    private static final int CONNECT_TIMEOUT = 30;
    private static final int READ_TIMEOUT = 60;
    private static final int WRITE_TIMEOUT = 60;

    private static final int DEFAULT_DIR_CACHE = 1024 * 1024 *10;
    private Retrofit mRetrofit;
    private String baseUrl;

    private static Context mContext;
    private static RetrofitClient sInstance;
    private static List<Interceptor> interceptors;

    private RetrofitClient(Context context) {
        this(context, null);
    }

    public static void setDebug(boolean debug){
        isDebug = debug;
    }

    private RetrofitClient(Context context, String url) {

        if (TextUtils.isEmpty(url)) {
            baseUrl = url;
        }

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient().newBuilder();
        okHttpBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);

        if (isDebug) {

            HttpLoggerInterceptor logging = new HttpLoggerInterceptor();
            //            okHttpBuilder.addNetworkInterceptor(logging);
            okHttpBuilder.addInterceptor(logging);
        }
        /**
         * 添加拦截器
         */
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                okHttpBuilder.addInterceptor(interceptor);
            }
        }

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        okHttpBuilder.cookieJar(new JavaNetCookieJar(cookieManager));

        OkHttpClient okHttpClient = okHttpBuilder.build();

        mRetrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    private static class RetrofitClientHolder{
        private static final RetrofitClient INSTANCE = new RetrofitClient(mContext);
    }

    public static RetrofitClient getInstance(Context context){
        if (context != null) {
            mContext = context;
        }
        return RetrofitClientHolder.INSTANCE;
    }

    public static RetrofitClient getInstance(Context context, String url) {
        if (context != null) {
            mContext = context;
        }
        sInstance = new RetrofitClient(context, url);
        return sInstance;
    }


    public static void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    private Map<String, Object> arrayMap = new HashMap<>();


    public <T> T createService(Class<T> clazz) {
        T t;
        if (!arrayMap.containsKey(clazz.getSimpleName())) {
            t = mRetrofit.create(clazz);
            arrayMap.put(clazz.getSimpleName(), t);
        } else {
            t = (T) arrayMap.get(clazz.getSimpleName());
        }

        return t;
    }

    public void clear(){
        arrayMap.clear();
        interceptors.clear();
    }

}
