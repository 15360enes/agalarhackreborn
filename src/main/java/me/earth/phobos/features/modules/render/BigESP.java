package me.earth.phobos.features.modules.render;

import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import net.minecraft.entity.player.EntityPlayer;

public class BigESP
        extends Module {
    public BigESP() {
        super("BigModule", "Big fucking module", Module.Category.RENDER, true, false, false);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (!BigESP.fullNullCheck()) {
            for (EntityPlayer player : BigESP.mc.world.playerEntities) {
                double x = this.interpolate(player.lastTickPosX, player.posX, event.getPartialTicks()) - BigESP.mc.getRenderManager().renderPosX;
                double y = this.interpolate(player.lastTickPosY, player.posY, event.getPartialTicks()) - BigESP.mc.getRenderManager().renderPosY;
                double z = this.interpolate(player.lastTickPosZ, player.posZ, event.getPartialTicks()) - BigESP.mc.getRenderManager().renderPosZ;
                this.renderBigESP(player, x, y, z, event.getPartialTicks());
            }
        }
    }

    public void renderBigESP(EntityPlayer player, double x, double y, double z, float delta) {
    }

    private double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * (double) delta;
    }
}

