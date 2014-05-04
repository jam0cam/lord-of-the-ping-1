package com.lotp.server.jskills.trueskill.layers;

import com.lotp.server.jskills.factorgraphs.Factor;
import com.lotp.server.jskills.factorgraphs.FactorGraphLayer;
import com.lotp.server.jskills.factorgraphs.Variable;
import com.lotp.server.jskills.numerics.GaussianDistribution;
import com.lotp.server.jskills.trueskill.TrueSkillFactorGraph;

public abstract class TrueSkillFactorGraphLayer<TInputVariable extends Variable<GaussianDistribution>,
        TFactor extends Factor<GaussianDistribution>,
        TOutputVariable extends Variable<GaussianDistribution>>
        extends FactorGraphLayer
        <TrueSkillFactorGraph, GaussianDistribution, Variable<GaussianDistribution>, TInputVariable,
                TFactor, TOutputVariable> {
    public TrueSkillFactorGraphLayer(TrueSkillFactorGraph parentGraph) {
        super(parentGraph);
    }
}