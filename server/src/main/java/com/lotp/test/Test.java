package com.lotp.test;

import jskills.*;
import jskills.trueskill.TwoPlayerTrueSkillCalculator;

import java.util.Collection;
import java.util.Map;

/**
 * User: jitse
 * Date: 12/26/13
 * Time: 6:48 PM
 */
public class Test {

    public static void main(String[] args){

        GameInfo gameInfo = new GameInfo(1200.0, 1200.0 / 3.0, 200.0, 1200.0 / 300.0, 0.03);


        TwoPlayerTrueSkillCalculator calculator;
        calculator = new TwoPlayerTrueSkillCalculator();

        Player<Integer> player1 = new Player<Integer>(1);
        Player<Integer> player2 = new Player<Integer>(2);

        Team team1 = new Team(player1, gameInfo.getDefaultRating());
        Team team2 = new Team(player2, gameInfo.getDefaultRating());
        Collection<ITeam> teams = Team.concat(team1, team2);

        Rating player1NewRating = null;
        Rating player2NewRating = null;

        for (int i=0; i<50; i++) {
            Map<IPlayer, Rating> newRatings = calculator.calculateNewRatings(gameInfo, teams, 1, 2);
            player1NewRating = newRatings.get(player1);
            player2NewRating = newRatings.get(player2);
        }


        System.out.println("hello world");


    }

}
