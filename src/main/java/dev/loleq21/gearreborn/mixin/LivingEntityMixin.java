package dev.loleq21.gearreborn.mixin;

import dev.loleq21.gearreborn.GRContent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import techreborn.init.TRContent;

import java.util.Random;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow protected abstract int computeFallDamage(float fallDistance, float damageMultiplier);


    private void spawnBootParticles(ItemStack stack, float fallDistance) {
        double d = Math.min((double)(0.2F + fallDistance / 15.0F), 2.5D);
        int i = (int)(150.0D * d);
        ((ServerWorld)this.world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), this.getX(), this.getY(), this.getZ(), i, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
    }

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("HEAD"), method = "handleFallDamage", cancellable = true)
    private void handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> info) {

        if ((Object) this instanceof PlayerEntity) {
            if (!world.isClient()) {
                PlayerEntity player = ((PlayerEntity) ((Object) this));
                if (player.getEquippedStack(EquipmentSlot.FEET).getItem() == GRContent.RUBBER_BOOTS) {
                    ItemStack equippedBootsItemStack = player.getEquippedStack(EquipmentSlot.FEET);
                    if (!isSneaking()) {
                        int vanillaPlayerDamage = this.computeFallDamage(fallDistance, damageMultiplier);
                        int userDamage = vanillaPlayerDamage / 3;
                        int bootDamage = (int) Math.round(vanillaPlayerDamage * 0.4375); //taken from https://wiki.industrial-craft.net/index.php/Rubber_Boots#Technical_Details
                        int bootDurability = equippedBootsItemStack.getMaxDamage() - equippedBootsItemStack.getDamage();
                        if (bootDamage > bootDurability) {
                            this.damage(DamageSource.FALL, (float) vanillaPlayerDamage);
                            equippedBootsItemStack.decrement(1);
                            player.sendEquipmentBreakStatus(EquipmentSlot.FEET);
                        }
                        if (bootDamage > 0) {
                            equippedBootsItemStack.damage(bootDamage, new Random(), (ServerPlayerEntity) player);
                            spawnBootParticles(TRContent.Parts.RUBBER.getStack(), fallDistance);
                        }
                        if (userDamage > 0) {
                            this.damage(DamageSource.FALL, (float) userDamage);
                        }
                        info.cancel();
                    }
                }
            }
        }
    }
}
