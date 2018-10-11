package com.mahta.rastin.broadcastapplicationadmin.activity.media;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplicationadmin.dialog.file_picker.controller.DialogSelectionListener;
import com.mahta.rastin.broadcastapplicationadmin.dialog.file_picker.model.DialogConfigs;
import com.mahta.rastin.broadcastapplicationadmin.dialog.file_picker.model.DialogProperties;
import com.mahta.rastin.broadcastapplicationadmin.dialog.file_picker.view.FilePickerDialog;
import com.mahta.rastin.broadcastapplicationadmin.global.Constant;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.helper.Utils;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Media;

import java.io.File;

public class insertMediaFragment extends Fragment implements View.OnClickListener{


    private EditTextPlus edtTitle, edtDesc;
    private TextViewPlus txtFile;
    private File file;

    private LinearLayout bottomSheetLinearLayout;
    private BottomSheetBehavior bottomSheetBehavior;

    private String path;
    private ContentValues contentValues;
    boolean serverResponsed = false;

    private Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            this.activity = (Activity) context;
        }
    }
    public static insertMediaFragment newInstance (Media media) {

        insertMediaFragment fragment = new insertMediaFragment();

        if (media != null) {
            Bundle data = new Bundle();

            data.putInt(Keys.KEY_ID, media.getId());
            data.putString(Keys.KEY_TITLE, media.getTitle());
            data.putString(Keys.KEY_DESCRIPTION, media.getDescription());

            fragment.setArguments(data);
        }

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_insert_media, container , false);

        rootView.findViewById(R.id.imgBack).setOnClickListener(this);
        rootView.findViewById(R.id.txtApply).setOnClickListener(this);
        rootView.findViewById(R.id.txtBack).setOnClickListener(this);

        edtTitle = rootView.findViewById(R.id.edt_title);
        edtDesc= rootView.findViewById(R.id.edt_desc);
        txtFile = rootView.findViewById(R.id.txt_file);

        edtTitle.setOnClickListener(this);
        edtDesc.setOnClickListener(this);

        rootView.findViewById(R.id.lay_inStorage).setOnClickListener(this);
        rootView.findViewById(R.id.lay_sdStorage).setOnClickListener(this);

        bottomSheetLinearLayout = rootView.findViewById(R.id.bottomsheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLinearLayout);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);


        txtFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                } else {
                    // hiding keyboard
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtDesc.getWindowToken(), 0);

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });


        if (getArguments() != null) {

            edtTitle.setText(getArguments().getString(Keys.KEY_TITLE));
            edtDesc.setText(getArguments().getString(Keys.KEY_DESCRIPTION));

            txtFile.setText("انتخاب فایل جدید");
        }

        return rootView;
    }

    private void showFileChooser() {

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        DialogProperties properties = new DialogProperties();

        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(path);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        //user "/" in order to browse all files

        properties.extensions = null;

        FilePickerDialog dialog = new FilePickerDialog(activity, properties);

        dialog.setTitle("Select a File");

        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {

                HandleSelectedFile(files);
            }
        });

        dialog.show();
    }

    private void HandleSelectedFile(String[] files) {

        File selectedFile = new File(files[0]);

        G.i( files[0]);

        if (!files[0].contains(".mp3") ) {

            G.toastShort("خطا : فرمت فایل", activity);

        } else if ((selectedFile.length()) / 1024 >= 10000) {

            G.i(("File Size: " + selectedFile.length()/1024) + "KB");
            G.toastShort("خطا : حجم فایل", activity);

        } else {

            file = selectedFile;
            txtFile.setText(file.getName());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.imgBack || id == R.id.txtBack) {
            activity.finish();

        } else if (id == R.id.txtApply) {

            String title = edtTitle.getText().toString().trim();
            String description = edtDesc.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty()) {

                G.toastShort("اطلاعات ورودی ناکافی است", activity);
                return;
            }

            Utils.changeLoadingResource(activity, 0);

            //using this way just to handle scenarios that server didn't respond in SERVER_RESPONSE_TIME
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (!serverResponsed) {
                        Utils.changeLoadingResource(activity, 1);
                    }
                }
            }, Constant.UPLOAD_TIME_OUT);

//            loadingLayout.setVisibility(View.VISIBLE);


            contentValues = new ContentValues();

            contentValues.put(Keys.KEY_TOKEN, RealmController.getInstance().getUserToken().getToken());
            contentValues.put(Keys.KEY_TITLE, title);
            contentValues.put(Keys.KEY_DESCRIPTION, description);

//            new UploadHelper(NewMediaActivity.this, file, contentValues);

            if (getArguments() == null) {

                if (file == null)
                    G.toastShort("فایل انتخاب نشده است", activity);
                else
                    createMedia();

            } else {
                updateMedia();
            }


        } else if (id == R.id.lay_inStorage) {

            path = "/mnt/sdcard";;
            showFileChooser();

        } else if (id == R.id.lay_sdStorage) {

            path = "/mnt/sdcard1";
            showFileChooser();

        } else if (id == R.id.edt_title || id == R.id.edt_desc) {

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    private void createMedia() {

        new HttpCommand(HttpCommand.COMMAND_CREATE_MEDIA, file, contentValues).setOnResultListener(new OnResultListener() {
            @Override
            public void onResult(String result) {

                serverResponsed = true;

                if (JSONParser.getResultCodeFromJson(result) == Keys.RESULT_COUNT_LIMIT){

                    G.toastShort("تعداد رسانه مجاز : " + JSONParser.getLimitationCode(result) , activity);

                } else if (JSONParser.getResultCodeFromJson(result) == Keys.RESULT_SUCCESS) {
                    G.toastShort("رسانه جدید افزوده شد", activity);
                    activity.finish();
                }
//                    loadingLayout.setVisibility(View.GONE);
            }
        }).execute();
    }

    private void updateMedia() {

        int mediaId = getArguments().getInt(Keys.KEY_ID);

        new HttpCommand(HttpCommand.COMMAND_UPDATE_MEDIA, file, contentValues, mediaId+"").setOnResultListener(new OnResultListener() {
            @Override
            public void onResult(String result) {

                serverResponsed = true;

                if (JSONParser.getResultCodeFromJson(result) == Keys.RESULT_SUCCESS) {

                    Toast.makeText(activity, "رسانه با موفقیت ویرایش شد", Toast.LENGTH_SHORT).show();
                    activity.finish();
                }
            }
        }).execute();
    }
}