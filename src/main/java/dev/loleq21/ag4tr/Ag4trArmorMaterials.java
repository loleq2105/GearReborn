package dev.loleq21.ag4tr;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Lazy;
import techreborn.init.TRContent;

import java.util.Locale;
import java.util.function.Supplier;

public enum Ag4trArmorMaterials implements ArmorMaterial {

    RUBBER(5, new int[]{1, 3, 3, 2}, 10, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F, 0F, () -> {
        return Ingredient.ofItems(TRContent.Parts.RUBBER.asItem());
    }),
    MISCGEAR(15, new int[]{1, 3, 4, 1}, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> {
        return Ingredient.ofItems(TRContent.Ingots.REFINED_IRON);
    }),
    HAZMAT(15, new int[]{2, 3, 4, 2}, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> {
        return Ingredient.ofItems(Ag4trContent.HAZMAT_SHEET);
    })
    ;

    private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
    private final int durabilityMultiplier;
    private final int[] protectionArray;
    private final int enchantability;
    private final SoundEvent soundEvent;
    private final float toughness;
    private final float kbResist;
    private final Lazy<Ingredient> repairMaterial;

    Ag4trArmorMaterials(int durMultiplier, int[] protArray, int ench,
                        SoundEvent soundEv, float toughne, float knockyKnocky, Supplier<Ingredient> repairMaterialIn) {
        this.durabilityMultiplier = durMultiplier;
        this.protectionArray = protArray;
        this.enchantability = ench;
        this.soundEvent = soundEv;
        this.toughness = toughne;
        this.kbResist = knockyKnocky;
        this.repairMaterial = new Lazy<>(repairMaterialIn);

    }

    @Override
    public int getDurability(EquipmentSlot slot) {
        return BASE_DURABILITY[slot.getEntitySlotId()] * durabilityMultiplier;
    }

    @Override
    public int getProtectionAmount(EquipmentSlot slot) {
        return protectionArray[slot.getEntitySlotId()];
    }

    @Override
    public int getEnchantability() {
        return enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return soundEvent;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairMaterial.get();
    }

    @Override
    public String getName() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return kbResist;
    }
}
