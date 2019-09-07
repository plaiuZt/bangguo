package com.bangguo.app.manager;

import com.bangguo.app.model.User;

/**
 * token管理器
 *
 * @author xuexiang
 * @since 2018/8/6 上午11:45
 */
public class TokenManager {

    private static TokenManager sInstance;

    private String mToken = "";

    private String mSign = "";

    /**
     * 当前登录的用户
     */
    private User mLoginUser;

    private TokenManager() {

    }

    public static TokenManager getInstance() {
        if (sInstance == null) {
            synchronized (TokenManager.class) {
                if (sInstance == null) {
                    sInstance = new TokenManager();
                }
            }
        }
        return sInstance;
    }

    public TokenManager setToken(String token) {
        mToken = token;
        return this;
    }

    public String getToken() {
        return mToken;
    }

    public TokenManager setSign(String sign) {
        mSign = sign;
        return this;
    }

    public String getSign() {
        return mSign;
    }

    public User getLoginUser() {
        return mLoginUser;
    }

    public boolean isUserLogined() {
        return mLoginUser != null;
    }

    public TokenManager setLoginUser(User loginUser) {
        mLoginUser = loginUser;
        return this;
    }
}
