package dev.loleq21.gearreborn.mixin;

import dev.loleq21.gearreborn.GRContent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {


    @Shadow protected abstract int computeFallDamage(float fallDistance, float damageMultiplier);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("HEAD"), method = "handleFallDamage", cancellable = true)
    private void handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> info) {
        if (!world.isClient) {
        if((Object) this instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = ((ServerPlayerEntity) ((Object) this));
            ItemStack equippedBootsSlotItemStack = player.getEquippedStack(EquipmentSlot.FEET);
            Item equippedBoots = equippedBootsSlotItemStack.getItem();
            if (equippedBoots == GRContent.RUBBER_BOOTS) {
                if(!isSneaking()) {
                    int vanillaPlayerDamage = this.computeFallDamage(fallDistance, damageMultiplier);
                    int userDamage = vanillaPlayerDamage/3;
                    int bootDamage = (int) Math.round(vanillaPlayerDamage*0.4375); //taken from https://wiki.industrial-craft.net/index.php/Rubber_Boots#Technical_Details
                    int bootDurability = equippedBootsSlotItemStack.getMaxDamage()-equippedBootsSlotItemStack.getDamage();

                    if (bootDamage>bootDurability){
                        this.damage(DamageSource.FALL, (float)vanillaPlayerDamage);
                        equippedBootsSlotItemStack.decrement(1);
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
