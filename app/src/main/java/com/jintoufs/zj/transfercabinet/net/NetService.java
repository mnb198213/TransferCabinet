package com.jintoufs.zj.transfercabinet.net;

import com.jintoufs.zj.transfercabinet.config.IpConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

/**
 * Created by zoujiang on 2017/2/15.
 */

public class NetService {
    public static final String HEAD_TOKEN = "TOKEN";
    private static ApiService apiService;

    public static ApiService getApiService() {
        if (apiService == null){
            Interceptor interceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    //从shared中将登陆时保存的token取出来，每此请求都需要带token
                    //为请求添加头添加token
                    String token = "";
                    Request request = chain.request().newBuilder().addHeader(HEAD_TOKEN,token).build();
                    Response response = chain.proceed(request);
                    return response;
                }
            };
            OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
            Retrofit retrofit = new Retrofit.Builder().baseUrl(IpConfig.IP_HOST)
                    .addConverterFactory(JsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}
