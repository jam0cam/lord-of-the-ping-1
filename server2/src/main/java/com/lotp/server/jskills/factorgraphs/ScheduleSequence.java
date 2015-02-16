package com.lotp.server.jskills.factorgraphs;

import java.util.Collection;

import static java.lang.Math.max;

public class ScheduleSequence<TValue>
        extends Schedule<TValue> {
    private final Collection<Schedule<TValue>> schedules;

    public ScheduleSequence(String name, Collection<Schedule<TValue>> schedules) {
        super(name);
        this.schedules = schedules;
    }

    @Override
    public double visit(int depth, int maxDepth) {
        double maxDelta = 0;

        for (Schedule<TValue> schedule : schedules)
            maxDelta = max(schedule.visit(depth + 1, maxDepth), maxDelta);

        return maxDelta;
    }
}
