package com.lotp.server.jskills.trueskill.factors;

import com.lotp.server.jskills.factorgraphs.Message;
import com.lotp.server.jskills.factorgraphs.Variable;
import com.lotp.server.jskills.numerics.GaussianDistribution;

import static com.lotp.server.jskills.numerics.GaussianDistribution.sub;

/**
 * Supplies the factor graph with prior information.
 * <remarks>See the accompanying math paper for more details.</remarks>
 */
public class GaussianPriorFactor extends GaussianFactor {
    private final GaussianDistribution _NewMessage;

    public GaussianPriorFactor(double mean, double variance, Variable<GaussianDistribution> variable) {
        super(String.format("Prior value going to %s", variable));
        _NewMessage = new GaussianDistribution(mean, Math.sqrt(variance));
        CreateVariableToMessageBinding(variable,
                new Message<GaussianDistribution>(
                        GaussianDistribution.fromPrecisionMean(0, 0), "message from %s to %s",
                        this, variable)
        );
    }

    @Override
    protected double updateMessage(Message<GaussianDistribution> message,
                                   Variable<GaussianDistribution> variable) {
        GaussianDistribution oldMarginal = new GaussianDistribution(variable.getValue());
        Message<GaussianDistribution> oldMessage = message;
        GaussianDistribution newMarginal =
                GaussianDistribution.fromPrecisionMean(
                        oldMarginal.getPrecisionMean() + _NewMessage.getPrecisionMean() - oldMessage.getValue().getPrecisionMean(),
                        oldMarginal.getPrecision() + _NewMessage.getPrecision() - oldMessage.getValue().getPrecision());
        variable.setValue(newMarginal);
        message.setValue(_NewMessage);
        return sub(oldMarginal, newMarginal);
    }

    @Override
    public double getLogNormalization() {
        return 0;
    }
}