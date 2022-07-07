package me.earth.phobos.features.modules.misc;

import me.earth.phobos.features.modules.*;
import me.earth.phobos.features.setting.*;
import me.earth.phobos.util.*;
import me.earth.phobos.event.events.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.effect.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import net.minecraft.init.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class KillEffect extends Module
{
    private final Setting<Boolean> sound;
    private Timer timer;

    public KillEffect() {
        super("KillEffect", "KillEffect", Category.MISC, true, false, false);
        this.sound = (Setting<Boolean>)this.register(new Setting("Sound", false));
        this.timer = new Timer();
    }

    @SubscribeEvent
    public void onDeath(final DeathEvent event) {
        if (!nullCheck() && event.player != null) {
            final Entity entity = (Entity)event.player;
            if (entity != null && (entity.isDead || (((EntityPlayer)entity).getHealth() <= 0.0f && this.timer.passedMs(1500L)))) {
                KillEffect.mc.world.spawnEntity((Entity)new EntityLightningBolt((World)KillEffect.mc.world, entity.posX, entity.posY, entity.posZ, true));
                if (this.sound.getValue()) {
                    KillEffect.mc.player.playSound(SoundEvents.ENTITY_LIGHTNING_THUNDER, 0.5f, 1.0f);
                }
                this.timer.reset();
            }
        }
    }
}