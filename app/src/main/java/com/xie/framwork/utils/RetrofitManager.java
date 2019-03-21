package com.xie.framwork.utils;

import com.xie.framwork.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by xie on 2018/1/4.
 * Description:
 */
public class RetrofitManager {
    private final static String API_GATE_CHECK = "http://10.84.147.134:8080/";
    private final static String API_GATE_DEVELOP = "http://10.84.147.134:8080/";
    private final static String API_GATE_PRODUCT = "http://10.84.147.134:8080/";
    private Retrofit mRetrofit;

    private RetrofitManager() {
        initRetrofit();
    }

    private static class Holder {
        static RetrofitManager INSTATNCE = new RetrofitManager();
    }

    public static synchronized RetrofitManager getInstance() {
        return Holder.INSTATNCE;
    }

    private void initRetrofit() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(getApiUrl())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpUtils.builder().build())
                .build();
    }

    public <T> T createReq(Class<T> reqServer) {
        return mRetrofit.create(reqServer);
    }

    public static String getApiUrl() {
        if (BuildConfig.ENV_TYPE == EnvType.DEVELOP) {
            return API_GATE_DEVELOP;
        } else if (BuildConfig.ENV_TYPE == EnvType.CHECK) {
            return API_GATE_CHECK;
        } else if (BuildConfig.ENV_TYPE == EnvType.PRODUCT) {
            return API_GATE_PRODUCT;
        }
        return API_GATE_PRODUCT;
    }
}
