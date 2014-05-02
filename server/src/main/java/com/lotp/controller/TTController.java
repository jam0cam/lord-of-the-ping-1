package com.lotp.controller;

import com.lotp.exception.EmailExistsException;
import com.lotp.exception.InvalidMatchException;
import com.lotp.exception.InvalidUserNameOrPasswordException;
import com.lotp.gmail.Gmail;
import com.lotp.model.*;
import com.lotp.postageapp.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: jitse
 * Date: 12/4/13
 * Time: 10:25 AM
 */
@Controller
@RequestMapping("/tt")
public class TTController extends BaseController{

    @Autowired
    private MailSender mailSender;

    SimpleDateFormat shortDateFormatter = new SimpleDateFormat("MM/dd/yyyy");

    @RequestMapping(value = "/player/{id}", method = RequestMethod.GET)
    public @ResponseBody
    Player getPlayerById (@PathVariable("id") String id) throws NoSuchAlgorithmException {
        Player rval = dao.getPlayerById(id);
        return rval;
    }

    @RequestMapping(value = "/players", method = RequestMethod.GET)
    public @ResponseBody
    List<Player> getPlayers () throws NoSuchAlgorithmException {
        List<Player> rval = dao.getPlayers();
        return rval;
    }

    @RequestMapping(value= "/saveMatch", method=RequestMethod.POST)
    @ResponseBody
    public String saveMatch(@RequestBody Match match) {

        if (!StringUtils.hasText(match.getP1().getId()) || !StringUtils.hasText(match.getP2().getId())) {
            throw new InvalidMatchException();
        }

        if (!StringUtils.hasText(match.getP1().getEmail())) {
            match.setP1(dao.getPlayerById(match.getP1().getId()));
        }

        if (!StringUtils.hasText(match.getP2().getEmail())) {
            match.setP2(dao.getPlayerById(match.getP2().getId()));
        }

        if (StringUtils.hasText(match.getDateString())){
            match.setDate(new Date(match.getDateString()));
        }

        dao.insertPendingMatch(match);

        String body = "You have a pending match against " + match.getP1().getName() + ". Please go to the site to confirm. http://www.lordoftheping.com/inbox";
//        mailSender.sendMail(match.getP2().getEmail(), "Pending match", body);
        Gmail.sendMail(match.getP2().getEmail(), "Pending match", body);
        return match.getId();
    }

    @RequestMapping(value= "/signin", method=RequestMethod.POST)
    @ResponseBody
    public Player signin(@RequestBody RegisterUser player) {
        if (!StringUtils.hasText(player.getEmail()) || !StringUtils.hasText(player.getPassword())) {
            throw new InvalidUserNameOrPasswordException();
        }

        Player p = dao.getByEmailAndPassword(player.getEmail(), player.getPassword());
        if (p==null) {
            throw new InvalidUserNameOrPasswordException();
        } else {
            return p;
        }
    }

    @RequestMapping(value= "/register", method=RequestMethod.POST)
    @ResponseBody
    public Player register(@RequestBody RegisterUser player) {
        Player p = dao.getByEmail(player.getEmail());
        if (p==null) {
            Player newPlayer = player.toPlayer();
            String id = dao.insertRegistration(newPlayer);
            newPlayer.setId(id);
            return newPlayer;
        } else {
            throw new EmailExistsException();
        }
    }

