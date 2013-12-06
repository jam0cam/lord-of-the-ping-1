package com.zappos.android.pingpong.service;

import com.zappos.android.pingpong.model.LeaderboardItem;
import com.zappos.android.pingpong.model.Match;
import com.zappos.android.pingpong.model.Player;
import com.zappos.android.pingpong.model.Profile;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by mattkranzler on 12/4/13.
 */
public interface PingPongService {

    @GET("/tt/leaderboard")
    void getLeaderBoard(Callback<List<LeaderboardItem>> leaderBoardCallback);

    @GET("/tt/players")
    void getAllPlayers(Callback<List<Player>> playerCallback);

    @GET("/tt/profile/{id}")
    void getProfile(@Path("id") long playerId, Callback<Profile> profileCallback);

    @POST("/tt/saveMatch")
    void saveMatch(@Body Match match, Callback<String> matchCallback);

    @POST("/tt/signin")
    void signIn(@Body Player player, Callback<Player> signInCallback);

    @POST("/tt/register")
    void register(@Body Player player, Callback<Player> registerCallback);
}
