package com.loleq21.gearreborn.items;

import com.loleq21.gearreborn.GearReborn;
import com.loleq21.gearreborn.items.hazmat.HazmatTag;
import com.loleq21.gearreborn.items.hazmat.HazmatUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import reborncore.common.powerSystem.RcEnergyItem;
import reborncore.common.powerSystem.RcEnergyTier;
import reborncore.common.util.ItemUtils;
import team.reborn.energy.api.base.SimpleEnergyItem;
import techreborn.init.ModSounds;
import techreborn.init.TRDamageTypes;

import java.util.List;

import static com.loleq21.gearreborn.GRConfig.CONFIG;

public class StunGunItem extends Item implements RcEnergyItem, CooldownItem {

    public StunGunItem() {
        super(new Settings().maxCount(1));
    }

    private static final long energyCapacity = CONFIG.stungunEnergyCapacity;
    private static final long zapEnergyCost = 2000;
    private static final int cooldownTicks = 32;
    private static final int slownessTicks = CONFIG.stungunSlownessTicks;
    private static final int weaknessTicks = CONFIG.stungunWeaknessTicks;
    private static final int arthropodDamage = CONFIG.stungunDamageDealtToArthropodsOnChargedHit;
    private static final boolean igniteCreeper = CONFIG.stungunShouldChargedHitsIgniteCreepers;
    private static final boolean stunBosses = CONFIG.stungunShouldStunBossMobs;

    private final static String COOLDOWN_KEY = "cooldown";

    @Override
    public void inventoryTick(ItemStack stunGun, World world, Entity entity, int slot, boolean selected) {
        if(!(entity instanceof PlayerEntity user))
            return;

        if (!ItemUtils.isActive(stunGun))
            return;

        if (shouldBeCoolingDown(stunGun)) {
            updateCooldown(stunGun);
            if(selected){
                user.playSound(ModSounds.CABLE_SHOCK, 0.4F, 1.0F);
            }
        }

    }

    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack stack = player.getStackInHand(hand);
        if (!player.isSneaking() || shouldBeCoolingDown(stack)) {
            return new TypedActionResult<>(ActionResult.PASS, player.getStackInHand(hand));
        }
        ItemUtils.switchActive(stack, (int) zapEnergyCost, player);
        ItemUtils.checkActive(stack, (int) zapEnergyCost, player);
        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {

        if (!isReady(stack)) {
            return false;
        }

        if (igniteCreeper && target instanceof CreeperEntity) {
            CreeperEntity creeper = (CreeperEntity) target;
            creeper.ignite();
        } else if (target.getGroup() == EntityGroup.ARTHROPOD) {
            World world = target.getWorld();
            target.damage(world.getDamageSources().create(TRDamageTypes.ELECTRIC_SHOCK), arthropodDamage);
        } else if (!stunBosses && GearReborn.bossMobs.contains(target.getType())) {
            // Do nothing, just make a fool of the player >:))))
        } else if (target instanceof PlayerEntity) {
            HazmatTag ht = HazmatUtil.getHazmatTag(target);
            if(ht!=null && ht.isWearingFullSet()){
                return false;
            }
        } else {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, slownessTicks, 5, false, true, true));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, weaknessTicks, 4, false, true, true));
        }

        target.playSound(ModSounds.CABLE_SHOCK, 1.1F, 0.8F);
        tryUseEnergy(stack, zapEnergyCost);

        ItemUtils.checkActive(stack, (int)zapEnergyCost, attacker);

        // To make this weapon less confusing to use, no cooldown is set when it runs out of power, and none is set after it is recharged again

        if(canCoolDown(stack)){
            setMaxCooldown(stack);
        }

        return true;

    }

    public static boolean isReady(ItemStack stack){
        return (ItemUtils.isActive(stack) &&
                !shouldBeCoolingDown(stack) &&
                SimpleEnergyItem.getStoredEnergyUnchecked(stack)>=zapEnergyCost);
    }

    public static boolean shouldBeCoolingDown(ItemStack stack){
        return (getCooldown(stack) != 0);
    }

    private static boolean canCoolDown(ItemStack stack){
        return (ItemUtils.isActive(stack) &&
                SimpleEnergyItem.getStoredEnergyUnchecked(stack)>=zapEnergyCost);
    }

    private static void updateCooldown(ItemStack stack) {
        int newAmount = getCooldown(stack) - 1;
        if (newAmount < 0) {
            return;
        } else {
            setCooldown(stack, newAmount);
        }
    }

    private static int getCooldown(ItemStack stack) {
        return getCooldown(stack.getNbt());
    }

    private static int getCooldown(@Nullable NbtCompound nbt) {
        return nbt != null && nbt.contains(COOLDOWN_KEY) ? nbt.getInt(COOLDOWN_KEY) : 0;
    }

    private static void setCooldown(ItemStack stack, int newAmount) {
        if (newAmount == 0) {
            stack.removeSubNbt(COOLDOWN_KEY);
        } else {
            stack.getOrCreateNbt().putInt(COOLDOWN_KEY, newAmount);
        }
    }

    private static void setMaxCooldown(ItemStack stack){
        setCooldown(stack, cooldownTicks);
    }

    @Override
    public float getCooldownProgress(PlayerEntity player, World world, ItemStack stack, float tickDelta) {
        if(!canCoolDown(stack)) {
            return 0.0f;
        }
        return MathHelper.clamp((getCooldown(stack) - 1 * tickDelta) / cooldownTicks, 0.0f, 1.0f);
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

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return !isEqualIgnoreEnergyAndCooldown(oldStack, newStack);
    }

    /**
        Adapted method of {@link reborncore.common.util.ItemUtils#isEqualIgnoreEnergy}
     */

    private static boolean isEqualIgnoreEnergyAndCooldown(ItemStack stack1, ItemStack stack2) {
        if (stack1 == stack2) {
            return true;
        } else if (!stack1.isOf(stack2.getItem())) {
            return false;
        } else if (stack1.getCount() != stack2.getCount()) {
            return false;
        } else if (stack1.getNbt() == stack2.getNbt()) {
            return true;
        } else if (stack1.getNbt() != null && stack2.getNbt() != null) {
            NbtCompound nbt1Copy = stack1.getNbt().copy();
            NbtCompound nbt2Copy = stack2.getNbt().copy();
            nbt1Copy.remove("energy");
            nbt2Copy.remove("energy");
            nbt1Copy.remove(COOLDOWN_KEY);
            nbt2Copy.remove(COOLDOWN_KEY);
            return nbt1Copy.equals(nbt2Copy);
        } else {
            return false;
        }
    }


    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        ItemUtils.buildActiveTooltip(stack, tooltip);
//        MutableText line1 = Text.literal("[");
//        line1.formatted(Formatting.GRAY);
//        if (getCapChargeForToolTip(stack) == chargeTicks) {
//            line1.append(Text.literal("■").formatted(Formatting.GREEN));
//        } else if (getCapChargeForToolTip(stack) == 0) {
//            line1.append(Text.literal("■").formatted(Formatting.DARK_GRAY));
//        } else {
//            line1.append(Text.literal("■").formatted(Formatting.YELLOW));
//        }
//        line1.append("]");
//        line1.formatted(Formatting.GRAY);
//        tooltip.add(line1);
    }

}
