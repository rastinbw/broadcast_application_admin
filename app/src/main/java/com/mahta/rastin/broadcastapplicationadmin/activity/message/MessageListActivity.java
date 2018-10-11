package com.mahta.rastin.broadcastapplicationadmin.activity.message;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.mahta.rastin.broadcastapplicationadmin.adapter.MessageAdapter;
import com.mahta.rastin.broadcastapplicationadmin.custom.ButtonPlus;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplicationadmin.dialog.DeleteDialog;
import com.mahta.rastin.broadcastapplicationadmin.dialog.FilterDialog;
import com.mahta.rastin.broadcastapplicationadmin.global.Constant;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.EndlessRecyclerViewScrollListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnDialogDeleteListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnDismissListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnItemClickListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Message;

import java.util.List;

public class MessageListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private boolean noMoreMessage = false;

    public SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton btnNewMessage;
    private EndlessRecyclerViewScrollListener scrollListener;
    private TextViewPlus txtGroupTitle;
    private TextViewPlus txtFieldTitle;
    private MessageAdapter adapter;
    private TextView txtNoMessages;
    public SearchView searchView;
    private String searchPhrase = "null";
    private String mGroupId = "null";
    private String mFieldId = "null";
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
        setContentView(R.layout.activity_message_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setupSearchBar(toolbar);

        RecyclerView rcvMessages = findViewById(R.id.rcvPrograms);

        findViewById(R.id.lnlGradeFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FilterDialog dialog = new FilterDialog(MessageListActivity.this, Constant.TYPE_GROUP);

                dialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(String filterId, String filterTitle) {

                        // if any filter is chosen
                        if (!filterTitle.isEmpty()) {
                            txtGroupTitle.setText(filterTitle);

                            mGroupId = filterId;
                            txtGroupTitle.setText(filterTitle);

                            reset();
                            loadMessages(Constant.PROGRAM_REQUEST_COUNT, 0, 0, searchPhrase, mGroupId, mFieldId);
                        }

                    }
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        findViewById(R.id.lnlFieldFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FilterDialog dialog = new FilterDialog(MessageListActivity.this, Constant.TYPE_FIELD);

                dialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(String filterId, String filterTitle) {

                        // if any filter is chosen
                        if (!filterTitle.isEmpty()) {
                            txtFieldTitle.setText(filterTitle);

                            mFieldId = filterId;
                            G.i(filterId);

                            reset();
                            loadMessages(Constant.PROGRAM_REQUEST_COUNT, 0, 0, searchPhrase, mGroupId, mFieldId);
                        }

                    }
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        btnNewMessage = findViewById(R.id.btnNewProgram);
        findViewById(R.id.btnNewProgram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MessageListActivity.this, NewMessageActivity.class);
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
                loadMessages(Constant.PROGRAM_REQUEST_COUNT,0,0, searchPhrase, mGroupId, mFieldId);
            }
        });

        txtGroupTitle = findViewById(R.id.txtGroup);
        txtFieldTitle = findViewById(R.id.txtField);
        txtNoMessages = findViewById(R.id.txtNoProgram);
        lnlLoading = findViewById(R.id.lnlLoading);
        lnlNoNetwork = findViewById(R.id.lnlNoNetwork);

        adapter = new MessageAdapter(this, RealmController.getInstance().getAllMessages());
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {

                if (G.isNetworkAvailable(MessageListActivity.this)){

                    Intent intent = new Intent(MessageListActivity.this, EditMessageActivity.class);
                    intent.putExtra(Keys.KEY_EXTRA_FLAG, RealmController.getInstance().getAllMessages().get(position));
                    startActivity(intent);

                }else {
                    G.toastLong(G.getStringFromResource(R.string.no_internet, MessageListActivity.this), MessageListActivity.this);
                }

            }
        });

        adapter.setOnItemDeleteListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(View view, final int position) {

                final Message message = RealmController.getInstance().getAllMessages().get(position);

                DeleteDialog deleteDialog = new DeleteDialog(MessageListActivity.this);

                deleteDialog.setOnDeleteListener(new OnDialogDeleteListener() {
                    @Override
                    public void onConfirmDeleteItem(boolean confirm) {

                        if (confirm) {

                            ContentValues contentValues = new ContentValues();

                            contentValues.put(Keys.KEY_TOKEN, RealmController.getInstance().getUserToken().getToken());

                            new HttpCommand(HttpCommand.COMMAND_DELETE_MESSAGE, contentValues, message.getId()+"")
                                    .setOnResultListener(new OnResultListener() {
                                @Override
                                public void onResult(String result) {

                                    if (JSONParser.getResultCodeFromJson(result) == Keys.RESULT_SUCCESS) {
                                        G.toastShort("پیام با موفقیت حذف شد", MessageListActivity.this);

                                        // rastin's movement
                                        RealmController.getInstance().removeMessage(message.getId());
                                        adapter.notifyItemRemoved(position);
                                    }
                                }
                            }).execute();
                        }

                    }
                });

                deleteDialog.show();
            }
        });

        rcvMessages.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvMessages.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                G.i("YOOOHO");

                G.i("passed threshold p: " + page + " activity: " + totalItemsCount);

                if (!noMoreMessage){
                    loadMessages(Constant.PROGRAM_REQUEST_COUNT,totalItemsCount,page, searchPhrase, mGroupId, mFieldId);
                }else
                    noMoreMessage = false;
            }

            @Override
            public void onScroll(RecyclerView view, int dx, int dy) {

                if (dy > 0 && btnNewMessage.getVisibility() == View.VISIBLE) {
                    btnNewMessage.hide();
                } else if (dy < 0 && btnNewMessage.getVisibility() != View.VISIBLE) {
                    btnNewMessage.show();
                }
            }
        };
        // Adds the scroll listener to RecyclerView
        rcvMessages.addOnScrollListener(scrollListener);

    }

    @Override
    protected void onResume() {
        super.onResume();

        reset();
        loadMessages(Constant.PROGRAM_REQUEST_COUNT,0,0, searchPhrase, mGroupId, mFieldId);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }


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
                    view = new View(MessageListActivity.this);
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
                    loadMessages(Constant.PROGRAM_REQUEST_COUNT, 0, 0, searchPhrase, mGroupId, mFieldId);
                }
                return false;
            }

            void callSearch(String query) {

                reset();
                searchPhrase = query;
                loadMessages(Constant.PROGRAM_REQUEST_COUNT, 0, 0, query, mGroupId, mFieldId);
//                //Make it visible
//                prgWait.setVisibility(View.VISIBLE);
            }

        });

    }

    private void loadMessages(final int count, final int start, final int page, String searchPhrase, String groupId, String fieldId){
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

        new HttpCommand(HttpCommand.COMMAND_GET_POSTS, contentValues,Constant.TYPE_MESSAGE, count + "" , page + "" , searchPhrase, groupId, fieldId)
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

                            List<Message> messages = JSONParser.parseMessages(result);

                            if (messages != null) {

                                for (Message message : messages) {
                                    G.i(message.getField_id() + "");
                                    RealmController.getInstance().addMessage(message);
                                }
                                adapter.notifyItemRangeInserted(start - 1, messages.size());

                                if (messages.size() < count)
                                    noMoreMessage = true;

                            } else {
                                G.i("No more Message is returned");
                            }

                            if (adapter.getItemCount() <= 0)
                                txtNoMessages.setVisibility(View.VISIBLE);
                            else
                                txtNoMessages.setVisibility(View.GONE);
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
                txtNoMessages.setVisibility(View.GONE);
                lnlNoNetwork.setVisibility(View.VISIBLE);
                btnTryAgain.setVisibility(View.VISIBLE);
                lnlLoading.setVisibility(View.GONE);
                break;
            case 1:
                txtNoMessages.setVisibility(View.GONE);
                lnlNoNetwork.setVisibility(View.GONE);
                btnTryAgain.setVisibility(View.GONE);
                lnlLoading.setVisibility(View.VISIBLE);
                break;
            default:
                txtNoMessages.setVisibility(View.GONE);
                lnlNoNetwork.setVisibility(View.GONE);
                btnTryAgain.setVisibility(View.GONE);
                lnlLoading.setVisibility(View.GONE);
                break;
        }
    }

    private void reset() {

        clearMessages();
        isFirstLoad = true;

        adapter.notifyDataSetChanged();
        scrollListener.resetState();
    }

    private void clearMessages(){
        RealmController.getInstance().clearAllMessages();
    }

    @Override
    public void onRefresh() {
        if (!isLoading){
            reset();
            loadMessages(Constant.POST_REQUEST_COUNT, 0, 0, searchPhrase, mGroupId, mFieldId);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doesFragmentExists = false;
    }

}
