package com.xie.framwork.request;


import com.xie.framwork.utils.Constants;

import retrofit.lifecycle.common.RetrofitInterface;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

@RetrofitInterface
public interface ApiService {

    @GET(Constants.TOKEN_AUTH_URL)
    Call<String> getAccessToken(@Query("username") String username,
                                @Query("password") String password,
                                @Query("grant_type") String grant_type,
                                @Query("scope") String scope,
                                @Query("client_id") String client_id,
                                @Query("client_secret") String client_secret);

    @GET(Constants.TOKEN_AUTH_URL)
    Call<String> refreshToken(@Query("refresh_token") String refreshToken,
                              @Query("grant_type") String grant_type,
                              @Query("scope") String scope,
                              @Query("client_id") String client_id,
                              @Query("client_secret") String client_secret);
}
