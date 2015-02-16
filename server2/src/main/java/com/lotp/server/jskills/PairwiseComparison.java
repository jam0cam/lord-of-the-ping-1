package com.lotp.server.jskills;

import java.util.TreeMap;

/**
 * Represents a comparison between two players.
 * <p/>
 * The actual values for the enum were chosen so that the also correspond to the
 * multiplier for updates to means.
 */
public enum PairwiseComparison {
    WIN(1),
    DRAW(0),
    LOSE(-1);
    private static TreeMap<Integer, PairwiseComparison> revmap = new TreeMap<Integer, PairwiseComparison>();
    static {
        for (PairwiseComparison pc : PairwiseComparison.values())
            revmap.put(pc.multiplier, pc);
    }
    public final int multiplier;

    private PairwiseComparison(int multiplier) {
        this.multiplier = multiplier;
    }

    public static PairwiseComparison fromMultiplier(int multiplier) {
        return revmap.get(multiplier);
    }
}