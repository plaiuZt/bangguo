package com.bangguo.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

import com.bangguo.app.common.enums.ParamType;
import com.bangguo.app.http.HttpCommonParamInterceptor;
import com.bangguo.app.http.Api;
import com.bangguo.app.common.notification.MainNotification;
import com.bangguo.app.common.utils.PreferenceUtils;
import com.bangguo.app.manager.ActivityLifecycleManager;
import com.bangguo.app.utils.sdkinit.TbsInit;
import com.bangguo.app.utils.sdkinit.XBasicLibInit;
import com.bangguo.app.utils.sdkinit.XUpdateInit;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.squareup.leakcanary.LeakCanary;
import com.xuexiang.xui.XUI;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.app.AppUtils;

import org.litepal.LitePal;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    public static Context getContext() {
        return context;
    }

    private static Api mApi; //本APP所使用的全局网络API
    public Api getApi() {
        return mApi;
    }

    private static App instance;
    public static App getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //解决4.x运行崩溃的问题
        MultiDex.install(this);
    }

    private static OkHttpClient.Builder builder;

    @Override
    public void onCreate() {
        super.onCreate();
        this.instance = this;
        context = getApplicationContext();
        // activity生命周期管理
        ActivityLifecycleManager.get().init(this);
        //XUI框架初始化
        initXUI();
        //初始化基础类库
        initLibs();
        // LitePal 数据库初始化
        LitePal.initialize(this);
        //全局配置Xhttp2网络访问框架
        initHttp();
        // 配置sharedPreferences
        PreferenceUtils.initPreference(this, AppUtils.getAppName(this.getPackageName()), Activity.MODE_PRIVATE);
        // 初始化通知栏消息渠道
        MainNotification.initNotificationChannel(this);
        // 内存泄漏检测
        initLeakCanary(false);
        // Log日志打印框架
        initLogCat();
    }
    /**
     * 配置全局的XUI框架
     */
    private void initXUI(){
        XUI.init(this); //初始化UI框架
        XUtil.init(this);//初始化工具类
        XUI.debug(true);  //开启UI框架调试日志
        XUI.getInstance().initFontStyle("fonts/hwxk.ttf"); //设置默认字体--华文行楷
    }
    /**
     * 内存泄漏检查
     * @param flag 是否启用内存泄漏检查
     */
    private void initLeakCanary(boolean flag){
        if (flag) {
            // leak 内存检测注册
            if (LeakCanary.isInAnalyzerProcess(this)) {
                return;
            }
            LeakCanary.install(this);
        }
    }

    /**
     * 初始化Log日志框架
     */
    private void initLogCat(){
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // （可选）是否显示线程信息。 默认值为true
                .methodCount(2)         // （可选）要显示的方法行数。 默认2
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }

    /**
     * 配置全局Retrofit网络访问框架
     */
    private void initHttp(){
        // 修改请求超时时间为6s，与全局超时时间分开
        builder = new OkHttpClient.Builder();
        builder.readTimeout(2000, TimeUnit.MILLISECONDS);
        builder.writeTimeout(2000, TimeUnit.MILLISECONDS);
        builder.connectTimeout(2000, TimeUnit.MILLISECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                //设置数据解析器
                .addConverterFactory(GsonConverterFactory.create())
                //设置网络请求的Url地址
                .baseUrl("https://www.easy-mock.com/mock/5d6cd381fca9c542568a36a3/api/")
                .client(builder.build())
                .build();
        // 创建网络请求接口的实例
        mApi = retrofit.create(Api.class);
    }

    public void setCommonParam(Map<String,String> param, ParamType type){
        HttpCommonParamInterceptor.Builder interceptorBuilder = new HttpCommonParamInterceptor.Builder();
        switch (type){
            case PARAM_MAP:
                interceptorBuilder.addParamsMap(param);
                break;
            case QUERY_PARAM_MAP:
                interceptorBuilder.addQueryParamsMap(param);
                break;
            case HEADER_PARAM_MAP:
                interceptorBuilder.addHeaderParamsMap(param);
                break;
        }
        builder.addInterceptor(interceptorBuilder.build());
    }
    private void initLibs(){
        //初始化基础库
        XBasicLibInit.init(this);
        //三方SDK初始化
        XUpdateInit.init(this);
        TbsInit.init(this);
        //运营统计数据运行时不初始化
//        if (!BuildConfig.DEBUG) {
//            UMengInit.init(this);
//            BuglyInit.init(this);
//        }
    }
}
