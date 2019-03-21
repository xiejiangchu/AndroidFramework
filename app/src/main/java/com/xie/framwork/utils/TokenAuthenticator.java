package com.xie.framwork.utils;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.xie.framwork.MyApplication;
import com.xie.framwork.bean.AccessTokenDto;
import com.xie.framwork.request.ApiService;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;

/**
 * Title:  401重试获取token
 * Description: 401重试获取token
 *
 * @author xie
 * @create 2019/3/15
 * @update 2019/3/15
 */
public class TokenAuthenticator implements Authenticator {

    private final String TAG = "TokenAuthenticator";

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        if (responseCount(response) >= 3) {
            return null; // If we've failed 3 times, give up.
        }
        if (TextUtils.isEmpty(OkHttpUtils.getRefreshToken())) {
            return null;
        }
        // 通过一个特定的接口获取新的token，此处要用到同步的retrofit请求
        ApiService service = RetrofitManager.getInstance().createReq(ApiService.class);
        Call<String> call = service.refreshToken(OkHttpUtils.getRefreshToken(), Constants.TOKEN_REFRESH_GRANT_TYPE, Constants.TOKEN_SCOPE, Constants.TOKEN_CLIENT_ID, Constants.TOKEN_CLIENT_SECRET);
        retrofit2.Response<String> resp = call.execute();
        if (resp.code() == 401) {
            Log.e(TAG, "token刷新失败");
            OkHttpUtils.clearLogin();
            Intent intent = new Intent();
            intent.setAction(MyApplication.BROADCAST_LOGOUT);
            MyApplication.getInstance().sendBroadcast(intent);
            return null;
        } else if (resp.code() == 200) {
            try {
                AccessTokenDto accessTokenDto = new Gson().fromJson(resp.body(), AccessTokenDto.class);
                if (accessTokenDto == null) {
                    Toasty.error(MyApplication.getInstance(), "服务器返回异常").show();
                    return null;
                } else {
                    Log.d(TAG, "token刷新成功");
                    OkHttpUtils.setToken(accessTokenDto.getAccess_token(), accessTokenDto.getRefresh_token());
                    return response.request().newBuilder()
                            .header("Authorization", OkHttpUtils.getTokenType() + " " + OkHttpUtils.getAccessToken())
                            .build();
                }
            } catch (JsonSyntaxException js) {
                Toasty.error(MyApplication.getInstance(), "登录异常").show();
            }
        } else {
            Log.e(TAG, "token刷新失败");
            OkHttpUtils.clearLogin();
            Intent intent = new Intent();
            intent.setAction(MyApplication.BROADCAST_LOGOUT);
            MyApplication.getInstance().sendBroadcast(intent);
            return null;
        }
        return null;
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}
