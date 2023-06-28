package dev.loleq21.gearreborn.items.hazmat;

import dev.loleq21.gearreborn.GRContent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

public final class HazmatSuitUtils {

    private HazmatSuitUtils(){ }

    public static boolean playerIsWearingHazmatBottoms(PlayerEntity playerEntity) {
        return playerEntity.getEquippedStack(EquipmentSlot.LEGS).getItem() == GRContent.HAZMAT_LEGGINGS && playerEntity.getEquippedStack(EquipmentSlot.FEET).getItem() == GRContent.RUBBER_BOOTS;
    }

    public static boolean playerIsWearingChestAndHelm(PlayerEntity playerEntity) {
        return playerEntity.getEquippedStack(EquipmentSlot.HEAD).getItem() == GRContent.HAZMAT_HELMET && playerEntity.getEquippedStack(EquipmentSlot.CHEST).getItem() == GRContent.HAZMAT_CHESTPIECE;
    }

    public static boolean playerIsWearingFullHazmat(PlayerEntity playerEntity) {
        return playerEntity.getEquippedStack(EquipmentSlot.HEAD).getItem() == GRContent.HAZMAT_HELMET && playerEntity.getEquippedStack(EquipmentSlot.CHEST).getItem() == GRContent.HAZMAT_CHESTPIECE && playerEntity.getEquippedStack(EquipmentSlot.LEGS).getItem() == GRContent.HAZMAT_LEGGINGS && playerEntity.getEquippedStack(EquipmentSlot.FEET).getItem() == GRContent.RUBBER_BOOTS;
    }

    public static void disableFireResist(LivingEntity entity) {
        entity.removeStatusEffect(StatusEffects.FIRE_RESISTANCE);
    }

    public static void disableWaterBreathing(LivingEntity entity) {
        entity.removeStatusEffect(StatusEffects.WATER_BREATHING);
    }

    public static void giveFireResist(LivingEntity entity) {
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 240, 0, false, false, true));
    }

    public static void giveWaterBreathing(LivingEntity entity) {
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 240, 0, false, false, true));

    }

    public static void removeHazmatEffects(LivingEntity entity) {
        entity.removeStatusEffect(StatusEffects.FIRE_RESISTANCE);
        entity.removeStatusEffect(StatusEffects.WATER_BREATHING);
    }

}
