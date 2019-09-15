package com.bangguo.app.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.bangguo.app.App;
import com.bangguo.app.R;
import com.bangguo.app.common.constants.SPConstants;
import com.bangguo.app.common.utils.PreferenceUtils;
import com.bangguo.app.http.Api;
import com.bangguo.app.http.JsonResult;
import com.bangguo.app.manager.ActivityLifecycleManager;
import com.bangguo.app.model.LoginBannerPic;
import com.bangguo.common.utils.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.bgabanner.BGABanner;
import okhttp3.OkHttpClient;
import rx.Observer;

public class WelcomeActivity extends AppCompatActivity implements BGABanner.Adapter<ImageView, LoginBannerPic.ImageAdModel>,BGABanner.Delegate<ImageView,LoginBannerPic.ImageAdModel> {
    private Handler handler = new Handler();
    /*
     * 启动模式：
     * 1：启动界面时间与App加载时间相等
     * 2：设置启动界面2秒后跳转
     * */
    private final static int SELECT_MODE = 1;
    private OkHttpClient.Builder builder;

    private static final String TAG = WelcomeActivity.class.getSimpleName();
    @BindView(R.id.banner_guide_background)
    public BGABanner mBackgroundBanner;
    @BindView(R.id.banner_guide_foreground)
    public BGABanner mForegroundBanner;

    private Api mApi;
    private boolean isDisplay = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApi = App.getInstance().getApi();
        initViews();
    }
    //视图初始化
    private void initViews() {
        setContentView(R.layout.activity_welcome_screen);
        ButterKnife.bind(this);
        mForegroundBanner.setEnterSkipViewIdAndDelegate(R.id.btn_guide_enter, R.id.tv_guide_skip, new BGABanner.GuideDelegate() {
            @Override
            public void onClickEnterOrSkip() {
                initWelcome();
            }
        });// 设置数据源
        mForegroundBanner.setAdapter(this);
        mForegroundBanner.setDelegate(this);
        //从API加载图片
        mApi.getBannerPic().subscribe(new Observer<JsonResult<LoginBannerPic>>() {
            @Override
            public void onCompleted() {
                ToastUtils.normal("加载广告数据完毕");
            }

            @Override
            public void onError(Throwable e) {
                ToastUtils.normal("加载广告数据失败");
                skipToLoginActivity();
            }

            @Override
            public void onNext(JsonResult<LoginBannerPic> loginBannerPicJsonResult) {
                LoginBannerPic model = loginBannerPicJsonResult.getData();
                mForegroundBanner.setData(model.getImgs(),model.getTips());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 如果开发者的引导页主题是透明的，需要在界面可见时给背景 Banner 设置一个白色背景，避免滑动过程中两个 Banner 都设置透明度后能看到 Launcher
        mBackgroundBanner.setBackgroundResource(android.R.color.white);
    }

    private void initWelcome() {
        switch (WelcomeActivity.SELECT_MODE) {
            case 1:
                fastWelcome();
                break;
            case 2:
                slowWelcome();
                break;
        }
    }

    /*方法1：启动界面时间与App加载时间相等*/
    private void fastWelcome() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //耗时任务，比如加载网络数据
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //判断token
                        doPost();
                    }
                });
            }
        }).start();
    }

    /*方法2：设置启动界面2秒后跳转*/
    private void slowWelcome() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //判断token
                doPost();
            }
        }, 5000);
    }

    /**
     * 请求服务器判断token是否过期
     */
    private void doPost() {
        //判断是否用户修改过密码
        boolean isChangePwd = PreferenceUtils.getBoolean(SPConstants.CHANGE_PWD, false);
        if (isChangePwd) {
            //若修改过密码，则使token失效
            PreferenceUtils.setString(SPConstants.TOKEN, "");
        }
        String token = PreferenceUtils.getString(SPConstants.TOKEN, "0123456789");
        // 请求后台判断token
        mApi.checkToken(token).subscribe(new Observer<JsonResult<String>>() {
            @Override
            public void onCompleted() {
                ToastUtils.normal("接口调用完成");
            }

            @Override
            public void onError(Throwable e) {
                Log.e("Welcome",e.getMessage());
                skipToLoginActivity();
            }

            @Override
            public void onNext(JsonResult<String> response) {
                ToastUtils.normal("接口调用成功");
                if(response.getData() == "true"){
                    skipToMainActivity();
                }else {
                    skipToLoginActivity();
                }
            }
        });
    }

    /**
     * 跳转到测试页面
     * */
    private void skipToTestActivity() {
        // token未过期，跳转到主界面
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(intent);
        // 结束所有Activity
        ActivityLifecycleManager.get().finishAllActivity();
    }

    /**
     * 跳转到主界面
     * */
    private void skipToMainActivity() {
        // token未过期，跳转到主界面
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        // 结束所有Activity
        ActivityLifecycleManager.get().finishAllActivity();
    }

    /**
     * 跳转到登录页面
     * */
    private void skipToLoginActivity() {
        // 跳转到登录页面
        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        // 结束所有Activity
        ActivityLifecycleManager.get().finishAllActivity();
    }

    /**
     * 屏蔽物理返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        //OkGo.cancelAll(builder.build());

        if (handler != null) {
            //If token is null, all callbacks and messages will be removed.
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }


    @Override
    public void fillBannerItem(BGABanner banner, ImageView itemView, @Nullable LoginBannerPic.ImageAdModel model, int position) {
        Glide.with(this)
                .load(model.getImgUrl())
                .apply(new RequestOptions().placeholder(R.drawable.ic_guide_b1).error(R.drawable.ic_guide_b2).dontAnimate().centerCrop())
                .into(itemView);
    }
    /**
     * 设置广告图片点击跳转
     */
    @Override
    public void onBannerItemClick(BGABanner banner, ImageView itemView, @Nullable LoginBannerPic.ImageAdModel model, int position) {
        ToastUtils.normal("点击了第"+position+"页");
//        startActivity(new Intent(WelcomeActivity.this, WebViewActivity.class).putExtra("url",model.getAdUrl()));
//        ActivityUtils.startActivity(WebViewActivity.class);
    }
}
