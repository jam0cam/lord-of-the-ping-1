// Generated by delombok at Mon May 27 20:39:51 CEST 2013
package com.lotp.server.jskills;

/**
 * Parameters about the game for calculating the TrueSkill.
 */

public class GameInfo {
    private static final double defaultInitialMean = 25.0;
    private static final double defaultBeta = defaultInitialMean / 6.0;
    private static final double defaultDynamicsFactor = defaultInitialMean / 300.0;
    private static final double defaultInitialStandardDeviation = defaultInitialMean / 3.0;
    private static final double defaultDrawProbability = 0.1;
    private double initialMean;
    private double initialStandardDeviation;
    private double beta;
    private double dynamicsFactor;
    private double drawProbability;

    public GameInfo(double initialMean, double initialStandardDeviation, double beta, double dynamicFactor, double drawProbability) {

        this.initialMean = initialMean;
        this.initialStandardDeviation = initialStandardDeviation;
        this.beta = beta;
        this.dynamicsFactor = dynamicFactor;
        this.drawProbability = drawProbability;
    }

    public static GameInfo getDefaultGameInfo() {
        // We return a fresh copy since we have public setters that can mutate state
        return new GameInfo(defaultInitialMean, defaultInitialStandardDeviation, defaultBeta, defaultDynamicsFactor, defaultDrawProbability);
    }

    public Rating getDefaultRating() {
        return new Rating(initialMean, initialStandardDeviation);
    }

    @java.lang.SuppressWarnings("all")
    public double getInitialMean() {
        return this.initialMean;
    }

    @java.lang.SuppressWarnings("all")
    public void setInitialMean(final double initialMean) {
        this.initialMean = initialMean;
    }

    @java.lang.SuppressWarnings("all")
    public double getInitialStandardDeviation() {
        return this.initialStandardDeviation;
    }

    @java.lang.SuppressWarnings("all")
    public void setInitialStandardDeviation(final double initialStandardDeviation) {
        this.initialStandardDeviation = initialStandardDeviation;
    }

    @java.lang.SuppressWarnings("all")
    public double getBeta() {
        return this.beta;
    }

    @java.lang.SuppressWarnings("all")
    public void setBeta(final double beta) {
        this.beta = beta;
    }

    @java.lang.SuppressWarnings("all")
    public double getDynamicsFactor() {
        return this.dynamicsFactor;
    }

    @java.lang.SuppressWarnings("all")
    public void setDynamicsFactor(final double dynamicsFactor) {
        this.dynamicsFactor = dynamicsFactor;
    }

    @java.lang.SuppressWarnings("all")
    public double getDrawProbability() {
        return this.drawProbability;
    }

    @java.lang.SuppressWarnings("all")
    public void setDrawProbability(final double drawProbability) {
        this.drawProbability = drawProbability;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof GameInfo)) return false;
        final GameInfo other = (GameInfo) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        if (java.lang.Double.compare(this.getInitialMean(), other.getInitialMean()) != 0) return false;
        if (java.lang.Double.compare(this.getInitialStandardDeviation(), other.getInitialStandardDeviation()) != 0)
            return false;
        if (java.lang.Double.compare(this.getBeta(), other.getBeta()) != 0) return false;
        if (java.lang.Double.compare(this.getDynamicsFactor(), other.getDynamicsFactor()) != 0) return false;
        if (java.lang.Double.compare(this.getDrawProbability(), other.getDrawProbability()) != 0) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    public boolean canEqual(final java.lang.Object other) {
        return other instanceof GameInfo;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        final long $initialMean = java.lang.Double.doubleToLongBits(this.getInitialMean());
        result = result * PRIME + (int) ($initialMean >>> 32 ^ $initialMean);
        final long $initialStandardDeviation = java.lang.Double.doubleToLongBits(this.getInitialStandardDeviation());
        result = result * PRIME + (int) ($initialStandardDeviation >>> 32 ^ $initialStandardDeviation);
        final long $beta = java.lang.Double.doubleToLongBits(this.getBeta());
        result = result * PRIME + (int) ($beta >>> 32 ^ $beta);
        final long $dynamicsFactor = java.lang.Double.doubleToLongBits(this.getDynamicsFactor());
        result = result * PRIME + (int) ($dynamicsFactor >>> 32 ^ $dynamicsFactor);
        final long $drawProbability = java.lang.Double.doubleToLongBits(this.getDrawProbability());
        result = result * PRIME + (int) ($drawProbability >>> 32 ^ $drawProbability);
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "GameInfo(initialMean=" + this.getInitialMean() + ", initialStandardDeviation=" + this.getInitialStandardDeviation() + ", beta=" + this.getBeta() + ", dynamicsFactor=" + this.getDynamicsFactor() + ", drawProbability=" + this.getDrawProbability() + ")";
    }
}