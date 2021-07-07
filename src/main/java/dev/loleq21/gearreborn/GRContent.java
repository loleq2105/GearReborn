package dev.loleq21.gearreborn;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class GRContent {

    public static final ArmorMaterial RUBBER_ARMOR_MATERIAL = GRArmorMaterials.RUBBER;
    public static final ArmorMaterial MISCGEAR_ARMOR_MATERIAL = GRArmorMaterials.MISCGEAR;
    public static final ArmorMaterial HAZMAT_ARMOR_MATERIAL = GRArmorMaterials.HAZMAT;

    private static final Map<Identifier, Item> GRITEMS = new LinkedHashMap<>();

    public static final Item RUBBER_HELMET = addItem("rubber_helmet", new RubberArmorPiece(RUBBER_ARMOR_MATERIAL, EquipmentSlot.HEAD));
    public static final Item RUBBER_CHESTPLATE = addItem("rubber_chestplate", new RubberArmorPiece(RUBBER_ARMOR_MATERIAL, EquipmentSlot.CHEST));
    public static final Item RUBBER_LEGGINGS = addItem("rubber_leggings", new RubberArmorPiece(RUBBER_ARMOR_MATERIAL, EquipmentSlot.LEGS));
    public static final Item RUBBER_BOOTS = addItem("rubber_boots", new RubberArmorPiece(RUBBER_ARMOR_MATERIAL, EquipmentSlot.FEET));

    public static final Item HAZMAT_HELMET = addItem("hazmat_helmet", new HazmatPeripherals(HAZMAT_ARMOR_MATERIAL, EquipmentSlot.HEAD));
    public static final Item HAZMAT_CHESTPIECE = addItem("hazmat_chestpiece", new HazmatChestPiece(HAZMAT_ARMOR_MATERIAL, EquipmentSlot.CHEST));
    public static final Item HAZMAT_LEGGINGS = addItem("hazmat_leggings", new HazmatPeripherals(HAZMAT_ARMOR_MATERIAL, EquipmentSlot.LEGS));

    public static final Item NV_GOGGLES = addItem("nv_goggles", new NightvisionGoggles(MISCGEAR_ARMOR_MATERIAL, EquipmentSlot.HEAD));
    public static final Item TASER = addItem("stun_gun", new StunGunItem());

    private static <I extends Item> I addItem(String name, I item) {
        GRITEMS.put(new Identifier(GearReborn.MOD_ID, name), item);
        return item;
    }

    public static void registerGearRebornContent() {
        for (Identifier id : GRITEMS.keySet()) {
            Registry.register(Registry.ITEM, id, GRITEMS.get(id));
        }
    }
}
