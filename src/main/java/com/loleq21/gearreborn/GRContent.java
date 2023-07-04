package com.loleq21.gearreborn;

import com.loleq21.gearreborn.items.*;
import com.loleq21.gearreborn.items.hazmat.HazmatArmorPiece;
import com.loleq21.gearreborn.items.hazmat.HazmatChestPiece;
import com.loleq21.gearreborn.items.hazmat.HazmatEmptyingRecipe;
import com.loleq21.gearreborn.items.hazmat.HazmatFillingRecipe;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import techreborn.init.TRToolMaterials;

import java.util.LinkedHashMap;
import java.util.Map;

public class GRContent {

    public static final ArmorMaterial RUBBER_ARMOR_MATERIAL = GRArmorMaterials.RUBBER;
    public static final ArmorMaterial MISCGEAR_ARMOR_MATERIAL = GRArmorMaterials.MISCGEAR;
    public static final ArmorMaterial HAZMAT_ARMOR_MATERIAL = GRArmorMaterials.HAZMAT;

    private static final Map<Identifier, Item> GRITEMS = new LinkedHashMap<>();

    public static final RecipeSerializer<HazmatEmptyingRecipe> CRAFTING_HAZMAT_EMPTY = Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(GearReborn.MOD_ID, "hazmat_empty"), new SpecialRecipeSerializer<>(HazmatEmptyingRecipe::new));
    public static final RecipeSerializer<HazmatFillingRecipe> CRAFTING_HAZMAT_FILL = Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(GearReborn.MOD_ID, "hazmat_fill"), new SpecialRecipeSerializer<>(HazmatFillingRecipe::new));


    public static final Item NV_GOGGLES = addItem("nv_goggles", new NightVisionGoggles(MISCGEAR_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
    public static final Item STUN_GUN = addItem("stun_gun", new StunGunItem());

    public static final Item HAZMAT_HELMET = addItem("hazmat_helmet", new HazmatArmorPiece(HAZMAT_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
    public static final Item HAZMAT_CHESTPIECE = addItem("hazmat_chestpiece", new HazmatChestPiece(HAZMAT_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE));
    public static final Item HAZMAT_LEGGINGS = addItem("hazmat_leggings", new HazmatArmorPiece(HAZMAT_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS));

    public static final Item RUBBER_BOOTS = addItem("rubber_boots", new RubberArmorPiece(RUBBER_ARMOR_MATERIAL, ArmorItem.Type.BOOTS));

    public static final Item RUBBER_HELMET = addItem("rubber_helmet", new RubberArmorPiece(RUBBER_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
    public static final Item RUBBER_CHESTPLATE = addItem("rubber_chestplate", new RubberArmorPiece(RUBBER_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE));
    public static final Item RUBBER_LEGGINGS = addItem("rubber_leggings", new RubberArmorPiece(RUBBER_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS));

    public static final Item ELECTRIC_WRENCH = addItem("electric_wrench", new ElectricWrenchItem());

    public static final Item ELECTRIC_HOE = addItem("electric_hoe", new ElectricHoeItem(TRToolMaterials.BASIC_DRILL, 0, 0, new FabricItemSettings().maxCount(1).maxDamage(-1)));
    private static <I extends Item> I addItem(String name, I item) {
        GRITEMS.put(new Identifier(GearReborn.MOD_ID, name), item);
        return item;
    }

    public static void registerGearRebornContent() {
        for (Identifier id : GRITEMS.keySet()) {
            Registry.register(Registries.ITEM, id, GRITEMS.get(id));
        }
    }
}
