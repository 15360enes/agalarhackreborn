package me.earth.phobos.features.modules.render;


import me.earth.phobos.features.modules.*;
import me.earth.phobos.features.setting.*;

public class Ambience extends Module
{
    public final Setting<ColorSetting> colorLight;

    public Ambience() {
        super("Ambience", "\u0438\u0437\u043c\u0435\u043d\u044f\u0435\u0442 \u0446\u0432\u0435\u0442-\u043e\u043a\u0440\u0443\u0436\u0435\u043d\u0438\u044f", Module.Category.RENDER, true, false, false);
        this.colorLight = (Setting<ColorSetting>)this.register(new Setting("Color Light", (T)new ColorSetting(-2013200640)));
    }
}