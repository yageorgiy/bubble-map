package com.yageorgiy.bubblemap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private boolean loginAttempt = false;
    public DrawerLayout drawer;

    public String username = "Аноним";
    public String email = "";
    public NavController navController;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO: скрытие

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
            R.id.nav_home,
            R.id.nav_my_account,
//            R.id.nav_following,
//            R.id.nav_followers,
            R.id.nav_login,
            R.id.nav_register,
            R.id.nav_settings,
            R.id.nav_about
        )
            .setDrawerLayout(drawer)
            .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_my_account_unauthorized_section).setVisible(true);
        nav_Menu.findItem(R.id.nav_my_account_authorized_section).setVisible(false);

        final DrawerLayout _drawer = drawer;
        final MainActivity _this = this;

        this.drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                final TextView username = _this.findViewById(R.id.headerUsername);
                final TextView email = _this.findViewById(R.id.headerEmail);

                username.setText(_this.username);
                email.setText(_this.email);
            }
        });


        _this.processLoginOnStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

//        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void processLoginOnStart(){
        if (!this.loginAttempt) {
            this.loginAttempt = true;
            SharedPreferences myPreferences = this.getPreferences(MODE_PRIVATE); //PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            String token = myPreferences.getString("TOKEN", "");

            if (!token.equals("")) {
                ((BubbleMapApplication) getApplication()).setCurrentToken(token);
                this.performLogin(token, this.getBaseContext(), false);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public boolean performLogin(String token, Context context){
        return performLogin(token, context, true);
    }

    public boolean performLogin(String token, Context context, boolean saveToken){
        if(saveToken){
            //TODO: make setCurrentToken in MainActivity (instead of BubbleMapApplication)
            ((BubbleMapApplication)getApplication()).setCurrentToken(token);

            SharedPreferences myPreferences = getPreferences(MODE_PRIVATE); //PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor myEditor = myPreferences.edit();
            myEditor.putString("TOKEN", token);
            myEditor.commit();
        }


        /*
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, new HomeFragment())
                .addToBackStack(null)
                .commit()
                ;
        */

        // Переходим домой
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.nav_home);

        //Делаем доступными секции
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_my_account_unauthorized_section).setVisible(false);
        nav_Menu.findItem(R.id.nav_my_account_authorized_section).setVisible(true);

        final MainActivity _this = this;
//        final TextView username = findViewById(R.id.headerUsername);
//        final TextView email = findViewById(R.id.headerEmail);

        if(username == null || email == null) return false;

//        username.setText("Загрузка...");
//        email.setText("Загрузка...");

        ((BubbleMapApplication)getApplication()).apolloClient().query(
                GetViewerInfoForSidebarQuery.builder()
                        .build()
        ).enqueue(new ApolloCall.Callback<GetViewerInfoForSidebarQuery.Data>() {

            @Override
            public void onResponse(@NotNull Response<GetViewerInfoForSidebarQuery.Data> dataResponse) {
                final Response<GetViewerInfoForSidebarQuery.Data> _dataResponse = dataResponse;

                MainActivity.this.runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override public void run() {

                        if(_dataResponse.hasErrors()){
                            _this.username = "Авторизованный пользователь";
                            _this.email = "Ошибка обновления: "+_dataResponse.getErrors().get(0).getMessage();
                            return;
                        }

                        _this.username =_dataResponse.getData().viewer.username;
                        _this.email = _dataResponse.getData().viewer.email.toString();

                    }
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override public void run() {
                        _this.username = "Авторизованный пользователь";
                        _this.email = "Ошибка обновления данных.";
                    }
                });
            }
        });



        Toast.makeText(context, "Добро-пожаловать!", Toast.LENGTH_LONG).show();

        return true;
    }

}
