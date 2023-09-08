package com.loleq21.gearreborn.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface CooldownItem
{
    float getCooldownProgress(PlayerEntity player, World world, ItemStack stack, float tickDelta);
}