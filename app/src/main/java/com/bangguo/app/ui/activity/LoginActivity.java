package com.bangguo.app.ui.activity;

import androidx.core.widget.NestedScrollView;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bangguo.app.R;
import com.bangguo.app.common.constants.SPConstants;
import com.bangguo.app.common.utils.PreferenceUtils;
import com.bangguo.app.http.ApiService;
import com.bangguo.app.common.utils.AnimationToolUtils;
import com.bangguo.app.common.utils.Des3Utils;
import com.bangguo.app.ui.contract.LoginContract;
import com.bangguo.app.ui.model.LoginModel;
import com.bangguo.app.ui.presenter.LoginPresenter;
import com.bangguo.common.base.BaseActivity;
import com.bangguo.common.utils.ToastUtils;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.xuexiang.xui.utils.StatusBarUtils;

import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity<LoginPresenter, LoginModel> implements LoginContract.View, Validator.ValidationListener {

    @BindView(R.id.logo)
    ImageView mLogo;

    @Order(1)
    @NotEmpty(message = "用户名不能为空")
    @BindView(R.id.et_account)
    EditText mEtAccount;

    @Order(2)
    @NotEmpty(message = "密码不能为空")
    @Password(min = 1, scheme = Password.Scheme.ANY,message = "密码不能少于1位")
    @BindView(R.id.et_password)
    EditText mEtPassword;

    @BindView(R.id.iv_clean_account)
    ImageView mIvCleanAccount;
    @BindView(R.id.clean_password)
    ImageView mCleanPassword;
    @BindView(R.id.iv_show_pwd)
    ImageView mIvShowPwd;

    @BindView(R.id.cb_checkbox)
    CheckBox cbCheckbox;
    @BindView(R.id.content)
    LinearLayout mContent;
    @BindView(R.id.scrollView)
    NestedScrollView mScrollView;

    private long exitTime = 0;
    private int screenHeight = 0;//屏幕高度
    private int keyHeight = 0; //软件盘弹起后所占高度
    private final float scale = 0.9f; //logo缩放比例
    private int height = 0;
    boolean isCheckBox;

    private Validator validator;
    private ApiService mApi;

    private static final String TAG = "Login";
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//        ButterKnife.bind(this);
//
//    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initPresenter() {
        mPresenter.setVM(this, mModel);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        //mApi = App.getInstance().getApi();
        StatusBarUtils.setStatusBarLightMode(this);
        validator = new Validator(this);
        validator.setValidationListener(this);

        initUser();
        initEvent();
    }

    /**
     * 表单验证成功
     */
    @Override
    public void onValidationSucceeded() {
        // 注解验证全部通过验证，开始后台验证
        attemptLogin();
    }

    /**
     * 表单验证失败
     * @param errors 失败信息
     */
    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // 显示上面注解中添加的错误提示信息
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                ToastUtils.normal(message);
            }
        }
    }
    /**
     * 初始化登录用户
     */
    private void initUser() {
        //从SharePreference中拿出保存的账号密码
        String username = PreferenceUtils.getString(SPConstants.USER_NAME,"");
        String password = PreferenceUtils.getString(SPConstants.PASSWORD,"");

//        mEtAccount.getText().clear();
//        mEtPassword.getText().clear();

        if(!TextUtils.isEmpty(username)){
            password = Des3Utils.decode(password);
            mEtAccount.setText(username);
            mEtAccount.setSelection(mEtAccount.getText().length());
            mIvCleanAccount.setVisibility(View.VISIBLE);;
        }

        if(!TextUtils.isEmpty(password)){
            mEtPassword.setText(password);

            mEtPassword.setSelection(mEtPassword.getText().length());

            mCleanPassword.setVisibility(View.VISIBLE);
            cbCheckbox.setChecked(true);
            isCheckBox = true;
        }else {
            cbCheckbox.setChecked(false);
            isCheckBox = false;
        }
    }
    /**
     * 登录
     */
    private void attemptLogin(){
        final String username = mEtAccount.getText().toString();
        final String password = mEtPassword.getText().toString();
        mPresenter.attemptLogin(username, password,isCheckBox);
    }


    /**
     * 忘记密码处理
     */
    private void forgetPassword(){
        ToastUtils.normal("请联系管理员修改密码！", 3000);
    }
    /**
     * 点击眼睛图标显示或隐藏密码
     */
    private void changePasswordEye(){
        if(mEtPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD){
            mEtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mIvShowPwd.setImageResource(R.drawable.icon_pass_visuable);
        }else {
            mEtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mIvShowPwd.setImageResource(R.drawable.icon_pass_gone);
        }

        //将光标移至末尾
        String pwd = mEtPassword.getText().toString();
        if(!TextUtils.isEmpty(pwd)){
            mEtPassword.setSelection(pwd.length());
        }
    }

    @OnClick({R.id.iv_clean_account,R.id.clean_password,R.id.iv_show_pwd,R.id.forget_password,R.id.btn_login,R.id.tv_register})
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.iv_clean_account:
                mEtAccount.getText().clear();
                mEtPassword.getText().clear();
                break;
            case R.id.clean_password:
                mEtPassword.getText().clear();
                break;
            case R.id.iv_show_pwd:
                changePasswordEye();
                break;
            case R.id.forget_password:
                forgetPassword();
                break;
            case R.id.btn_login:
                validator.validate();
                break;
            case R.id.tv_register:
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        // 获取屏幕高度
        screenHeight = this.getResources().getDisplayMetrics().heightPixels;
        // 弹起高度为屏幕高度的1/3
        keyHeight = screenHeight / 3;

        // 输入账号状态监听，在右边显示或隐藏clean
        addIconClearListener(mEtAccount,mIvCleanAccount);
        // 监听EtPassword输入状态，在右边显示或隐藏clean
        addIconClearListener(mEtPassword,mCleanPassword);

        /*
         * 记住密码Checkbox点击监听器
         * */
        cbCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCheckBox = isChecked;
            }
        });

        /*
         * 禁止键盘弹起的时候可以滚动
         */
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        // ScrollView监听滑动状态
        mScrollView.addOnLayoutChangeListener(new ViewGroup.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
              /* old是改变前的左上右下坐标点值，没有old的是改变后的左上右下坐标点值
              现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起*/
                if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
                    int dist = mContent.getBottom() - mScrollView.getHeight();
                    if (dist > 0) {
                        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(mContent, "translationY", 0.0f, -dist);
                        mAnimatorTranslateY.setDuration(300);
                        mAnimatorTranslateY.setInterpolator(new LinearInterpolator());
                        mAnimatorTranslateY.start();
                        AnimationToolUtils.zoomIn(mLogo, scale, dist);
                    }

                } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
                    if ((mContent.getBottom() - oldBottom) > 0) {
                        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(mContent, "translationY", mContent.getTranslationY(), 0);
                        mAnimatorTranslateY.setDuration(300);
                        mAnimatorTranslateY.setInterpolator(new LinearInterpolator());
                        mAnimatorTranslateY.start();
                        //键盘收回后，logo恢复原来大小，位置同样回到初始位置
                        AnimationToolUtils.zoomOut(mLogo, scale);
                    }
                }
            }
        });
    }
    /**
     * 设置文本框与右侧删除图标监听器
     */
    private void addIconClearListener(final EditText et, final ImageView iv) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //如果文本框长度大于0，则显示删除图标，否则不显示
                if (s.length() > 0) {
                    iv.setVisibility(View.VISIBLE);
                } else {
                    iv.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


    /**
     * 连续点击两次返回键退出APP
     */
    //记录上一次点击的时间戳
    long lastTime = System.currentTimeMillis();
    @Override
    public void onBackPressed() {
        //记录本次按下的时间戳
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastTime < 1000){
            super.onBackPressed();
            return;
        }else {
            ToastUtils.info("再按一下退出",3000);
        }
        lastTime = currentTime;
    }

    @Override
    public void showLoading(String title) {
        Log.i(TAG,title+"正在加载");
    }

    @Override
    public void stopLoading() {
        Log.i(TAG,"加载结束");
    }

    @Override
    public void showErrorTip(String msg) {
        Log.i(TAG,"错误信息:"+msg);
    }
}
