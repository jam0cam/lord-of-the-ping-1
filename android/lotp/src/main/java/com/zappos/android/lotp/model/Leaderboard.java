package com.zappos.android.lotp.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mattkranzler on 12/4/13.
 */
public class Leaderboard implements Serializable {
    List<LeaderboardItem> leaderBoardItems;

    public List<LeaderboardItem> getLeaderBoardItems() {
        return leaderBoardItems;
    }

    public void setLeaderBoardItems(List<LeaderboardItem> leaderBoardItems) {
        this.leaderBoardItems = leaderBoardItems;
    }
}
