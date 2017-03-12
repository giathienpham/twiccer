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
import com.codepath.apps.restclienttemplate.adapter.MentionAdapter;
import com.codepath.apps.restclienttemplate.adapter.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Mention;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ThienPG on 3/12/2017.
 */

public class MentionFragment extends Fragment{

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    RecyclerView rvMentions;
    MentionAdapter rvAdapter;
    Context mContext;
    private ArrayList<Mention> mentions = new ArrayList<>();
    private EndlessRecyclerViewScrollListener scrollListener;
    int page = 1;
    private SwipeRefreshLayout swipeContainer;


    public static MentionFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        MentionFragment fragment = new MentionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mention, container, false);

        mContext = this.getContext();

        rvMentions = (RecyclerView) view.findViewById(R.id.rvMentions);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        rvMentions.setLayoutManager(layoutManager);
        rvAdapter = new MentionAdapter(mContext, mentions);
        rvMentions.setAdapter(rvAdapter);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                scrollListener.resetState();
                getMentionTimeline();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getMentionTimeline();


        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
            }
        };
        // Adds the scroll listener to RecyclerView
        rvMentions.addOnScrollListener(scrollListener);
        return view;
    }

    private void getMentionTimeline(){
        RestClient client = RestApplication.getRestClient();
        client.getMentionTimeline(new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                swipeContainer.setRefreshing(false);
                try {
                    ArrayList<Mention> mentions = Mention.fromJson(jsonArray);
                    rvAdapter.refreshData(mentions);
                    Log.d("Mentions Json", jsonArray.toString());
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
