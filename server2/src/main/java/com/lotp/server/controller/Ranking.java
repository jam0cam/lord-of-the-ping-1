package com.lotp.server.controller;

import com.lotp.server.jskills.*;
import com.lotp.server.entity.Match;
import com.lotp.server.entity.Player;
import com.lotp.server.jskills.trueskill.TwoPlayerTrueSkillCalculator;

import java.util.Collection;
import java.util.Map;


/**
 * User: jitse
 * Date: 12/30/13
 * Time: 2:55 PM
 */
public class Ranking {

    static TwoPlayerTrueSkillCalculator calculator = new TwoPlayerTrueSkillCalculator();
    static GameInfo gameInfo = new GameInfo(1200, 400, 200, 4, 0.03);

    public static void calculateRanking(Match m) {
        com.lotp.server.jskills.Player<Integer> player1 = new com.lotp.server.jskills.Player<Integer>(1);
        com.lotp.server.jskills.Player<Integer> player2 = new com.lotp.server.jskills.Player<Integer>(2);

        Player p1 = m.getPlayerOne();
        Player p2 = m.getPlayerTwo();

        Rating p1Rating = new Rating(p1.getRanking(), p1.getSigma());
        Rating p2Rating = new Rating(p2.getRanking(), p2.getSigma());

        Team team1 = new Team(player1, p1Rating);
        Team team2 = new Team(player2, p2Rating);
        Collection<ITeam> teams = Team.concat(team1, team2);

        Rating player1NewRating;
        Rating player2NewRating;

        int winner;
        int loser;

        if (m.getP1Score() > m.getP2Score()) {
            winner = 1;
            loser = 2;
        } else {
            winner = 2;
            loser = 1;
        }

        Map<IPlayer, Rating> newRatings = calculator.calculateNewRatings(gameInfo, teams, winner, loser);
        player1NewRating = newRatings.get(player1);
        player2NewRating = newRatings.get(player2);

        m.getPlayerOne().setRanking((int) Math.round(player1NewRating.getMean()));
        m.getPlayerOne().setSigma(player1NewRating.getStandardDeviation());

        m.getPlayerTwo().setRanking((int) Math.round(player2NewRating.getMean()));
        m.getPlayerTwo().setSigma(player2NewRating.getStandardDeviation());
    }
}
