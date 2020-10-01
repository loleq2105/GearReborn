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
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.reborn.energy.Energy;

import java.util.Random;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {


    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("HEAD"), method = "handleFallDamage", cancellable = true)
    private void handleFallDamage(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Boolean> info) {
        if((Object) this instanceof PlayerEntity) {
            PlayerEntity player = ((PlayerEntity) ((Object) this));
            //TODO make the rubber boots degreade durability
            ItemStack equeepedBootsItmStck = player.getEquippedStack(EquipmentSlot.FEET);
            Item equpeedBoots = equeepedBootsItmStck.getItem();
            if (equpeedBoots == Ag4trContent.RUBBER_BOOTS) {
                if(!world.isClient && !isSneaking()) {
                    StatusEffectInstance statusEffectInstance = player.getStatusEffect(StatusEffects.JUMP_BOOST);
                    float f = statusEffectInstance == null ? 0.0F : (float)(statusEffectInstance.getAmplifier() + 1);
                    int aye = MathHelper.ceil((fallDistance - 4.0F - f) * damageMultiplier);
                    if (aye>0){
                        this.damage(DamageSource.FALL, (float)aye);
                    }
                    info.cancel();
                }
            }
            if (equpeedBoots == Ag4trContent.FDR_BOOTS) {
                if(!world.isClient && !isSneaking()) {
                    StatusEffectInstance statusEffectInstance = player.getStatusEffect(StatusEffects.JUMP_BOOST);
                    float f = statusEffectInstance == null ? 0.0F : (float)(statusEffectInstance.getAmplifier() + 1);
                    int ayeer = MathHelper.ceil((fallDistance - 4.0F - f) * damageMultiplier);
                    if (ayeer>0 && Energy.of(equeepedBootsItmStck).getEnergy()>=ayeer*4) {
                        Energy.of(equeepedBootsItmStck).use(ayeer*4);
                        info.cancel();
                    }
                    if (ayeer>0 && Energy.of(equeepedBootsItmStck).getEnergy()<ayeer*4) {
                        int damageTITHTBHWTCELR = ayeer*4-(int)Energy.of(equeepedBootsItmStck).getEnergy();
                        Energy.of(equeepedBootsItmStck).use(Energy.of(equeepedBootsItmStck).getEnergy());
                        this.damage(DamageSource.FALL, (float)damageTITHTBHWTCELR/4);
                        info.cancel();
                    }

                }
            }
        }
    }
}
