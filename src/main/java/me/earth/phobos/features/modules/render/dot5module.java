package me.earth.phobos.features.modules.render;

import me.earth.phobos.event.events.Render2DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.Components;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class dot5module extends Module {

    public dot5module() {
        super("dot5module", "topsekret", Category.RENDER, true, false, false);
    }

    public static final ResourceLocation logo = new ResourceLocation("textures/vhakan.png");
    public static final ResourceLocation logo2 = new ResourceLocation("textures/vhakan.png");
    public Setting<Boolean> leftlogo = this.register(new Setting<>("Left", false));
    public Setting<Integer> leftWidth = this.register(new Setting<Object>("LeftWidth", 100, -1000, 1000, v -> this.leftlogo.getValue()));
    public Setting<Integer> leftHeight = this.register(new Setting<Object>("LeftHeight", 100, -1000, 1000, v -> this.leftlogo.getValue()));
    public Setting<Boolean> rightlogo = this.register(new Setting<>("Right", false));
    public Setting<Integer> rightWidth = this.register(new Setting<Object>("RightWidth", 100, -1000, 1000, v -> this.rightlogo.getValue()));
    public Setting<Integer> rightHeight = this.register(new Setting<Object>("RightHeight", 100, -1000, 1000, v -> this.rightlogo.getValue()));


    @Override
    public void onRender2D(Render2DEvent event) {
        if (dot5module.fullNullCheck()) {
            return;
        }
        if (leftlogo.getValue()) {
            drawleftlogo();
            return;
        }
        if (rightlogo.getValue()) {
            drawrightlogo();
            return;
        }
    }

    public void drawrightlogo() {
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        mc.getTextureManager().bindTexture(logo2);
        Components.drawCompleteImage(764, 230, this.rightHeight.getValue(), this.rightWidth.getValue());
        mc.getTextureManager().deleteTexture(logo2);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
    }

    public void drawleftlogo() {
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        mc.getTextureManager().bindTexture(logo);
        Components.drawCompleteImage(60, 230, this.leftHeight.getValue(), this.leftWidth.getValue());
        mc.getTextureManager().deleteTexture(logo);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
    }
}
