package com.mahta.rastin.broadcastapplicationadmin.activity.media;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
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
import com.mahta.rastin.broadcastapplicationadmin.activity.message.MessageListActivity;
import com.mahta.rastin.broadcastapplicationadmin.adapter.MediaAdapter;
import com.mahta.rastin.broadcastapplicationadmin.custom.ButtonPlus;
import com.mahta.rastin.broadcastapplicationadmin.dialog.DeleteDialog;
import com.mahta.rastin.broadcastapplicationadmin.global.Constant;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.EndlessRecyclerViewScrollListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnDialogDeleteListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnItemClickListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Media;
import com.mahta.rastin.broadcastapplicationadmin.model.Message;

import java.util.List;

public class MediaListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private boolean noMoreMedia = false;

    public SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton btnAddMedia;
    private EndlessRecyclerViewScrollListener scrollListener;
    private MediaAdapter adapter;
    private TextView txtNoMedia;
    public SearchView searchView;
    private String searchPhrase = "null";
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
        setContentView(R.layout.activity_media_list);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setupSearchBar(toolbar);

        RecyclerView rcvPosts = findViewById(R.id.rcvMedia);


        btnAddMedia = findViewById(R.id.btnAddMedia);
        btnAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaListActivity.this, MediaActivity.class);
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
                loadMedia(Constant.MEDIA_REQUEST_COUNT,0,0, searchPhrase);
            }
        });

        txtNoMedia = findViewById(R.id.txtNoMedia);
        lnlLoading = findViewById(R.id.lnlLoading);
        lnlNoNetwork = findViewById(R.id.lnlNoNetwork);

        adapter = new MediaAdapter(this, RealmController.getInstance().getAllMedia());
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {

                if (G.isNetworkAvailable(MediaListActivity.this)){

                    Intent intent = new Intent(MediaListActivity.this, MediaActivity.class);
                    intent.putExtra(Keys.KEY_EXTRA_FLAG, RealmController.getInstance().getAllMedia().get(position));
                    startActivity(intent);

                }else {
                    G.toastLong(G.getStringFromResource(R.string.no_internet, MediaListActivity.this), MediaListActivity.this);
                }

            }
        });

        adapter.setOnItemDeleteListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(View view, final int position) {

                final Media media = RealmController.getInstance().getAllMedia().get(position);

                DeleteDialog deleteDialog = new DeleteDialog(MediaListActivity.this);

                deleteDialog.setOnDeleteListener(new OnDialogDeleteListener() {
                    @Override
                    public void onConfirmDeleteItem(boolean confirm) {

                        if (confirm) {

                            ContentValues contentValues = new ContentValues();

                            contentValues.put(Keys.KEY_TOKEN, RealmController.getInstance().getUserToken().getToken());

                            new HttpCommand(HttpCommand.COMMAND_DELETE_MEDIA, contentValues, media.getId()+"")
                                    .setOnResultListener(new OnResultListener() {
                                        @Override
                                        public void onResult(String result) {

                                            if (JSONParser.getResultCodeFromJson(result) == Keys.RESULT_SUCCESS) {
                                                G.toastShort("رسانه با موفقیت حذف شد", MediaListActivity.this);

                                                // rastin's movement
                                                RealmController.getInstance().removeMedia(media.getId());
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvPosts.setLayoutManager(linearLayoutManager);
        rcvPosts.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                G.i("YOOOHO");

                G.i("passed threshold p: " + page + " activity: " + totalItemsCount);
                if (!noMoreMedia) {
                    loadMedia(Constant.MEDIA_REQUEST_COUNT, totalItemsCount, page, searchPhrase);
                } else
                    noMoreMedia = false;

            }

            @Override
            public void onScroll(RecyclerView view, int dx, int dy) {

                if (dy > 0 && btnAddMedia.getVisibility() == View.VISIBLE) {
                    btnAddMedia.hide();
                } else if (dy < 0 && btnAddMedia.getVisibility() != View.VISIBLE) {
                    btnAddMedia.show();
                }
            }
        };
        rcvPosts.addOnScrollListener(scrollListener);



    }

    @Override
    protected void onResume() {
        super.onResume();

        reset();
        loadMedia(Constant.MEDIA_REQUEST_COUNT, 0, 0, "null");
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
                    view = new View(MediaListActivity.this);
                }imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                callSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (searchView.getQuery().length() == 0) {
                    reset();
                    searchPhrase = "null";
                    loadMedia(Constant.MEDIA_REQUEST_COUNT, 0, 0, "null");
                }
                return false;
            }

            void callSearch(String query) {

                reset();
                searchPhrase = query;
                loadMedia(Constant.MEDIA_REQUEST_COUNT, 0, 0, query);
            }

        });

    }

    private void loadMedia(final int count, final int start, final int page, String searchPhrase) {
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

        new HttpCommand(HttpCommand.COMMAND_GET_POSTS, contentValues,Constant.TYPE_MEDIA,count + "", page + "", searchPhrase, "null", "null")
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

                    List<Media> media = JSONParser.parseMedia(result);

                    if (media != null) {
                        for (Media m : media) {
                            G.i(m.getPath() + "\n");
                            RealmController.getInstance().addMedia(m);
                        }
                        adapter.notifyItemRangeInserted(start - 1, media.size());

                        if (media.size() < count)
                            noMoreMedia = true;

                    } else {
                        G.i("No more Media is returned");
                    }

                    if (adapter.getItemCount() <= 0)
                        txtNoMedia.setVisibility(View.VISIBLE);
                    else
                        txtNoMedia.setVisibility(View.GONE);

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
                txtNoMedia.setVisibility(View.GONE);
                lnlNoNetwork.setVisibility(View.VISIBLE);
                btnTryAgain.setVisibility(View.VISIBLE);
                lnlLoading.setVisibility(View.GONE);
                break;
            case 1:
                txtNoMedia.setVisibility(View.GONE);
                lnlNoNetwork.setVisibility(View.GONE);
                btnTryAgain.setVisibility(View.GONE);
                lnlLoading.setVisibility(View.VISIBLE);
                break;
            default:
                txtNoMedia.setVisibility(View.GONE);
                lnlNoNetwork.setVisibility(View.GONE);
                btnTryAgain.setVisibility(View.GONE);
                lnlLoading.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                break;
        }
    }

    private void reset() {
        clearMedia();
        isFirstLoad = true;
        adapter.notifyDataSetChanged();
        scrollListener.resetState();
    }

    private void clearMedia(){
        RealmController.getInstance().clearAllMedia();
    }

    @Override
    public void onRefresh() {
        if (!isLoading){
            reset();
            loadMedia(Constant.MEDIA_REQUEST_COUNT, 0, 0, searchPhrase);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doesFragmentExists = false;
    }
}
