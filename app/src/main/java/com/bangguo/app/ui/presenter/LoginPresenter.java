package com.bangguo.app.ui.presenter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.bangguo.app.App;
import com.bangguo.app.common.constants.Constants;
import com.bangguo.app.common.constants.SPConstants;
import com.bangguo.app.common.enums.ParamsType;
import com.bangguo.app.common.utils.AlertDialogUtils;
import com.bangguo.app.common.utils.Des3Utils;
import com.bangguo.app.common.utils.KeyboardToolUtils;
import com.bangguo.app.common.utils.PreferenceUtils;
import com.bangguo.app.http.JsonResult;
import com.bangguo.app.manager.ActivityLifecycleManager;
import com.bangguo.app.model.LoginInfo;
import com.bangguo.app.ui.activity.LoginActivity;
import com.bangguo.app.ui.activity.Main2Activity;
import com.bangguo.app.ui.activity.MainActivity;
import com.bangguo.app.ui.contract.LoginContract;
import com.bangguo.common.base.BasePresenter;
import com.bangguo.common.baserx.RxSubscriber;
import com.xuexiang.xui.utils.StatusBarUtils;

import org.litepal.LitePal;

import java.util.HashMap;
import java.util.Map;

import rx.Observer;
import rx.Subscriber;

public class LoginPresenter extends LoginContract.Presenter {
    private boolean isCheckBox;
    @Override
    public void attemptLogin(String username, String password,boolean isSave) {
        mModel.attemptLogin(username,password)
                .subscribe(new RxSubscriber<JsonResult<LoginInfo>>(mContext,false){

                    @Override
                    protected void _onNext(JsonResult<LoginInfo> response) {
                        LoginInfo loginInfo = response.getData();
                        if(loginInfo != null){
                            isCheckBox = isSave;
                            saveUserDataToSharePreference(loginInfo);
                            whichUserType(loginInfo);
                        }
                    }
                    @Override
                    protected void _onError(String message) {
                        Log.i("异常",message);
                    }
                });
    }

    private void whichUserType(LoginInfo loginInfo){
        int userType = loginInfo.getUserType();
        switch (userType){
            case Constants.USER_APP_DRIVER:
                skipToOwnerActivity();
                break;
            case Constants.USER_APP_OWNER:
                skipToDriverActivity();
                break;
            default:
                AlertDialogUtils.showDialog(
                        mContext,
                        "登录提示：",
                        "该账户未确认组织类型，无法登录，请与管理员联系！",
                        "重新登录");
        }
    }

    private void skipToOwnerActivity() {
        // 隐藏软键盘
        KeyboardToolUtils.hideSoftInput((Activity) mContext);
        // 退出界面之前把状态栏还原为白色字体与图标
        StatusBarUtils.setStatusBarDarkMode((Activity) mContext);
        Intent intent = new Intent(mContext, Main2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
        // 结束所有Activity
        ActivityLifecycleManager.get().finishAllActivity();
    }
    private void skipToDriverActivity(){
        // 隐藏软键盘
        KeyboardToolUtils.hideSoftInput((Activity) mContext);
        // 退出界面之前把状态栏还原为白色字体与图标
        StatusBarUtils.setStatusBarDarkMode((Activity) mContext);
        Intent intent = new Intent(mContext, Main2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
        // 结束所有Activity
        ActivityLifecycleManager.get().finishAllActivity();
    }

    /**
     * 根据返回的结果，存储用户必要的数据到SharePreference
     * */
    private void saveUserDataToSharePreference(LoginInfo loginInfo) {
        //保存用户账号密码到sharePreference
        PreferenceUtils.setString(SPConstants.USER_NAME, loginInfo.getUserName());
        PreferenceUtils.remove(SPConstants.PASSWORD);
        // 如果用户点击了“记住密码”，保存密码
        if (isCheckBox) {
            String psd = Des3Utils.encode(loginInfo.getPassword());
            PreferenceUtils.setString(SPConstants.PASSWORD, psd);
        }
        // 保存用户名字
        PreferenceUtils.setString(SPConstants.USER, loginInfo.getName());
        // 保存用户ID
        PreferenceUtils.setInt(SPConstants.USER_ID, loginInfo.getId());
        // 保存角色
        PreferenceUtils.setString(SPConstants.ROLE_NAMES,loginInfo.getRoleNames());

        // 保存部门
        if (loginInfo.getFirstDepId() != null) {
            PreferenceUtils.setString(SPConstants.FIRST_DEP_ID,loginInfo.getFirstDepId());
        }

        //保存token到sharePreference
        PreferenceUtils.setString(SPConstants.TOKEN, loginInfo.getToken());
        //获取token,将token保存至Http全局请求参数中
        Map<String,String> params = new HashMap<>();
        params.put(Constants.APP_TOKEN,PreferenceUtils.getString(SPConstants.TOKEN,""));
        App.getInstance().setCommonParam(params, ParamsType.PARAM_MAP);
        //启动本地数据库
        LitePal.getDatabase();
    }
}
