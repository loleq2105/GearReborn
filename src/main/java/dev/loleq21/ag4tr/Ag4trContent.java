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
    public static final ArmorMaterial DIVING_ARMOR_MATERIAL = Ag4trArmorMaterials.DIVING;
    public static final ArmorMaterial MISCGEAR_ARMOR_MATERIAL = Ag4trArmorMaterials.MISCGEAR;

    private static final Map<Identifier, Item> AG4TRITEMS = new LinkedHashMap<>();

    public static final Item RUBBER_BOOTS = addItem("rubber_boots", new RubberArmorPiece(RUBBER_ARMOR_MATERIAL, EquipmentSlot.FEET));
    public static final Item RUBBER_CHESTPLATE = addItem("rubber_chestplate",new RubberArmorPiece(RUBBER_ARMOR_MATERIAL, EquipmentSlot.CHEST));
    public static final Item RUBBER_LEGGINGS = addItem("rubber_leggings",new RubberArmorPiece(RUBBER_ARMOR_MATERIAL, EquipmentSlot.LEGS));
    public static final Item RUBBER_HELMET = addItem("rubber_helmet",new RubberArmorPiece(RUBBER_ARMOR_MATERIAL, EquipmentSlot.HEAD));

    public static final Item COMPOSITE_ARMOR_PLATE = addItem("composite_armor_plate",new Item(new Item.Settings().group(ItemGroup.MISC)));
    public static final Item COMPOSITE_CHESTPLATE = addItem("composite_chestplate",new CompositeArmorPiece(COMPOSITE_ARMOR_MATERIAL, EquipmentSlot.CHEST));

    public static final Item NV_GOGGLES = addItem("nv_goggles", new NightvisionGoggles(MISCGEAR_ARMOR_MATERIAL, EquipmentSlot.HEAD));
    public static final Item EXOLEGS = addItem("exolegs_leggings", new ExoskeletonArmorPiece(MISCGEAR_ARMOR_MATERIAL, EquipmentSlot.LEGS));
    public static final Item REBREATHER = addItem("rebreather", new DivingArmorPiece(DIVING_ARMOR_MATERIAL, EquipmentSlot.HEAD));

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
