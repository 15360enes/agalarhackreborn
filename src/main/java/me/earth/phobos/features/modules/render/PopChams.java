package me.earth.phobos.features.modules.render;

import com.mojang.authlib.GameProfile;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.NordTessellator;
import me.earth.phobos.util.PopChamsUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import java.awt.color.*;
import java.awt.*;

public class PopChams extends Module {
    public static final Setting<Boolean> self = new Setting<Boolean>("Self", false);
    public static final Setting<Integer> RED_LINE = new Setting<Integer>("RedLine", 255, 0, 255);
    public static final Setting<Integer> GREEN_LINE = new Setting<Integer>("GreenLine", 26, 0, 255);
    public static final Setting<Integer> BLUE_LINE = new Setting<Integer>("BlueLine", 42, 0, 255);
    public static final Setting<Integer> ALPHA_LINE = new Setting<Integer>("AlphaLine", 42, 0, 255);

    public static final Setting<Integer> RED_FILL = new Setting<Integer>("RedFill", 255, 0, 255);
    public static final Setting<Integer> GREEN_FILL = new Setting<Integer>("GreenFill", 26, 0, 255);
    public static final Setting<Integer> BLUE_FILL = new Setting<Integer>("BlueFill", 42, 0, 255);
    public static final Setting<Integer> ALPHA_FILL = new Setting<Integer>("AlphaFill", 42, 0, 255);

    public static final Setting<Integer> fadestart = new Setting<Integer>("FadeStart", 200, 0, 5000);
    public static final Setting<Double> fadetime = new Setting<Double>("FadeTime", .5, .0,2d);
    public static final Setting<Boolean> onlyOneEsp = new Setting<Boolean>("OnlyOneEsp", true);
    public static final Setting<Boolean> colorsync = new Setting<Boolean>("Csync", false);

    EntityOtherPlayerMP player;
    ModelPlayer playerModel;
    Long startTime;
    double alphaFill;
    double alphaLine;

    public PopChams() {
        super("PopChams", "Renders when some1 pops", Category.RENDER, true, false, false);
        register(self);
        register(RED_LINE);
        register(GREEN_LINE);
        register(BLUE_LINE);
        register(ALPHA_LINE);
        register(RED_FILL);
        register(GREEN_FILL);
        register(BLUE_FILL);
        register(ALPHA_FILL);
        register(fadestart);
        register(fadetime);
        register(onlyOneEsp);
        register(colorsync);
    }

