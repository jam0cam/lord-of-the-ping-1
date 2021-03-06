package com.lotp.dao;

import com.lotp.controller.Util;
import com.lotp.model.LeaderBoardItem;
import com.lotp.model.Match;
import com.lotp.model.Person;
import com.lotp.model.Player;
import com.ibatis.sqlmap.client.SqlMapClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
@Controller
@RequestMapping("/service")
public class Dao implements InitializingBean {
    private SqlMapClientTemplate sqlMapClientTemplate;

    @Autowired
    private SqlMapClient sqlMapClient;

    public Person getByResultId(String id) {
        return (Person)sqlMapClientTemplate.queryForObject("sql.getById", id);
    }

    public Player getPlayerById(String id) {
        Player p = (Player)sqlMapClientTemplate.queryForObject("sql.getPlayerById", id);
        try {
            if (p != null) {
                if (!StringUtils.hasText(p.getAvatarUrl())) {
                    p.setAvatarUrl(Util.getAvatarUrlFromEmail(p.getEmail()));
                }

                if (p.getAvatarUrl().contains("google") && p.getAvatarUrl().contains("?sz=")) {
                    p.setAvatarUrl(p.getAvatarUrl().substring(0, p.getAvatarUrl().indexOf("?sz=")));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return p;
    }

    public String insertRegistration(Player command) {
        sqlMapClientTemplate.insert("sql.insertRegistration", command);
        return command.getId();
    }


    public String insertMatch(Match command) {
        sqlMapClientTemplate.insert("sql.insertMatch", command);
        return command.getId();
    }

    public String insertPendingMatch(Match command) {
        sqlMapClientTemplate.insert("sql.insertPendingMatch", command);
        return command.getId();
    }


    public Player getByEmailAndPassword(String email, String password) {
        Map<String, String> params = new HashMap<String, String>();

        params.put("email", email);
        params.put("password", password);

        Player rval = (Player)sqlMapClientTemplate.queryForObject("sql.getByEmailAndPassword", params);

        try {
            if (rval!= null) {
                if (!StringUtils.hasText(rval.getAvatarUrl())) {
                    rval.setAvatarUrl(Util.getAvatarUrlFromEmail(rval.getEmail()));
                }

                if (rval.getAvatarUrl().contains("google") && rval.getAvatarUrl().contains("?sz=")) {
                    rval.setAvatarUrl(rval.getAvatarUrl().substring(0, rval.getAvatarUrl().indexOf("?sz=")));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return rval;
    }

    public void declineMatch(String pendingId) {
        sqlMapClientTemplate.update("sql.declineMatch", pendingId);
    }

    public void deletePending(String pendingId) {
        sqlMapClientTemplate.delete("sql.deletePending", pendingId);
    }

    public Player getByEmail(String email) {
        Player rval = (Player)sqlMapClientTemplate.queryForObject("sql.getByEmail", email);
        try {
            if (rval != null) {
                if (!StringUtils.hasText(rval.getAvatarUrl())) {
                    rval.setAvatarUrl(Util.getAvatarUrlFromEmail(rval.getEmail()));
                }

                if (rval.getAvatarUrl().contains("google") && rval.getAvatarUrl().contains("?sz=")) {
                    rval.setAvatarUrl(rval.getAvatarUrl().substring(0, rval.getAvatarUrl().indexOf("?sz=")));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return rval;
    }

    public List<Player> getPlayers() {
        List<Player> list = (List<Player>)sqlMapClientTemplate.queryForList("sql.getPlayers");

        for (Player p : list) {
            try {
                if (!StringUtils.hasText(p.getAvatarUrl())) {
                    p.setAvatarUrl(Util.getAvatarUrlFromEmail(p.getEmail()));
                }

                if (p.getAvatarUrl().contains("google") && p.getAvatarUrl().contains("?sz=")) {
                    p.setAvatarUrl(p.getAvatarUrl().substring(0, p.getAvatarUrl().indexOf("?sz=")));
                }

            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        return list;
    }

    public List<String> getNamesList() {
        return (List<String>)sqlMapClientTemplate.queryForList("sql.getNames");
    }

    public List<String> getEmailList() {
        return (List<String>)sqlMapClientTemplate.queryForList("sql.getEmails");
    }

    public List<Match> getMatchesByPlayer(String playerId) {
        return (List<Match>)sqlMapClientTemplate.queryForList("sql.getMatchesByPlayer", playerId);
    }

    public List<Match> getPendingMatchesByPlayer(String playerId) {
        List<Match> matches = (List<Match>)sqlMapClientTemplate.queryForList("sql.getPendingMatchesByPlayer", playerId);

        //we need to fill out the avatars for player 1s only. Player 2 will see the pending match, and therefore
        //will want to know who player 1 is
        for (Match m : matches) {
            if (!StringUtils.hasText(m.getP1().getAvatarUrl())) {

                if (!StringUtils.hasText(m.getP1().getAvatarUrl())) {
                    m.getP1().setAvatarUrl(Util.getAvatarUrlFromEmail(m.getP1().getEmail()));
                }

                if (m.getP1().getAvatarUrl().contains("google") && m.getP1().getAvatarUrl().contains("?sz=")) {
                    m.getP1().setAvatarUrl(m.getP1().getAvatarUrl().substring(0, m.getP1().getAvatarUrl().indexOf("?sz=")));
                }
            }
        }
        return matches;
    }

    public Match getPendingMatch(String pendingId) {
        return (Match)sqlMapClientTemplate.queryForObject("sql.getPendingMatch", pendingId);
    }


    public List<Match> getMatchesByPlayerByDateDesc(String playerId) {
        return (List<Match>)sqlMapClientTemplate.queryForList("sql.getMatchesByPlayerByDateDesc", playerId);
    }

    public List<Match> getAllMatches() {
        return (List<Match>)sqlMapClientTemplate.queryForList("sql.getAllMatches");
    }

    public void updatePlayerRating(String playerId, String ranking, String sigma) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("playerId", playerId);
        params.put("ranking", ranking);
        params.put("sigma", sigma);

        sqlMapClientTemplate.update("sql.updatePlayerRating", params);
    }

    public Player getPlayerFromHash(String hash) {
        return (Player)sqlMapClientTemplate.queryForObject("sql.getPlayerFromHash", hash);
    }

    public void updatePlayerPassword(String password, String hash) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("password", password);
        params.put("hash", hash);

        sqlMapClientTemplate.update("sql.updatePlayerPassword", params);
    }

    public String insertLeaderboardItem(LeaderBoardItem item) {
        sqlMapClientTemplate.insert("sql.insertLeaderboardItem", item);
        return item.getId();
    }

    public String updateLeaderboardItemByPlayerId(LeaderBoardItem item) {
        sqlMapClientTemplate.update("sql.updateLeaderboardItemByPlayerId", item);
        return item.getId();
    }

    public void clearLeaderBoard() {
        sqlMapClientTemplate.delete("sql.clearLeaderBoard");
    }

    public List<LeaderBoardItem> getLeaderboard() {
        List<LeaderBoardItem> items = (List<LeaderBoardItem>)sqlMapClientTemplate.queryForList("sql.getLeaderboard");
        for (LeaderBoardItem item : items) {
            if (!StringUtils.hasText(item.getPlayer().getAvatarUrl())) {
                Util.getAvatarUrlFromEmail(item.getPlayer().getEmail());
            }
        }

        return items;
    }

    public LeaderBoardItem getLeaderboardItem(String playerId) {
        return (LeaderBoardItem) sqlMapClientTemplate.queryForObject("sql.getLeaderboardItem", playerId);
    }

    /**
     * Checks whether to do update or insert based on the existence of an id
     * @param item
     * @return
     */
    public String saveLeaderboardItem(LeaderBoardItem item) {
        if (StringUtils.hasText(item.getId())) {
            return updateLeaderboardItemByPlayerId(item);
        } else {
            return insertLeaderboardItem(item);
        }
    }

    public void updateAvatar(Player player) {
        sqlMapClientTemplate.update("sql.updateAvatar", player);
    }

    public void deletePasswordReset(String hash) {
        sqlMapClientTemplate.delete("sql.deletePasswordReset", hash);
    }

    public void afterPropertiesSet() throws Exception {
        this.sqlMapClientTemplate = new SqlMapClientTemplate(sqlMapClient);
    }

}