package com.loleq21.gearreborn.items.hazmat;

import com.loleq21.gearreborn.GearReborn;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import techreborn.items.armor.TRArmourItem;

public class HazmatArmorPiece extends TRArmourItem {

    public HazmatArmorPiece(ArmorMaterial material, ArmorItem.Type slot) {
        super(material, slot, new Settings().maxCount(1).fireproof());
        GearReborn.hazmatSlotMap.put(slot.getEquipmentSlot(), this);
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

}
