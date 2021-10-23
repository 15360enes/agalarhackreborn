package me.earth.phobos.mixin.mixins.accessors;

import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ EntityLivingBase.class })
public interface IEntityLivingBase
{
    @Invoker("getArmSwingAnimationEnd")
    int getArmSwingAnimationEnd();
}
