package com.mahta.rastin.broadcastapplicationadmin.activity.program;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.adapter.ProgramAdapter;
import com.mahta.rastin.broadcastapplicationadmin.custom.ButtonPlus;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplicationadmin.dialog.GroupListDialog;
import com.mahta.rastin.broadcastapplicationadmin.global.Constant;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.EndlessRecyclerViewScrollListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnDismissListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnItemClickListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Program;

import java.util.List;

public class ProgramListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private boolean noMoreProgram = false;

    public SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton btnNewProgram;
    private EndlessRecyclerViewScrollListener scrollListener;
    private TextViewPlus txtGroups;
    private ProgramAdapter adapter;
    private TextView txtNoPrograms;
    public SearchView searchView;
    private String searchPhrase = "null";
    private String mGroupId = "null";
    private boolean isLoaded;
    private LinearLayout lnlLoading;
    private LinearLayout lnlNoNetwork;
    private ButtonPlus btnTryAgain;
    private boolean isFirstLoad = true;
    private boolean doesFragmentExists = true;
    private boolean isLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_list);

        //must clear realm before loading
        clearPrograms();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setupSearchBar(toolbar);

        RecyclerView rcvPrograms = findViewById(R.id.rcvPrograms);

        findViewById(R.id.lnlGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GroupListDialog dialog = new GroupListDialog(ProgramListActivity.this);

                dialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(String groupId, String groupTitle) {

                        mGroupId = groupId;
                        txtGroups.setText(groupTitle);
                        G.i(groupId);
                        reset();
                        loadPrograms(Constant.PROGRAM_REQUEST_COUNT, 0, 0, searchPhrase, mGroupId);
                    }
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        btnNewProgram = findViewById(R.id.btnNewProgram);
        findViewById(R.id.btnNewProgram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ProgramListActivity.this, NewProgramActivity.class);
                startActivity(intent);
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.colorAccent
        );
        swipeRefreshLayout.setOnRefreshListener(this);


        btnTryAgain = findViewById(R.id.btnTryAgain);
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.i("pressed");
                loadPrograms(Constant.PROGRAM_REQUEST_COUNT,0,0, searchPhrase, mGroupId);
            }
        });

        txtGroups = findViewById(R.id.txtGroup);
        txtNoPrograms = findViewById(R.id.txtNoProgram);
        lnlLoading = findViewById(R.id.lnlLoading);
        lnlNoNetwork = findViewById(R.id.lnlNoNetwork);

        adapter = new ProgramAdapter(this, RealmController.getInstance().getAllPrograms());
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {

                if (G.isNetworkAvailable(ProgramListActivity.this)){

                    Intent intent = new Intent(ProgramListActivity.this, ProgramContentActivity.class);
                    intent.putExtra(Keys.KEY_EXTRA_FLAG, RealmController.getInstance().getAllPrograms().get(position));
                    startActivity(intent);

                }else {
                    G.toastLong(G.getStringFromResource(R.string.no_internet, ProgramListActivity.this), ProgramListActivity.this);
                }

            }
        });
        rcvPrograms.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvPrograms.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                G.i("YOOOHO");

                G.i("passed threshold p: " + page + " activity: " + totalItemsCount);

                if (!noMoreProgram){
                    loadPrograms(Constant.PROGRAM_REQUEST_COUNT,totalItemsCount,page, searchPhrase, mGroupId);
                }else
                    noMoreProgram = false;
            }

            @Override
            public void onScroll(RecyclerView view, int dx, int dy) {

                if (dy > 0 && btnNewProgram.getVisibility() == View.VISIBLE) {
                    btnNewProgram.hide();
                } else if (dy < 0 && btnNewProgram.getVisibility() != View.VISIBLE) {
                    btnNewProgram.show();
                }
            }
        };
        // Adds the scroll listener to RecyclerView
        rcvPrograms.addOnScrollListener(scrollListener);


        loadPrograms(Constant.PROGRAM_REQUEST_COUNT,0,0, searchPhrase, mGroupId);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }


