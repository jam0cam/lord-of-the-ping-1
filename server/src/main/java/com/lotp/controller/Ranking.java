package com.lotp.controller;

import com.lotp.model.Match;
import com.lotp.model.Player;
import jskills.*;
import jskills.trueskill.TwoPlayerTrueSkillCalculator;

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
        jskills.Player<Integer> player1 = new jskills.Player<Integer>(1);
        jskills.Player<Integer> player2 = new jskills.Player<Integer>(2);

        Player p1 = m.getP1();
        Player p2 = m.getP2();

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

        m.getP1().setRanking((int)Math.round(player1NewRating.getMean()));
        m.getP1().setSigma(player1NewRating.getStandardDeviation());

        m.getP2().setRanking((int)Math.round(player2NewRating.getMean()));
        m.getP2().setSigma(player2NewRating.getStandardDeviation());
    }
}
