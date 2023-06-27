package dev.loleq21.gearreborn;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;


public class RubberArmorPiece extends ArmorItem {

    public RubberArmorPiece(ArmorMaterial material, ArmorItem.Type slot){
        super(material, slot, new Settings().maxCount(1));
    }

}
