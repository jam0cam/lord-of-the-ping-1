package com.lotp.server.jskills.trueskill.layers;

import com.lotp.server.jskills.IPlayer;
import com.lotp.server.jskills.PartialPlay;
import com.lotp.server.jskills.factorgraphs.KeyedVariable;
import com.lotp.server.jskills.factorgraphs.Schedule;
import com.lotp.server.jskills.factorgraphs.ScheduleStep;
import com.lotp.server.jskills.factorgraphs.Variable;
import com.lotp.server.jskills.numerics.GaussianDistribution;
import com.lotp.server.jskills.trueskill.TrueSkillFactorGraph;
import com.lotp.server.jskills.trueskill.factors.GaussianWeightedSumFactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerPerformancesToTeamPerformancesLayer extends
        TrueSkillFactorGraphLayer<KeyedVariable<IPlayer, GaussianDistribution>,
                GaussianWeightedSumFactor,
                Variable<GaussianDistribution>> {
    public PlayerPerformancesToTeamPerformancesLayer(TrueSkillFactorGraph parentGraph) {
        super(parentGraph);
    }

    @Override
    public void BuildLayer() {
        for (List<KeyedVariable<IPlayer, GaussianDistribution>> currentTeam : getInputVariablesGroups()) {
            Variable<GaussianDistribution> teamPerformance = CreateOutputVariable(currentTeam);
            AddLayerFactor(createPlayerToTeamSumFactor(currentTeam, teamPerformance));

            // REVIEW: Does it make sense to have groups of one?
            addOutputVariable(teamPerformance);
        }
    }

    @Override
    public Schedule<GaussianDistribution> createPriorSchedule() {
        Collection<Schedule<GaussianDistribution>> schedules = new ArrayList<Schedule<GaussianDistribution>>();
        for (GaussianWeightedSumFactor weightedSumFactor : getLocalFactors()) {
            schedules.add(new ScheduleStep<GaussianDistribution>("Perf to Team Perf Step", weightedSumFactor, 0));
        }
        return ScheduleSequence(schedules, "all player perf to team perf schedule");
    }

    protected GaussianWeightedSumFactor createPlayerToTeamSumFactor(
            List<KeyedVariable<IPlayer, GaussianDistribution>> teamMembers, Variable<GaussianDistribution> sumVariable) {
        double[] weights = new double[teamMembers.size()];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = PartialPlay.getPartialPlayPercentage(teamMembers.get(i).getKey());
        }
        return new GaussianWeightedSumFactor(sumVariable, teamMembers, weights);
    }

    @Override
    public Schedule<GaussianDistribution> createPosteriorSchedule() {
        List<Schedule<GaussianDistribution>> schedules = new ArrayList<Schedule<GaussianDistribution>>();
        for (GaussianWeightedSumFactor currentFactor : getLocalFactors()) {
            // TODO is there an off by 1 error here?
            for (int i = 0; i < currentFactor.getNumberOfMessages(); i++) {
                schedules.add(new ScheduleStep<GaussianDistribution>(
                        "team sum perf @" + i,
                        currentFactor,
                        i));
            }
        }
        return ScheduleSequence(schedules,
                "all of the team's sum iterations");
    }

    private Variable<GaussianDistribution> CreateOutputVariable(
            List<KeyedVariable<IPlayer, GaussianDistribution>> team) {
        StringBuilder sb = new StringBuilder();
        for (KeyedVariable<IPlayer, GaussianDistribution> teamMember : team) {
            sb.append(teamMember.getKey().toString());
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());

        return new Variable<GaussianDistribution>(GaussianDistribution.UNIFORM, "Team[%s]'s performance", sb.toString());
    }
}