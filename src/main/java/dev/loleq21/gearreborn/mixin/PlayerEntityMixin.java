package dev.loleq21.gearreborn.mixin;


import dev.loleq21.gearreborn.GRContent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.loleq21.gearreborn.hazmat.HazmatChestPiece.tryConsumeAir;
import static dev.loleq21.gearreborn.hazmat.HazmatSuitUtils.playerIsWearingHazmatBottoms;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    public PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    private boolean hadHazmatHelmet;

    private void disableFireResist() {
        this.removeStatusEffect(StatusEffects.FIRE_RESISTANCE);
    }

    private void disableWaterBreathing() {
        this.removeStatusEffect(StatusEffects.WATER_BREATHING);
    }

    private void removeHazmatEffects() {
        this.removeStatusEffect(StatusEffects.FIRE_RESISTANCE);
        this.removeStatusEffect(StatusEffects.WATER_BREATHING);
    }

    private void updateHazmatSuit() {
        ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
        if (itemStack.isOf(GRContent.HAZMAT_HELMET)) {
            hadHazmatHelmet = true;
            if (this.getEquippedStack(EquipmentSlot.CHEST).getItem() != GRContent.HAZMAT_CHESTPIECE) {
                removeHazmatEffects();
                return;
            }
            PlayerEntity playerEntity = (PlayerEntity) ((Object) this);

            ItemStack hazmatChestplate = playerEntity.getEquippedStack(EquipmentSlot.CHEST);

            if (playerEntity.isSubmergedInWater() && tryConsumeAir(playerEntity, hazmatChestplate)) {
                playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 999999, 0, false, false, true));
            } else {
                disableWaterBreathing();
            }

            if (!playerIsWearingHazmatBottoms(playerEntity)) {
                disableFireResist();
                return;
            }

            if (playerEntity.isOnFire()) {
                playerEntity.extinguish();
            }

            boolean second = world.getTime() % 20 == 0;

            if (second && playerEntity.isInLava()) {
                Iterable<ItemStack> suitPieces = playerEntity.getArmorItems();
                Random random = playerEntity.getRandom();
                for (ItemStack stack : suitPieces) {
                    if (random.nextFloat() < 0.2F) {
                        stack.damage(1, playerEntity, (e) -> {
                            e.sendEquipmentBreakStatus(((ArmorItem) (itemStack.getItem())).getSlotType());
                        });
                    }
                }
            }

            playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 999999, 0, false, false, true));
        } else if (hadHazmatHelmet) {
            removeHazmatEffects();
            hadHazmatHelmet = false;
        }

    }

    @Inject(at = @At("TAIL"), method = "tick()V", cancellable = false)
    public void tick(CallbackInfo ci) {
        this.updateHazmatSuit();
    }

}
