package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.adapter.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.adapter.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ThienPG on 3/12/2017.
 */

public class UserFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    RecyclerView rvTweets;
    TweetAdapter rvAdapter;
    Context mContext;
    private ArrayList<Tweet> tweets = new ArrayList<>();
    private EndlessRecyclerViewScrollListener scrollListener;
    int page = 1;
    private SwipeRefreshLayout swipeContainer;
    private static UserFragment sharedInstance;
    private static String userId = "";

    public static UserFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        UserFragment fragment = new UserFragment();
        fragment.setArguments(args);
        sharedInstance = fragment;
        return sharedInstance;
    }

    public TweetAdapter getRvAdapter() {
        return rvAdapter;
    }

    public static UserFragment getSharedInstance() {
        return sharedInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        userId = UserActivity.newInstance().getUserId();

        mContext = this.getContext();

        rvTweets = (RecyclerView) view.findViewById(R.id.rvTweets);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        rvTweets.setLayoutManager(layoutManager);
        rvAdapter = new TweetAdapter(mContext, tweets);
        rvTweets.setAdapter(rvAdapter);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                scrollListener.resetState();
                updateHomeTimeline(userId);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getUserTimeLine(userId);


        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };
//         Adds the scroll listener to RecyclerView
       // rvTweets.addOnScrollListener(scrollListener);
        return view;
    }

    public void loadNextDataFromApi(int offset) {
        page = offset;
        getUserTimeLine(userId);
        System.out.println("offset" + offset);
    }

    private void getUserTimeLine(String userId){
        RestClient client = RestApplication.getRestClient();
        client.getUserTimeLine(userId, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                swipeContainer.setRefreshing(false);
                try {
                    ArrayList<Tweet> tweets = Tweet.fromJson(jsonArray);
                    rvAdapter.refreshData(tweets);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            public void onFailure(int statusCode, Header[] headers, Throwable t , JSONObject jsonObject){
                Log.d("Error", t.toString());
            }
        });
    }

    private void updateHomeTimeline(String userId){
        RestClient client = RestApplication.getRestClient();
        client.getUserTimeLine(userId, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                swipeContainer.setRefreshing(false);
                try {
                    ArrayList<Tweet> tweets = Tweet.fromJson(jsonArray);
                    rvAdapter.refreshData(tweets);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            public void onFailure(int statusCode, Header[] headers, Throwable t , JSONObject jsonObject){
                Log.d("Error", t.toString());
            }
        });
    }

}
