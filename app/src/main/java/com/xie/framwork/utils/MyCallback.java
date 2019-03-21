package com.xie.framwork.utils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xie on 2018/1/26.
 * Description: 回调封装
 */
public abstract class MyCallback<T> implements Callback<T> {

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.code() == 200) {
            onSuccess(call, response);
        } else {
            String errMsg = null;
            try {
                errMsg = response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            onFailed(call, new Throwable(errMsg));
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable throwable) {
        onFailed(call, throwable);
    }

    public abstract void onSuccess(Call<T> call, Response<T> response);

    public abstract void onFailed(Call<T> call, Throwable throwable);

}
