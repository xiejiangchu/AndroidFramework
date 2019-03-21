package com.xie.framwork.utils;

import android.text.TextUtils;

import com.xie.framwork.BuildConfig;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Util;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by xie on 2018/1/9.
 * Description:
 */
public class OkHttpUtils {

    private static String ACCESS_TOKEN = "";
    private static String REFRESH_TOKEN = "";
    private final static String TOKEN_TYPE = "bearer";

    public synchronized static void clearLogin() {
        OkHttpUtils.ACCESS_TOKEN = null;
        OkHttpUtils.REFRESH_TOKEN = null;
    }

    public synchronized static void setToken(String accessToken, String refreshToken) {
        ACCESS_TOKEN = accessToken;
        REFRESH_TOKEN = refreshToken;
    }

    public synchronized static void setAccessToken(String accessToken) {
        ACCESS_TOKEN = accessToken;
    }

    public synchronized static void setRefreshToken(String refreshToken) {
        REFRESH_TOKEN = refreshToken;
    }

    public static String getAccessToken() {
        return ACCESS_TOKEN;
    }

    public static String getRefreshToken() {
        return REFRESH_TOKEN;
    }

    public static String getTokenType() {
        return TOKEN_TYPE;
    }

    public static OkHttpClient.Builder builder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(getSSLSocketFactory())
                .hostnameVerifier(getHostnameVerifier());

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor LoginInterceptor = new HttpLoggingInterceptor();
            LoginInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(LoginInterceptor); //添加retrofit日志打印
        }
        builder.authenticator(new TokenAuthenticator());

        builder.connectTimeout(5, TimeUnit.SECONDS);
        builder.readTimeout(5, TimeUnit.SECONDS);
        builder.writeTimeout(5, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);

        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                final Request.Builder builder = chain.request().newBuilder();
                String url = chain.request().url().encodedPath().toString();
                if (url.endsWith(Constants.TOKEN_AUTH_URL)) {
                    return chain.proceed(builder.build());
                } else if (!TextUtils.isEmpty(ACCESS_TOKEN)) {
                    builder.header("Authorization", TOKEN_TYPE + " " + ACCESS_TOKEN);
                    return chain.proceed(builder.build());
                }
                return new Response.Builder()
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .code(401)
                        .message("无授权信息")
                        .body(Util.EMPTY_RESPONSE)
                        .sentRequestAtMillis(-1L)
                        .receivedResponseAtMillis(System.currentTimeMillis())
                        .build();
            }
        });

        return builder;
    }

    public static OkHttpClient.Builder glideBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(getSSLSocketFactory())
                .hostnameVerifier(getHostnameVerifier());
        HttpLoggingInterceptor LoginInterceptor = new HttpLoggingInterceptor();
        LoginInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        builder.addInterceptor(LoginInterceptor);
        return builder;
    }

    private static SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static TrustManager[] getTrustManagers() {
        return new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        }};
    }

    private static HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true; // 直接返回true，默认verify通过
            }
        };
    }
}