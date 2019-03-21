package com.xie.framwork.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import retrofit.lifecycle.common.RetrofitLifecycle;
import retrofit2.Call;

/**
 * Description: Helper class to get proxy api
 * Created by Andy on 2017/7/4
 */

public class RetrofitHelper {

    private static final Map<Class, Object> sInterfaceImplementCache = new ConcurrentHashMap<>();

    public static <T> T get(Class<T> apiInterface) {
        T apiImplement;
        Object cacheApiImplement = sInterfaceImplementCache.get(apiInterface);
        if (cacheApiImplement != null) {
            apiImplement = apiInterface.cast(cacheApiImplement);
        } else {
            apiImplement = RetrofitManager.getInstance().createReq(apiInterface);
            sInterfaceImplementCache.put(apiInterface, apiImplement);
        }

        return RetrofitLifecycle.getProxyInterface(apiInterface, apiImplement);

    }

    public static void cancel(Object retrofitAPI, Call... excludes) {
        RetrofitLifecycle.cancelAll(retrofitAPI, excludes);
    }
}
