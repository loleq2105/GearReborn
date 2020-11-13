package dev.loleq21.ag4tr;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.util.ItemDurabilityExtensions;
import reborncore.common.util.ItemUtils;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHolder;
import team.reborn.energy.EnergyTier;
import techreborn.init.ModSounds;
import techreborn.utils.InitUtils;
import techreborn.utils.MessageIDs;


import java.util.List;

public class TaserItem extends Item implements EnergyHolder, ItemDurabilityExtensions {

    public TaserItem(int ZAP_COST) {
        super(new Settings().group(Ag4tr.AG4TR_GROUP).maxCount(1));
        this.ZAP_COST = ZAP_COST;
    }

    public final int ZAP_COST;

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (ItemUtils.isActive(stack)) {
            if (getCapacitorCharge(stack) < 64 && Energy.of(stack).use(ZAP_COST)) {
                setCapacitorCharge(stack, getCapacitorCharge(stack) + 1);
                entity.playSound(ModSounds.CABLE_SHOCK, 0.4F, 1.0F);
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack stack = player.getStackInHand(hand);
        if (player.isSneaking()) {
            Ag4trItemUtils.switchActive(stack, world.isClient(), MessageIDs.poweredToolID, "ag4tr.misc.shortenedtasername4switchchatmessage");
            return new TypedActionResult<>(ActionResult.SUCCESS, stack);
        }
        return new TypedActionResult<>(ActionResult.PASS, stack);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (ItemUtils.isActive(stack) && getCapacitorCharge(stack) == 64) {
            if (target.getType() == EntityType.CREEPER) {
                if (target instanceof CreeperEntity) {
                    CreeperEntity creeper = (CreeperEntity) target;
                    creeper.ignite();
                    target.playSound(ModSounds.CABLE_SHOCK, 1.1F, 8.0F);
                    setCapacitorCharge(stack, 0);
                    return true;
                } else {
                    return false;
                }
            } else if (target.getGroup() == EntityGroup.ARTHROPOD) {
                target.playSound(ModSounds.CABLE_SHOCK, 1.1F, 8.0F);
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 4, false, true, false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 2, false, false, false));
                setCapacitorCharge(stack, 0);
                if (attacker instanceof PlayerEntity) {
                    target.damage(DamageSource.player((PlayerEntity) attacker), 16);
                    return true;
                } else {
                    return false;
                }

            } else {
                if (target instanceof PlayerEntity) {
                    return false;
                }
                target.playSound(ModSounds.CABLE_SHOCK, 1.1F, 0.8F);
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 5, false, true, false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 4, false, false, false));
                setCapacitorCharge(stack, 0);
                return true;
            }

        } else {
            return false;
        }
    }


    public static int getCapacitorCharge(ItemStack stack) {
        if (stack.getItem() == Ag4trContent.TASER) {
            validateCapChargeNBTTag(stack);
            return stack.getTag().getInt("capcharge");
        } else {
            return 0;
        }
    }

    public static void setCapacitorCharge(ItemStack stack, int amount) {
        if (stack.getItem() == Ag4trContent.TASER) {
            validateCapChargeNBTTag(stack);
            stack.getTag().putInt("capcharge", amount);
        }
    }

    private static void validateCapChargeNBTTag(ItemStack stack) {
        if (!stack.getTag().contains("capcharge", 3)){
            stack.getTag().putInt("capcharge", 0);
        }
    }

    public int getCapCharge4ToolTip(ItemStack stack) {
        if (stack.hasTag()) {
            return stack.getTag().getInt("capcharge");
        }
        else {
            return 0;
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
        return 40000;
    }

    @Override
    public EnergyTier getTier() {
        return EnergyTier.MEDIUM;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        Ag4trItemUtils.buildActiveTooltip(stack, tooltip);
        if (getCapCharge4ToolTip(stack)!=64) {
            tooltip.add((new TranslatableText("ag4tr.misc.tasertooltipcapacitorsuncharged").formatted(Formatting.RED)));
        } else {
            tooltip.add((new TranslatableText("ag4tr.misc.tasertooltipcapacitorscharged").formatted(Formatting.GREEN)));
        }
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