    @RequestMapping(value= "/confirmMatch", method=RequestMethod.POST)
    @ResponseBody
    public HttpResponse confirmMatch(@RequestBody ConfirmMatch confirmation) {
        Match pendingMatch = dao.getPendingMatch(confirmation.getPendingId());
        if (pendingMatch == null) {
            return new HttpResponse(200, "OK");
        }

        if (confirmation.isConfirmed()){
            dao.deletePending(confirmation.getPendingId());
            dao.insertMatch(pendingMatch);

            Ranking.calculateRanking(pendingMatch);

            //update the player's ranking
            dao.updatePlayerRating(pendingMatch.getP1().getId(), Integer.toString(pendingMatch.getP1().getRanking()), Double.toString(pendingMatch.getP1().getSigma()));
            dao.updatePlayerRating(pendingMatch.getP2().getId(), Integer.toString(pendingMatch.getP2().getRanking()), Double.toString(pendingMatch.getP2().getSigma()));

            //update the leaderboard
            LeaderBoardItem lbp1 = dao.getLeaderboardItem(pendingMatch.getP1().getId());
            LeaderBoardItem lbp2 = dao.getLeaderboardItem(pendingMatch.getP2().getId());

            if (lbp1 == null) { //this is a new player and don't have a leaderboard item yet
                lbp1 = new LeaderBoardItem();
                lbp1.setPlayer(pendingMatch.getP1());
            }

            if (lbp2 == null) { //this is a new player and don't have a leaderboard item yet
                lbp2 = new LeaderBoardItem();
                lbp2.setPlayer(pendingMatch.getP2());
            }


            lbp1.getPlayer().setRanking(pendingMatch.getP1().getRanking());
            lbp2.getPlayer().setRanking(pendingMatch.getP2().getRanking());

            if (pendingMatch.getP1Score() > pendingMatch.getP2Score()) {
                lbp1.setMatchWins(lbp1.getMatchWins()+1);
                lbp2.setMatchLosses(lbp2.getMatchLosses()+1);
            } else {
                lbp1.setMatchLosses(lbp1.getMatchLosses() + 1);
                lbp2.setMatchWins(lbp2.getMatchWins() + 1);
            }

            dao.saveLeaderboardItem(lbp1);
            dao.saveLeaderboardItem(lbp2);


        } else {
            //on a decline, we send an email to player "1" notifying that player 2 declined.
            Player player1 = pendingMatch.getP1();
            Player player2 = pendingMatch.getP2();

            if (player1 != null && player2 != null) {
//                mailSender.sendMail(player1.getEmail(), "match declined", "Your match with " + player2.getName() + " has been declined.");
                Gmail.sendMail(player1.getEmail(), "match declined", "Your match with " + player2.getName() + " has been declined.");
            }
            dao.declineMatch(confirmation.getPendingId());
        }

        return new HttpResponse(200, "OK");
    }

    @RequestMapping(value = "/pending/player/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<Match> getPendingMatches(@PathVariable("id") String id) {
        List<Match> matches = dao.getPendingMatchesByPlayer(id);
        return matches;
    }

    @RequestMapping(value = "/pending/count", method = RequestMethod.GET)
    @ResponseBody
    public String getPendingMatchesCount() {
        String id = "-3243";
        try{
          id = myUserContext.getCurrentUser().getId();
        } catch (Exception e){}

        List<Match> matches = dao.getPendingMatchesByPlayer(id);

        if (!matches.isEmpty()) {
            return Integer.toString(matches.size());
        } else {
            return "";
        }
    }

    @RequestMapping(value = "/profile/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ProfileCommand getProfile(@PathVariable("id") String id) {
        ProfileCommand rval = new ProfileCommand();

        Player player = dao.getPlayerById(id);

        List<Match> matches = dao.getMatchesByPlayerByDateDesc(id);

        Stats stats = new Stats();

        for (Match match : matches) {
            if (!match.getP1().getId().equals(id)) {
                //we want to swap the info and always make p1 = player passed in
                match.swapPlayer();
            }
            match.setDateString(shortDateFormatter.format(match.getDate()));

            if (match.getP1Score() > match.getP2Score()){
                match.setStatus("W");
            } else {
                match.setStatus("L");
            }

            stats.setGameWins(stats.getGameWins() + match.getP1Score());
            stats.setGameLosses(stats.getGameLosses() + match.getP2Score());

            if (match.getP1Score() > match.getP2Score()){
                stats.setMatchWins(stats.getMatchWins() + 1);
            } else {
                stats.setMatchLosses(stats.getMatchLosses() + 1);
            }

            match.getP2().setAvatarUrl(Util.getAvatarUrlFromEmail(match.getP2().getEmail()));

        }

        stats.setTotalGames(stats.getGameWins() + stats.getGameLosses());
        stats.setTotalMatches(stats.getMatchWins() + stats.getMatchLosses());

        if (stats.getTotalGames() > 0) {
            stats.setGameWinPercentage(stats.getGameWins() * 100 /stats.getTotalGames());
        }

        if (stats.getTotalMatches() > 0) {
            stats.setMatchWinPercentage(stats.getMatchWins() * 100 / stats.getTotalMatches());
        }

        rval.setMatches(matches);
        rval.setStats(stats);
        rval.setPlayer(player);
        return rval;
    }

