package com.bangguo.app.http;

import com.bangguo.app.model.LoginBannerPic;
import com.bangguo.app.model.LoginInfo;
import com.bangguo.app.model.request.LoginRequest;

import java.util.HashMap;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface ApiService {

    @GET("ad/loginbanner")
    Call<JsonResult<LoginBannerPic>> getBannerPic(@Header("Cache-Control") String cacheControl);

    @GET("auth/checktoken")
    Call<JsonResult<String>> checkToken(@Header("Cache-Control") String cacheControl,
                                        @Query("token") String token);

    @POST("auth/applogin")
    Observable<JsonResult<LoginInfo>> appLogin(@Header("Cache-Control") String cacheControl,
                                               @Body LoginRequest body);

    @POST("auth/applogin")
    Call<JsonResult<LoginInfo>> appLogin(@Header("Cache-Control") String cacheControl,
                                         @QueryMap HashMap<String, String> loginInfo);

    @GET("auth/getSMScode")
    Call<JsonResult<String>> getSmsCode(@Header("Cache-Control") String cacheControl,
                                        @Query("type") String type);
}
