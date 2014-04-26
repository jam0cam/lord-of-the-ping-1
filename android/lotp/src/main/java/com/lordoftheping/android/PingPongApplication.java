package com.lordoftheping.android;

import android.app.Application;

import com.lordoftheping.android.model.Player;
import com.lordoftheping.android.preference.PingPongPreferences;
import com.lordoftheping.android.service.PingPongService;

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
                .setServer(PingPongService.SERVER)
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
