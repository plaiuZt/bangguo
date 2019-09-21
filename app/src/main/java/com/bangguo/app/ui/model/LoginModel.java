package com.bangguo.app.ui.model;

import android.util.Log;

import com.bangguo.app.common.utils.Des3Utils;
import com.bangguo.app.http.Api;
import com.bangguo.app.http.ApiService;
import com.bangguo.app.http.HostType;
import com.bangguo.app.http.JsonResult;
import com.bangguo.app.model.LoginInfo;
import com.bangguo.app.model.request.LoginRequest;
import com.bangguo.app.ui.contract.LoginContract;
import com.bangguo.common.baserx.RxSchedulers;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;

public class LoginModel implements LoginContract.Model {

    @Override
    public Observable<JsonResult<LoginInfo>> attemptLogin(String username, String password) {
        //密码加密
        String newPassword = Des3Utils.encode(password);
        LoginRequest param = new LoginRequest();
        param.setUserName(username);
        param.setPassword(newPassword);

        return Api.getDefault(HostType.TMS_MANAGE_API).appLogin(Api.getCacheControl(),param);
    }
}
