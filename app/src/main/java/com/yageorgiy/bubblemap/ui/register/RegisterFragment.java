package com.yageorgiy.bubblemap.ui.register;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
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
import com.yageorgiy.bubblemap.MainActivity;
import com.yageorgiy.bubblemap.R;
import com.yageorgiy.bubblemap.RegisterMutation;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RegisterFragment extends Fragment {

    private RegisterViewModel mViewModel;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.register_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);
        // TODO: Use the ViewModel


        final FragmentActivity activity = getActivity();
        final BubbleMapApplication application = (BubbleMapApplication) Objects.requireNonNull(activity).getApplication();

        final TextView emailTextView = activity.findViewById(R.id.email);
        final TextView usernameTextView = activity.findViewById(R.id.my_account_username);
        final TextView passwordTextView = activity.findViewById(R.id.password);
        final TextView password2TextView = activity.findViewById(R.id.password2);
        final Spinner sexSpinner = activity.findViewById(R.id.sexSpinner);

        final TextView registerErrorText = activity.findViewById(R.id.errorText);
        final Button registerButton = activity.findViewById(R.id.registerButton);


        final ProgressDialog progress = new ProgressDialog(this.getContext());
        progress.setTitle("Загрузка...");
        progress.setMessage("Регистрация аккаунта...");
        progress.setCancelable(false);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                registerErrorText.setText("");

                String email = emailTextView.getText().toString();
                String username = usernameTextView.getText().toString();
                String password = passwordTextView.getText().toString();
                String password2 = password2TextView.getText().toString();
                String sexSelected = sexSpinner.getSelectedItem().toString();

                if(email.equals("") || !email.matches("(.*)@(.*)\\.(.*)")){
                    registerErrorText.setText("Введите корректный e-mail");
                    return;
                }

                if(username.equals("")){
                    registerErrorText.setText("Введите имя пользователя");
                    return;
                }

                if(password.equals("")){
                    registerErrorText.setText("Введите пароль");
                    return;
                }

                if(password2.equals("")){
                    registerErrorText.setText("Повторите пароль ещё раз во втором поле");
                    return;
                }

                if(!password.equals(password2)){
                    registerErrorText.setText("Пароли не совпадают");
                    return;
                }

                if(!sexSelected.equals("м") && !sexSelected.equals("ж")){
                    registerErrorText.setText("Выберите корректный пол");
                    return;
                }

                progress.show();

                application.apolloClient().mutate(
                    RegisterMutation.builder()
                        .username(username)
                        .password(password)
                        .email(email)
                        .sex(sexSelected)
                        .build()
                ).enqueue(new ApolloCall.Callback<RegisterMutation.Data>() {

                    @Override
                    public void onResponse(@NotNull Response<RegisterMutation.Data> dataResponse) {
                        final Response<RegisterMutation.Data> _dataResponse = dataResponse;

                        RegisterFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override public void run() {
                                progress.hide();

                                if(_dataResponse.hasErrors()){
                                    registerErrorText.setText(_dataResponse.getErrors().get(0).getMessage());
                                    return;
                                }
                                String token = _dataResponse.getData().login().get(0);
                                ((MainActivity)activity).performLogin(token, getContext());
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        RegisterFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override public void run() {
                                progress.hide();
                                registerErrorText.setText("Ошибка запроса, поропбуйте позже");
                            }
                        });
                    }
                });



            }
        });



    }

}