    @SubscribeEvent
    public void onPacketReceived(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketEntityStatus) {
            final SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if (packet.getOpCode() == 35 && packet.getEntity(PopChams.mc.world) != null && (self.getValue() || packet.getEntity(PopChams.mc.world).getEntityId() != PopChams.mc.player.getEntityId())) {
                final GameProfile profile = new GameProfile(mc.player.getUniqueID(), "");
                (this.player = new EntityOtherPlayerMP(mc.world, profile)).copyLocationAndAnglesFrom(packet.getEntity(mc.world));
                this.playerModel = new ModelPlayer(0.0f, false);
                this.startTime = System.currentTimeMillis();
                playerModel.bipedHead.showModel = false;
                playerModel.bipedBody.showModel = false;
                playerModel.bipedLeftArmwear.showModel = false;
                playerModel.bipedLeftLegwear.showModel = false;
                playerModel.bipedRightArmwear.showModel = false;
                playerModel.bipedRightLegwear.showModel = false;

                alphaFill = ALPHA_FILL.getValue();
                alphaLine = ALPHA_LINE.getValue();
                if (!onlyOneEsp.getValue()) {
                    PopChamsUtil p = new PopChamsUtil(player, playerModel, startTime, alphaFill, alphaLine);
                }

            }
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (onlyOneEsp.getValue()) {
            if (player == null || mc.world == null || mc.player == null) {
                return;
            }
            GL11.glLineWidth(1.0f);
            Color lineColorS = this.colorsync.getValue() ?  Colors.INSTANCE.getCurrentColor() :  new Color(RED_LINE.getValue(), BLUE_LINE.getValue(), GREEN_LINE.getValue(), ALPHA_LINE.getValue());
            Color fillColorS = this.colorsync.getValue() ?  Colors.INSTANCE.getCurrentColor() :  new Color(RED_FILL.getValue(), BLUE_FILL.getValue(), GREEN_FILL.getValue(), ALPHA_FILL.getValue());
            int lineA = lineColorS.getAlpha();
            int fillA = (fillColorS).getAlpha();
            final long time = System.currentTimeMillis() - this.startTime - ((Number) fadestart.getValue()).longValue();
            if (System.currentTimeMillis() - this.startTime > ((Number) fadestart.getValue()).longValue()) {
                double normal = this.normalize((double) time, 0.0, ((Number) fadetime.getValue()).doubleValue());
                normal = MathHelper.clamp(normal, 0.0, 1.0);
                normal = -normal + 1.0;
                lineA *= (int) normal;
                fillA *= (int) normal;
            }
            Color lineColor = newAlpha(lineColorS, lineA);
            Color fillColor = newAlpha(fillColorS, fillA);
            if (this.player != null && this.playerModel != null) {
                NordTessellator.prepareGL();
                GL11.glPushAttrib(1048575);
                GL11.glEnable(2881);
                GL11.glEnable(2848);
                if (alphaFill > 1) alphaFill -= fadetime.getValue();
                Color fillFinal = new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), (int) alphaFill);

                if (alphaLine > 1) alphaLine -= fadetime.getValue();
                Color outlineFinal = new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), (int) alphaLine);
                glColor(fillFinal);
                GL11.glPolygonMode(1032, 6914);
                renderEntity(this.player, this.playerModel, this.player.limbSwing, this.player.limbSwingAmount, (float) this.player.ticksExisted, this.player.rotationYawHead, this.player.rotationPitch, 1);
                glColor(outlineFinal);
                GL11.glPolygonMode(1032, 6913);
                renderEntity(this.player, this.playerModel, this.player.limbSwing, this.player.limbSwingAmount, (float) this.player.ticksExisted, player.rotationYawHead, this.player.rotationPitch, 1);
                GL11.glPolygonMode(1032, 6914);
                GL11.glPopAttrib();
                NordTessellator.releaseGL();

            }

        }
    }

    double normalize(final double value, final double min, final double max) {
        return (value - min) / (max - min);
    }

    public static void renderEntity(final EntityLivingBase entity, final ModelBase modelBase, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
        if (mc.getRenderManager() == null) {
            return;
        }
        final float partialTicks = mc.getRenderPartialTicks();
        final double x = entity.posX - mc.getRenderManager().viewerPosX;
        double y = entity.posY - mc.getRenderManager().viewerPosY;
        final double z = entity.posZ - mc.getRenderManager().viewerPosZ;
        GlStateManager.pushMatrix();
        if (entity.isSneaking()) {
            y -= 0.125;
        }
        final float interpolateRotation = interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
        final float interpolateRotation2 = interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
        final float rotationInterp = interpolateRotation2 - interpolateRotation;
        final float renderPitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
        renderLivingAt(x, y, z);
        final float f8 = handleRotationFloat(entity, partialTicks);
        prepareRotations(entity);
        final float f9 = prepareScale(entity, scale);
        GlStateManager.enableAlpha();
        modelBase.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
        modelBase.setRotationAngles(limbSwing, limbSwingAmount, f8, entity.rotationYaw, entity.rotationPitch, f9, entity);
        modelBase.render(entity, limbSwing, limbSwingAmount, f8, entity.rotationYaw, entity.rotationPitch, f9);
        GlStateManager.popMatrix();
    }

    public static void prepareTranslate(final EntityLivingBase entityIn, final double x, final double y, final double z) {
        renderLivingAt(x - mc.getRenderManager().viewerPosX, y - mc.getRenderManager().viewerPosY, z - mc.getRenderManager().viewerPosZ);
    }

    public static void renderLivingAt(final double x, final double y, final double z) {
        GlStateManager.translate((float)x, (float)y, (float)z);
    }

    public static float prepareScale(final EntityLivingBase entity, final float scale) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0f, -1.0f, 1.0f);
        final double widthX = entity.getRenderBoundingBox().maxX - entity.getRenderBoundingBox().minX;
        final double widthZ = entity.getRenderBoundingBox().maxZ - entity.getRenderBoundingBox().minZ;
        GlStateManager.scale(scale + widthX, scale * entity.height, scale + widthZ);
        final float f = 0.0625f;
        GlStateManager.translate(0.0f, -1.501f, 0.0f);
        return f;
    }


    public static void prepareRotations(final EntityLivingBase entityLivingBase) {
        GlStateManager.rotate(180.0f - entityLivingBase.rotationYaw, 0.0f, 1.0f, 0.0f);
    }

    public static float interpolateRotation(final float prevYawOffset, final float yawOffset, final float partialTicks) {
        float f;
        for (f = yawOffset - prevYawOffset; f < -180.0f; f += 360.0f) {}
        while (f >= 180.0f) {
            f -= 360.0f;
        }
        return prevYawOffset + partialTicks * f;
    }

    public static Color newAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static void glColor(final Color color) {
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }

    public static float handleRotationFloat(final EntityLivingBase livingBase, final float partialTicks) {
        return 0.0f;
    }
}