package dev.loleq21.gearreborn.mixin;;
import dev.loleq21.gearreborn.items.hazmat.HazmatSuitUtils;
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

        if(target instanceof PlayerEntity && HazmatSuitUtils.playerIsWearingFullHazmat((PlayerEntity)target)){
            cir.setReturnValue(false);
        }

    }

}
