package com.mahta.rastin.broadcastapplicationadmin.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.adapter.FieldAdapter;
import com.mahta.rastin.broadcastapplicationadmin.adapter.GroupAdapter;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplicationadmin.global.Constant;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnItemClickListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Field;
import com.mahta.rastin.broadcastapplicationadmin.model.Group;

import java.util.List;


public class FilterDialog extends Dialog {

    public Activity activity;
    public Dialog dialog;
    private com.mahta.rastin.broadcastapplicationadmin.interfaces.OnDismissListener onDismissListener;
    private String filterId = "null";
    private String filterTitle = "";
    private String filterType;

    private TextViewPlus txtAllFilters, txtDialogTitle;

    public FilterDialog(Activity activity, String filterType) {
        super(activity);
        this.activity = activity;
        this.filterType = filterType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_group_list_dialog);

        txtAllFilters = findViewById(R.id.txtAllFilters);
        txtDialogTitle = findViewById(R.id.txtDialogTitle);

        if (filterType.equals(Constant.TYPE_GROUP)) {

            txtAllFilters.setText(R.string.all_grades);
            txtDialogTitle.setText(R.string.choose_grade);

            final List<Group> list = RealmController.getInstance().getGroupList();
            GroupAdapter adapter = new GroupAdapter(activity, list);

            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClicked(View view, int position) {

                    filterId = Integer.toString(list.get(position).getId());
                    filterTitle = list.get(position).getTitle();
                    dismiss();
                }
            });

            final RecyclerView rcvGroups = findViewById(R.id.rcvGroups);
            rcvGroups.setAdapter(adapter);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
            rcvGroups.setLayoutManager(linearLayoutManager);


        } else if (filterType.equals(Constant.TYPE_FIELD)) {

            txtAllFilters.setText(R.string.all_fields);
            txtDialogTitle.setText(R.string.choose_field);

            final List<Field> list = RealmController.getInstance().getFieldList();
            FieldAdapter adapter = new FieldAdapter(activity, list);

            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClicked(View view, int position) {

                    filterId = Integer.toString(list.get(position).getId());
                    filterTitle = list.get(position).getTitle();
                    dismiss();
                }
            });

            final RecyclerView rcvGroups = findViewById(R.id.rcvGroups);
            rcvGroups.setAdapter(adapter);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
            rcvGroups.setLayoutManager(linearLayoutManager);

        }


        findViewById(R.id.lytNoFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterId = "null";
                filterTitle = txtAllFilters.getText().toString();
                dismiss();
            }
        });


        if (onDismissListener != null)
            setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onDismissListener.onDismiss(filterId, filterTitle);
                }
            });

    }

    public void setOnDismissListener(com.mahta.rastin.broadcastapplicationadmin.interfaces.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }
}