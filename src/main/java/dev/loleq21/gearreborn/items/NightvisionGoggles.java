package dev.loleq21.gearreborn.items;

import dev.loleq21.gearreborn.GRConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import reborncore.api.items.ArmorBlockEntityTicker;
import reborncore.api.items.ArmorRemoveHandler;
import reborncore.common.powerSystem.RcEnergyItem;
import reborncore.common.powerSystem.RcEnergyTier;
import reborncore.common.util.ItemUtils;
import techreborn.utils.InitUtils;

import java.util.List;

public class NightvisionGoggles extends ArmorItem implements ArmorBlockEntityTicker, RcEnergyItem, ArmorRemoveHandler {

    public NightvisionGoggles(ArmorMaterial material, ArmorItem.Type slot) {
        super(material, slot, new Settings().maxCount(1).maxDamage(-1));
    }

    private static GRConfig config = AutoConfig.getConfigHolder(GRConfig.class).getConfig();

    public static final int energyPerTickCost = config.nvgActiveEnergyPerTickCost;
    private static final long energyCapacity = config.nvgEnergyCapacity;

    @Override
    public void tickArmor(ItemStack stack, PlayerEntity playerEntity) {

        if (!(playerEntity instanceof ServerPlayerEntity user)) {
            return;
        }

        if (this.getSlotType() != EquipmentSlot.HEAD) {
            disableNightVision(user);
            if(ItemUtils.isActive(stack)){
                ItemUtils.switchActive(stack, 0, user);
            }
            return;
        }

        ItemUtils.checkActive(stack, energyPerTickCost, user);

        if (!ItemUtils.isActive(stack)) {
            disableNightVision(user);
            return;
        }

        if ((user.isCreative() || user.isSpectator()) || tryUseEnergy(stack, energyPerTickCost)) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 240, 0, false, false, false));
        }
    }

    @Override
    public void onRemoved(PlayerEntity playerEntity) {
        if (!(playerEntity instanceof ServerPlayerEntity))
            return;
        disableNightVision(playerEntity);
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    public static void disableNightVision(PlayerEntity entity) {
        if (!entity.getEntityWorld().isClient()) {
            entity.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
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
        ItemUtils.buildActiveTooltip(stack, tooltip);
    }

}
