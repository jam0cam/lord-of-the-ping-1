package com.zappos.android.pingpong;

import android.app.Application;

import com.zappos.android.pingpong.model.Player;
import com.zappos.android.pingpong.preference.PingPongPreferences;
import com.zappos.android.pingpong.service.PingPongService;

import retrofit.RestAdapter;

/**
 * Created by mattkranzler on 12/4/13.
 */
public class PingPongApplication extends Application {

    private PingPongService mPingPongService;
    private Player mCurrentPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setServer("http://zappos-tt.elasticbeanstalk.com/")
                .build();

        mPingPongService = restAdapter.create(PingPongService.class);
        mCurrentPlayer = PingPongPreferences.getCurrentPlayer(this);
    }

    public PingPongService getPingPongService() {
        return mPingPongService;
    }

    public Player getCurrentPlayer() {
        return mCurrentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        mCurrentPlayer = currentPlayer;
    }
}
