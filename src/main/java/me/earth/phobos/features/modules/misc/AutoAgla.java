package me.earth.phobos.features.modules.misc;

import me.earth.phobos.event.events.*;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.manager.FileManager;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class AutoAgla extends Module {
    public Map<EntityPlayer, Integer> targets = new ConcurrentHashMap<EntityPlayer, Integer>();
    public EntityPlayer player;

    public AutoAgla() {
        super("AutoAgla", "CTFU Clientten calindi", Category.MISC, true, false, false);
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onEnable() {
        this.loadMessages();
        this.timer.reset();
        this.cooldownTimer.reset();
    }
    private static final String path = "agalarclient/autoagla.txt";
    private final Setting<Boolean> greentext = this.register(new Setting<Boolean>("Greentext", false));
    private final Setting<Boolean> loadFiles = this.register(new Setting<Boolean>("LoadFiles", false));
    private final Setting<Integer> targetResetTimer = this.register(new Setting<Integer>("Reset", 30, 0, 90));
    private final Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 10, 0, 30));
    private final Setting<Boolean> test = this.register(new Setting<Boolean>("Test", false));
    public List<String> messages = new ArrayList<String>();
    private final Timer timer = new Timer();
    private final Timer cooldownTimer = new Timer();
    private boolean cooldown;

    @Override
    public void onTick() {
        if (this.loadFiles.getValue().booleanValue()) {
            this.loadMessages();
            Command.sendMessage("<AutoAgla> Loaded messages.");
            this.loadFiles.setValue(false);
        }
        if (this.test.getValue().booleanValue()) {
            this.announceDeath(AutoAgla.mc.player);
            this.test.setValue(false);
        }
        if (!this.cooldown) {
            this.cooldownTimer.reset();
        }
        if (this.cooldownTimer.passedS(this.delay.getValue().intValue()) && this.cooldown) {
            this.cooldown = false;
            this.cooldownTimer.reset();
        }
        this.targets.replaceAll((p, v) -> (int) (this.timer.getPassedTimeMs() / 1000L));
        for (EntityPlayer player : this.targets.keySet()) {
            if (this.targets.get(player) <= this.targetResetTimer.getValue()) continue;
            this.targets.remove(player);
        }
    }
    @SubscribeEvent
    public void onEntityDeath(DeathEvent event) {
        if (Minecraft.getMinecraft().player.isDead = true) {
            this.announceDeath(event.player);
            this.cooldown = true;
            this.targets.remove(event.player);
        }

    }
    public void loadMessages() {
        this.messages = FileManager.readTextFileAllLines(path);
    }
    public String getRandomMessage() {
        this.loadMessages();
        Random rand = new Random();
        if (this.messages.size() == 0) {
            return "ama yaaaaa";
        }
        if (this.messages.size() == 1) {
            return this.messages.get(0);
        }
        return this.messages.get(MathUtil.clamp(rand.nextInt(this.messages.size()), 0, this.messages.size() - 1));
    }
    public void announceDeath(EntityPlayer target) {
        AutoAgla.mc.player.connection.sendPacket(new CPacketChatMessage((this.greentext.getValue() != false ? ">" : "") + this.getRandomMessage().replaceAll("<player>", target.getDisplayNameString())));
    }

}

