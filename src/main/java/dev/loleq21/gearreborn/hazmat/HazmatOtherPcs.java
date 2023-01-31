package dev.loleq21.gearreborn.hazmat;

import dev.loleq21.gearreborn.GearReborn;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;

public class HazmatOtherPcs extends ArmorItem {

    public HazmatOtherPcs(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Settings().group(GearReborn.ITEMGROUP).maxCount(1).fireproof());
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