    @RequestMapping(value = "/leaderboard", method = RequestMethod.GET)
    @ResponseBody
    public List<LeaderBoardItem> getLeaderBoard() {
        return dao.getLeaderboard();
    }

    public List<LeaderBoardItem> calculateLeaderboard() {
        List<LeaderBoardItem> rval = new ArrayList<LeaderBoardItem>();

        List<Player> players = dao.getPlayers();

        //for each player, get their matches
        for (Player p : players){
            List<Match> matches = dao.getMatchesByPlayer(p.getId());

            if (matches.isEmpty()) {
                //need at least 1 matches
                continue;
            }

            int wins = 0;
            int losses = 0;
            for (Match match : matches) {
                if (match.getP1().getId().equals(p.getId())) {
                    if (match.getP1Score() > match.getP2Score()) {
                        wins ++;
                    } else {
                        losses++;
                    }
                } else {
                    if (match.getP2Score() > match.getP1Score()) {
                        wins ++;
                    } else {
                        losses++;
                    }
                }
            }

            LeaderBoardItem item = new LeaderBoardItem();
            item.setMatchLosses(losses);
            item.setMatchWins(wins);
            item.setPlayer(p);
            item.setWinningPercentage(((double)(wins))/(losses+wins));

            insertLeaderBoardItemCorrectly(rval, item);
        }

        return rval;
    }

    /**
     * This will clear the leaderboard table and fill it with newly calculated data
     */
    @RequestMapping(value = "/resetLeaderboard", method = RequestMethod.GET)
    @ResponseBody
    public String redoLeaderBoard() {
        List<LeaderBoardItem> lb = calculateLeaderboard();
        Collections.sort(lb);

        dao.clearLeaderBoard();
        for (LeaderBoardItem item : lb) {
            dao.insertLeaderboardItem(item);
        }

        return "Leaderboard Recalculated";
    }

    /**
     * This is meant to re-calculate the rankings starting from the first game
     */
    @RequestMapping(value = "/resetRankings", method = RequestMethod.GET)
    @ResponseBody
    public String initializeRankgings() {
        List<Match> matches = dao.getAllMatches();

        for (Match m : matches) {
            //since we are looping through a bunch of matches in the DB, the player ratings could've changed. Reload them.
            m.setP1(dao.getPlayerById(m.getP1().getId()));
            m.setP2(dao.getPlayerById(m.getP2().getId()));

            Ranking.calculateRanking(m);

            dao.updatePlayerRating(m.getP1().getId(), Integer.toString(m.getP1().getRanking()), Double.toString(m.getP1().getSigma()));
            dao.updatePlayerRating(m.getP2().getId(), Integer.toString(m.getP2().getRanking()), Double.toString(m.getP2().getSigma()));
        }

        return "Rankings Recalculated";
    }



    /**
     * It inserts the item into the correct spot to produce a decreasing order of winning percentage
     * @param items
     * @param item
     */
    private void insertLeaderBoardItemCorrectly(List<LeaderBoardItem> items, LeaderBoardItem item) {

        if (items == null) return;

        if (items.isEmpty()) {
            items.add(item);
        } else {
            for (int i=0; i<items.size(); i++) {
                LeaderBoardItem lbi = items.get(i);
                if (lbi.getPlayer().getRanking() < item.getPlayer().getRanking()) {
                    items.add(i, item);
                    return;
                }
            }

            //if it gets here that means the item we are adding has the lowest ranking
            items.add(item);
        }
    }
}
