package dev.loleq21.gearreborn;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
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
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import reborncore.api.items.ArmorRemoveHandler;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.util.ChatUtils;
import reborncore.common.util.ItemDurabilityExtensions;
import reborncore.common.util.ItemUtils;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHolder;
import team.reborn.energy.EnergyTier;
import techreborn.utils.InitUtils;
import techreborn.utils.MessageIDs;

import java.util.List;

import static dev.loleq21.gearreborn.GearRebornClient.NV_KEY_BIND;

public class NightvisionGoggles extends ArmorItem implements EnergyHolder, ItemDurabilityExtensions, ArmorRemoveHandler {

    public NightvisionGoggles(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Settings().group(GearReborn.ITEMGROUP).maxCount(1).maxDamage(-1));
    }

    GRConfig config = AutoConfig.getConfigHolder(GRConfig.class).getConfig();

    public final double energyPerTickCost = config.nvgActiveEnergyPerTickCost;
    public final double energyCapacity = config.nvgEnergyCapacity;

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) { //start method
        if (entity instanceof PlayerEntity) {
            PlayerEntity user = (PlayerEntity) entity;

            if (user.getEquippedStack(EquipmentSlot.HEAD) == stack) {
                ItemUtils.checkActive(stack, (int) energyPerTickCost, world.isClient(), MessageIDs.poweredToolID);
                boolean active = stack.getNbt().getBoolean("isActive");
                if (Energy.of(stack).getEnergy() < energyPerTickCost) {
                    disableNightVision(world, user);
                }
                byte toggleCooldown = stack.getNbt().getByte("toggleTimer");

                if (NV_KEY_BIND.isPressed() && toggleCooldown == 0) {
                    toggleCooldown = 10;
                    if (!active && Energy.of(stack).getEnergy() >= energyPerTickCost) {
                        active = true;
                        if (world.isClient) {
                            ChatUtils.sendNoSpamMessages(MessageIDs.poweredToolID, (new TranslatableText("gearreborn.misc.shortnvgname").formatted(Formatting.GRAY).append(" ").append(new TranslatableText("gearreborn.misc.deviceon").formatted(Formatting.GOLD))));
                        }
                    } else if (active) {
                        active = false;
                        disableNightVision(world, user);
                        if (world.isClient()) {
                            ChatUtils.sendNoSpamMessages(MessageIDs.poweredToolID, (new TranslatableText("gearreborn.misc.shortnvgname").formatted(Formatting.GRAY).append(" ").append(new TranslatableText("gearreborn.misc.deviceoff").formatted(Formatting.GOLD))));
                        }
                    } else {
                        if (world.isClient) {
                            ChatUtils.sendNoSpamMessages(MessageIDs.poweredToolID, new TranslatableText("reborncore.message.energyError").formatted(Formatting.GRAY).append(" ").append(new TranslatableText("reborncore.message.deactivating").formatted(Formatting.GOLD)));
                        }
                    }
                    if (!world.isClient()) {
                        stack.getOrCreateNbt().putBoolean("isActive", active);
                    }
                }
                if (!world.isClient()) {
                    if (ItemUtils.isActive(stack) && ((user.isCreative() || user.isSpectator()) || Energy.of(stack).use(energyPerTickCost))) {
                        user.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 999999, 0, false, false, false));
                    }
                }
                if (!world.isClient() && toggleCooldown > 0) {
                    --toggleCooldown;
                    stack.getOrCreateNbt().putByte("toggleTimer", toggleCooldown);
                }
            }
        }
    }

    private void disableNightVision(World world, PlayerEntity entity) {
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


}
