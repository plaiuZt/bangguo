package com.bangguo.app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bangguo.app.ui.activity.Main2Activity;
import com.bangguo.common.base.BaseActivity;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
    }

    public void testClick(View view){
        Intent intent = new Intent(TestActivity.this,Main2Activity.class);
        startActivity(intent);
    }
}
