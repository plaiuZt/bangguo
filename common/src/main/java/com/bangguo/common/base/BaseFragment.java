package com.bangguo.common.base;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bangguo.common.R;
import com.bangguo.common.baseapp.BaseApplication;
import com.bangguo.common.baserx.RxManager;
import com.bangguo.common.utils.AlertDialogUtils;
import com.bangguo.common.utils.TUtil;
import com.bangguo.common.utils.ToastUtils;
import com.bangguo.common.widget.LoadingDialog;
import com.bangguo.common.constants.Constants;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * des:基类fragment
 * Created by xsf
 * on 2016.07.12:38
 */
/***************使用例子*********************/
//1.mvp模式
//public class SampleFragment extends BaseFragment<NewsChanelPresenter, NewsChannelModel>implements NewsChannelContract.View {
//    @Override
//    public int getLayoutId() {
//        return R.layout.activity_news_channel;
//    }
//
//    @Override
//    public void initPresenter() {
//        mPresenter.setVM(this, mModel);
//    }
//
//    @Override
//    public void initView() {
//    }
//}
//2.普通模式
//public class SampleFragment extends BaseFragment {
//    @Override
//    public int getLayoutResource() {
//        return R.layout.activity_news_channel;
//    }
//
//    @Override
//    public void initPresenter() {
//    }
//
//    @Override
//    public void initView() {
//    }
//}
public abstract class BaseFragment<T extends BasePresenter, E extends BaseModel> extends Fragment implements EasyPermissions.PermissionCallbacks {
    protected View rootView;
    public T mPresenter;
    public E mModel;
    public RxManager mRxManager;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null)
            rootView = inflater.inflate(getLayoutResource(), container, false);
        mRxManager=new RxManager();
        unbinder = ButterKnife.bind(this, rootView);
        mPresenter = TUtil.getT(this, 0);
        mModel= TUtil.getT(this,1);
        if(mPresenter!=null){
            mPresenter.mContext=this.getActivity();
        }
        initPresenter();
        initView();
        return rootView;
    }

    //获取布局文件
    protected abstract int getLayoutResource();
    //简单页面无需mvp就不用管此方法即可,完美兼容各种实际场景的变通
    public abstract void initPresenter();
    //初始化view
    protected abstract void initView();


    /**
     * 通过Class跳转界面
     **/
    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, int requestCode) {
        startActivityForResult(cls, null, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }



    /**
     * 开启加载进度条
     */
    public void startProgressDialog() {
        LoadingDialog.showDialogForLoading(getActivity());
    }

    /**
     * 开启加载进度条
     *
     * @param msg
     */
    public void startProgressDialog(String msg) {
        LoadingDialog.showDialogForLoading(getActivity(), msg, true);
    }

    /**
     * 停止加载进度条
     */
    public void stopProgressDialog() {
        LoadingDialog.cancelDialogForLoading();
    }


    /**
     * 短暂显示Toast提示(来自String)
     **/
    public void showShortToast(String text) {
        ToastUtils.normal(text);
    }

    /**
     * 短暂显示Toast提示(id)
     **/
    public void showShortToast(int resId) {
        String message = (String)BaseApplication.getAppResources().getText(resId);
        ToastUtils.normal(message);
    }

    /**
     * 长时间显示Toast提示(来自res)
     **/
    public void showLongToast(int resId) {
        String message = (String)BaseApplication.getAppResources().getText(resId);
        ToastUtils.normal(message,5000);
    }

    /**
     * 长时间显示Toast提示(来自String)
     **/
    public void showLongToast(String text) {
        ToastUtils.normal(text,5000);
    }


    public void showToastWithImg(String text,int res) {
        Drawable drawable = BaseApplication.getAppResources().getDrawable(res);
        ToastUtils.normal(text,drawable);
    }

    /**
     * 网络访问错误提醒
     */
    public void showNetErrorTip() {
        showToastWithImg(getText(R.string.net_error).toString(),R.drawable.ic_wifi_off);
    }

    public void showNetErrorTip(String error) {
        showToastWithImg(error,R.drawable.ic_wifi_off);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        ButterKnife.unbind(this);
        unbinder.unbind();
        if (mPresenter != null)
            mPresenter.onDestroy();
        mRxManager.clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.APP_SETTING_DIALOG_REQUEST_CODE) {
            //在这儿，你可以再对权限进行检查，从而给出提示，或进行下一步操作
            checkPermission();
            ToastUtils.info("从设置中返回");
        }
    }

    //----------------以下是请求权限base，子类activity只需要重写checkPermission()方法即可----------------//

    /**
     * 检查权限，子类要申请权限，需要重写该方法
     * */
    public  void checkPermission() {
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 将结果转发给EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        //若是在权限弹窗中，用户勾选了'NEVER ASK AGAIN.'或者'不再提示'，且拒绝权限。
        //跳转到设置界面去，让用户手动开启。
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {

            String title = "需要权限，才能正常使用：";
            String message = "如果没有请求的权限，此应用可能无法正常工作。请打开应用设置以修改应用权限。";
            AlertDialogUtils.showDialog(getContext(), title, message, "去设置", new QMUIDialogAction.ActionListener() {
                @Override
                public void onClick(QMUIDialog dialog, int index) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", BaseApplication.getAppContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, Constants.APP_SETTING_DIALOG_REQUEST_CODE);
                    dialog.dismiss();
                }
            });
        }
    }

}
