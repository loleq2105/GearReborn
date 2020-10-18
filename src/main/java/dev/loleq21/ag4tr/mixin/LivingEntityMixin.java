package dev.loleq21.ag4tr.mixin;

import dev.loleq21.ag4tr.Ag4trContent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
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
            ItemStack equeepedBootsItmStck = player.getEquippedStack(EquipmentSlot.FEET);
            Item equpeedBoots = equeepedBootsItmStck.getItem();
            if (equpeedBoots == Ag4trContent.RUBBER_BOOTS) {
                if(!world.isClient && !isSneaking()) {
                    StatusEffectInstance statusEffectInstance = player.getStatusEffect(StatusEffects.JUMP_BOOST);
                    float f = statusEffectInstance == null ? 0.0F : (float)(statusEffectInstance.getAmplifier() + 1);
                    int vanillaPlayerDamage = MathHelper.ceil((fallDistance - 3.0F - f) * damageMultiplier);
                    int userDamage = Math.round(vanillaPlayerDamage/2);
                    int bootDamage = Math.round(vanillaPlayerDamage*2);
                    int bootDurability = equeepedBootsItmStck.getMaxDamage()-equeepedBootsItmStck.getDamage();
                    if (bootDamage>bootDurability){
                        this.damage(DamageSource.FALL, (float)Math.round(vanillaPlayerDamage-(bootDurability/2)));
                        equeepedBootsItmStck.decrement(1);
                        player.addCritParticles(player);
                    }
                    if (bootDamage>0){
                        equeepedBootsItmStck.damage(vanillaPlayerDamage-1, new Random((long)Math.random()), null);
                    }
                    if (userDamage>0){
                        this.damage(DamageSource.FALL, (float)userDamage);
                    }
                    if (!world.isClient) {
                        info.cancel();
                    }
                }
            }
            if (equpeedBoots == Ag4trContent.FDR_BOOTS) {
                if(!world.isClient && !isSneaking()) {
                    StatusEffectInstance statusEffectInstance = player.getStatusEffect(StatusEffects.JUMP_BOOST);
                    float f = statusEffectInstance == null ? 0.0F : (float)(statusEffectInstance.getAmplifier() + 1);
                    int vanillaFallDamage = MathHelper.ceil((fallDistance - 3.0F - f) * damageMultiplier);
                    int powerRequiredToNullDmg = vanillaFallDamage*32;
                    if (vanillaFallDamage>0 && Energy.of(equeepedBootsItmStck).use(powerRequiredToNullDmg)) {
                        if (!world.isClient) {
                            info.cancel();
                        }
                    }
                    if (vanillaFallDamage>0 && Energy.of(equeepedBootsItmStck).getEnergy()<powerRequiredToNullDmg) {
                        int howMuchMoreEnergyBootsShouldve2NullDmgCompletely = powerRequiredToNullDmg-(int)Energy.of(equeepedBootsItmStck).getEnergy();
                        Energy.of(equeepedBootsItmStck).use(Energy.of(equeepedBootsItmStck).getEnergy());
                        float damageThatTheBootsWerentAbleToNullify = Math.round(howMuchMoreEnergyBootsShouldve2NullDmgCompletely/32);
                        this.damage(DamageSource.FALL, damageThatTheBootsWerentAbleToNullify);
                        player.addCritParticles(player);
                        if (!world.isClient) {
                            info.cancel();
                        }
                    }
                }
            }
        }
    }
}
