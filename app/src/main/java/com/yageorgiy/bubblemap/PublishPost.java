package com.yageorgiy.bubblemap;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.yageorgiy.bubblemap.ui.publishpost.PublishPostFragment;

public class PublishPost extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_post_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, PublishPostFragment.newInstance())
                    .commitNow();
        }
    }
}