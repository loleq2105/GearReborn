package dev.loleq21.ag4tr;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import reborncore.api.items.ArmorTickable;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.util.ItemDurabilityExtensions;
import reborncore.common.util.ItemUtils;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHolder;
import team.reborn.energy.EnergyTier;
import techreborn.init.TRContent;
import techreborn.utils.InitUtils;

import java.util.List;

public class RHMChestPiece extends ArmorItem implements ArmorTickable, EnergyHolder, ItemDurabilityExtensions {

    public RHMChestPiece(ArmorMaterial material, EquipmentSlot slot, int airCapacity) {
        super(material, slot, new Settings().group(Ag4tr.AG4TR_GROUP).maxCount(1).maxDamage(-1).fireproof());
        AIR_CAPACITY = airCapacity;
    }

    private final int AIR_CAPACITY;


    @Override
    public void tickArmor(ItemStack itemStack, PlayerEntity playerEntity) {
        boolean canFireFight = playerEntity.getEquippedStack(EquipmentSlot.HEAD).getItem() == Ag4trContent.RHM_HELMET && playerEntity.getEquippedStack(EquipmentSlot.CHEST).getItem() == Ag4trContent.RHM_CHESTPLATE && playerEntity.getEquippedStack(EquipmentSlot.LEGS).getItem() == Ag4trContent.RHM_LEGGINGS;
        boolean canWaterFight = playerEntity.getEquippedStack(EquipmentSlot.HEAD).getItem() == Ag4trContent.RHM_HELMET && playerEntity.getEquippedStack(EquipmentSlot.CHEST).getItem() == Ag4trContent.RHM_CHESTPLATE;

        if (HazmatSuitUtils.playerIsWearingFullHazmat(playerEntity)) {
            if (!playerEntity.isInLava()) {
                playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 5, 1, false, false, false));
            } else {
                if (this.slot == EquipmentSlot.CHEST) {
                    if (Energy.of(itemStack).use(16)) {
                        playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 5, 1, false, false, false));
                    }
                }
            }

            if (playerEntity.isOnFire()) {
                playerEntity.extinguish();
            }
        }


        if (this.slot == EquipmentSlot.CHEST) {
            if ((getStoredAir(itemStack) == 0)) {
                for (int i = 0; i < playerEntity.inventory.size(); i++) {
                    ItemStack iteratedStack = playerEntity.inventory.getStack(i);
                    if (iteratedStack.getItem() == TRContent.CELL) {
                        if (TRContent.CELL.getFluid(iteratedStack) == (Fluid)Registry.FLUID.get(new Identifier("techreborn:compressed_air"))) {
                            iteratedStack.decrement(1);
                            ItemStack emptyCell = new ItemStack(TRContent.CELL, 1);
                            playerEntity.giveItemStack(emptyCell);
                            setStoredAir(itemStack, AIR_CAPACITY);
                        }
                    }
                }
            }
            if (playerEntity.isSubmergedInWater()) {
                /*
                if (!(getStoredAir(itemStack) == AIR_CAPACITY) && Energy.of(itemStack).use(2)) {
                    setStoredAir(itemStack, getStoredAir(itemStack) + 1);
                }
            } else {*/
                if (HazmatSuitUtils.playerIsWearingChestAndHelm(playerEntity)) {
                    if (!(getStoredAir(itemStack) == 0)) {
                        playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 5, 1, false, false, false));
                        setStoredAir(itemStack, getStoredAir(itemStack) - 1);
                    }
                }
            }
        }
    }

    public int getStoredAir(ItemStack stack) {
        if (stack.getItem() == Ag4trContent.RHM_CHESTPLATE) {
            validateAirNBTTag(stack);
            return stack.getTag().getInt("air");
        } else {
            return 0;
        }
    }

    public void setStoredAir(ItemStack stack, int amount) {
        if (stack.getItem() == Ag4trContent.RHM_CHESTPLATE) {
            validateAirNBTTag(stack);
            stack.getTag().putInt("air", amount);
        }
    }

    public boolean useStoredAir(ItemStack stack, int amount) {
        if (stack.getItem() == Ag4trContent.RHM_CHESTPLATE) {
            validateAirNBTTag(stack);
        if(getStoredAir(stack)>=amount) {
            setStoredAir(stack, getStoredAir(stack)-amount);
            return true;
        } else {
            return false;
        }
        } else {
            return  false;
        }
    }

    public boolean addStoredAir(ItemStack stack, int amount) {
        if (stack.getItem() == Ag4trContent.RHM_CHESTPLATE) {
            validateAirNBTTag(stack);
            if(getStoredAir(stack)+amount>AIR_CAPACITY) {
                return false;
            } else {
                setStoredAir(stack, getStoredAir(stack)+amount);
                return true;
            }
        } else {
            return  false;
        }
    }

    public int getAirCapacity() { return AIR_CAPACITY; }

    private void validateAirNBTTag(ItemStack stack) {
        if (!stack.getTag().contains("air", 3)){
            stack.getTag().putInt("air", 0);
        }
    }

    public int getStoredAir4ToolTip(ItemStack stack) {
        if (stack.hasTag()) {
            return stack.getTag().getInt("air");
        }
        else {
            return 0;
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
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        LiteralText line1 = new LiteralText(String.valueOf(getStoredAir4ToolTip(stack)));
        line1.append("/");
        line1.append(String.valueOf(AIR_CAPACITY));
        line1.append(" ");
        line1.append("Air");
        line1.formatted(Formatting.GOLD);
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
}


