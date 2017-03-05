package com.codepath.apps.restclienttemplate;



import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.adapter.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.adapter.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;


public class HomeActivity extends AppCompatActivity implements NewTweetFragment.NewTweetFragmentListener {

    private static String CURRENT_LOGON_USERNAME = "Phạm Gia Thiện";
    private static String CURRENT_LOGON_USERAVT = "https://pbs.twimg.com/profile_images/690568049368260609/E61BXIdR_200x200.jpg";
    private static String CURRENT_LOGON_AT = "@giathienpham";
    RecyclerView rvTweets;
    TweetAdapter rvAdapter;
    Context mContext;
    private ArrayList<Tweet> tweets = new ArrayList<>();
    private EndlessRecyclerViewScrollListener scrollListener;
    int page = 1;
    private SwipeRefreshLayout swipeContainer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.logo2);

        mContext = this;





        rvTweets = (RecyclerView) findViewById(R.id.rvTweets);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        rvTweets.setLayoutManager(layoutManager);
        rvAdapter = new TweetAdapter(mContext, tweets);
        rvTweets.setAdapter(rvAdapter);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                page = 1;
                rvAdapter.clearData();
                scrollListener.resetState();
                getHomeLine(1);
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is the up button
            case R.id.mnNewTweet:
                showEditDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        NewTweetFragment newTweetFragment = NewTweetFragment.newInstance(CURRENT_LOGON_USERNAME, CURRENT_LOGON_AT ,CURRENT_LOGON_USERAVT);
        newTweetFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle);
        newTweetFragment.show(fm, "fragment_new_tweet");
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
//        tweets.add(0, tweet);
//        Toast.makeText(this, "Hi, " + body, Toast.LENGTH_SHORT).show();

    }
}
