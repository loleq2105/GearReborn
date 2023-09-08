package com.loleq21.gearreborn.mixin;

import com.loleq21.gearreborn.items.CooldownItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getItemCooldownManager()Lnet/minecraft/entity/player/ItemCooldownManager;"),
            method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
    private void renderStunGunCooldownOverlay(TextRenderer textRenderer, @NotNull ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {

        //var mc = MinecraftClient.getInstance();
        if (!stack.isEmpty() && stack.getItem() instanceof CooldownItem && client.currentScreen == null)
        {
            var clientPlayerEntity = this.client.player;
            var f = clientPlayerEntity == null ? 0.0F : ((CooldownItem)stack.getItem()).getCooldownProgress(clientPlayerEntity, clientPlayerEntity.getWorld(), stack, client.getTickDelta());
            if (f > 0.0F)
            {
                //RenderSystem.disableDepthTest();
                ((DrawContext)(Object)this).fill(RenderLayer.getGuiOverlay(), x, y + MathHelper.floor(16.0F * (1.0F - f)), x + 16, y + 16, Integer.MAX_VALUE);
                //RenderSystem.enableDepthTest();
            }
        }

    }

}