package com.yageorgiy.bubblemap.ui.publishpost;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.github.irshulx.models.EditorTextStyle;
import com.yageorgiy.bubblemap.BubbleMapApplication;
import com.yageorgiy.bubblemap.MainActivity;
import com.yageorgiy.bubblemap.PublishPostMutation;
import com.yageorgiy.bubblemap.R;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.app.Activity.RESULT_OK;

public class PublishPostFragment extends Fragment {

    private MainViewModel mViewModel;
    private String selected_image_path = "";

    public int id = 0;

    public static PublishPostFragment newInstance() {
        return new PublishPostFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.publish_post_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // TODO: Use the ViewModel


        this.id = getArguments().getInt("id");




        selected_image_path = "";

        final MainActivity activity = (MainActivity)getActivity();

        final Editor editor = activity.findViewById(R.id.publish_post_editor);

        activity.findViewById(R.id.publish_post_button_h1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.H1);
            }
        });

        activity.findViewById(R.id.publish_post_button_h2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.H2);
            }
        });

        activity.findViewById(R.id.publish_post_button_h3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.H3);
            }
        });

        activity.findViewById(R.id.publish_post_button_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.BOLD);
            }
        });

        activity.findViewById(R.id.publish_post_button_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.ITALIC);
            }
        });

        activity.findViewById(R.id.publish_post_button_intent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.INDENT);
            }
        });

        activity.findViewById(R.id.publish_post_button_outdent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.OUTDENT);
            }
        });

        /*
        activity.findViewById(R.id.publish_post_button_bulleted_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertList(false);
            }
        });
        */

        /*
        activity.findViewById(R.id.publish_post_button_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextColor("#FF3333");
            }
        });
         */

        activity.findViewById(R.id.publish_post_button_numeric_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertList(true);
            }
        });

        activity.findViewById(R.id.publish_post_button_line_divider).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertDivider();
            }
        });

        activity.findViewById(R.id.publish_post_button_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.openImagePicker();
            }
        });

        activity.findViewById(R.id.publish_post_button_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertLink();
            }
        });

        /*
        activity.findViewById(R.id.action_erase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clearAllContents();
            }
        });
         */

        activity.findViewById(R.id.publish_post_button_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.BLOCKQUOTE);
            }
        });

        editor.render();


        final PublishPostFragment _this = this;

        activity.findViewById(R.id.publish_post_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

                // Update with mime types
                intent.setType("image/jpeg");

                // Update with additional mime types here using a String[].
                //TODO
//                intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/png", "image/jpg"});
                intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg"});

                // Only pick openable and local files. Theoretically we could pull files from google drive
                // or other applications that have networked files, but that's unnecessary for this example.
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

                // REQUEST_CODE = <some-integer>
                startActivityForResult(intent, 4);




            }
        });


        final BubbleMapApplication application = (BubbleMapApplication)getActivity().getApplication();

        final EditText editTextTitle = activity.findViewById(R.id.publish_post_title);
        final Spinner spinnerTag = activity.findViewById(R.id.publish_post_tag_spinner);

        final TextView error = activity.findViewById(R.id.publish_post_error_text);

        final ProgressDialog progress = new ProgressDialog(this.getContext());
        progress.setTitle("Загрузка...");
        progress.setMessage("Публикация записи...");
        progress.setCancelable(false);

        activity.findViewById(R.id.publish_post_action_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String title = editTextTitle.getText().toString();
                String tag = spinnerTag.getSelectedItem().toString();

                String html = editor.getContentAsHTML();

                if(title.equals("")){
                    error.setText("Введите название поста");
                    return;
                }

                if(tag.equals("")){
                    error.setText("Выберите корректную категорию поста");
                    return;
                }

//                if(_this.selected_image_path.equals("")){
//                    error.setText("Выберите картинку поста");
//                    return;
//                }

                if(html.equals("")){
                    error.setText("Введите текст поста");
                    return;
                }

                progress.show();


                application.apolloClient().mutate(
                    PublishPostMutation.builder()
                        .title(title)
                        .tag(tag)
//                        .image(new FileUpload("image/jpeg", _this.selected_image_path))
                        .content(html)
                        .build()
                ).enqueue(new ApolloCall.Callback<PublishPostMutation.Data>() {

                    @Override
                    public void onResponse(@NotNull Response<PublishPostMutation.Data> dataResponse) {
                        final Response<PublishPostMutation.Data> _dataResponse = dataResponse;

                        PublishPostFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override public void run() {
//                                TextView txtResponse = (TextView) findViewById(R.id.txtResponse);
//                                txtResponse.setText(buffer.toString());
//                            loginButton.setEnabled(true);

                                progress.hide();

                                if(_dataResponse.hasErrors()){
                                    error.setText(_dataResponse.getErrors().get(0).getMessage());
                                    return;
                                }

                                activity.navController.navigate(R.id.nav_home);
                                Toast.makeText(_this.getContext(), "Успешно опубликовано.", Toast.LENGTH_LONG).show();

                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull final ApolloException e) {
                        PublishPostFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override public void run() {
                                progress.hide();
                                error.setText("Ошибка запроса, поропбуйте позже "+e.getMessage());
                            }
                        });
                    }
                });









            }
        });







    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the user doesn't pick a file just return
        if (requestCode != 4 || resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == 4) {
            String folderPath = data.getDataString();
            try {
                importFile(data.getData(), folderPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        // Import the file

    }


    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }


    public void importFile(Uri uri, String filePath) throws Exception {
        String fileName = getFileName(uri);

//        File test = new File(uri.getPath());



        // filePath
        this.selected_image_path = filePath;

        TextView tv = getActivity().findViewById(R.id.publish_post_image);
        tv.setText(fileName);

        Toast.makeText(this.getContext(), "Выбран файл: "+fileName+" ( "+this.selected_image_path+" )", Toast.LENGTH_SHORT).show();

        // The temp file could be whatever you want
//        File fileCopy = copyToTempFile(uri, File tempFile)

        // Done!
    }


    public String getFilePathFromContentPath(Uri uri) {
        String filePath = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = this.getActivity().getContentResolver().query(uri,
                    new String[]{android.provider.MediaStore.Images.ImageColumns.DATA},
                    null,
                    null,
                    null);
            cursor.moveToFirst();
            filePath = cursor.getString(0);
            cursor.close();
        } else {
            filePath = uri.getPath();
        }
        return filePath;
    }


    /**
     * Obtains the file name for a URI using content resolvers. Taken from the following link
     * https://developer.android.com/training/secure-file-sharing/retrieve-info.html#RetrieveFileInfo
     *
     * @param uri a uri to query
     * @return the file name with no path
     * @throws IllegalArgumentException if the query is null, empty, or the column doesn't exist
     */
    private String getFileName(Uri uri) throws IllegalArgumentException {
        // Obtain a cursor with information regarding this uri
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            throw new IllegalArgumentException("Can't obtain file name, cursor is empty");
        }

        cursor.moveToFirst();

        String fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));

        cursor.close();

        return fileName;
    }

    /**
     * Copies a uri reference to a temporary file
     *
     * @param uri      the uri used as the input stream
     * @param tempFile the file used as an output stream
     * @return the input tempFile for convenience
     * @throws IOException if an error occurs
     */
    /*
    private File copyToTempFile(Uri uri, File tempFile) throws IOException {
        // Obtain an input stream from the uri
        InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);

        if (inputStream == null) {
            throw new IOException("Unable to obtain input stream from URI");
        }

        // Copy the stream to the temp file
//        FileUtils.copyInputStreamToFile(inputStream, tempFile);

        return tempFile;
    }
     */



}