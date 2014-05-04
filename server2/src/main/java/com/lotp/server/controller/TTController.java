package com.lotp.server.controller;

import com.lotp.server.command.MatchConfirmation;
import com.lotp.server.entity.*;
import com.lotp.server.exception.EmailExistsException;
import com.lotp.server.exception.InvalidUserNameOrPasswordException;
import com.lotp.server.gmail.Gmail;
import com.lotp.server.model.*;
import com.lotp.server.postageapp.MailSender;
import com.lotp.server.repository.LeaderboardItemRepository;
import com.lotp.server.repository.PendingMatchRepository;
import com.lotp.server.repository.MatchRepository;
import com.lotp.server.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PendingMatchRepository pendingMatchRepository;

    @Autowired
    private LeaderboardItemRepository leaderboardItemRepository;

    SimpleDateFormat shortDateFormatter = new SimpleDateFormat("MM/dd/yyyy");

    @RequestMapping(value = "/player/{id}", method = RequestMethod.GET)
    public @ResponseBody
    Player getPlayerById (@PathVariable("id") long id) {
        return playerRepository.findOne(id);
    }

    @RequestMapping(value = "/players", method = RequestMethod.GET)
    public @ResponseBody List<Player> getPlayers () {
        Iterable<Player> players = playerRepository.findAll();

        // TODO: Pass in a pageable, not this.
        List<Player> rval = new ArrayList<>();
        for( Player p : players ){
            rval.add(p);
        }

        return rval;
    }

    @RequestMapping(value= "/saveMatch", method= RequestMethod.POST)
    @ResponseBody
    public long saveMatch(@RequestBody Match match) {

        if (!StringUtils.hasText(match.getPlayerOne().getEmail())) {
            match.setPlayerOne(playerRepository.findOne(match.getPlayerOne().getId()));
        }

        if (!StringUtils.hasText(match.getPlayerTwo().getEmail())) {
            match.setPlayerTwo(playerRepository.findOne(match.getPlayerTwo().getId()));
        }

        if (StringUtils.hasText(match.getDateString())){
            match.setDate(new Date(match.getDateString()));
        }

        matchRepository.save(match);

        String body = "You have a pending match against " + match.getPlayerOne().getName() + ". Please go to the site to confirm. http://www.lordoftheping.com/inbox";
//        mailSender.sendMail(match.getPlayerTwo().getEmail(), "Pending match", body);
        Gmail.sendMail(match.getPlayerTwo().getEmail(), "Pending match", body);
        return match.getId();
    }

    @RequestMapping(value= "/signin", method= RequestMethod.POST)
    @ResponseBody
    public Player signin(@RequestBody RegisterUser player) {
        if (!StringUtils.hasText(player.getEmail()) || !StringUtils.hasText(player.getPassword())) {
            throw new InvalidUserNameOrPasswordException();
        }

        Player p = playerRepository.findByEmailAndPassword(player.getEmail(), player.getPassword());
        if (p==null) {
            throw new InvalidUserNameOrPasswordException();
        } else {
            return p;
        }
    }

    @RequestMapping(value= "/register", method= RequestMethod.POST)
    @ResponseBody
    public Player register(@RequestBody RegisterUser player) {
        Player p = playerRepository.findByEmail(player.getEmail());
        if (p==null) {
            Player newPlayer = player.toPlayer();

            newPlayer = playerRepository.save(newPlayer);

            return newPlayer;
        } else {
            throw new EmailExistsException();
        }
    }

    @RequestMapping(value= "/confirmMatch", method= RequestMethod.POST)
    @ResponseBody
    public HttpResponse confirmMatch(@RequestBody MatchConfirmation confirmation) {
        Match pendingMatch = pendingMatchRepository.findOne(confirmation.getPendingId());
        if (pendingMatch == null ) {
            return new HttpResponse(200, "OK");
        }

        if (confirmation.isConfirmed()){
            pendingMatch.setPending(false);

            pendingMatchRepository.delete(confirmation.getPendingId());
            matchRepository.save(pendingMatch);

            Ranking.calculateRanking(pendingMatch);

            //update the player's ranking
            Player one = playerRepository.findOne(pendingMatch.getPlayerOne().getId());
            Player two = playerRepository.findOne(pendingMatch.getPlayerTwo().getId());

            one.setRanking(pendingMatch.getPlayerOne().getRanking());
            two.setRanking(pendingMatch.getPlayerTwo().getRanking());

            one.setSigma(pendingMatch.getPlayerOne().getSigma());
            two.setSigma(pendingMatch.getPlayerTwo().getSigma());

            playerRepository.save(one);
            playerRepository.save(two);

            //update the leaderboard
            LeaderboardItem lbp1 = leaderboardItemRepository.findOneByPlayerId(pendingMatch.getPlayerOne().getId());
            LeaderboardItem lbp2 = leaderboardItemRepository.findOneByPlayerId(pendingMatch.getPlayerTwo().getId());

            if (lbp1 == null) { //this is a new player and don't have a leaderboard item yet
                lbp1 = new LeaderboardItem();
                lbp1.setPlayer(pendingMatch.getPlayerOne());
            }

            if (lbp2 == null) { //this is a new player and don't have a leaderboard item yet
                lbp2 = new LeaderboardItem();
                lbp2.setPlayer(pendingMatch.getPlayerTwo());
            }


            lbp1.getPlayer().setRanking(pendingMatch.getPlayerOne().getRanking());
            lbp2.getPlayer().setRanking(pendingMatch.getPlayerTwo().getRanking());

            if (pendingMatch.getP1Score() > pendingMatch.getP2Score()) {
                lbp1.setMatchWins(lbp1.getMatchWins()+1);
                lbp2.setMatchLosses(lbp2.getMatchLosses()+1);
            } else {
                lbp1.setMatchLosses(lbp1.getMatchLosses() + 1);
                lbp2.setMatchWins(lbp2.getMatchWins() + 1);
            }

            leaderboardItemRepository.save(lbp1);
            leaderboardItemRepository.save(lbp2);

        } else {
            //on a decline, we send an email to player "1" notifying that player 2 declined.
            Player player1 = pendingMatch.getPlayerOne();
            Player player2 = pendingMatch.getPlayerTwo();

            if (player1 != null && player2 != null) {
//                mailSender.sendMail(player1.getEmail(), "match declined", "Your match with " + player2.getName() + " has been declined.");
                Gmail.sendMail(player1.getEmail(), "match declined", "Your match with " + player2.getName() + " has been declined.");
            }

            // update pending set status = 'declined' where pending_id = #pendingId#
            PendingMatch match = pendingMatchRepository.findOneById(confirmation.getPendingId());

            match.setStatus("declined");

            pendingMatchRepository.save(match);
        }

        return new HttpResponse(200, "OK");
    }

    @RequestMapping(value = "/pending/player/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<Match> getPendingMatches(@PathVariable("id") long id) {
        List<PendingMatch> matches = pendingMatchRepository.findAllByPlayerTwoId(id);

        List<Match> result = new ArrayList<>(matches.size());
        for (Match m : matches) {
            m.getPlayerOne().setAvatarUrl(com.lotp.server.util.Util.getAvatarUrlFromEmail(m.getPlayerOne().getEmail()));

            result.add(m);
        }

        return result;
    }

    @RequestMapping(value = "/pending/count", method = RequestMethod.GET)
    @ResponseBody
    public String getPendingMatchesCount() {
        // TODO: Huh?
        long id = -3243;

        try{
          id = myUserContext.getCurrentUser().getId();
        } catch (Exception e){

        }

        List<PendingMatch> matches = pendingMatchRepository.findAllByPlayerTwoId(id);

        if (!matches.isEmpty()) {
            return Integer.toString(matches.size());
        } else {
            return "";
        }
    }

    @RequestMapping(value = "/profile/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ProfileCommand getProfile(@PathVariable("id") long id) {
        ProfileCommand rval = new ProfileCommand();

        Player player = playerRepository.findOne(id);

        Iterable<Match> matches = matchRepository.findAll(new Sort(new Sort.Order(Sort.Direction.DESC, "date")));

        Stats stats = new Stats();

        for (Match match : matches) {
            if ( match.getPlayerOne().getId() != id ) {
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

            match.getPlayerTwo().setAvatarUrl(Util.getAvatarUrlFromEmail(match.getPlayerTwo().getEmail()));

        }

        stats.setTotalGames(stats.getGameWins() + stats.getGameLosses());
        stats.setTotalMatches(stats.getMatchWins() + stats.getMatchLosses());

        if (stats.getTotalGames() > 0) {
            stats.setGameWinPercentage(stats.getGameWins() * 100 /stats.getTotalGames());
        }

        if (stats.getTotalMatches() > 0) {
            stats.setMatchWinPercentage(stats.getMatchWins() * 100 / stats.getTotalMatches());
        }

        List<Match> matchList = new ArrayList<>();
        for( Match m : matches ){
            matchList.add(m);
        }

        rval.setMatches(matchList);
        rval.setStats(stats);
        rval.setPlayer(player);
        return rval;
    }

    @RequestMapping(value = "/leaderboard", method = RequestMethod.GET)
    @ResponseBody
    public List<LeaderboardItem> getLeaderBoard() {
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "playerRanking");
        Sort ratingDesc = new Sort(order);

        Iterable<LeaderboardItem> leaderboardItems = leaderboardItemRepository.findAll(ratingDesc);

        List<LeaderboardItem> rval = new ArrayList<>();
        for (LeaderboardItem item : leaderboardItems) {
            try {
                item.getPlayer().setAvatarUrl("http://www.gravatar.com/avatar/" + Util.md5(item.getPlayer().getEmail()) + ".png");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            rval.add(item);
        }

        return rval;
    }

    private List<LeaderboardItem> calculateLeaderboard() {
        List<LeaderboardItem> rval = new ArrayList<LeaderboardItem>();

        Iterable<Player> players = playerRepository.findAll();

        //for each player, get their matches
        for (Player p : players){
            long playerId = p.getId();
            List<Match> matches = matchRepository.findByPlayerOneIdOrPlayerTwoId(playerId, playerId);

            if (!matches.iterator().hasNext()) {
                //need at least 1 matches
                continue;
            }

            int wins = 0;
            int losses = 0;
            for (Match match : matches) {
                if (match.getPlayerOne().getId() == playerId) {
                    if (match.getP1Score() > match.getP2Score()) {
                        wins++;
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

            LeaderboardItem item = new LeaderboardItem();
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
        List<LeaderboardItem> lb = calculateLeaderboard();
        Collections.sort(lb);

        leaderboardItemRepository.deleteAll();

        for (LeaderboardItem item : lb) {
            leaderboardItemRepository.save(item);
        }

        return "Leaderboard Recalculated";
    }

    /**
     * This is meant to re-calculate the rankings starting from the first game
     */
    @RequestMapping(value = "/resetRankings", method = RequestMethod.GET)
    @ResponseBody
    public String initializeRankgings() {
        Iterable<Match> matches = matchRepository.findAll();

        for (Match m : matches) {
            //since we are looping through a bunch of matches in the DB, the player ratings could've changed. Reload them.
            m.setPlayerOne(playerRepository.findOne(m.getPlayerOne().getId()));
            m.setPlayerTwo(playerRepository.findOne(m.getPlayerTwo().getId()));

            Ranking.calculateRanking(m);

            Player one = m.getPlayerOne();
            Player two = m.getPlayerTwo();

            one.setRanking(m.getPlayerOne().getRanking());
            one.setSigma(m.getPlayerOne().getSigma());

            two.setRanking(m.getPlayerTwo().getRanking());
            two.setSigma(m.getPlayerTwo().getSigma());

            playerRepository.save(one);
            playerRepository.save(two);
        }

        return "Rankings Recalculated";
    }



    /**
     * It inserts the item into the correct spot to produce a decreasing order of winning percentage
     * @param items
     * @param item
     */
    private void insertLeaderBoardItemCorrectly(List<LeaderboardItem> items, LeaderboardItem item) {

        if (items == null) return;

        if (items.isEmpty()) {
            items.add(item);
        } else {
            for (int i=0; i<items.size(); i++) {
                LeaderboardItem lbi = items.get(i);
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
