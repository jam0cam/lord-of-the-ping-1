package com.lotp.server.jskills.trueskill;

import com.lotp.server.jskills.*;
import com.lotp.server.jskills.numerics.Range;
import org.ejml.simple.SimpleMatrix;

import java.util.*;

import static com.lotp.server.jskills.numerics.MathUtils.square;


/**
 * Calculates TrueSkill using a full factor graph.
 */
public class FactorGraphTrueSkillCalculator extends SkillCalculator {
    public FactorGraphTrueSkillCalculator() {
        super(EnumSet.of(SupportedOptions.PartialPlay, SupportedOptions.PartialUpdate), Range.<ITeam>atLeast(2), Range
                .<IPlayer>atLeast(1));
    }

    private static SimpleMatrix GetPlayerMeansVector(Collection<ITeam> teamAssignmentsList) {
        // A simple list of all the player means.
        List<Double> temp = GetPlayerMeanRatingValues(teamAssignmentsList);
        double[] tempa = new double[temp.size()];
        for (int i = 0; i < tempa.length; i++)
            tempa[i] = temp.get(i);
        return new SimpleMatrix(new double[][]{tempa}).transpose();
    }

    /**
     * This is a square matrix whose diagonal values represent the variance (square of standard deviation) of all
     * players.
     */
    private static SimpleMatrix GetPlayerCovarianceMatrix(Collection<ITeam> teamAssignmentsList) {
        List<Double> temp = GetPlayerVarianceRatingValues(teamAssignmentsList);
        double[] tempa = new double[temp.size()];
        for (int i = 0; i < tempa.length; i++)
            tempa[i] = temp.get(i);
        return SimpleMatrix.diag(tempa).transpose();
    }

    /**
     * TODO Make array? Helper function that gets a list of values for all player ratings
     */
    private static List<Double> GetPlayerMeanRatingValues(Collection<ITeam> teamAssignmentsList) {
        List<Double> playerRatingValues = new ArrayList<Double>();
        for (ITeam currentTeam : teamAssignmentsList)
            for (Rating currentRating : currentTeam.values())
                playerRatingValues.add(currentRating.getMean());

        return playerRatingValues;
    }

    /**
     * TODO Make array? Helper function that gets a list of values for all player ratings
     */
    private static List<Double> GetPlayerVarianceRatingValues(Collection<ITeam> teamAssignmentsList) {
        List<Double> playerRatingValues = new ArrayList<Double>();
        for (ITeam currentTeam : teamAssignmentsList)
            for (Rating currentRating : currentTeam.values())
                playerRatingValues.add(currentRating.getVariance());

        return playerRatingValues;
    }

    /**
     * The team assignment matrix is often referred to as the "A" matrix. It's a matrix whose rows represent the players
     * and the columns represent teams. At Matrix[row, column] represents that player[row] is on team[col] Positive
     * values represent an assignment and a negative value means that we subtract the value of the next team since we're
     * dealing with pairs. This means that this matrix always has teams - 1 columns. The only other tricky thing is that
     * values represent the play percentage.
     * <p/>
     * For example, consider a 3 team game where team1 is just player1, team 2 is player 2 and player 3, and team3 is
     * just player 4. Furthermore, player 2 and player 3 on team 2 played 25% and 75% of the time (e.g. partial play),
     * the A matrix would be:
     * <p/>
     * <p/>
     * <pre>
     * A = this 4x2 matrix:
     * |  1.00  0.00 |
     * | -0.25  0.25 |
     * | -0.75  0.75 |
     * |  0.00 -1.00 |
     * </pre>
     */
    private static SimpleMatrix CreatePlayerTeamAssignmentMatrix(List<ITeam> teamAssignmentsList, int totalPlayers) {

        List<List<Double>> playerAssignments = new ArrayList<List<Double>>();
        int totalPreviousPlayers = 0;

        for (int i = 0; i < teamAssignmentsList.size() - 1; i++) {
            ITeam currentTeam = teamAssignmentsList.get(i);

            // Need to add in 0's for all the previous players, since they're not
            // on this team
            List<Double> currentRowValues = new ArrayList<Double>();
            for (int j = 0; j < totalPreviousPlayers; j++)
                currentRowValues.add(0.);
            playerAssignments.add(currentRowValues);

            for (IPlayer player : currentTeam.keySet()) {
                currentRowValues.add(PartialPlay.getPartialPlayPercentage(player));
                // indicates the player is on the team
                totalPreviousPlayers++;
            }

            ITeam nextTeam = teamAssignmentsList.get(i + 1);
            for (IPlayer nextTeamPlayer : nextTeam.keySet()) {
                // Add a -1 * playing time to represent the difference
                currentRowValues.add(-1 * PartialPlay.getPartialPlayPercentage(nextTeamPlayer));
            }
        }

        SimpleMatrix playerTeamAssignmentsMatrix = new SimpleMatrix(totalPlayers, teamAssignmentsList.size() - 1);
        for (int i = 0; i < playerAssignments.size(); i++)
            for (int j = 0; j < playerAssignments.get(i).size(); j++)
                playerTeamAssignmentsMatrix.set(j, i, playerAssignments.get(i).get(j));

        return playerTeamAssignmentsMatrix;
    }

