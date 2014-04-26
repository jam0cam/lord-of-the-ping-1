package com.lordoftheping.android.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lordoftheping.android.model.Player;
import com.lordoftheping.android.util.ObjectMapperFactory;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * Created by mattkranzler on 12/5/13.
 */
public class PingPongPreferences {

    public static final String SHARED_PREFS = "ping_pong_shared_prefs";

    private static final String CURRENT_PLAYER = "current_player";

    public static Player getCurrentPlayer(Context context) {
        String player = getSharedPreferences(context).getString(CURRENT_PLAYER, null);
        if (StringUtils.isNotEmpty(player)) {
            try {
                return ObjectMapperFactory.getObjectMapper().readValue(player, Player.class);
            } catch (IOException e) {
                // TODO do something
            }
        }
        return null;
    }

    public static boolean setCurrentPlayer(Player player, Context context) {
        try {
            getSharedPreferences(context)
                    .edit()
                    .putString(CURRENT_PLAYER, ObjectMapperFactory.getObjectMapper().writeValueAsString(player))
                    .commit();
            return true;
        } catch (JsonProcessingException e) {
            // TODO log or something
            return false;
        }
    }

    public static void signOut(Context context) {
        getSharedPreferences(context).edit().remove(CURRENT_PLAYER).commit();
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }
}
