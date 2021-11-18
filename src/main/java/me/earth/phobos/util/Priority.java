package me.earth.phobos.util;

public enum Priority
{
    Highest(200),
    High(100),
    Normal(0),
    Low(-100),
    Lowest(-200);

    int priority;

    private Priority(final int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }
}