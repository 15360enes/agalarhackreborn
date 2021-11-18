package me.earth.phobos.event.events;

import me.earth.phobos.event.EventProcessor;

class RenderWorldEvent
        extends EventProcessor {
    private final float partialTicks;

    public RenderWorldEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}