//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        loadPrograms(Constant.PROGRAM_REQUEST_COUNT,0,0, searchPhrase, mGroupId);
//    }


    private void setupSearchBar(final View parent){

        searchView = parent.findViewById(R.id.searchView);
        searchView.setQueryHint(G.getStringFromResource(R.string.search, this));

        EditText text = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        text.setTextColor(Color.WHITE);
        text.setHintTextColor(G.getColorFromResource(R.color.colorGray, this));
        text.setHint(G.getStringFromResource(R.string.search, this));
        searchView.onActionViewCollapsed();

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchView.isIconified()) {
                    searchView.setIconified(false);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                //Closing the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                View view = getCurrentFocus();
                if (view == null) {
                    view = new View(ProgramListActivity.this);
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                callSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (searchView.getQuery().length() == 0) {
                    reset();
                    searchPhrase = "null";
                    loadPrograms(Constant.PROGRAM_REQUEST_COUNT, 0, 0, searchPhrase, mGroupId);
                }
                return false;
            }

            void callSearch(String query) {

                reset();
                searchPhrase = query;
                loadPrograms(Constant.PROGRAM_REQUEST_COUNT, 0, 0, query, mGroupId);
//                //Make it visible
//                prgWait.setVisibility(View.VISIBLE);
            }

        });

    }

    private void loadPrograms(final int count, final int start, final int page, String searchPhrase, String groupId){
        isLoading = true;

        if (isFirstLoad){
            changeLoadingResource(1);
            isLoaded = false;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (doesFragmentExists) {
                        if (!isLoaded) {
                            changeLoadingResource(0);
                            isLoading = false;
                        }
                    }
                }
            }, Constant.TIME_OUT);
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(Keys.KEY_TOKEN, RealmController.getInstance().getUserToken().getToken());

        new HttpCommand(HttpCommand.COMMAND_GET_POSTS, contentValues,Constant.TYPE_PROGRAM, count + "" , page + "" , searchPhrase, groupId)
                .setOnResultListener(new OnResultListener() {
                    @Override
                    public void onResult(String result) {

                        if (doesFragmentExists) {
                            isLoading = false;

                            if (isFirstLoad) {
                                isLoaded = true;
                                isFirstLoad = false;
                                changeLoadingResource(2);
                            }

                            List<Program> programs = JSONParser.parsePrograms(result);

                            if (programs != null) {

                                for (Program program : programs) {
                                    G.i(program.getId() + "");
                                    RealmController.getInstance().addProgram(program);
                                }
                                adapter.notifyItemRangeInserted(start - 1, programs.size());

                                if (programs.size() < count)
                                    noMoreProgram = true;

                            } else {
                                G.i("No more program is returned");
                            }

                            if (adapter.getItemCount() <= 0)
                                txtNoPrograms.setVisibility(View.VISIBLE);
                            else
                                txtNoPrograms.setVisibility(View.GONE);
                        }
                    }
                }).execute();
    }

    private void changeLoadingResource(int state) {

        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }

        switch (state) {
            case 0:
                txtNoPrograms.setVisibility(View.GONE);
                lnlNoNetwork.setVisibility(View.VISIBLE);
                btnTryAgain.setVisibility(View.VISIBLE);
                lnlLoading.setVisibility(View.GONE);
                break;
            case 1:
                txtNoPrograms.setVisibility(View.GONE);
                lnlNoNetwork.setVisibility(View.GONE);
                btnTryAgain.setVisibility(View.GONE);
                lnlLoading.setVisibility(View.VISIBLE);
                break;
            default:
                txtNoPrograms.setVisibility(View.GONE);
                lnlNoNetwork.setVisibility(View.GONE);
                btnTryAgain.setVisibility(View.GONE);
                lnlLoading.setVisibility(View.GONE);
                break;
        }
    }

    private void reset() {

        clearPrograms();
        isFirstLoad = true;

        adapter.notifyDataSetChanged();
        scrollListener.resetState();
    }

    private void clearPrograms(){
        RealmController.getInstance().clearAllPrograms();
    }

    @Override
    public void onRefresh() {
        if (!isLoading){
            reset();
            loadPrograms(Constant.POST_REQUEST_COUNT, 0, 0, searchPhrase, mGroupId);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doesFragmentExists = false;
    }

}
