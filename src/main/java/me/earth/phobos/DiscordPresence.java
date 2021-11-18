package me.earth.phobos;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.earth.phobos.features.modules.misc.RPC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;

public class DiscordPresence {
    private static final DiscordRPC rpc;
    public static DiscordRichPresence presence;
    private static Thread thread;
    private static int index;
    private static final Minecraft mc = Minecraft.getMinecraft();

    static {
        rpc = DiscordRPC.INSTANCE;
        presence = new DiscordRichPresence();
    }

    public static void start() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        rpc.Discord_Initialize("910141516356411412", handlers, true, "");
        DiscordPresence.presence.startTimestamp = System.currentTimeMillis() / 1000L;
        DiscordPresence.presence.details = Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu ? "In the main menu." : "Playing " + (Minecraft.getMinecraft().currentServerData != null ? (RPC.INSTANCE.showIP.getValue().booleanValue() ? "on " + Minecraft.getMinecraft().currentServerData.serverIP + "." : " multiplayer.") : " singleplayer.");
        DiscordPresence.presence.state = RPC.INSTANCE.state.getValue();
        DiscordPresence.presence.largeImageKey = "agalar";
        DiscordPresence.presence.largeImageText = Agalar.MODNAME + " " + Agalar.MODVER;
        DiscordPresence.presence.joinSecret = "MTI4NzM0OjFpMmhuZToxMjMxMjM= ";
        DiscordPresence.presence.partyId = "ae488379-351d-4a4f-ad32-2b9b01c91657";
        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                rpc.Discord_RunCallbacks();
                DiscordPresence.presence.details = Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu ? "In the main menu." : "Playing " + (Minecraft.getMinecraft().currentServerData != null ? (RPC.INSTANCE.showIP.getValue().booleanValue() ? "on " + Minecraft.getMinecraft().currentServerData.serverIP + "." : " multiplayer.") : " singleplayer.");
                DiscordPresence.presence.state = RPC.INSTANCE.state.getValue();
                if (RPC.INSTANCE.lokummode.getValue().booleanValue()) {
                    if (index == 8) {
                        index = 1;
                    }
                    DiscordPresence.presence.largeImageKey = "lokum" + index;
                    ++index;
                }
                rpc.Discord_UpdatePresence(presence);
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException interruptedException) {
                }
            }
        }, "RPC-Callback-Handler");
        thread.start();
    }

    public static void stop() {
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }
        rpc.Discord_Shutdown();
    }
}