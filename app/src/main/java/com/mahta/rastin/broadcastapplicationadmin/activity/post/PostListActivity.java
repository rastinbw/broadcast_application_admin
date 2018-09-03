package com.mahta.rastin.broadcastapplicationadmin.activity.post;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.activity.MyActivity;
import com.mahta.rastin.broadcastapplicationadmin.adapter.PostAdapter;
import com.mahta.rastin.broadcastapplicationadmin.custom.ButtonPlus;
import com.mahta.rastin.broadcastapplicationadmin.global.Constant;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.EndlessRecyclerViewScrollListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnItemClickListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Post;

import java.util.List;

public class PostListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private boolean noMorePost = false;

    public SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton btnNewPost;
    private EndlessRecyclerViewScrollListener scrollListener;
    private PostAdapter adapter;
    private TextView txtNoPosts;
    public SearchView searchView;
    private String searchPhrase = "null";
    private boolean isLoaded;
    private LinearLayout lnlLoading;
    private LinearLayout lnlNoNetwork;
    private ButtonPlus btnTryAgain;
    private boolean isFirstLoad = true;
    private boolean doesActivityExists = true;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        //must clear realm before loading
        clearPosts();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setupSearchBar(toolbar);

        RecyclerView rcvPosts = findViewById(R.id.rcvPosts);

        btnNewPost = findViewById(R.id.btnNewPost);

        findViewById(R.id.btnNewPost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostListActivity.this, NewPostActivity.class);
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
                loadPosts(Constant.POST_REQUEST_COUNT,0,0, searchPhrase);
            }
        });

        txtNoPosts = findViewById(R.id.txtNoPost);
        lnlLoading = findViewById(R.id.lnlLoading);
        lnlNoNetwork = findViewById(R.id.lnlNoNetwork);

        adapter = new PostAdapter(this, RealmController.getInstance().getAllPosts());
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {

                if (G.isNetworkAvailable(PostListActivity.this)){

                    Intent intent = new Intent(PostListActivity.this, EditPostActivity.class);
                    intent.putExtra(Keys.KEY_EXTRA_FLAG, RealmController.getInstance().getAllPosts().get(position));
                    startActivity(intent);


                }else {
                    G.toastLong(G.getStringFromResource(R.string.no_internet, PostListActivity.this), PostListActivity.this);
                }

            }
        });
        rcvPosts.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvPosts.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                G.i("YOOOHO");

                G.i("passed threshold p: " + page + " activity: " + totalItemsCount);
                if (!noMorePost){
                    loadPosts(Constant.POST_REQUEST_COUNT,totalItemsCount,page, searchPhrase);
                }else
                    noMorePost = false;

            }

            @Override
            public void onScroll(RecyclerView view, int dx, int dy) {

                if (dy > 0 && btnNewPost.getVisibility() == View.VISIBLE) {
                    btnNewPost.hide();
                } else if (dy < 0 && btnNewPost.getVisibility() != View.VISIBLE) {
                    btnNewPost.show();
                }
            }
        };
        // Adds the scroll listener to RecyclerView
        rcvPosts.addOnScrollListener(scrollListener);

        loadPosts(Constant.POST_REQUEST_COUNT,0,0, searchPhrase);
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
                    view = new View(PostListActivity.this);
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
                    loadPosts(Constant.POST_REQUEST_COUNT, 0, 0, "null");
                }
                return false;
            }

            void callSearch(String query) {

                reset();
                searchPhrase = query;
                loadPosts(Constant.POST_REQUEST_COUNT, 0, 0, query);
//                //Make it visible
//                prgWait.setVisibility(View.VISIBLE);
            }

        });
    }

    private void loadPosts(final int count, final int start, final int page, String searchPhrase){

        isLoading = true;

        if (isFirstLoad){
            changeLoadingResource(1);
            isLoaded = false;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (doesActivityExists) {
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

        new HttpCommand(HttpCommand.COMMAND_GET_POSTS, contentValues,Constant.TYPE_HTML, count + "", page + "", searchPhrase, "null")
                .setOnResultListener(new OnResultListener() {
                    @Override
                    public void onResult(String result) {

                        if (doesActivityExists) {
                            isLoading = false;

                            if (isFirstLoad) {
                                isLoaded = true;
                                isFirstLoad = false;
                                changeLoadingResource(2);
                            }

                            List<Post> posts = JSONParser.parsePosts(result);

                            if (posts != null) {

                                for (Post post : posts) {
                                    G.i(post.getId() + "");
                                    RealmController.getInstance().addPost(post);
                                }
                                adapter.notifyItemRangeInserted(start - 1, posts.size());

                                if (posts.size() < count)
                                    noMorePost = true;

                            } else {
                                G.i("No more Post is returned");
                            }

                            if (adapter.getItemCount() <= 0)
                                txtNoPosts.setVisibility(View.VISIBLE);
                            else
                                txtNoPosts.setVisibility(View.GONE);
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
                txtNoPosts.setVisibility(View.GONE);
                lnlNoNetwork.setVisibility(View.VISIBLE);
                btnTryAgain.setVisibility(View.VISIBLE);
                lnlLoading.setVisibility(View.GONE);
                break;
            case 1:
                txtNoPosts.setVisibility(View.GONE);
                lnlNoNetwork.setVisibility(View.GONE);
                btnTryAgain.setVisibility(View.GONE);
                lnlLoading.setVisibility(View.VISIBLE);
                break;
            default:
                txtNoPosts.setVisibility(View.GONE);
                lnlNoNetwork.setVisibility(View.GONE);
                btnTryAgain.setVisibility(View.GONE);
                lnlLoading.setVisibility(View.GONE);
                break;
        }
    }


    private void reset() {

        clearPosts();
        isFirstLoad = true;
        adapter.notifyDataSetChanged();
        scrollListener.resetState();
    }

    private void clearPosts(){
        RealmController.getInstance().clearAllPosts();
    }

    @Override
    public void onRefresh() {

        if (!isLoading){
            reset();
            loadPosts(Constant.POST_REQUEST_COUNT, 0, 0, searchPhrase);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doesActivityExists = false;
    }
}
