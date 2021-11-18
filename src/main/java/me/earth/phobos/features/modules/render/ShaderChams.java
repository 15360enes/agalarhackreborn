package me.earth.phobos.features.modules.render;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.shader.shaders.*;
import me.earth.phobos.util.Nullutil;
import me.earth.phobos.shader.FramebufferShader;
import me.earth.phobos.util.MathUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class ShaderChams extends Module {

    static ShaderChams INSTANCE = new ShaderChams();
    public Setting <Object> mode = this.register(new Setting <Object> ( "Mode" , modes.Smoke ) );
    public Setting <Boolean> playerOnly = this.register(new Setting <> ( "playerOnly" , false) );
    public Setting <Boolean> alwaysGlow = this.register(new Setting <> ( "alwaysGlow" , false) );

    public
    ShaderChams( ) {
        super ( "ShaderChams" , "getvalueenum" , Module.Category.RENDER , false , false , false );
        INSTANCE = this;
    }

    public static ShaderChams getInstance() {
        return null;
    }

    public enum modes {
        Smoke,
        Aqua,
        Flow,
        Red,
        Outline,
        Rainbow,
        Star,
        RainbowStar,
        Galaxy,
        IIV,
        Cloud,
        BlueSpace,
        Aurora,
        Custom,
        Hamburger }
}