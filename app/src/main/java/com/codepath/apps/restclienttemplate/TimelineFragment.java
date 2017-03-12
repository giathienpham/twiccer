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

public class TimelineFragment extends Fragment implements NewTweetFragment.NewTweetFragmentListener {

    public static final String ARG_PAGE = "ARG_PAGE";
    private static String CURRENT_LOGON_USERNAME = "Phạm Gia Thiện";
    private static String CURRENT_LOGON_USERAVT = "https://pbs.twimg.com/profile_images/690568049368260609/E61BXIdR_200x200.jpg";
    private static String CURRENT_LOGON_AT = "@giathienpham";

    private int mPage;

    RecyclerView rvTweets;
    TweetAdapter rvAdapter;
    Context mContext;
    private ArrayList<Tweet> tweets = new ArrayList<>();
    private EndlessRecyclerViewScrollListener scrollListener;
    int page = 1;
    private SwipeRefreshLayout swipeContainer;
    private static TimelineFragment sharedInstance;

    public static TimelineFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        TimelineFragment fragment = new TimelineFragment();
        fragment.setArguments(args);
        sharedInstance = fragment;
        return sharedInstance;
    }

    public TweetAdapter getRvAdapter() {
        return rvAdapter;
    }

    public static TimelineFragment getSharedInstance() {
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
                page = 1;
                scrollListener.resetState();
                updateHomeTimeline(1);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getHomeLine(page);


        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);
        return view;
    }

    public void loadNextDataFromApi(int offset) {
        page = offset;
        getHomeLine(offset);
        System.out.println("offset" + offset);
    }

    private void getHomeLine(int page){
        RestClient client = RestApplication.getRestClient();
        client.getHomeTimeline(page, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                swipeContainer.setRefreshing(false);
                try {
                    ArrayList<Tweet> tweets = Tweet.fromJson(jsonArray);
                    rvAdapter.addData(tweets);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            public void onFailure(int statusCode, Header[] headers, Throwable t , JSONObject jsonObject){
                Log.d("Error", t.toString());
            }
        });
    }

    private void updateHomeTimeline(int page){
        RestClient client = RestApplication.getRestClient();
        client.getHomeTimeline(page, new JsonHttpResponseHandler() {
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

    @Override
    public void onFinishNewTweetFragmentDialog(String body, String time) {
        Tweet tweet = new Tweet();
        tweet.setBody(body);
        tweet.setTimestamp(time);
        tweet.setId((long) Math.random() * 212 * ((long)Math.random()* 232 * ((long)Math.random() * 121)));
        tweet.setUserAvt(CURRENT_LOGON_USERAVT);
        tweet.setUserHandle(CURRENT_LOGON_USERNAME);
        tweet.setGif("nogif");
        tweet.setImgUrl("noimage");
        rvAdapter.addDataFirst(tweet);

    }

}