    @Override
    public Map<IPlayer, Rating> calculateNewRatings(GameInfo gameInfo, Collection<ITeam> teams, int... teamRanks) {
        Guard.argumentNotNull(gameInfo, "gameInfo");
        validateTeamCountAndPlayersCountPerTeam(teams);

        List<ITeam> teamsl = RankSorter.sort(teams, teamRanks);

        TrueSkillFactorGraph factorGraph = new TrueSkillFactorGraph(gameInfo, teamsl, teamRanks);
        factorGraph.BuildGraph();
        factorGraph.RunSchedule();

        @SuppressWarnings("unused")
        // TODO use this somehow?
                double probabilityOfOutcome = factorGraph.GetProbabilityOfRanking();

        return factorGraph.GetUpdatedRatings();
    }

    @Override
    public double calculateMatchQuality(GameInfo gameInfo, Collection<ITeam> teams) {
        // We need to create the A matrix which is the player team assigments.
        List<ITeam> teamAssignmentsList = new ArrayList<ITeam>(teams);
        SimpleMatrix skillsMatrix = GetPlayerCovarianceMatrix(teamAssignmentsList);
        SimpleMatrix meanVector = GetPlayerMeansVector(teamAssignmentsList);
        SimpleMatrix meanVectorTranspose = meanVector.transpose();

        SimpleMatrix playerTeamAssignmentsMatrix = CreatePlayerTeamAssignmentMatrix(teamAssignmentsList,
                meanVector.numRows());
        SimpleMatrix playerTeamAssignmentsMatrixTranspose = playerTeamAssignmentsMatrix.transpose();

        double betaSquared = square(gameInfo.getBeta());

        SimpleMatrix start = meanVectorTranspose.mult(playerTeamAssignmentsMatrix);
        SimpleMatrix aTa = playerTeamAssignmentsMatrixTranspose.mult(playerTeamAssignmentsMatrix).scale(betaSquared);
        SimpleMatrix aTSA = playerTeamAssignmentsMatrixTranspose.mult(skillsMatrix).mult(playerTeamAssignmentsMatrix);
        SimpleMatrix middle = aTa.plus(aTSA);

        SimpleMatrix middleInverse = middle.invert();

        SimpleMatrix end = playerTeamAssignmentsMatrixTranspose.mult(meanVector);

        SimpleMatrix expPartMatrix = start.mult(middleInverse).mult(end).scale(-0.5);
        double expPart = expPartMatrix.determinant();

        double sqrtPartNumerator = aTa.determinant();
        double sqrtPartDenominator = middle.determinant();
        double sqrtPart = sqrtPartNumerator / sqrtPartDenominator;

        double result = Math.exp(expPart) * Math.sqrt(sqrtPart);

        return result;
    }
}