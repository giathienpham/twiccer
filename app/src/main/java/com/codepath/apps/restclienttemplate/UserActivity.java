package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.adapter.TimelineFragmentPagerAdapter;
import com.codepath.apps.restclienttemplate.adapter.UserFragmentPagerAdapter;
import com.codepath.apps.restclienttemplate.models.LoggedOnUser;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class UserActivity extends AppCompatActivity {

    private static String CURRENT_LOGON_USERNAME = "Phạm Gia Thiện";
    private static String CURRENT_LOGON_USERAVT = "https://pbs.twimg.com/profile_images/690568049368260609/E61BXIdR_200x200.jpg";
    private static String CURRENT_LOGON_AT = "@giathienpham";

    Context mContext;
    String userId = "";
    String username;
    String userAvt;

    public String getUserId() {
        return userId;
    }

    private static UserActivity sharedInstance;

    @BindView(R.id.ivProfileAvt)
    ImageView profileAvt;
    @BindView(R.id.tvProfileUsername)
    TextView profileUsername;
    @BindView(R.id.tvProfileAt)
    TextView profileAt;
    @BindView(R.id.tvFollowerCount)
    TextView follower;
    @BindView(R.id.tvFollowingCount)
    TextView following;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static UserActivity newInstance() {
        return sharedInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.logo2);

        mContext = this;
        sharedInstance = this;
        Intent intent = getIntent();
        userId = intent.getStringExtra("user_id");
        CURRENT_LOGON_USERNAME = intent.getStringExtra("user_avt");
        CURRENT_LOGON_USERAVT = intent.getStringExtra("user_name");
        getUserInfo(userId);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        UserFragmentPagerAdapter pagerAdapter =
                new UserFragmentPagerAdapter(getSupportFragmentManager(), UserActivity.this);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is the up button
            case R.id.mnNewTweet:
                showEditDialog();
                return true;
            case R.id.mnProfile:
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

    private void getUserInfo(String userId){
        RestClient client = RestApplication.getRestClient();
        client.getUserInformation(userId, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonUser) {
                LoggedOnUser user = new LoggedOnUser(jsonUser);
                profileUsername.setText(user.getUsername());
                profileAt.setText("@" +user.getAt().toLowerCase());
                follower.setText(user.getFollower());
                following.setText(user.getFollowing());
                Picasso.with(mContext).load(user.getAvatar()).into(profileAvt);
            }
            public void onFailure(int statusCode, Header[] headers, Throwable t , JSONObject jsonObject){
                Log.d("Error", t.toString());
            }
        });
    }
}
