package com.bangguo.app.http;

import com.bangguo.app.model.LoginBannerPic;
import com.bangguo.app.model.LoginInfo;
import com.bangguo.app.model.request.LoginRequest;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface Api {

    @GET("ad/loginbanner")
    Call<JsonResult<LoginBannerPic>> getBannerPic();

    @GET("auth/checktoken")
    Call<JsonResult<String>> checkToken(@Query("token") String token);

    @POST("auth/applogin")
    Call<JsonResult<LoginInfo>> appLogin(@Body LoginRequest body);

    @POST("auth/applogin")
    Call<JsonResult<LoginInfo>> appLogin(@QueryMap HashMap<String, String> loginInfo);
}
