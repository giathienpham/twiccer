package com.codepath.apps.restclienttemplate;



import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.adapter.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class HomeActivity extends AppCompatActivity {

    RecyclerView rvTweets;
    TweetAdapter rvAdapter;
    Context mContext;
    private List<Tweet> tweets = new ArrayList<>();

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



        RestClient client = RestApplication.getRestClient();
        client.getHomeTimeline(1, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                Log.d("DEBUG", "timeline: " + jsonArray.toString());
                // json.getJSONObject(0).getLong("id");
                try {
                    System.out.println(jsonArray.getJSONObject(0).toString());
                    ArrayList<Tweet> tweets = Tweet.fromJson(jsonArray);
                    rvAdapter = new TweetAdapter(mContext, tweets);
                    rvTweets.setAdapter(rvAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            public void onFailure(int statusCode, Header[] headers, Throwable t , JSONObject jsonObject){
                Log.d("Error", t.toString());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }


}
