package dev.loleq21.gearreborn;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.powerSystem.RcEnergyItem;
import reborncore.common.powerSystem.RcEnergyTier;
import reborncore.common.util.ItemUtils;
import techreborn.init.ModSounds;
import techreborn.utils.InitUtils;
import techreborn.utils.MessageIDs;

import java.util.List;

public class StunGunItem extends Item implements RcEnergyItem {

    public StunGunItem() {
        super(new Settings().group(GearReborn.ITEMGROUP).maxCount(1));
    }

    GRConfig config = AutoConfig.getConfigHolder(GRConfig.class).getConfig();


    public final long zapEnergyCost = config.stungunOneClickEnergyCost;
    public final long energyCapacity = config.stungunEnergyCapacity;
    public final int capacitorChargeUnits = config.stungunChargeTicks;
    public final int slownessTicks = config.stungunSlownessTicks;
    public final int weaknessTicks = config.stungunWeaknessTicks;
    public final int arthropodDamage = config.stungunDamageDealtToArthropodsOnChargedHit;
    public final boolean igniteCreeper = config.stungunShouldChargedHitsIgniteCreepers;
    public final boolean stunBosses = config.stungunShouldStunBossMobs;


    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        ItemUtils.checkActive(stack, config.stungunOneClickEnergyCost, MessageIDs.poweredToolID, entity);
        if (ItemUtils.isActive(stack)) {
            if (getCapacitorCharge(stack) < capacitorChargeUnits && tryUseEnergy(stack, zapEnergyCost)) {
                setCapacitorCharge(stack, getCapacitorCharge(stack) + 1);
                entity.playSound(ModSounds.CABLE_SHOCK, 0.4F, 1.0F);
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack stack = player.getStackInHand(hand);
        if (player.isSneaking()) {
            GRItemUtils.switchActive(stack, world.isClient(), MessageIDs.poweredToolID, "gearreborn.misc.shortstungunname", player);
            return new TypedActionResult<>(ActionResult.SUCCESS, stack);
        }
        return new TypedActionResult<>(ActionResult.PASS, stack);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (ItemUtils.isActive(stack) && getCapacitorCharge(stack) == capacitorChargeUnits) {

            if (target instanceof CreeperEntity) {
                CreeperEntity creeper = (CreeperEntity) target;
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, slownessTicks, 4, false, true, true));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, weaknessTicks, 2, false, true, true));
                if (igniteCreeper) {
                    creeper.ignite();
                }
                target.playSound(ModSounds.CABLE_SHOCK, 1.1F, 8.0F);
                setCapacitorCharge(stack, 0);
                return true;
            }
            else if (target.getGroup() == EntityGroup.ARTHROPOD) {
                target.playSound(ModSounds.CABLE_SHOCK, 1.1F, 8.0F);
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, slownessTicks, 4, false, true, true));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, weaknessTicks, 2, false, true, true));
                setCapacitorCharge(stack, 0);
                if (attacker instanceof PlayerEntity) {
                    target.damage(DamageSource.player((PlayerEntity) attacker), arthropodDamage);
                    return true;
                }
                return false;
            }
            else if (GearReborn.bossMobs.contains(target.getType()) && !stunBosses){
                target.playSound(ModSounds.CABLE_SHOCK, 1.1F, 8.0F);
                setCapacitorCharge(stack, 0);
                return false;
            }
            else {
                if (target instanceof PlayerEntity) {
                    if (HazmatSuitUtils.playerIsWearingFullHazmat((PlayerEntity)target)) {
                        return false;
                    }
                }
                target.playSound(ModSounds.CABLE_SHOCK, 1.1F, 0.8F);
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, slownessTicks, 5, false, true, true));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, weaknessTicks, 4, false, true, true));
                setCapacitorCharge(stack, 0);
                return true;
            }
        }
            return false;
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
        if (!stack.getNbt().contains("capcharge", 3)){
            stack.getNbt().putInt("capcharge", 0);
            return;
        }
        if (stack.getNbt().getInt("capcharge")>config.stungunChargeTicks) {
            stack.getNbt().putInt("capcharge", config.stungunChargeTicks);
        }

    }

    public int getCapCharge4ToolTip(ItemStack stack) {
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
        GRItemUtils.buildActiveTooltip(stack, tooltip);
        LiteralText line1 = new LiteralText("[");
        line1.formatted(Formatting.GRAY);
        if (getCapCharge4ToolTip(stack)==capacitorChargeUnits) {
            line1.append(new LiteralText("■").formatted(Formatting.GREEN));
        } else if (getCapCharge4ToolTip(stack)==0){
            line1.append(new LiteralText("■").formatted(Formatting.DARK_GRAY));
        } else {
            line1.append(new LiteralText("■").formatted(Formatting.YELLOW));
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
