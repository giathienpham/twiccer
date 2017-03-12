package com.codepath.apps.restclienttemplate;



import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.adapter.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.adapter.TweetAdapter;
import com.codepath.apps.restclienttemplate.adapter.TimelineFragmentPagerAdapter;
import com.codepath.apps.restclienttemplate.models.LoggedOnUser;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        TimelineFragmentPagerAdapter pagerAdapter =
                new TimelineFragmentPagerAdapter(getSupportFragmentManager(), HomeActivity.this);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        getUserInfo();

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
            case R.id.mnProfile:
//                showEditDialog();
                Intent i = new Intent(this, UserActivity.class);
                startActivity(i);

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


    private void getUserInfo(){
        RestClient client = RestApplication.getRestClient();
        client.getUserInformation(new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonUser) {
//                try {
                    LoggedOnUser user = new LoggedOnUser(jsonUser);
                    System.out.println(user.toString());
//                    ArrayList<Tweet> tweets = Tweet.fromJson(jsonArray);
//                    rvAdapter.addData(tweets);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
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
        TimelineFragment.getSharedInstance().getRvAdapter().addDataFirst(tweet);

    }
}
