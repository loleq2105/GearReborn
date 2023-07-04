package com.loleq21.gearreborn.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import reborncore.common.powerSystem.RcEnergyItem;
import reborncore.common.powerSystem.RcEnergyTier;
import reborncore.common.util.ItemUtils;
import techreborn.items.tool.WrenchItem;

public class ElectricWrenchItem extends WrenchItem implements RcEnergyItem {

    public boolean isDamageable() {
        return false;
    }

    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false;
    }

    public int getItemBarStep(ItemStack stack) {
        return ItemUtils.getPowerForDurabilityBar(stack);
    }

    public int getItemBarColor(ItemStack stack) {
        return ItemUtils.getColorForDurabilityBar(stack);
    }

    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public long getEnergyCapacity() {
        return 10000;
    }

    @Override
    public RcEnergyTier getTier() {
        return RcEnergyTier.MEDIUM;
    }

    public long getEnergyMaxOutput() {
        return 0L;
    }

    @Override
    public boolean handleTool(ItemStack stack, BlockPos pos, World world, PlayerEntity player, Direction side, boolean damage) {
        if (!player.getWorld().isClient && this.getStoredEnergy(stack) > 50.0) {
            this.tryUseEnergy(stack, (long) 5);
            return true;
        } else {
            return false;
        }
    }
}