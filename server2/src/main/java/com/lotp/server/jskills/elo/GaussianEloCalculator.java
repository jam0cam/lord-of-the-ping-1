package com.lotp.server.jskills.elo;

import com.lotp.server.jskills.GameInfo;
import com.lotp.server.jskills.numerics.GaussianDistribution;

public class GaussianEloCalculator extends TwoPlayerEloCalculator {
    // From the paper
    private static final KFactor StableKFactor = new KFactor(24);

    public GaussianEloCalculator() {
        super(StableKFactor);
    }

    @Override
    protected double getPlayerWinProbability(GameInfo gameInfo,
                                             double playerRating, double opponentRating) {
        double ratingDifference = playerRating - opponentRating;

        // See equation 1.1 in the TrueSkill paper
        return GaussianDistribution.cumulativeTo(
                ratingDifference / (Math.sqrt(2) * gameInfo.getBeta()));
    }
}