package com.yageorgiy.bubblemap.ui.viewpost;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.github.irshulx.Editor;
import com.yageorgiy.bubblemap.BubbleMapApplication;
import com.yageorgiy.bubblemap.GetPostInfoQuery;
import com.yageorgiy.bubblemap.MainActivity;
import com.yageorgiy.bubblemap.R;

import org.jetbrains.annotations.NotNull;

public class ViewPostFragment extends Fragment {

    private ViewPostViewModel mViewModel;
    private Integer id;

    public static ViewPostFragment newInstance() {
        return new ViewPostFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_post_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ViewPostViewModel.class);
        // TODO: Use the ViewModel

        this.id = getArguments().getInt("id");

        final ProgressDialog progress = new ProgressDialog(this.getContext());
        progress.setTitle("Загрузка...");
        progress.setMessage("Входим в аккаунт...");
        progress.setCancelable(false);

        final ViewPostFragment _this = this;

        final TextView title = getActivity().findViewById(R.id.post_title);
        final TextView author = getActivity().findViewById(R.id.post_author);
        final TextView date = getActivity().findViewById(R.id.post_date);
        final Editor editor = getActivity().findViewById(R.id.post_contents);

        ((BubbleMapApplication)getActivity().getApplication()).apolloClient().query(
                GetPostInfoQuery.builder()
                        .id(this.id)
                        .build()
        ).enqueue(new ApolloCall.Callback<GetPostInfoQuery.Data>() {

            @Override
            public void onResponse(@NotNull Response<GetPostInfoQuery.Data> dataResponse) {
                final Response<GetPostInfoQuery.Data> _dataResponse = dataResponse;

//                        for (String data : Objects.requireNonNull(Objects.requireNonNull(dataResponse.getData()).login())) {
//                            buffer.append("name:" + feed.repository().fragments().repositoryFragment().name());
//                            ().login());
//                            buffer.append(" postedBy: " + feed.postedBy().login());
//                        }

                // onResponse returns on a background thread. If you want to make UI updates make sure they are done on the Main Thread.
                ViewPostFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override public void run() {
//                                TextView txtResponse = (TextView) findViewById(R.id.txtResponse);
//                                txtResponse.setText(buffer.toString());
//                            loginButton.setEnabled(true);

                        progress.hide();

                        if(_dataResponse.hasErrors()){
                            ((MainActivity)getActivity()).navController
                                    .navigate(R.id.nav_home);

                            Toast.makeText(getContext(), "Ошибка: "+_dataResponse.getErrors().get(0).getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        title.setText(_dataResponse.getData().mapNode().title());
                        author.setText("Автор: "+_dataResponse.getData().mapNode().getAuthor().username());
                        date.setText("Дата создания: "+_dataResponse.getData().mapNode().date_created());
                        editor.render(_dataResponse.getData().mapNode().description());

                    }
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                ViewPostFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override public void run() {
                        progress.hide();
                        ((MainActivity)getActivity()).navController
                        .navigate(R.id.nav_home);
                        Toast.makeText(getContext(), "Ошибка запроса, попробуйте позже.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

}