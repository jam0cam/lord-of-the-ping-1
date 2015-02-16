package com.lotp.server.jskills.trueskill.layers;

import com.lotp.server.jskills.GameInfo;
import com.lotp.server.jskills.factorgraphs.DefaultVariable;
import com.lotp.server.jskills.factorgraphs.Variable;
import com.lotp.server.jskills.numerics.GaussianDistribution;
import com.lotp.server.jskills.trueskill.DrawMargin;
import com.lotp.server.jskills.trueskill.TrueSkillFactorGraph;
import com.lotp.server.jskills.trueskill.factors.GaussianFactor;
import com.lotp.server.jskills.trueskill.factors.GaussianGreaterThanFactor;
import com.lotp.server.jskills.trueskill.factors.GaussianWithinFactor;

public class TeamDifferencesComparisonLayer extends
        TrueSkillFactorGraphLayer<Variable<GaussianDistribution>, GaussianFactor, DefaultVariable<GaussianDistribution>> {
    private final double _Epsilon;
    private final int[] _TeamRanks;

    public TeamDifferencesComparisonLayer(TrueSkillFactorGraph parentGraph, int[] teamRanks) {
        super(parentGraph);
        _TeamRanks = teamRanks;
        GameInfo gameInfo = ParentFactorGraph.getGameInfo();
        _Epsilon = DrawMargin.GetDrawMarginFromDrawProbability(gameInfo.getDrawProbability(), gameInfo.getBeta());
    }

    @Override
    public void BuildLayer() {
        for (int i = 0; i < getInputVariablesGroups().size(); i++) {
            boolean isDraw = (_TeamRanks[i] == _TeamRanks[i + 1]);
            Variable<GaussianDistribution> teamDifference = getInputVariablesGroups().get(i).get(0);

            GaussianFactor factor =
                    isDraw
                            ? (GaussianFactor) new GaussianWithinFactor(_Epsilon, teamDifference)
                            : new GaussianGreaterThanFactor(_Epsilon, teamDifference);

            AddLayerFactor(factor);
        }
    }
}