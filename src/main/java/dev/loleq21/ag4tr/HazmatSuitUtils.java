package dev.loleq21.ag4tr;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class HazmatSuitUtils {

    public static boolean playerIsWearingFullHazmat(PlayerEntity playerEntity) {
        return playerEntity.getEquippedStack(EquipmentSlot.HEAD).getItem() == Ag4trContent.HAZMAT_HELMET && playerEntity.getEquippedStack(EquipmentSlot.CHEST).getItem() == Ag4trContent.HAZMAT_CHESTPLATE && playerEntity.getEquippedStack(EquipmentSlot.LEGS).getItem() == Ag4trContent.HAZMAT_LEGGINGS && playerEntity.getEquippedStack(EquipmentSlot.FEET).getItem() == Ag4trContent.RUBBER_BOOTS;
    }

    public static boolean playerIsWearingChestAndHelm(PlayerEntity playerEntity) {
        return playerEntity.getEquippedStack(EquipmentSlot.HEAD).getItem() == Ag4trContent.HAZMAT_HELMET && playerEntity.getEquippedStack(EquipmentSlot.CHEST).getItem() == Ag4trContent.HAZMAT_CHESTPLATE;
    }

}
