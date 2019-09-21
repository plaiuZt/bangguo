package com.bangguo.app.ui.contract;

import com.bangguo.app.http.JsonResult;
import com.bangguo.app.model.LoginInfo;
import com.bangguo.common.base.BaseModel;
import com.bangguo.common.base.BasePresenter;
import com.bangguo.common.base.BaseView;

import rx.Observable;

public interface LoginContract {
    interface Model extends BaseModel{
        Observable<JsonResult<LoginInfo>> attemptLogin(String username, String password);
    }

    interface View extends BaseView{

    }
    abstract static class Presenter extends BasePresenter<View,Model>{
        public abstract void attemptLogin(String username,String password,boolean isSave);
    }
}
