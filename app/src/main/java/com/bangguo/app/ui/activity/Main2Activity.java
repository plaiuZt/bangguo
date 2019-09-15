package com.bangguo.app.ui.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentTransaction;

import com.bangguo.app.R;
import com.bangguo.app.bean.TabEntity;
import com.bangguo.app.common.constants.AppConstant;
import com.bangguo.app.ui.fragment.DiscoverFragment;
import com.bangguo.app.ui.fragment.HomeFragment;
import com.bangguo.app.ui.fragment.MessageFragment;
import com.bangguo.app.ui.fragment.MineFragment;
import com.bangguo.common.base.BaseActivity;
import com.bangguo.common.baseapp.AppConfig;
import com.bangguo.common.utils.LogUtils;
import com.bangguo.common.daynightmodeutils.ChangeModeController;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;

import butterknife.BindView;
import cn.hugeterry.updatefun.UpdateFunGO;
import cn.hugeterry.updatefun.config.UpdateKey;
import rx.functions.Action1;

public class Main2Activity extends BaseActivity {

    @BindView(R.id.tab_layout)
    CommonTabLayout tabLayout;

//    private String[] mTitles = {getString(R.string.tab1),getString(R.string.tab2),getString(R.string.tab3),getString(R.string.tab4)};
    private String[] mTitles = {"首页","发现","消息","我的"};
    private int[] mIconUnselectIds = {
            R.mipmap.ic_home_normal,R.mipmap.ic_girl_normal,R.mipmap.ic_video_normal,R.mipmap.ic_care_normal};
    private int[] mIconSelectIds = {
            R.mipmap.ic_home_selected,R.mipmap.ic_girl_selected, R.mipmap.ic_video_selected,R.mipmap.ic_care_selected};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    private HomeFragment homeFragment;
    private DiscoverFragment discoverFragment;
    private MessageFragment messageFragment;
    private MineFragment mineFragment;
    private static int tabLayoutHeight;

    /**
     * 入口
     * @param activity
     */
    public static void startAction(Activity activity){
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in,
                com.bangguo.common.R.anim.fade_out);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main2;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView(Bundle savedInstanceState) {
        //此处填上在http://fir.im/注册账号后获得的API_TOKEN以及APP的应用ID
        UpdateKey.API_TOKEN = AppConfig.API_FIRE_TOKEN;
        UpdateKey.APP_ID = AppConfig.APP_FIRE_ID;
        //如果你想通过Dialog来进行下载，可以如下设置
//        UpdateKey.DialogOrNotification=UpdateKey.WITH_DIALOG;
        UpdateFunGO.init(this);
        //初始化菜单
        initTab();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //切换daynight模式要立即变色的页面
        ChangeModeController.getInstance().init(this,R.attr.class);
        super.onCreate(savedInstanceState);
        //初始化frament
        initFragment(savedInstanceState);
        tabLayout.measure(0,0);
        tabLayoutHeight=tabLayout.getMeasuredHeight();
        //监听菜单显示或隐藏
        mRxManager.on(AppConstant.MENU_SHOW_HIDE, new Action1<Boolean>() {

            @Override
            public void call(Boolean hideOrShow) {
                startAnimation(hideOrShow);
            }
        });
    }
    /**
     * 初始化tab
     */
    private void initTab() {
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        tabLayout.setTabData(mTabEntities);
        //点击监听
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                SwitchTo(position);
            }
            @Override
            public void onTabReselect(int position) {
            }
        });
    }
    /**
     * 初始化碎片
     */
    private void initFragment(Bundle savedInstanceState) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int currentTabPosition = 0;
        if (savedInstanceState != null) {
            homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("newsMainFragment");
            discoverFragment = (DiscoverFragment) getSupportFragmentManager().findFragmentByTag("photosMainFragment");
            messageFragment = (MessageFragment) getSupportFragmentManager().findFragmentByTag("videoMainFragment");
            mineFragment = (MineFragment) getSupportFragmentManager().findFragmentByTag("careMainFragment");
            currentTabPosition = savedInstanceState.getInt(AppConstant.HOME_CURRENT_TAB_POSITION);
        } else {
            homeFragment = new HomeFragment();
            discoverFragment = new DiscoverFragment();
            messageFragment = new MessageFragment();
            mineFragment = new MineFragment();

            transaction.add(R.id.fl_body, homeFragment, "homeFragment");
            transaction.add(R.id.fl_body, discoverFragment, "discoverFragment");
            transaction.add(R.id.fl_body, messageFragment, "messageFragment");
            transaction.add(R.id.fl_body, mineFragment, "mineFragment");
        }
        transaction.commit();
        SwitchTo(currentTabPosition);
        tabLayout.setCurrentTab(currentTabPosition);
    }

    /**
     * 切换
     */
    private void SwitchTo(int position) {
        LogUtils.logd("主页菜单position" + position);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (position) {
            //首页
            case 0:
                transaction.hide(discoverFragment);
                transaction.hide(messageFragment);
                transaction.hide(mineFragment);
                transaction.show(homeFragment);
                transaction.commitAllowingStateLoss();
                break;
            //发现
            case 1:
                transaction.show(discoverFragment);
                transaction.hide(messageFragment);
                transaction.hide(mineFragment);
                transaction.hide(homeFragment);
                transaction.commitAllowingStateLoss();
                break;
            //消息
            case 2:
                transaction.hide(discoverFragment);
                transaction.show(messageFragment);
                transaction.hide(mineFragment);
                transaction.hide(homeFragment);
                transaction.commitAllowingStateLoss();
                break;
            //我的
            case 3:
                transaction.hide(discoverFragment);
                transaction.hide(messageFragment);
                transaction.show(mineFragment);
                transaction.hide(homeFragment);
                transaction.commitAllowingStateLoss();
                break;
            default:
                break;
        }
    }

    /**
     * 菜单显示隐藏动画
     * @param showOrHide
     */
    private void startAnimation(boolean showOrHide){
        final ViewGroup.LayoutParams layoutParams = tabLayout.getLayoutParams();
        ValueAnimator valueAnimator;
        ObjectAnimator alpha;
        if(!showOrHide){
            valueAnimator = ValueAnimator.ofInt(tabLayoutHeight, 0);
            alpha = ObjectAnimator.ofFloat(tabLayout, "alpha", 1, 0);
        }else{
            valueAnimator = ValueAnimator.ofInt(0, tabLayoutHeight);
            alpha = ObjectAnimator.ofFloat(tabLayout, "alpha", 0, 1);
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                layoutParams.height= (int) valueAnimator.getAnimatedValue();
                tabLayout.setLayoutParams(layoutParams);
            }
        });
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.playTogether(valueAnimator,alpha);
        animatorSet.start();
    }

    /**
     * 监听返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //奔溃前保存位置
        LogUtils.loge("onSaveInstanceState进来了1");
        if (tabLayout != null) {
            LogUtils.loge("onSaveInstanceState进来了2");
            outState.putInt(AppConstant.HOME_CURRENT_TAB_POSITION, tabLayout.getCurrentTab());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateFunGO.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UpdateFunGO.onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChangeModeController.onDestory();
    }
}
