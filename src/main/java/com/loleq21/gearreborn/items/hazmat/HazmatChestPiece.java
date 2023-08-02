package com.loleq21.gearreborn.items.hazmat;

import com.loleq21.gearreborn.GRConfig;
import com.loleq21.gearreborn.GRContent;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import reborncore.api.items.ArmorBlockEntityTicker;
import reborncore.api.items.ArmorRemoveHandler;
import techreborn.init.ModFluids;
import techreborn.init.TRContent;

import java.util.List;
import java.util.Optional;

public class HazmatChestPiece extends HazmatArmorPiece implements ArmorBlockEntityTicker, ArmorRemoveHandler {

    public HazmatChestPiece(ArmorMaterial material, ArmorItem.Type slot) {
        super(material, slot);
    }

    static GRConfig config = AutoConfig.getConfigHolder(GRConfig.class).getConfig();

    public static final String AIR_KEY = "air";
    public static final int airCapacity = config.hazmatChestpieceAirTicksCapacity;
    public static final boolean degradeHazmat = config.hazmatDegradesInLava;
    public static final float degradeRate = 1.0F / config.hazmatLavaDegradeRate;

    @Override
    public void tickArmor(ItemStack stack, PlayerEntity user) {

        if (user.getEquippedStack(EquipmentSlot.CHEST)!=stack)
            return;

        if (!user.getEquippedStack(EquipmentSlot.HEAD).isOf(GRContent.HAZMAT_HELMET))
            return;

        World world = user.getEntityWorld();

        if (user.isSubmergedInWater() && tryConsumeAir(user, stack)) {
            int airCells = 0;
            for (int i = 0; i < user.getInventory().size(); i++) {
                ItemStack iteratedStack = user.getInventory().getStack(i);
                if (iteratedStack.getItem() == TRContent.CELL) {
                    if (TRContent.CELL.getFluid(iteratedStack) == ModFluids.COMPRESSED_AIR.getFluid()) {
                        airCells += iteratedStack.getCount();
                    }
                }
            }
            int waterBreathingTicks = airCells*airCapacity+getStoredAir(stack);
            user.setAir(Math.min(user.getAir() + 4, user.getMaxAir()));
            if (!world.isClient()) {
                user.setStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, waterBreathingTicks, 0, false, false, true), null);
            }
        } else {
            HazmatSuitUtils.disableWaterBreathing(user, world);
        }

        if (!HazmatSuitUtils.playerIsWearingHazmatBottoms(user)) {
            return;
        }

        if (degradeHazmat && user.isInLava()) {
            Random random = user.getRandom();
            if (random.nextFloat() <= degradeRate) {
                user.getInventory().damageArmor(user.getDamageSources().generic(), 1.0F, PlayerInventory.ARMOR_SLOTS);
            }
        }

        user.extinguish();

    }

    @Override
    public void onRemoved(PlayerEntity playerEntity) {
        if (!(playerEntity instanceof ServerPlayerEntity))
            return;
        HazmatSuitUtils.removeHazmatEffects(playerEntity);

    }

    private static boolean tryConsumeAir(PlayerEntity playerEntity, ItemStack hazmatChestplate) {
        if (tryUseAir(hazmatChestplate, 1)) {
            return true;
        } else {
                for (int i = 0; i < playerEntity.getInventory().size(); i++) {
                    ItemStack iteratedStack = playerEntity.getInventory().getStack(i);
                    if (iteratedStack.getItem() == TRContent.CELL) {
                        if (TRContent.CELL.getFluid(iteratedStack) == ModFluids.COMPRESSED_AIR.getFluid()) {
                            iteratedStack.decrement(1);
                            playerEntity.giveItemStack(new ItemStack(TRContent.CELL));
                            setStoredAir(hazmatChestplate, airCapacity);
                            World world = playerEntity.getEntityWorld();
                            world.playSound(null, playerEntity.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.NEUTRAL, 0.8F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
                            return true;
                        }
                    }
                }
            return false;
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        boolean shiftDown = Screen.hasShiftDown();
        int storedAir = getStoredAirForToolTip(stack);

        if (!shiftDown && storedAir == 0) return;

        int airTicks;
        int airCells = 0;

        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
        for (int i = 0; i < clientPlayerEntity.getInventory().size(); i++) {
            ItemStack iteratedStack = clientPlayerEntity.getInventory().getStack(i);
            if (iteratedStack.getItem() == TRContent.CELL) {
                if (TRContent.CELL.getFluid(iteratedStack) == ModFluids.COMPRESSED_AIR.getFluid()) {
                    airCells += iteratedStack.getCount();
                }
            }
        }

        if (!shiftDown || storedAir == 0) {
            airTicks = airCells * airCapacity + storedAir;
        } else {
            airTicks = storedAir;
        }

        if(airTicks==0) return;

        MutableText line = Text.literal(StringHelper.formatTicks(airTicks));
        line.append(" ");
        line.append(Formatting.GRAY + I18n.translate("block.techreborn.compressed_air"));
        tooltip.add(line);

        if (shiftDown && airCells != 0) {
            if (storedAir != 0) {
                tooltip.add(Text.literal(Formatting.DARK_GRAY + I18n.translate("gearreborn.tooltip.hazmat_chestpiece.in_item")));
            } else {
                tooltip.add(Text.literal(Formatting.DARK_GRAY + I18n.translate("gearreborn.tooltip.hazmat_chestpiece.in_inventory")));
            }
        }

    }

    private int getStoredAirForToolTip(ItemStack stack) {
        if (stack.hasNbt()) {
            return getStoredAir(stack);
        } else {
            return 0;
        }
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        return super.getTooltipData(stack);
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


