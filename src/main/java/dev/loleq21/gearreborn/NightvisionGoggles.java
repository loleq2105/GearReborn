package dev.loleq21.gearreborn;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import reborncore.api.items.ArmorRemoveHandler;
import reborncore.common.powerSystem.RcEnergyItem;
import reborncore.common.powerSystem.RcEnergyTier;
import reborncore.common.util.ItemUtils;

import java.util.List;

public class NightvisionGoggles extends ArmorItem implements RcEnergyItem, ArmorRemoveHandler {

    public NightvisionGoggles(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Settings().maxCount(1).maxDamage(-1));
    }

    GRConfig config = AutoConfig.getConfigHolder(GRConfig.class).getConfig();

    public final long energyPerTickCost = config.nvgActiveEnergyPerTickCost;
    public final long energyCapacity = config.nvgEnergyCapacity;

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {


        if (world.isClient()) {
            return;
        }

        if (!(entity instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity user = (PlayerEntity) entity;

        if (!(user.getEquippedStack(EquipmentSlot.HEAD) == stack)) {
            return;
        }

        if (!ItemUtils.isActive(stack)) {
            return;
        }

        if (user.isCreative() || user.isSpectator()) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 999999, 0, false, false, false));
            return;
        }

        checkActive(stack, (int) energyPerTickCost, world, user);

        if (!tryUseEnergy(stack, energyPerTickCost)) {
            return;
        }
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 999999, 0, false, false, false));

    }

    public static void disableNightVision(World world, PlayerEntity entity) {
        if (!world.isClient()) {
            entity.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }

    @Override
    public void onRemoved(PlayerEntity user) {
        disableNightVision(user.getEntityWorld(), user);
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return ItemUtils.getPowerForDurabilityBar(stack);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return ItemUtils.getColorForDurabilityBar(stack);
    }

    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public long getEnergyCapacity() {
        return energyCapacity;
    }

    @Override
    public RcEnergyTier getTier() {
        return RcEnergyTier.MEDIUM;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        GRItemUtils.buildActiveTooltip(stack, tooltip);
    }

    private void checkActive(ItemStack stack, int cost, World world, PlayerEntity user) {
        if (!ItemUtils.isActive(stack)) {
            disableNightVision(world, user);
            return;
        }
        if (getStoredEnergy(stack) >= cost) {
            return;
        }
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            serverPlayerEntity.sendMessage(Text.translatable("reborncore.message.energyError")
                    .formatted(Formatting.GRAY)
                    .append(" ")
                    .append(
                            Text.translatable("reborncore.message.deactivating")
                                    .formatted(Formatting.GOLD)
                    )
            );
        }
        stack.getOrCreateNbt().putBoolean("isActive", false);
        disableNightVision(world, user);
        world.playSound(null, user.getBlockPos(), GearReborn.NVG_SOUND_EVENT, SoundCategory.MASTER, 1f, 0.6f);
    }

}
