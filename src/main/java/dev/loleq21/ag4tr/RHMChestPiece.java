package dev.loleq21.ag4tr;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import reborncore.api.items.ArmorTickable;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.util.ItemDurabilityExtensions;
import reborncore.common.util.ItemUtils;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHolder;
import team.reborn.energy.EnergyTier;
import techreborn.utils.InitUtils;

public class RHMChestPiece extends ArmorItem implements ArmorTickable, EnergyHolder, ItemDurabilityExtensions {

    public RHMChestPiece(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Settings().group(Ag4tr.AG4TR_GROUP).maxCount(1).maxDamage(-1).fireproof());
    }

    @Override
    public void tickArmor(ItemStack itemStack, PlayerEntity playerEntity) {
        boolean canFireFight = playerEntity.getEquippedStack(EquipmentSlot.HEAD).getItem() == Ag4trContent.RHM_HELMET && playerEntity.getEquippedStack(EquipmentSlot.CHEST).getItem() == Ag4trContent.RHM_CHESTPLATE && playerEntity.getEquippedStack(EquipmentSlot.LEGS).getItem() == Ag4trContent.RHM_LEGGINGS;
        boolean canWaterFight = playerEntity.getEquippedStack(EquipmentSlot.HEAD).getItem() == Ag4trContent.RHM_HELMET && playerEntity.getEquippedStack(EquipmentSlot.CHEST).getItem() == Ag4trContent.RHM_CHESTPLATE;

        if (canFireFight) {
            if (!playerEntity.isInLava()) {
                playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 5, 1, false, false, false));
            } else {
                if (this.slot == EquipmentSlot.CHEST) {
                    if (Energy.of(itemStack).use(32)) {
                        playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 5, 1, false, false, false));
                    }
                }
            }

            if (playerEntity.isOnFire()) {
                playerEntity.extinguish();
            }
        }
            if (playerEntity.isSubmergedInWater()) {
                   if (canWaterFight && this.slot == EquipmentSlot.CHEST) {
                       if (Energy.of(itemStack).use(8)) {
                           playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 5, 1, false, false, false));
                       }
                   }
                }
            }

    @Override
    public double getDurability(ItemStack stack) {
        return 1 - ItemUtils.getPowerForDurabilityBar(stack);
    }

    @Override
    public boolean showDurability(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getDurabilityColor(ItemStack stack) {
        return PowerSystem.getDisplayPower().colour;
    }

    @Override
    public double getMaxStoredPower() {
        return 20000;
    }

    @Override
    public EnergyTier getTier() {
        return EnergyTier.MEDIUM;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> itemList) {
        if (!isIn(group)) {
            return;
        }
        InitUtils.initPoweredItems(this, itemList);
    }
}


