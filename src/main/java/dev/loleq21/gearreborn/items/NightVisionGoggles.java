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
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import reborncore.api.items.ArmorBlockEntityTicker;
import reborncore.api.items.ArmorRemoveHandler;
import reborncore.common.powerSystem.RcEnergyTier;
import reborncore.common.util.ItemUtils;
import techreborn.items.armor.TREnergyArmourItem;

import java.util.List;

public class NightVisionGoggles extends TREnergyArmourItem implements ArmorBlockEntityTicker, ArmorRemoveHandler {

    public NightVisionGoggles(ArmorMaterial material, ArmorItem.Type slot) {
        super(material, slot, energyCapacity, RcEnergyTier.MEDIUM);
    }
    private static final GRConfig config = AutoConfig.getConfigHolder(GRConfig.class).getConfig();

    public static final int energyPerTickCost = config.nvgActiveEnergyPerTickCost;
    public static final int energyCapacity = config.nvgEnergyCapacity;

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

    public static void disableNightVision(PlayerEntity entity) {
        if (!entity.getEntityWorld().isClient()) {
            entity.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }

    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        ItemUtils.buildActiveTooltip(stack, tooltip);
    }

}