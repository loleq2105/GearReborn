package dev.loleq21.gearreborn;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import reborncore.api.items.ArmorRemoveHandler;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.powerSystem.RcEnergyItem;
import reborncore.common.powerSystem.RcEnergyTier;
import reborncore.common.util.ChatUtils;
import reborncore.common.util.ItemDurabilityExtensions;
import reborncore.common.util.ItemUtils;
import techreborn.utils.InitUtils;
import techreborn.utils.MessageIDs;

import java.util.List;

import static dev.loleq21.gearreborn.GearRebornClient.NV_KEY_BIND;

public class NightvisionGoggles extends ArmorItem implements RcEnergyItem, ItemDurabilityExtensions, ArmorRemoveHandler {

    public NightvisionGoggles(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Settings().group(GearReborn.ITEMGROUP).maxCount(1).maxDamage(-1));
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
        if (entity instanceof PlayerEntity) {
            PlayerEntity user = (PlayerEntity) entity;

            if (user.getEquippedStack(EquipmentSlot.HEAD) == stack) {

                if (world.isClient() && NV_KEY_BIND.isPressed()){
                    ClientPlayNetworking.send(GearReborn.gogglesTogglePacketIdentifier, PacketByteBufs.empty());
                }
                if (!world.isClient()) {
                    checkActive(stack, (int) energyPerTickCost, world.isClient(), MessageIDs.poweredToolID, world, user);
                    if (ItemUtils.isActive(stack) && ((user.isCreative() || user.isSpectator()) || tryUseEnergy(stack, energyPerTickCost))) {
                        user.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 999999, 0, false, false, false));
                    }
                    byte toggleCooldown = stack.getOrCreateNbt().getByte("toggleTimer");
                    if (toggleCooldown > 0) {
                        --toggleCooldown;
                        stack.getOrCreateNbt().putByte("toggleTimer", toggleCooldown);
                    }
                }
            }
        }
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
    public double getDurability(ItemStack stack) {
        return 1 - ItemUtils.getPowerForDurabilityBar(stack);
    }

    @Override
    public boolean showDurability(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public int getDurabilityColor(ItemStack stack) {
        return PowerSystem.getDisplayPower().colour;
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

    @Environment(EnvType.CLIENT)
    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> itemList) {
        if (!isIn(group)) {
            return;
        }
        InitUtils.initPoweredItems(this, itemList);
    }

    private void checkActive(ItemStack stack, int cost, boolean isClient, int messageId, World world, PlayerEntity user) {
        if (!ItemUtils.isActive(stack)) {
            disableNightVision(world, user);
            return;
        }
        if (getStoredEnergy(stack) >= cost) {
            return;
        }
        if (isClient) {
            ChatUtils.sendNoSpamMessages(messageId, new TranslatableText("reborncore.message.energyError")
                    .formatted(Formatting.GRAY)
                    .append(" ")
                    .append(
                            new TranslatableText("reborncore.message.deactivating")
                                    .formatted(Formatting.GOLD)
                    )
            );
        }
        stack.getOrCreateNbt().putBoolean("isActive", false);
        disableNightVision(world, user);
        world.playSound(null, user.getBlockPos(), GearReborn.NVG_SOUND_EVENT, SoundCategory.MASTER, 1f, 0.6f);
    }

}
