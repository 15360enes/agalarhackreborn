package me.earth.phobos.features.modules.render;

import me.earth.phobos.features.modules.*;
import me.earth.phobos.features.setting.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.*;
import java.awt.*;

public class CustomFog extends Module
{
    public final Setting<Integer> timeSetting;
    private final Setting<Boolean> color;
    private final Setting<Integer> red;
    private final Setting<Integer> green;
    private final Setting<Integer> blue;
    private final Setting<Integer> red1;
    private final Setting<Integer> green1;
    private final Setting<Integer> blue1;
    private final Setting<Integer> red2;
    private final Setting<Integer> green2;
    private final Setting<Integer> blue2;
    private static CustomFog INSTANCE;

    public CustomFog() {
        super("CustomFog", "CustomFog", Module.Category.RENDER, true, false, false);
        this.timeSetting = (Setting<Integer>)this.register(new Setting("Time", (Object)12000, (Object)0, (Object)23000));
        this.color = (Setting<Boolean>)this.register(new Setting("Color", (Object)false));
        this.red = (Setting<Integer>)this.register(new Setting("Red", (Object)255, (Object)0, (Object)255, v -> (boolean)this.color.getValue()));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (Object)255, (Object)0, (Object)255, v -> (boolean)this.color.getValue()));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (Object)255, (Object)0, (Object)255, v -> (boolean)this.color.getValue()));
        this.red1 = (Setting<Integer>)this.register(new Setting("NetherRed", (Object)255, (Object)0, (Object)255, v -> (boolean)this.color.getValue()));
        this.green1 = (Setting<Integer>)this.register(new Setting("NetherGreen", (Object)255, (Object)0, (Object)255, v -> (boolean)this.color.getValue()));
        this.blue1 = (Setting<Integer>)this.register(new Setting("NetherBlue", (Object)255, (Object)0, (Object)255, v -> (boolean)this.color.getValue()));
        this.red2 = (Setting<Integer>)this.register(new Setting("EndRed", (Object)255, (Object)0, (Object)255, v -> (boolean)this.color.getValue()));
        this.green2 = (Setting<Integer>)this.register(new Setting("EndGreen", (Object)255, (Object)0, (Object)255, v -> (boolean)this.color.getValue()));
        this.blue2 = (Setting<Integer>)this.register(new Setting("EndBlue", (Object)255, (Object)0, (Object)255, v -> (boolean)this.color.getValue()));
    }

    public static CustomFog getInstance() {
        if (CustomFog.INSTANCE == null) {
            CustomFog.INSTANCE = new CustomFog();
        }
        return CustomFog.INSTANCE;
    }

    private void setInstance() {
        CustomFog.INSTANCE = this;
    }



    @SubscribeEvent
    public void fog(final EntityViewRenderEvent.FogColors event) {
        if (this.color.getValue()) {
            if (CustomFog.mc.player.dimension == 0) {
                event.setRed((int)this.red.getValue() / 255.0f);
                event.setGreen((int)this.green.getValue() / 255.0f);
                event.setBlue((int)this.blue.getValue() / 255.0f);
            }
            else if (CustomFog.mc.player.dimension == -1) {
                event.setRed((int)this.red1.getValue() / 255.0f);
                event.setGreen((int)this.green1.getValue() / 255.0f);
                event.setBlue((int)this.blue1.getValue() / 255.0f);
            }
            else if (CustomFog.mc.player.dimension == 1) {
                event.setRed((int)this.red2.getValue() / 255.0f);
                event.setGreen((int)this.green2.getValue() / 255.0f);
                event.setBlue((int)this.blue2.getValue() / 255.0f);
            }
        }
    }

    @SubscribeEvent
    public void fog_density(final EntityViewRenderEvent.FogDensity event) {
        event.setDensity(0.0f);
        event.setCanceled(true);
    }

    static {
        CustomFog.INSTANCE = new CustomFog();
    }
}
