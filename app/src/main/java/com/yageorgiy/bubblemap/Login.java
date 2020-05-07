package com.yageorgiy.bubblemap;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.yageorgiy.bubblemap.ui.login.LoginFragment;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, LoginFragment.newInstance())
                    .commitNow();
        }
    }
}
