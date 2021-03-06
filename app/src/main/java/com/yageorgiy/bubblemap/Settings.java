package com.yageorgiy.bubblemap;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.yageorgiy.bubblemap.ui.settings.SettingsFragment;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, SettingsFragment.newInstance())
                    .commitNow();
        }
    }
}
