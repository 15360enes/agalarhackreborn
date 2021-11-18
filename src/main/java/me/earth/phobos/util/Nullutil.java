package me.earth.phobos.util;

import me.earth.phobos.Agalar;

public class Nullutil {

    public static boolean fullNullCheck() {
        return Agalar.INSTANCE.mc.player == null || Agalar.INSTANCE.mc.world == null;
    }
}