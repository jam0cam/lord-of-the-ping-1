package com.zappos.android.lotp.service;

import com.zappos.android.lotp.model.LeaderboardItem;
import com.zappos.android.lotp.model.Match;
import com.zappos.android.lotp.model.MatchConfirmationResponse;
import com.zappos.android.lotp.model.Player;
import com.zappos.android.lotp.model.Profile;

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

    public static String SERVER = "http://zappos-tt.elasticbeanstalk.com/";

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

    @GET("/tt/pending/player/{id}")
    void getPendingMatches(@Path("id") long playerId, Callback<List<Match>> pendingMatchCallback);

    @POST("/tt/confirmMatch")
    void confirmMatch(@Body MatchConfirmationResponse response, Callback<Void> confirmationCallback);
}
