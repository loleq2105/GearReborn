package dev.loleq21.ag4tr;

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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import reborncore.api.items.ArmorRemoveHandler;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.util.ItemDurabilityExtensions;
import reborncore.common.util.ItemUtils;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHolder;
import team.reborn.energy.EnergyTier;
import techreborn.utils.InitUtils;
import techreborn.utils.MessageIDs;

import java.util.List;

public class NightvisionGoggles extends ArmorItem implements EnergyHolder, ItemDurabilityExtensions, ArmorRemoveHandler {

    public NightvisionGoggles(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Settings().group(Ag4tr.AG4TR_GROUP).maxCount(1).maxDamage(-1));
    }

    Ag4trConfig config = AutoConfig.getConfigHolder(Ag4trConfig.class).getConfig();

    public final double energyPerTickCost = config.nvgActiveEnergyPerTickCost;
    public final double energyCapacity = config.nvgEnergyCapacity;

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack stack = player.getStackInHand(hand);

            if (player.isSneaking()) {
                Ag4trItemUtils.switchActive(stack, world.isClient(), MessageIDs.poweredToolID, "ag4tr.misc.shortenednvgname4switchchatmessage");
                StatusEffectInstance nightVisionEffectInstance = player.getStatusEffect(StatusEffects.NIGHT_VISION);
                if (nightVisionEffectInstance != null) {
                    player.removeStatusEffectInternal(StatusEffects.NIGHT_VISION);
                }
                return new TypedActionResult<>(ActionResult.SUCCESS, stack);
            }
        return new TypedActionResult<>(ActionResult.PASS, stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient()) {
            if (entity instanceof PlayerEntity) {
                PlayerEntity user = (PlayerEntity) entity;

                if (user.getEquippedStack(EquipmentSlot.HEAD) == stack) {

                    if (Energy.of(stack).getEnergy() < energyPerTickCost) {
                        StatusEffectInstance statusEffectInstance = user.getStatusEffect(StatusEffects.NIGHT_VISION);
                        if (statusEffectInstance != null) {
                            user.removeStatusEffectInternal(StatusEffects.NIGHT_VISION);
                        }
                    }

                    if (ItemUtils.isActive(stack) && Energy.of(stack).getEnergy() >= energyPerTickCost) {
                        Energy.of(stack).use(energyPerTickCost);
                        user.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 240, 0, false, false, false));
                    }
                }
            }
        }
    }

    @Override
    public void onRemoved(PlayerEntity user) {
        StatusEffectInstance statusEffectInstance = user.getStatusEffect(StatusEffects.NIGHT_VISION);
        if (statusEffectInstance!=null) {
            user.removeStatusEffectInternal(StatusEffects.NIGHT_VISION);
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
        Ag4trItemUtils.buildActiveTooltip(stack, tooltip);
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
