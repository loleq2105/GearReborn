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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
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
import static dev.loleq21.ag4tr.Ag4trClient.NV_KEY_BIND;

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
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) { //start method
            if (entity instanceof PlayerEntity) {
                PlayerEntity user = (PlayerEntity) entity;

                if (user.getEquippedStack(EquipmentSlot.HEAD) == stack) {
                    ItemUtils.checkActive(stack, (int)energyPerTickCost, world.isClient(), MessageIDs.poweredToolID);
                    boolean active = stack.getTag().getBoolean("isActive");
                    if (Energy.of(stack).getEnergy() < energyPerTickCost) { disableNightVision(world, user); }
                    byte toggleCooldown = stack.getTag().getByte("toggleTimer");

                    if (NV_KEY_BIND.isPressed() && toggleCooldown == 0){
                        toggleCooldown = 10;
                        if (!active && Energy.of(stack).getEnergy()>=energyPerTickCost){
                            active = true;
                            if (world.isClient) {
                                ChatUtils.sendNoSpamMessages(MessageIDs.poweredToolID, (new TranslatableText("ag4tr.misc.shortenednvgname").formatted(Formatting.GRAY).append(" ").append(new TranslatableText("ag4tr.misc.deviceon").formatted(Formatting.GOLD))));
                            }
                        } else if (active) {
                            active = false;
                            disableNightVision(world, user);
                            if (world.isClient()) { ChatUtils.sendNoSpamMessages(MessageIDs.poweredToolID, (new TranslatableText("ag4tr.misc.shortenednvgname").formatted(Formatting.GRAY).append(" ").append(new TranslatableText("ag4tr.misc.deviceoff").formatted(Formatting.GOLD)))); }
                        }
                        else {
                            if (world.isClient) { ChatUtils.sendNoSpamMessages(MessageIDs.poweredToolID, new TranslatableText("reborncore.message.energyError").formatted(Formatting.GRAY).append(" ").append(new TranslatableText("reborncore.message.deactivating").formatted(Formatting.GOLD))); }
                        }
                        if(!world.isClient()) { stack.getOrCreateTag().putBoolean("isActive", active); }
                    }
                    if(!world.isClient()) {
                        if (ItemUtils.isActive(stack) && Energy.of(stack).use(energyPerTickCost)) {
                                   user.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 999999, 0, false, false, false));
                          }
                    }
                    if (!world.isClient() && toggleCooldown>0) {
                        --toggleCooldown;
                        stack.getOrCreateTag().putByte("toggleTimer", toggleCooldown);
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
