package com.loleq21.gearreborn.mixin;

import com.loleq21.gearreborn.items.hazmat.HazmatSuitUtils;
import com.loleq21.gearreborn.GRContent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import techreborn.init.TRContent;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    protected abstract int computeFallDamage(float fallDistance, float damageMultiplier);

    @Shadow
    protected abstract void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition);

    private void spawnBootParticles(ItemStack stack, float fallDistance) {
        double d = Math.min((double) (0.2F + fallDistance / 15.0F), 2.5D);
        int i = (int) (150.0D * d);
        ((ServerWorld) this.getWorld()).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), this.getX(), this.getY(), this.getZ(), i, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
    }

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    /*
     Code adapted from Kibe
     Available at: https://github.com/lucaargolo/kibe/blob/b437f714dd72a0d77af763a58de3d191c732444e/src/main/java/io/github/lucaargolo/kibe/mixin/LivingEntityMixin.java#L72
     Licensed under the MPL-2.0 license available at: https://tldrlegal.com/license/mozilla-public-license-2.0-(mpl-2)
    */


    @SuppressWarnings("ConstantConditions")
    @Inject(at = @At("HEAD"), method = "handleFallDamage", cancellable = true)
    private void handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> info) {

        if (!getWorld().isClient() && (Object) this instanceof PlayerEntity) {
            PlayerEntity player = ((PlayerEntity) ((Object) this));
            if (player.getEquippedStack(EquipmentSlot.FEET).getItem() == GRContent.RUBBER_BOOTS) {
                ItemStack equippedBootsItemStack = player.getEquippedStack(EquipmentSlot.FEET);
                if (!isSneaking()) {
                    int vanillaPlayerDamage = this.computeFallDamage(fallDistance, damageMultiplier);
                    int userDamage = (int) Math.floor(vanillaPlayerDamage / 8);
                    int bootDamage = (int) Math.ceil(vanillaPlayerDamage * 0.4375); //reference: https://wiki.industrial-craft.net/index.php/Rubber_Boots#Technical_Details
                    if (bootDamage > 0) {
                        spawnBootParticles(TRContent.Parts.RUBBER.getStack(), fallDistance);
                        equippedBootsItemStack.damage(bootDamage, player, (e) -> {
                            e.sendEquipmentBreakStatus(EquipmentSlot.FEET);
                        });
                    }
                    int bootDurability = equippedBootsItemStack.getMaxDamage() - equippedBootsItemStack.getDamage();
                    if (userDamage > 0) {
                        if (bootDurability < 0) {
                            this.damage(damageSource, (float) vanillaPlayerDamage);
                        } else {
                            this.damage(damageSource, (float) userDamage);
                        }
                    }
                    info.cancel();
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "canFreeze", cancellable = true)
    private void canFreeze(CallbackInfoReturnable<Boolean> cir) {

        if ((Object) this instanceof PlayerEntity && HazmatSuitUtils.playerIsWearingFullHazmat((PlayerEntity) (Object) this)) {
            cir.setReturnValue(false);
        }

    }


}
