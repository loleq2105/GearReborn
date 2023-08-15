package com.loleq21.gearreborn.items;

import com.loleq21.gearreborn.GearReborn;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import techreborn.items.armor.TRArmourItem;


public class RubberArmorPiece extends TRArmourItem {

    public RubberArmorPiece(ArmorMaterial material, ArmorItem.Type slot){
        super(material, slot, new Settings().maxCount(1));
        if(slot==Type.BOOTS) {
            GearReborn.hazmatSlotMap.put(slot.getEquipmentSlot(), this);
        }
    }

}
