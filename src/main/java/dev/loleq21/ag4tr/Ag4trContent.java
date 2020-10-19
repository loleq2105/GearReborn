package dev.loleq21.ag4tr;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class Ag4trContent {

    public static final ArmorMaterial COMPOSITE_ARMOR_MATERIAL = Ag4trArmorMaterials.COMPOSITE;
    public static final ArmorMaterial RUBBER_ARMOR_MATERIAL = Ag4trArmorMaterials.RUBBER;
    public static final ArmorMaterial MISCGEAR_ARMOR_MATERIAL = Ag4trArmorMaterials.MISCGEAR;
    public static final ArmorMaterial RHM_ARMOR_MATERIAL = Ag4trArmorMaterials.RHM;

    private static final Map<Identifier, Item> AG4TRITEMS = new LinkedHashMap<>();

    public static final Item RUBBER_BOOTS = addItem("rubber_boots", new RubberArmorPiece(RUBBER_ARMOR_MATERIAL, EquipmentSlot.FEET));
    public static final Item RUBBER_CHESTPLATE = addItem("rubber_chestplate",new RubberArmorPiece(RUBBER_ARMOR_MATERIAL, EquipmentSlot.CHEST));
    public static final Item RUBBER_LEGGINGS = addItem("rubber_leggings",new RubberArmorPiece(RUBBER_ARMOR_MATERIAL, EquipmentSlot.LEGS));
    public static final Item RUBBER_HELMET = addItem("rubber_helmet",new RubberArmorPiece(RUBBER_ARMOR_MATERIAL, EquipmentSlot.HEAD));

    public static final Item RHM_CHESTPLATE = addItem("rhm_chestplate",new RHMChestPiece(RHM_ARMOR_MATERIAL, EquipmentSlot.CHEST, 512));
    public static final Item RHM_LEGGINGS = addItem("rhm_leggings",new RHMPeripheralsLol(RHM_ARMOR_MATERIAL, EquipmentSlot.LEGS));
    public static final Item RHM_HELMET = addItem("rhm_helmet",new RHMPeripheralsLol(RHM_ARMOR_MATERIAL, EquipmentSlot.HEAD));

    public static final Item COMPOSITE_ARMOR_PLATE = addItem("composite_armor_plate",new Item(new Item.Settings().group(Ag4tr.AG4TR_GROUP)));
    public static final Item HAZMAT_SHEET = addItem("hazmat_sheet",new Item(new Item.Settings().group(Ag4tr.AG4TR_GROUP)));
    public static final Item REFRIGERATION_UNIT = addItem("refrigeration_unit",new Item(new Item.Settings().group(Ag4tr.AG4TR_GROUP)));
    public static final Item FLAME_RETARDANT = addItem("flame_retardant",new Item(new Item.Settings().group(Ag4tr.AG4TR_GROUP)));

    public static final Item COMPOSITE_CHESTPLATE = addItem("composite_chestplate",new CompositeArmorPiece(COMPOSITE_ARMOR_MATERIAL, EquipmentSlot.CHEST));
    public static final Item NV_GOGGLES = addItem("nv_goggles", new NightvisionGoggles(MISCGEAR_ARMOR_MATERIAL, EquipmentSlot.HEAD));
    //public static final Item EXOLEGS = addItem("exolegs_leggings", new ExoskeletonArmorPiece(MISCGEAR_ARMOR_MATERIAL, EquipmentSlot.LEGS));
    public static final Item FDR_BOOTS = addItem("fdr_boots", new FdrBootsArmorPiece(MISCGEAR_ARMOR_MATERIAL, EquipmentSlot.FEET));
    //public static final Item COMPOSITE_SHIELD = addItem("composite_armor_shield", new Ag4trShieldItem(50, 512, Ag4trContent.COMPOSITE_ARMOR_PLATE));

    private static <I extends Item> I addItem(String name, I item) {
        AG4TRITEMS.put(new Identifier(Ag4tr.MOD_ID, name), item);
        return item;
    }

    public static void registerAg4trContent() {
        for (Identifier id : AG4TRITEMS.keySet()) {
            Registry.register(Registry.ITEM, id, AG4TRITEMS.get(id));
        }
    }
}
