package com.yageorgiy.bubblemap;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.yageorgiy.bubblemap.ui.viewpost.ViewPostFragment;

public class ViewPost extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_post_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ViewPostFragment.newInstance())
                    .commitNow();
        }
    }
}