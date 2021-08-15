package dev.loleq21.gearreborn;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import reborncore.api.items.ArmorBlockEntityTicker;
import reborncore.api.items.ArmorRemoveHandler;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.util.ItemDurabilityExtensions;
import reborncore.common.util.ItemUtils;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHolder;
import team.reborn.energy.EnergyTier;
import techreborn.init.TRContent;
import techreborn.utils.InitUtils;

import java.util.List;

import static dev.loleq21.gearreborn.HazmatSuitUtils.playerIsWearingChestAndHelm;
import static dev.loleq21.gearreborn.HazmatSuitUtils.playerIsWearingFullHazmat;

public class HazmatChestPiece extends DyeableArmorItem implements ArmorBlockEntityTicker, EnergyHolder, ItemDurabilityExtensions, ArmorRemoveHandler {

    public HazmatChestPiece(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Settings().group(GearReborn.ITEMGROUP).maxCount(1).fireproof().maxDamage(-1));
    }

    GRConfig config = AutoConfig.getConfigHolder(GRConfig.class).getConfig();

    public final int airCapacity = config.hazmatChestpieceAirTicksCapacity;
    public final double energyCapacity = config.hazmatChestpieceEnergyCapacity;
    public final double coolingEnergyCost = config.hazmatChestpieceLavaCoolingEnergyCost;
    public final double airCanSwapEnergyCost = config.hazmatChestpieceCellSwapEnergyCost;

    @Override
    public void tickArmor(ItemStack itemStack, PlayerEntity playerEntity) {
        if(!playerEntity.getEntityWorld().isClient()) {
            if (playerIsWearingFullHazmat(playerEntity)) {
                if (!playerEntity.isInLava()) {
                    playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 999999, 0, false, false, false));
                } else {
                        if (Energy.of(itemStack).use(coolingEnergyCost)) {
                            playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 999999, 0, false, false, false));
                        } else {
                            disableFireResist(playerEntity);
                        }
                }

                if (playerEntity.isOnFire() && Energy.of(itemStack).getEnergy() >= coolingEnergyCost * 2) {
                    playerEntity.extinguish();
                }
            } else {
                disableFireResist(playerEntity);
            }

                if ((getStoredAir(itemStack) == 0)) {
                    for (int i = 0; i < playerEntity.getInventory().size(); i++) {
                        ItemStack iteratedStack = playerEntity.getInventory().getStack(i);
                        if (iteratedStack.getItem() == TRContent.CELL) {
                            if ((TRContent.CELL.getFluid(iteratedStack) == (Fluid) Registry.FLUID.get(new Identifier("techreborn:compressed_air"))) && Energy.of(itemStack).use(airCanSwapEnergyCost)) {
                                iteratedStack.decrement(1);
                                ItemStack emptyCell = new ItemStack(TRContent.CELL, 1);
                                playerEntity.giveItemStack(emptyCell);
                                setStoredAir(itemStack, airCapacity);
                            }
                        }
                    }
                }
                if (playerIsWearingChestAndHelm(playerEntity)) {
                    if (playerEntity.isSubmergedInWater()) {
                        if (useStoredAir(itemStack, 1)) {
                            playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 999999, 0, false, false, false));
                        } else {
                            disableWaterBreathing(playerEntity);
                        }
                    } else {
                        disableWaterBreathing(playerEntity);
                    }
                }
                else {
                    disableWaterBreathing(playerEntity);
                }
        }
    }

    private void disableFireResist(PlayerEntity playerEntity){
        if (!playerEntity.getEntityWorld().isClient()) {
            playerEntity.removeStatusEffect(StatusEffects.FIRE_RESISTANCE);
        }
    }

    private void disableWaterBreathing(PlayerEntity playerEntity){
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
    
    public int getStoredAir(ItemStack stack) {
        if (stack.getItem() == GRContent.HAZMAT_CHESTPIECE) {
            validateAirNbtTag(stack);
            return stack.getNbt().getInt("air");
        }
            return 0;
    }

    public void setStoredAir(ItemStack stack, int amount) {
        if (stack.getItem() == GRContent.HAZMAT_CHESTPIECE) {
            validateAirNbtTag(stack);
            stack.getNbt().putInt("air", amount);
        }
    }

    public boolean useStoredAir(ItemStack stack, int amount) {
        if (stack.getItem() == GRContent.HAZMAT_CHESTPIECE) {
            validateAirNbtTag(stack);
        if(getStoredAir(stack)>=amount) {
            setStoredAir(stack, getStoredAir(stack)-amount);
            return true;
        }
            return false;
        }
            return  false;
    }

    public boolean addStoredAir(ItemStack stack, int amount) {
        if (stack.getItem() == GRContent.HAZMAT_CHESTPIECE) {
            validateAirNbtTag(stack);
            if(getStoredAir(stack)+amount>airCapacity) {
                return false;
            } else {
                setStoredAir(stack, getStoredAir(stack)+amount);
                return true;
            }
        }
            return  false;
    }

    public int getAirCapacity() { return airCapacity; }

    private void validateAirNbtTag(ItemStack stack) {
        GRConfig config = AutoConfig.getConfigHolder(GRConfig.class).getConfig();
        if (!stack.getNbt().contains("air", 3)){
            stack.getNbt().putInt("air", 0);
            return;
        }
        if (stack.getNbt().getInt("air")>config.hazmatChestpieceAirTicksCapacity) {
            stack.getNbt().putInt("air", config.hazmatChestpieceAirTicksCapacity);
        }
    }

    public int getStoredAir4ToolTip(ItemStack stack) {
        if (stack.hasNbt()) {
            return stack.getNbt().getInt("air");
        }
        else {
            return 0;
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public double getMaxStoredPower() {
        return energyCapacity;
    }

    @Override
    public EnergyTier getTier() {
        return EnergyTier.MEDIUM;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        LiteralText line1 = new LiteralText(String.valueOf((getStoredAir4ToolTip(stack)*100)/airCapacity));
        line1.append("%");
        line1.append(" ");
        line1.append(new TranslatableText("gearreborn.misc.hazmatairpressure"));
        line1.formatted(Formatting.AQUA);
        tooltip.add(1, line1);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> itemList) {
        if (!isIn(group)) {
            return;
        }
        InitUtils.initPoweredItems(this, itemList);
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
    public int getDurabilityColor(ItemStack stack) {
        return PowerSystem.getDisplayPower().colour;
    }

    @Override
    public void onRemoved(PlayerEntity playerEntity) {
        removeEffects(playerEntity);
    }

    @Override
    public int getColor(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubNbt("display");
        return nbtCompound != null && nbtCompound.contains("color", 99) ? nbtCompound.getInt("color") : GearReborn.DEFAULT_HAZMAT_COLOR;
    }
}


