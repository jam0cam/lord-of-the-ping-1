package com.lotp.server.jskills.trueskill.factors;

import com.lotp.server.jskills.factorgraphs.Factor;
import com.lotp.server.jskills.factorgraphs.Message;
import com.lotp.server.jskills.factorgraphs.Variable;
import com.lotp.server.jskills.numerics.GaussianDistribution;

import static com.lotp.server.jskills.numerics.GaussianDistribution.logProductNormalization;

public abstract class GaussianFactor extends Factor<GaussianDistribution> {

    GaussianFactor(String name) {
        super(name);
    }

    /**
     * Sends the factor-graph message with and returns the log-normalization constant *
     */
    @Override
    protected double SendMessage(Message<GaussianDistribution> message,
                                 Variable<GaussianDistribution> variable) {
        GaussianDistribution marginal = variable.getValue();
        GaussianDistribution messageValue = message.getValue();
        double logZ = logProductNormalization(marginal, messageValue);
        variable.setValue(marginal.mult(messageValue));
        return logZ;
    }

    @Override
    public Message<GaussianDistribution> CreateVariableToMessageBinding(
            Variable<GaussianDistribution> variable) {
        return CreateVariableToMessageBinding(variable,
                new Message<GaussianDistribution>(GaussianDistribution
                        .fromPrecisionMean(0, 0), "message from %s to %s",
                        this, variable
                )
        );
    }
}