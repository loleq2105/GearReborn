package dev.loleq21.gearreborn.items;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import techreborn.items.armor.TRArmourItem;


public class RubberArmorPiece extends TRArmourItem {

    public RubberArmorPiece(ArmorMaterial material, ArmorItem.Type slot){
        super(material, slot, new Settings().maxCount(1));
    }

}
