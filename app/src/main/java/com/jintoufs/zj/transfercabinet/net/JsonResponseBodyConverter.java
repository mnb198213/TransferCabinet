package com.jintoufs.zj.transfercabinet.net;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by YWH on 2016/12/7.
 */
public class JsonResponseBodyConverter<T>implements Converter<ResponseBody,T> {

    private final Gson gson;
    private final TypeAdapter<T> adapter;

    JsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String sss = value.string();
        // JsonReader jsonReader = gson.newJsonReader(value.charStream());
        JsonReader jsonReader = gson.newJsonReader(new StringReader(sss));
        try {
            return adapter.read(jsonReader);
        } finally {
            value.close();
        }
    }
}
