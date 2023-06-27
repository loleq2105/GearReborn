package dev.loleq21.gearreborn.items.hazmat;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import techreborn.items.armor.TRArmourItem;

public class HazmatArmorPiece extends TRArmourItem {

    public HazmatArmorPiece(ArmorMaterial material, ArmorItem.Type slot) {
        super(material, slot, new Settings().maxCount(1).fireproof());
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
