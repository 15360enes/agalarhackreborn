package me.earth.phobos.mixin.mixins.accessors;

import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderer.class)
public interface AccessorEntityRenderer {
    @Invoker
    void invokesetupCameraTransform(float partialTicks, int Pass);

}