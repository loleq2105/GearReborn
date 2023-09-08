package com.loleq21.gearreborn.items.hazmat;

import com.loleq21.gearreborn.GRConfig;
import com.loleq21.gearreborn.GRContent;
import com.loleq21.gearreborn.components.HazmatComponent;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import reborncore.api.items.ArmorBlockEntityTicker;
import reborncore.api.items.ArmorRemoveHandler;
import techreborn.init.ModFluids;
import techreborn.init.TRContent;

import java.util.List;

import static com.loleq21.gearreborn.GRConfig.CONFIG;
import static com.loleq21.gearreborn.components.GRComponents.HAZMAT_COMPONENT_KEY;
import static com.loleq21.gearreborn.items.hazmat.HazmatAirUtil.*;

public class HazmatChestPiece extends HazmatArmorPiece implements ArmorBlockEntityTicker, ArmorRemoveHandler{

    public HazmatChestPiece(ArmorMaterial material, ArmorItem.Type slot) {
        super(material, slot);
    }

    public static final int airCapacity = CONFIG.hazmatChestpieceAirTicksCapacity;

    @Override
    public void tickArmor(ItemStack stack, PlayerEntity user) {

        World world = user.getEntityWorld();

        if (world.isClient()) return;

        HazmatComponent hazmatInfo = HAZMAT_COMPONENT_KEY.get(user);

        if (user.isSubmergedInWater() && hazmatInfo.canDive() && tryConsumeAir(user, stack)) {
            int airCells = 0;
            for (int i = 0; i < user.getInventory().size(); i++) {
                ItemStack iteratedStack = user.getInventory().getStack(i);
                if (iteratedStack.getItem() == TRContent.CELL) {
                    if (TRContent.CELL.getFluid(iteratedStack) == ModFluids.COMPRESSED_AIR.getFluid()) {
                        airCells += iteratedStack.getCount();
                    }
                }
            }
            if (!hazmatInfo.wasDiving() || airCells != hazmatInfo.getLastCells()) {
                int waterBreathingTicks = airCells * airCapacity + getStoredAir(stack);
                user.setStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, waterBreathingTicks, 0, false, false, true), null);
                hazmatInfo.setLastCells(airCells);
            }
            user.setAir(Math.min(user.getAir() + 4, user.getMaxAir()));
            hazmatInfo.setWasDiving(true);
        } else {
            removeDivingEffect(user, hazmatInfo);
        }

//        if (degradeHazmat && user.isInLava()) {
//            Random random = user.getRandom();
//            if (random.nextFloat() <= degradeRate) {
//                user.getInventory().damageArmor(user.getDamageSources().generic(), 1.0F, PlayerInventory.ARMOR_SLOTS);
//            }
//        }

        if (!hazmatInfo.isWearingFullSet()) return;

        user.extinguish();

    }

    @Override
    public void onRemoved(PlayerEntity user) {

        World world = user.getEntityWorld();

        if (world.isClient) return;

        HazmatComponent hazmatInfo = HAZMAT_COMPONENT_KEY.get(user);

        removeDivingEffect(user, hazmatInfo);

    }

    private static void removeDivingEffect(PlayerEntity playerEntity, HazmatComponent hazmatComponent) {
        if (hazmatComponent.wasDiving()) {
            playerEntity.removeStatusEffect(StatusEffects.WATER_BREATHING);
            hazmatComponent.setWasDiving(false);
        }
    }

    private static boolean tryConsumeAir(PlayerEntity playerEntity, ItemStack hazmatChestpiece) {
        if (tryUseAir(hazmatChestpiece, 1)) {
            return true;
        } else {
            for (int i = 0; i < playerEntity.getInventory().size(); i++) {
                ItemStack iteratedStack = playerEntity.getInventory().getStack(i);
                if (iteratedStack.getItem() == TRContent.CELL &&
                        TRContent.CELL.getFluid(iteratedStack) == ModFluids.COMPRESSED_AIR.getFluid()) {
                    iteratedStack.decrement(1);
                    playerEntity.giveItemStack(new ItemStack(TRContent.CELL));
                    fillAir(hazmatChestpiece);
                    World world = playerEntity.getEntityWorld();
                    playerEntity.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.NEUTRAL, 0.8F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
                    return true;
                }
            }
            emptyAir(hazmatChestpiece);
            return false;
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        boolean shiftDown = Screen.hasShiftDown();
        NbtCompound nbt = stack.getNbt();

        if (!shiftDown && !hasAir(nbt)) return;

        int airTicks;
        int airCells = 0;
        int storedAir = getStoredAir(nbt);

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

        if (!shiftDown || !hasAir(nbt)) {
            airTicks = airCells * airCapacity + storedAir;
        } else {
            airTicks = storedAir;
        }

        if (airTicks == 0) return;

        MutableText line = Text.literal(StringHelper.formatTicks(airTicks));
        line.append(" ");
        line.append(Formatting.GRAY + I18n.translate("block.techreborn.compressed_air"));
        tooltip.add(line);

        if (shiftDown && airCells != 0) {
            if (hasAir(nbt)) {
                tooltip.add(Text.literal(Formatting.DARK_GRAY + I18n.translate("gearreborn.tooltip.hazmat_chestpiece.in_item")));
            } else {
                tooltip.add(Text.literal(Formatting.DARK_GRAY + I18n.translate("gearreborn.tooltip.hazmat_chestpiece.in_inventory")));
            }
        }

    }

}


