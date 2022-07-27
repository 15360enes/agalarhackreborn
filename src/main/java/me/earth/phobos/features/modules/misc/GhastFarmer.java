package me.earth.phobos.features.modules.misc;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.init.Items;

public class GhastFarmer
        extends Module {
    public int StartingX;
    public int StartingY;
    public int StartingZ;
    public int posx;
    public int posy;
    public int posz;
    public int itemX;
    public int itemY;
    public int itemZ;
    public int ghastX;
    public int ghastY;
    public int ghastZ;

    public GhastFarmer() {
        super("GhastFarmer", "Helps you find ghasts", Module.Category.MISC, true, false, false);
    }

    @Override
    public void onEnable() {
        block3: {
            block2: {
                if (GhastFarmer.mc.player == null) break block2;
                if (GhastFarmer.mc.world != null) break block3;
            }
            return;
        }
        this.StartingX = (int)GhastFarmer.mc.player.posX;
        this.StartingY = (int)GhastFarmer.mc.player.posY;
        this.StartingZ = (int)GhastFarmer.mc.player.posZ;
    }

    @Override
    public void onDisable() {
        block3: {
            block2: {
                if (GhastFarmer.mc.player == null) break block2;
                if (GhastFarmer.mc.world != null) break block3;
            }
            return;
        }
        GhastFarmer.mc.player.sendChatMessage("#stop");
    }

    @Override
    public void onUpdate() {
        if (GhastFarmer.mc.player == null || GhastFarmer.mc.world == null) {
            return;
        }
        Entity ghastEnt = null;
        double dist = Double.longBitsToDouble(Double.doubleToLongBits(0.017520017079696953) ^ 0x7FC8F0C47187D7FBL);
        for (Entity entity : GhastFarmer.mc.world.loadedEntityList) {
            double ghastDist;
            if (!(entity instanceof EntityGhast) || !((ghastDist = (double)GhastFarmer.mc.player.getDistance(entity)) < dist)) continue;
            dist = ghastDist;
            ghastEnt = entity;
            this.ghastX = (int)entity.posX;
            this.ghastY = (int)entity.posY;
            this.ghastZ = (int)entity.posZ;
        }
        ArrayList entityItems = new ArrayList();
        entityItems.addAll(GhastFarmer.mc.world.loadedEntityList.stream().filter(GhastFarmer::lambda$onUpdate$0).map(GhastFarmer::lambda$onUpdate$1).filter(GhastFarmer::lambda$onUpdate$2).collect(Collectors.toList()));
        Entity itemEnt = null;
        Iterator iterator = entityItems.iterator();
        while (iterator.hasNext()) {
            Entity item;
            itemEnt = item = (Entity)iterator.next();
            this.itemX = (int)item.posX;
            this.itemY = (int)item.posY;
            this.itemZ = (int)item.posZ;
        }
        posx = (int) mc.player.posX;
        posy = (int) mc.player.posY;
        posz = (int) mc.player.posZ;
        if (ghastEnt != null) {
            Command.sendMessage("Ghast kesiyom.");
            GhastFarmer.mc.player.sendChatMessage("#goto " + this.ghastX + " " + this.ghastY + " " + this.ghastZ);
        } else if (itemEnt != null) {
            Command.sendMessage("Esyalari aliyom.");
            GhastFarmer.mc.player.sendChatMessage("#goto " + this.itemX + " " + this.itemY + " " + this.itemZ);
        } else {
            Command.sendMessage("Geri donuyom.");
            GhastFarmer.mc.player.sendChatMessage("#goto " + this.StartingX + " " + this.StartingY + " " + this.StartingZ);
        }
    }

    public static boolean lambda$onUpdate$2(EntityItem entityItem) {
        EntityItem entityItem2 = null;
        return entityItem2.getItem().getItem() == Items.GHAST_TEAR;
    }

    public static EntityItem lambda$onUpdate$1(Entity entity) {
        Entity entity2 = null;
        return (EntityItem)entity2;
    }

    public static boolean lambda$onUpdate$0(Entity entity) {
        Entity entity2 = null;
        return entity2 instanceof EntityItem;
    }
}