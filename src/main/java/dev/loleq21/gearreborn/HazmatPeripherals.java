package dev.loleq21.gearreborn;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;

public class HazmatPeripherals extends ArmorItem {

    public HazmatPeripherals(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Settings().maxCount(1).fireproof().maxDamage(-1));
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

}
