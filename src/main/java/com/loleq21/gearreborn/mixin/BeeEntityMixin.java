package com.loleq21.gearreborn.mixin;;
import com.loleq21.gearreborn.components.GRComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeeEntity.class)
public abstract class BeeEntityMixin {

    @Inject(at = @At("HEAD"), method = "tryAttack", cancellable = true)
    public void tryAttack(Entity target, CallbackInfoReturnable<Boolean> cir) {

        if(target instanceof PlayerEntity && GRComponents.HAZMAT_COMPONENT_KEY.get(target).isWearingFullSet()){
            cir.setReturnValue(false);
        }

    }

}
