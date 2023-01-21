package dev.loleq21.gearreborn.hazmat;

import dev.loleq21.gearreborn.GRConfig;
import dev.loleq21.gearreborn.GRContent;
import dev.loleq21.gearreborn.GearReborn;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import reborncore.api.items.ArmorBlockEntityTicker;
import reborncore.api.items.ArmorRemoveHandler;
import techreborn.init.TRContent;

import static dev.loleq21.gearreborn.hazmat.HazmatChestPiece.*;
import static dev.loleq21.gearreborn.hazmat.HazmatSuitUtils.playerIsWearingHazmatBottoms;

public class HazmatHelmet extends ArmorItem implements ArmorBlockEntityTicker, ArmorRemoveHandler {

    public HazmatHelmet(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Settings().group(GearReborn.ITEMGROUP).maxCount(1).fireproof().maxDamage(-1));
    }

    GRConfig config = AutoConfig.getConfigHolder(GRConfig.class).getConfig();
    public final int airCapacity = config.hazmatChestpieceAirTicksCapacity;


    @Override
    public void tickArmor(ItemStack itemStack, PlayerEntity playerEntity) {

        if(playerEntity.getEntityWorld().isClient){return;}

        if(!((playerEntity.getEquippedStack(EquipmentSlot.HEAD).getItem() == GRContent.HAZMAT_HELMET) && (playerEntity.getEquippedStack(EquipmentSlot.CHEST).getItem() == GRContent.HAZMAT_CHESTPIECE))){
            return;
        }
        ItemStack hazmatChestplate = playerEntity.getEquippedStack(EquipmentSlot.CHEST);

        if (playerEntity.isSubmergedInWater()) {
            if (tryUseAir(hazmatChestplate, 1)) {
                playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 999999, 0, false, false, true));
            } else {
                if ((getStoredAir(hazmatChestplate) == 0)) {
                    for (int i = 0; i < playerEntity.getInventory().size(); i++) {
                        ItemStack iteratedStack = playerEntity.getInventory().getStack(i);
                        if (iteratedStack.getItem() == TRContent.CELL) {
                            if ((TRContent.CELL.getFluid(iteratedStack) == (Fluid) Registry.FLUID.get(new Identifier("techreborn:compressed_air")))) {
                                iteratedStack.decrement(1);
                                ItemStack emptyCell = new ItemStack(TRContent.CELL, 1);
                                playerEntity.giveItemStack(emptyCell);
                                setStoredAir(hazmatChestplate, airCapacity);
                                World world = playerEntity.getEntityWorld();
                                world.playSound(null, playerEntity.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.NEUTRAL, 0.8F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
                                break;
                            }
                        }
                    }
                    disableWaterBreathing(playerEntity);
                }

            }
        } else {
            disableWaterBreathing(playerEntity);
        }

        if (!playerIsWearingHazmatBottoms(playerEntity)){
            disableFireResist(playerEntity);
            return;
        }

        if (playerEntity.isOnFire()) {
            playerEntity.extinguish();
        }

        playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 999999, 0, false, false, true));

    }

    private void disableFireResist(PlayerEntity playerEntity) {
        if (!playerEntity.getEntityWorld().isClient()) {
            playerEntity.removeStatusEffect(StatusEffects.FIRE_RESISTANCE);
        }
    }

    private void disableWaterBreathing(PlayerEntity playerEntity) {
        if (!playerEntity.getEntityWorld().isClient()) {
            playerEntity.removeStatusEffect(StatusEffects.WATER_BREATHING);
        }
    }

    private void removeEffects(PlayerEntity playerEntity) {
        if (!playerEntity.getEntityWorld().isClient()) {
            playerEntity.removeStatusEffect(StatusEffects.FIRE_RESISTANCE);
            playerEntity.removeStatusEffect(StatusEffects.WATER_BREATHING);
        }
    }

    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public void onRemoved(PlayerEntity playerEntity) {
        removeEffects(playerEntity);
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

}


