package dev.loleq21.gearreborn;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Lazy;
import techreborn.init.TRContent;

import java.util.Locale;
import java.util.function.Supplier;

public enum GRArmorMaterials implements ArmorMaterial {

    RUBBER("rubber", 5, new int[]{2, 3, 3, 2}, 10, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0F, 0F, () -> {
        return Ingredient.ofItems(TRContent.Parts.RUBBER.asItem());
    }),
    MISCGEAR("miscgear", 15, new int[]{0, 3, 4, 1}, 9, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0F, 0.0F, () -> {
        return Ingredient.ofItems(TRContent.Ingots.REFINED_IRON);
    }),
    HAZMAT("hazmat", 8, new int[]{2, 2, 2, 2}, 9, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0F, 0.0F, () -> {
        return Ingredient.ofItems(TRContent.Parts.RUBBER.asItem());
    })
    ;

    private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
    private final String name;
    private final int durabilityMultiplier;
    private final int[] protectionAmounts;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Lazy<Ingredient> repairIngredientSupplier;

    GRArmorMaterials(String name, int durabilityMultiplier, int[] protectionAmounts, int enchantability, SoundEvent equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredientSupplier) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionAmounts = protectionAmounts;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredientSupplier = new Lazy(repairIngredientSupplier);
    }

    public int getDurability(EquipmentSlot slot) {
        return BASE_DURABILITY[slot.getEntitySlotId()] * this.durabilityMultiplier;
    }

    public int getProtectionAmount(EquipmentSlot slot) {
        return this.protectionAmounts[slot.getEntitySlotId()];
    }

    public int getEnchantability() {
        return this.enchantability;
    }

    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    public Ingredient getRepairIngredient() {
        return (Ingredient)this.repairIngredientSupplier.get();
    }

    public String getName() {
        return this.name;
    }

    public float getToughness() {
        return this.toughness;
    }

    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
