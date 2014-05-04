package com.lotp.server.jskills.elo;

import com.lotp.server.jskills.Rating;

/**
 * An Elo rating represented by a single number (mean).
 */
public class EloRating extends Rating {
    public EloRating(double rating) {
        super(rating, 0);
    }
}