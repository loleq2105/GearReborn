package dev.loleq21.gearreborn.hazmat;

import dev.loleq21.gearreborn.GRConfig;
import dev.loleq21.gearreborn.GearReborn;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import techreborn.init.TRContent;

import java.util.List;

public class HazmatChestPiece extends ArmorItem {

    public HazmatChestPiece(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Settings().group(GearReborn.ITEMGROUP).maxCount(1).fireproof());
    }

    static GRConfig config = AutoConfig.getConfigHolder(GRConfig.class).getConfig();

    public static final String AIR_KEY = "air";
    public static final int barColor = 0xFFFFFF;
    public static final int airCapacity = config.hazmatChestpieceAirTicksCapacity;

    public static boolean tryConsumeAir(PlayerEntity playerEntity, ItemStack hazmatChestplate){
        if (tryUseAir(hazmatChestplate, 1)) {
                return true;
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
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        MutableText line1 = Text.literal(String.valueOf((getStoredAirForToolTip(stack) * 100) / airCapacity));
        line1.append("%");
        line1.append(" ");
        line1.append(Text.translatable("gearreborn.misc.hazmatairpressure"));
        line1.formatted(Formatting.AQUA);
        tooltip.add(1, line1);
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return Math.round((getStoredAir(stack) * 100f / airCapacity) * 13) / 100;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return barColor;
    }

    @Override
    public boolean isDamageable() {
        return true;
    }


    protected int getStoredAirForToolTip(ItemStack stack) {
        if (stack.hasNbt()) {
            return getStoredAir(stack);
        } else {
            return 0;
        }
    }

    //adapted code from RC's SimpleBatteryItem class

    protected static int getStoredAir(ItemStack stack) {
        return getStoredAirUnchecked(stack);
    }

    protected static void setStoredAirUnchecked(ItemStack stack, int newAmount) {
        if (newAmount == 0) {
            // Make sure newly crafted ~~energy~~ air containers stack with emptied ones.
            stack.removeSubNbt(AIR_KEY);
        } else {
            stack.getOrCreateNbt().putInt(AIR_KEY, newAmount);
        }
    }

    protected static void setStoredAir(ItemStack stack, int newAmount) {
        setStoredAirUnchecked(stack, newAmount);
    }

    protected static boolean tryUseAir(ItemStack stack, int amount) {

        int newAmount = getStoredAir(stack) - amount;

        if (newAmount < 0) {
            return false;
        } else {
            setStoredAir(stack, newAmount);
            return true;
        }
    }

    protected static int getStoredAirUnchecked(ItemStack stack) {
        return getStoredAirUnchecked(stack.getNbt());
    }

    protected static int getStoredAirUnchecked(@Nullable NbtCompound nbt) {
        return nbt != null ? nbt.getInt(AIR_KEY) : 0;
    }


}


