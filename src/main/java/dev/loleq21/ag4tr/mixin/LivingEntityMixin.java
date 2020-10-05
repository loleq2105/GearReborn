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


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {


    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("HEAD"), method = "handleFallDamage", cancellable = true)
    private void handleFallDamage(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Boolean> info) {
        if((Object) this instanceof PlayerEntity) {
            PlayerEntity player = ((PlayerEntity) ((Object) this));
            //TODO make the rubber boots degrade durability... somehow...
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
                    int ayeer = MathHelper.ceil((fallDistance - 3.0F - f) * damageMultiplier);
                    int powerRequiredToNullDmg = ayeer*32;
                    if (ayeer>0 && Energy.of(equeepedBootsItmStck).getEnergy()>=powerRequiredToNullDmg) {
                        Energy.of(equeepedBootsItmStck).use(powerRequiredToNullDmg);
                        if (!world.isClient) {
                            info.cancel();
                        }
                    }
                    if (ayeer>0 && Energy.of(equeepedBootsItmStck).getEnergy()<powerRequiredToNullDmg) {
                        int howMuchMoreEnergyBootsShouldve2NullDmgCompletely = powerRequiredToNullDmg-(int)Energy.of(equeepedBootsItmStck).getEnergy();
                        Energy.of(equeepedBootsItmStck).use(Energy.of(equeepedBootsItmStck).getEnergy());
                        float damageThatTheBootsWerentAbleToNullify = howMuchMoreEnergyBootsShouldve2NullDmgCompletely/32;
                        this.damage(DamageSource.FALL, damageThatTheBootsWerentAbleToNullify);
                        if (!world.isClient) {
                            info.cancel();
                        }
                    }
                    //this doesn't work for some reason...
                    //this.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1f, 1f);

                }

            }
        }
    }
}
