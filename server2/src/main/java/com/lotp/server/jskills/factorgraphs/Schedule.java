package com.lotp.server.jskills.factorgraphs;

public abstract class Schedule<T> {
    private final String _Name;

    protected Schedule(String name) {
        _Name = name;
    }

    public abstract double visit(int depth, int maxDepth);

    public double visit() {
        return visit(-1, 0);
    }

    @Override
    public String toString() {
        return _Name;
    }

}
