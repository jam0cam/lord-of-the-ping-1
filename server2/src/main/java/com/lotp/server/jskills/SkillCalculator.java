package com.lotp.server.jskills;

import com.lotp.server.jskills.numerics.Range;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

/**
 * Base class for all skill calculator implementations.
 */
public abstract class SkillCalculator {

    private final EnumSet<SupportedOptions> supportedOptions;
    private final Range<IPlayer> playersPerTeamAllowed;
    private final Range<ITeam> totalTeamsAllowed;
    protected SkillCalculator(EnumSet<SupportedOptions> supportedOptions,
                              Range<ITeam> totalTeamsAllowed,
                              Range<IPlayer> playerPerTeamAllowed) {
        this.supportedOptions = supportedOptions;
        this.totalTeamsAllowed = totalTeamsAllowed;
        this.playersPerTeamAllowed = playerPerTeamAllowed;
    }

    private static void validateTeamCountAndPlayersCountPerTeam(
            Collection<ITeam> teams,
            Range<ITeam> totalTeams,
            Range<IPlayer> playersPerTeam) {
        Guard.argumentNotNull(teams, "teams");
        int countOfTeams = 0;
        for (ITeam currentTeam : teams) {
            if (!playersPerTeam.isInRange(currentTeam.size())) {
                throw new IllegalArgumentException();
            }
            countOfTeams++;
        }

        if (!totalTeams.isInRange(countOfTeams)) {
            throw new IllegalArgumentException();
        }
    }

    public boolean isSupported(SupportedOptions option) {
        return supportedOptions.contains(option);
    }

    /**
     * Calculates new ratings based on the prior ratings and team ranks.
     *
     * @param gameInfo  Parameters for the game.
     * @param teams     A mapping of team players and their ratings.
     * @param teamRanks The ranks of the teams where 1 is first place. For a tie,
     *                  repeat the number (e.g. 1, 2, 2)
     * @returns All the players and their new ratings.
     */
    public abstract Map<IPlayer, Rating> calculateNewRatings(GameInfo gameInfo,
                                                             Collection<ITeam> teams, int... teamRanks);

    /**
     * Calculates the match quality as the likelihood of all teams drawing.
     *
     * @param gameInfo Parameters for the game.
     * @param teams    A mapping of team players and their ratings.
     * @returns The quality of the match between the teams as a percentage (0% =
     * bad, 100% = well matched).
     */
    public abstract double calculateMatchQuality(GameInfo gameInfo,
                                                 Collection<ITeam> teams);

    protected void validateTeamCountAndPlayersCountPerTeam(
            Collection<ITeam> teams) {
        validateTeamCountAndPlayersCountPerTeam(teams, totalTeamsAllowed, playersPerTeamAllowed);
    }

    public enum SupportedOptions {PartialPlay, PartialUpdate;}
}