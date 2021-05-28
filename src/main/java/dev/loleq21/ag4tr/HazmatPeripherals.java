package dev.loleq21.ag4tr;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;

public class HazmatPeripherals extends ArmorItem {

    public HazmatPeripherals(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Settings().group(Ag4tr.AG4TR_GROUP).maxCount(1).fireproof());
    }

}
