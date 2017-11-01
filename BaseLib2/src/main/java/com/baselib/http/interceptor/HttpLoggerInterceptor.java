package com.baselib.http.interceptor;

import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by Spirit on 2016/12/28 17:06.
 */

public class HttpLoggerInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final Logger logger;


    public HttpLoggerInterceptor() {
        this(Logger.DEFAULT);
    }

    public HttpLoggerInterceptor(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        long t1 = System.nanoTime();
        //the request body
        RequestBody requestBody = request.body();


        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;

        if (requestBody != null) {

            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
            logger.log(requestStartMessage);

            if (requestBody.contentType() != null) {
                logger.log("Content-Type: " + requestBody.contentType());
            }
            if (requestBody.contentLength() != -1) {
                logger.log("Content-Length: " + requestBody.contentLength());
            }
        }



        Response response = chain.proceed(request);
        long t2 = System.nanoTime();
        int time = (int) ((t2 - t1) / 1e6d);

        ResponseBody responseBody = response.body();
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();
        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }

        String dataJson = buffer.clone().readString(charset);


        logger.log("--------------------------BEGIN------------------------------");
        logger.log(request.method() + "-->" + request.url().toString());
        logger.log("time->" + time + "ms");
        logger.log("request headers->" + request.headers());
        logger.log("response code->" + response.code());
        logger.log("response headers->" + response.headers());

        if (request.method().equals("POST")) {
            Request copyRequest = request.newBuilder().build();
            Buffer bufferbody = new Buffer();
            copyRequest.body().writeTo(bufferbody);
            logger.log("POST params:" + bufferbody.readUtf8());
        }

        logger.log("response body->" + dataJson);
        logger.log("----------------------------END------------------------------");

        return response;
    }


    public interface Logger {
        void log(String message);

        /**
         * A {@link Logger} defaults output appropriate for the current platform.
         */
        Logger DEFAULT = new Logger() {
            @Override
            public void log(String message) {
                Log.i("APILog", message);
            }
        };
    }
}
