package dev.loleq21.gearreborn;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;

public class HazmatSuitUtils {

    public static boolean playerIsWearingFullHazmat(PlayerEntity playerEntity) {
        return playerEntity.getEquippedStack(EquipmentSlot.HEAD).getItem() == GRContent.HAZMAT_HELMET && playerEntity.getEquippedStack(EquipmentSlot.CHEST).getItem() == GRContent.HAZMAT_CHESTPIECE && playerEntity.getEquippedStack(EquipmentSlot.LEGS).getItem() == GRContent.HAZMAT_LEGGINGS && playerEntity.getEquippedStack(EquipmentSlot.FEET).getItem() == GRContent.RUBBER_BOOTS;
    }

    public static boolean playerIsWearingChestAndHelm(PlayerEntity playerEntity) {
        return playerEntity.getEquippedStack(EquipmentSlot.HEAD).getItem() == GRContent.HAZMAT_HELMET && playerEntity.getEquippedStack(EquipmentSlot.CHEST).getItem() == GRContent.HAZMAT_CHESTPIECE;
    }

}
