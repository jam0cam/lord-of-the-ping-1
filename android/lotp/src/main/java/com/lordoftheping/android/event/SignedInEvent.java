package com.lordoftheping.android.event;

import com.lordoftheping.android.model.Player;

/**
 * Created by mattkranzler on 12/5/13.
 */
public class SignedInEvent {
    private Player player;

    public SignedInEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
