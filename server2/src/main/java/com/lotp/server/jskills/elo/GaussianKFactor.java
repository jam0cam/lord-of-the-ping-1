package com.lotp.server.jskills.elo;

import com.lotp.server.jskills.GameInfo;

public class GaussianKFactor extends KFactor {
    // From paper
    static final double StableDynamicsKFactor = 24.0;

    public GaussianKFactor() {
        super(StableDynamicsKFactor);
    }

    public GaussianKFactor(GameInfo gameInfo, double latestGameWeightingFactor) {
        super(latestGameWeightingFactor * gameInfo.getBeta()
                * Math.sqrt(Math.PI));
    }
}