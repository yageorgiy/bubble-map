package com.yageorgiy.bubblemap.ui.myaccount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.yageorgiy.bubblemap.MainActivity;
import com.yageorgiy.bubblemap.R;

public class MyAccountFragment extends Fragment {

    private MyAccountViewModel mViewModel;

    public static MyAccountFragment newInstance() {
        return new MyAccountFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_account_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MyAccountViewModel.class);


        TextView username = getActivity().findViewById(R.id.my_account_username);
        username.setText(((MainActivity)getActivity()).username);

        TextView email = getActivity().findViewById(R.id.my_account_email);
        email.setText("Мой e-mail: "+((MainActivity)getActivity()).email);

    }

}
