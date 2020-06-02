package com.yageorgiy.bubblemap.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yageorgiy.bubblemap.BubbleMapApplication;
import com.yageorgiy.bubblemap.MainActivity;
import com.yageorgiy.bubblemap.R;
import com.yageorgiy.bubblemap.map.BubbleMap;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final MainActivity activity = (MainActivity)getActivity();
        final BubbleMapApplication application = (BubbleMapApplication) getActivity().getApplication();

        /*
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
         */

        ConstraintLayout relativeLayout = root.findViewById(R.id.mapLayout);
        relativeLayout.addView(new BubbleMap(getActivity(), getActivity())); //TODO: clear the mess

        FloatingActionButton fab = root.findViewById(R.id.fab);

        if(application.isAuthorized()){
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Bundle bundle = new Bundle();
                bundle.putInt("id", 0);

                activity.navController
                        .navigate(R.id.nav_publish_post, bundle);

            }
        });

//        ((MainActivity)getActivity()).processLoginOnStart();



        return root;
    }
}