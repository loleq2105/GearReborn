package dev.loleq21.ag4tr;

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

import static dev.loleq21.ag4tr.client.Ag4trClient.NV_KEY_BIND;

public class NightvisionGoggles extends ArmorItem implements EnergyHolder, ItemDurabilityExtensions, ArmorRemoveHandler {

    public NightvisionGoggles(ArmorMaterial material, EquipmentSlot slot, int energyPerTickConsumption) {
        super(material, slot, new Settings().group(Ag4tr.AG4TR_GROUP).maxCount(1).maxDamage(-1));
        EPT_CONSUMPTION = energyPerTickConsumption;
    }

    public static int EPT_CONSUMPTION;

    @Override
    public boolean isDamageable() {
        return false;
    }


    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

            if (entity instanceof PlayerEntity) {
                PlayerEntity user = (PlayerEntity) entity;

                if (user.getEquippedStack(EquipmentSlot.HEAD) == stack) {

                    if (NV_KEY_BIND.isPressed()) {
                        if (stack.getCooldown() == 0) {
                            Ag4trItemUtils.switchActive(stack, world.isClient(), MessageIDs.poweredToolID, "Night Vision");
                            StatusEffectInstance statusEffectInstance = user.getStatusEffect(StatusEffects.NIGHT_VISION);
                            if (statusEffectInstance != null) {
                                user.removeStatusEffectInternal(StatusEffects.NIGHT_VISION);
                            }
                        }
                        stack.setCooldown(2);
                    }

                    if (Energy.of(stack).getEnergy()< EPT_CONSUMPTION) {
                        StatusEffectInstance statusEffectInstance = user.getStatusEffect(StatusEffects.NIGHT_VISION);
                        if (statusEffectInstance != null) {
                            user.removeStatusEffectInternal(StatusEffects.NIGHT_VISION);
                        }
                    }

                    if (ItemUtils.isActive(stack) && Energy.of(stack).use(EPT_CONSUMPTION)) {
                        user.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 240, 0, false, false, false));
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
        return 10000;
    }

    @Override
    public EnergyTier getTier() {
        return EnergyTier.LOW;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        Ag4trItemUtils.buildActiveTooltip(stack, tooltip, "Night Vision Enabled", "Night Vision Disabled");
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
