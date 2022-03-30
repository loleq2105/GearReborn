package dev.loleq21.gearreborn.mixin;

import dev.loleq21.gearreborn.GRContent;
import net.minecraft.block.Block;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
public abstract class PowderSnowBlockMixin extends Block implements FluidDrainable {

    public PowderSnowBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "canWalkOnPowderSnow", cancellable = true)
        private static void canWalkOnPowderSnow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if(entity instanceof LivingEntity && ((LivingEntity)entity).getEquippedStack(EquipmentSlot.FEET).isOf(GRContent.RUBBER_BOOTS))  cir.setReturnValue(true);
    }

}
