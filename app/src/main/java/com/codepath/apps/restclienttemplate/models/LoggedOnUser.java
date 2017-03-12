package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ThienPG on 3/12/2017.
 */

public class LoggedOnUser {
    private String userId;
    private String username;
    private String follower;
    private String following;



    public LoggedOnUser(JSONObject object){
        super();

        try {
            this.userId = object.getString("id");
            this.username = object.getString("name");
            this.follower = object.getString("followers_count");
            this.following = object.getString("friends_count");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String toString() {
        return "LoggedOnUser{" +
                "username='" + username + '\'' +
                ", follower='" + follower + '\'' +
                ", following='" + following + '\'' +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public LoggedOnUser(String username, String follower, String following) {
        this.username = username;
        this.follower = follower;
        this.following = following;
    }

    public LoggedOnUser() {

    }
}
