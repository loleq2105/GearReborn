package dev.loleq21.gearreborn.items.hazmat;

import dev.loleq21.gearreborn.GRConfig;
import dev.loleq21.gearreborn.GRContent;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import reborncore.api.items.ArmorBlockEntityTicker;
import techreborn.init.TRContent;

import java.util.List;

import static dev.loleq21.gearreborn.items.hazmat.HazmatSuitUtils.*;

public class HazmatChestPiece extends HazmatArmorPiece implements ArmorBlockEntityTicker {

    public HazmatChestPiece(ArmorMaterial material, ArmorItem.Type slot) {
        super(material,slot);
    }

    static GRConfig config = AutoConfig.getConfigHolder(GRConfig.class).getConfig();

    public static final String AIR_KEY = "air";
    public static final int barColor = 0xFFFFFF;
    public static final int airCapacity = config.hazmatChestpieceAirTicksCapacity;
    public static final boolean degradeHazmat = config.hazmatDegradesInLava;
    public static final float degradeRate = 1.0F/config.hazmatLavaDegradeRate;

    @Override
    public void tickArmor(ItemStack stack, PlayerEntity playerEntity) {

        if (playerEntity.getEquippedStack(EquipmentSlot.HEAD).isOf(GRContent.HAZMAT_HELMET)) {

            if (playerEntity.isSubmergedInWater() && tryConsumeAir(playerEntity, stack)) {
                giveWaterBreathing(playerEntity);
            } else {
                disableWaterBreathing(playerEntity);
            }

            if (!playerIsWearingHazmatBottoms(playerEntity)) {
                disableFireResist(playerEntity);
                return;
            }

            if (playerEntity.isOnFire()) {
                playerEntity.extinguish();
            }

            if (degradeHazmat && playerEntity.isInLava()) {
                Iterable<ItemStack> suitPieces = playerEntity.getArmorItems();
                Random random = playerEntity.getRandom();
                for (ItemStack itemStack : suitPieces) {
                    if (random.nextFloat() <= degradeRate) {
                        itemStack.damage(1, playerEntity, (e) -> {
                            e.sendEquipmentBreakStatus(((ArmorItem) (itemStack.getItem())).getSlotType());
                        });
                    }
                }
            }
            giveFireResist(playerEntity);
        } else {
            removeHazmatEffects(playerEntity);
        }

    }

    public static void onRemoved(PlayerEntity playerEntity){
        removeHazmatEffects(playerEntity);
    }

    private static boolean tryConsumeAir(PlayerEntity playerEntity, ItemStack hazmatChestplate){
        if (tryUseAir(hazmatChestplate, 1)) {
                return true;
        } else {
            if ((getStoredAir(hazmatChestplate) == 0)) {
                for (int i = 0; i < playerEntity.getInventory().size(); i++) {
                    ItemStack iteratedStack = playerEntity.getInventory().getStack(i);
                    if (iteratedStack.getItem() == TRContent.CELL) {
                        if ((TRContent.CELL.getFluid(iteratedStack) == (Fluid) Registries.FLUID.get(new Identifier("techreborn:compressed_air")))) {
                            iteratedStack.decrement(1);
                            ItemStack emptyCell = new ItemStack(TRContent.CELL, 1);
                            playerEntity.giveItemStack(emptyCell);
                            setStoredAir(hazmatChestplate, airCapacity);
                            World world = playerEntity.getEntityWorld();
                            world.playSound(null, playerEntity.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.NEUTRAL, 0.8F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        if(getStoredAirForToolTip(stack)==0) return;

        MutableText line1 = Text.literal(String.valueOf((getStoredAirForToolTip(stack) * 100) / airCapacity));
        line1.append("%");
        line1.append(" ");
        line1.append(Text.translatable("block.techreborn.compressed_air").formatted(Formatting.GRAY));
        tooltip.add(1, line1);

    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        if(!super.isItemBarVisible(stack)&&getStoredAir(stack)>0){
        return Math.round((getStoredAir(stack) * 100f / airCapacity) * 13) / 100;
        } else{
            return super.getItemBarStep(stack);
        }
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        if(!super.isItemBarVisible(stack)&&getStoredAir(stack)>0){
            return barColor;
        } else{
            return super.getItemBarColor(stack);
        }
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        if(getStoredAir(stack)>0){
            return true;
        } else{
            return super.isItemBarVisible(stack);
        }
    }


    private int getStoredAirForToolTip(ItemStack stack) {
        if (stack.hasNbt()) {
            return getStoredAir(stack);
        } else {
            return 0;
        }
    }

    //adapted code from RC's SimpleBatteryItem class.
    //Why all this relentless code copying and private- everything? Well, I don't expect anyone to even consider using the same system as mine, and it does its job.

    public static int getStoredAir(ItemStack stack) {
        return getStoredAirUnchecked(stack);
    }

    private static void setStoredAirUnchecked(ItemStack stack, int newAmount) {
        if (newAmount == 0) {
            stack.removeSubNbt(AIR_KEY);
        } else {
            stack.getOrCreateNbt().putInt(AIR_KEY, newAmount);
        }
    }

    public static void setStoredAir(ItemStack stack, int newAmount) {
        setStoredAirUnchecked(stack, newAmount);
    }

    private static boolean tryUseAir(ItemStack stack, int amount) {

        int newAmount = getStoredAir(stack) - amount;

        if (newAmount < 0) {
            return false;
        } else {
            setStoredAir(stack, newAmount);
            return true;
        }
    }

    public static int getStoredAirUnchecked(ItemStack stack) {
        return getStoredAirUnchecked(stack.getNbt());
    }

    private static int getStoredAirUnchecked(@Nullable NbtCompound nbt) {
        return nbt != null ? nbt.getInt(AIR_KEY) : 0;
    }

    public static int getAirCapacity() {
        GRConfig config = new GRConfig();
        return config.hazmatChestpieceAirTicksCapacity;
    }

    //end of adapted code
}


