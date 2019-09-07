package com.bangguo.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.bangguo.app.R;
import com.bangguo.app.base.BaseActivity;
import com.bangguo.app.base.webview.XPageWebViewFragment;
import com.bangguo.app.fragment.AdvertisementFragment;

public class WebViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement);

        XPageWebViewFragment.openUrl(this,getIntent().getStringExtra("url"));
    }
}
