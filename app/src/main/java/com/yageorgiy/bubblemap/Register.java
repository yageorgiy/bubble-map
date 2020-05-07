package com.yageorgiy.bubblemap;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.yageorgiy.bubblemap.ui.register.RegisterFragment;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, RegisterFragment.newInstance())
                    .commitNow();
        }
    }
}
