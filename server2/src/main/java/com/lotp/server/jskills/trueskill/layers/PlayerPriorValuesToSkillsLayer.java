package com.lotp.server.jskills.trueskill.layers;

import com.lotp.server.jskills.IPlayer;
import com.lotp.server.jskills.ITeam;
import com.lotp.server.jskills.Rating;
import com.lotp.server.jskills.factorgraphs.*;
import com.lotp.server.jskills.numerics.GaussianDistribution;
import com.lotp.server.jskills.numerics.MathUtils;
import com.lotp.server.jskills.trueskill.TrueSkillFactorGraph;
import com.lotp.server.jskills.trueskill.factors.GaussianPriorFactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

// We intentionally have no Posterior schedule since the only purpose here is to 
public class PlayerPriorValuesToSkillsLayer extends
        TrueSkillFactorGraphLayer<DefaultVariable<GaussianDistribution>,
                GaussianPriorFactor,
                KeyedVariable<IPlayer, GaussianDistribution>> {
    private final Collection<ITeam> _Teams;

    public PlayerPriorValuesToSkillsLayer(TrueSkillFactorGraph parentGraph,
                                          Collection<ITeam> teams) {
        super(parentGraph);
        _Teams = teams;
    }

    @Override
    public void BuildLayer() {
        for (ITeam currentTeam : _Teams) {
            List<KeyedVariable<IPlayer, GaussianDistribution>> currentTeamSkills = new ArrayList<KeyedVariable<IPlayer, GaussianDistribution>>();

            for (Entry<IPlayer, Rating> currentTeamPlayer : currentTeam.entrySet()) {
                KeyedVariable<IPlayer, GaussianDistribution> playerSkill =
                        CreateSkillOutputVariable(currentTeamPlayer.getKey());
                AddLayerFactor(CreatePriorFactor(currentTeamPlayer.getKey(), currentTeamPlayer.getValue(), playerSkill));
                currentTeamSkills.add(playerSkill);
            }

            addOutputVariableGroup(currentTeamSkills);
        }
    }

    @Override
    public Schedule<GaussianDistribution> createPriorSchedule() {
        Collection<Schedule<GaussianDistribution>> schedules = new ArrayList<Schedule<GaussianDistribution>>();
        for (GaussianPriorFactor prior : getLocalFactors()) {
            schedules.add(new ScheduleStep<GaussianDistribution>("Prior to Skill Step", prior, 0));
        }
        return ScheduleSequence(schedules, "All priors");
    }

    private GaussianPriorFactor CreatePriorFactor(IPlayer player, Rating priorRating,
                                                  Variable<GaussianDistribution> skillsVariable) {
        return new GaussianPriorFactor(priorRating.getMean(),
                MathUtils.square(priorRating.getStandardDeviation()) +
                        MathUtils.square(getParentFactorGraph().getGameInfo().getDynamicsFactor()), skillsVariable
        );
    }

    private KeyedVariable<IPlayer, GaussianDistribution> CreateSkillOutputVariable(IPlayer key) {
        return new KeyedVariable<IPlayer, GaussianDistribution>(key, GaussianDistribution.UNIFORM, "%s's skill", key.toString());
    }
}