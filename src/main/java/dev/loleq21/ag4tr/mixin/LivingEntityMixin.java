package dev.loleq21.ag4tr.mixin;

import dev.loleq21.ag4tr.Ag4tr;
import dev.loleq21.ag4tr.Ag4trContent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.reborn.energy.Energy;

import java.util.Random;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {


    //@Shadow @Nullable public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);
    @Shadow protected abstract int computeFallDamage(float fallDistance, float damageMultiplier);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("HEAD"), method = "handleFallDamage", cancellable = true)
    private void handleFallDamage(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Boolean> info) {
        if (!world.isClient) {
        if((Object) this instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = ((ServerPlayerEntity) ((Object) this));
            ItemStack equippedBootsSlotItemStack = player.getEquippedStack(EquipmentSlot.FEET);
            Item equippedBoots = equippedBootsSlotItemStack.getItem();
            if (equippedBoots == Ag4trContent.RUBBER_BOOTS) {
                if(!isSneaking()) {
                    int vanillaPlayerDamage = this.computeFallDamage(fallDistance, damageMultiplier);
                    int userDamage = vanillaPlayerDamage/3;
                    int bootDamage = (int) Math.round(vanillaPlayerDamage*0.4375);
                    int bootDurability = equippedBootsSlotItemStack.getMaxDamage()-equippedBootsSlotItemStack.getDamage();

                    if (bootDamage>bootDurability){
                        this.damage(DamageSource.FALL, (float)vanillaPlayerDamage);
                        equippedBootsSlotItemStack.decrement(1);                //we do a little trolling
                        player.sendEquipmentBreakStatus(EquipmentSlot.FEET);
                    }
                    if (bootDamage>0){
                        equippedBootsSlotItemStack.damage(bootDamage, new Random(), player);
                    }
                    if (userDamage>0){
                        this.damage(DamageSource.FALL, (float)userDamage);
                    }
                        info.cancel();
                    }
                }
            }
        }
    }
}
