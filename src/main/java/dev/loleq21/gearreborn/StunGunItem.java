package dev.loleq21.gearreborn;

import dev.loleq21.gearreborn.hazmat.HazmatSuitUtils;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import reborncore.common.powerSystem.RcEnergyItem;
import reborncore.common.powerSystem.RcEnergyTier;
import reborncore.common.util.ItemUtils;
import techreborn.init.ModSounds;
import techreborn.utils.InitUtils;

import java.util.List;

public class StunGunItem extends Item implements RcEnergyItem {

    public StunGunItem() {
        super(new Settings().group(GearReborn.ITEMGROUP).maxCount(1));
    }

    GRConfig config = AutoConfig.getConfigHolder(GRConfig.class).getConfig();


    public final long chargeEnergyCost = config.stungunChargeEnergyCost;
    public final long energyCapacity = config.stungunEnergyCapacity;
    public final int chargeTicks = config.stungunChargeTicks;
    public final int slownessTicks = config.stungunSlownessTicks;
    public final int weaknessTicks = config.stungunWeaknessTicks;
    public final int arthropodDamage = config.stungunDamageDealtToArthropodsOnChargedHit;
    public final boolean igniteCreeper = config.stungunShouldChargedHitsIgniteCreepers;
    public final boolean stunBosses = config.stungunShouldStunBossMobs;

    private final int energyPerChargeTick = (((int)chargeEnergyCost/chargeTicks)>0 ? ((int)chargeEnergyCost/chargeTicks) : 1);


    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!ItemUtils.isActive(stack)) {
            return;
        }
        if (getCapacitorCharge(stack) < chargeTicks && tryUseEnergy(stack, energyPerChargeTick)) {
            setCapacitorCharge(stack, getCapacitorCharge(stack) + 1);
            entity.playSound(ModSounds.CABLE_SHOCK, 0.4F, 1.0F);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        if (!player.isSneaking()) {
            return new TypedActionResult<>(ActionResult.PASS, player.getStackInHand(hand));
        }
        final ItemStack stack = player.getStackInHand(hand);
        ItemUtils.switchActive(stack, energyPerChargeTick, player);
        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {

        if (!ItemUtils.isActive(stack)) {
            return false;
        }

        if (getCapacitorCharge(stack) != chargeTicks) {
            return false;
        }

        if (igniteCreeper && target instanceof CreeperEntity) {
            CreeperEntity creeper = (CreeperEntity) target;
            creeper.ignite();
        } else if (target.getGroup() == EntityGroup.ARTHROPOD) {
            if (attacker instanceof PlayerEntity) {
                target.damage(DamageSource.player((PlayerEntity) attacker), arthropodDamage);
                return true;
            }
        } else if (!stunBosses && GearReborn.bossMobs.contains(target.getType())) {
            target.playSound(ModSounds.CABLE_SHOCK, 1.1F, 8.0F);
            setCapacitorCharge(stack, 0);
            return false;
        } else if (target instanceof PlayerEntity && HazmatSuitUtils.playerIsWearingFullHazmat((PlayerEntity) target)) {
            return false;
        }
        target.playSound(ModSounds.CABLE_SHOCK, 1.1F, 0.8F);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, slownessTicks, 5, false, true, true));
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, weaknessTicks, 4, false, true, true));
        setCapacitorCharge(stack, 0);
        return true;

    }


    public static int getCapacitorCharge(ItemStack stack) {
        if (stack.getItem() == GRContent.STUN_GUN) {
            validateCapChargeNbtTag(stack);
            return stack.getNbt().getInt("capcharge");
        }
        return 0;
    }


    public static void setCapacitorCharge(ItemStack stack, int amount) {
        if (stack.getItem() == GRContent.STUN_GUN) {
            validateCapChargeNbtTag(stack);
            stack.getNbt().putInt("capcharge", amount);
        }
    }

    private static void validateCapChargeNbtTag(ItemStack stack) {
        GRConfig config = AutoConfig.getConfigHolder(GRConfig.class).getConfig();
        if (!stack.getNbt().contains("capcharge", 3)) {
            stack.getNbt().putInt("capcharge", 0);
            return;
        }
        if (stack.getNbt().getInt("capcharge") > config.stungunChargeTicks) {
            stack.getNbt().putInt("capcharge", config.stungunChargeTicks);
        }

    }

    public int getCapChargeForToolTip(ItemStack stack) {
        if (stack.hasNbt()) {
            return stack.getNbt().getInt("capcharge");
        }
        return 0;
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
        MutableText line1 = Text.literal("[");
        line1.formatted(Formatting.GRAY);
        if (getCapChargeForToolTip(stack) == chargeTicks) {
            line1.append(Text.literal("■").formatted(Formatting.GREEN));
        } else if (getCapChargeForToolTip(stack) == 0) {
            line1.append(Text.literal("■").formatted(Formatting.DARK_GRAY));
        } else {
            line1.append(Text.literal("■").formatted(Formatting.YELLOW));
        }
        line1.append("]");
        line1.formatted(Formatting.GRAY);
        tooltip.add(line1);
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
