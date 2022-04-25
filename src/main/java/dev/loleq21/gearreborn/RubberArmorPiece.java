package dev.loleq21.gearreborn;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;


public class RubberArmorPiece extends ArmorItem {

    public RubberArmorPiece(ArmorMaterial material, EquipmentSlot slot){
        super(material, slot, new Settings().group(GearReborn.ITEMGROUP).maxCount(1));
    }

}
