package com.yageorgiy.bubblemap.ui.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.yageorgiy.bubblemap.BubbleMapApplication;
import com.yageorgiy.bubblemap.LoginQueryMutation;
import com.yageorgiy.bubblemap.MainActivity;
import com.yageorgiy.bubblemap.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private LoginViewModel mViewModel;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        // TODO: Use the ViewModel

        final FragmentActivity activity = getActivity();
        final BubbleMapApplication application = (BubbleMapApplication) Objects.requireNonNull(activity).getApplication();

        final TextView usernameTextView = activity.findViewById(R.id.email);
        final TextView passwordTextView = activity.findViewById(R.id.password);

        final TextView loginErrorText = activity.findViewById(R.id.error);
        final Button loginButton = activity.findViewById(R.id.actionLogin);


        final ProgressDialog progress = new ProgressDialog(this.getContext());
        progress.setTitle("Загрузка...");
        progress.setMessage("Входим в аккаунт...");
        progress.setCancelable(false);

        Button b = Objects.requireNonNull(activity).findViewById(R.id.actionLogin);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                loginErrorText.setText("");
//                loginButton.setEnabled(false);


                String username = usernameTextView.getText().toString();
                String password = passwordTextView.getText().toString();

                if(username.equals("")){
                    loginErrorText.setText("Введите логин или e-mail");
                    return;
                }

                if(password.equals("")){
                    loginErrorText.setText("Введите пароль");
                    return;
                }

                progress.show();

                application.apolloClient().mutate(
                    LoginQueryMutation.builder()
                            .username(username)
                            .password(password)
                            .build()
                ).enqueue(new ApolloCall.Callback<LoginQueryMutation.Data>() {

                    @Override
                    public void onResponse(@NotNull Response<LoginQueryMutation.Data> dataResponse) {
                        final Response<LoginQueryMutation.Data> _dataResponse = dataResponse;

//                        for (String data : Objects.requireNonNull(Objects.requireNonNull(dataResponse.getData()).login())) {
//                            buffer.append("name:" + feed.repository().fragments().repositoryFragment().name());
//                            ().login());
//                            buffer.append(" postedBy: " + feed.postedBy().login());
//                        }

                        // onResponse returns on a background thread. If you want to make UI updates make sure they are done on the Main Thread.
                        LoginFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override public void run() {
//                                TextView txtResponse = (TextView) findViewById(R.id.txtResponse);
//                                txtResponse.setText(buffer.toString());
//                            loginButton.setEnabled(true);

                                progress.hide();

                                if(_dataResponse.hasErrors()){
                                    loginErrorText.setText(_dataResponse.getErrors().get(0).getMessage());
                                    return;
                                }

    //                            final StringBuffer buffer = new StringBuffer();
    //                            buffer.append("token: ").append(_dataResponse.getData().login().get(0));
                                String token = _dataResponse.getData().login().get(0);
                                ((MainActivity)activity).performLogin(token, getContext());
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        LoginFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override public void run() {
                                progress.hide();
                                loginErrorText.setText("Ошибка запроса, поропбуйте позже");
                            }
                        });
                    }
                });



            }
        });



    }

}
