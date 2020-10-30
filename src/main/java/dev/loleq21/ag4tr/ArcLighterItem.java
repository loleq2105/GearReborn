package dev.loleq21.ag4tr;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
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

public class ArcLighterItem extends Item implements EnergyHolder, ItemDurabilityExtensions {

    public ArcLighterItem(int IGNITE_COST) {
        super(new Settings().group(Ag4tr.AG4TR_GROUP).maxCount(1));
        ArcLighterItem.IGNITE_COST = IGNITE_COST;
    }

    public static int IGNITE_COST;

    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack stack = player.getStackInHand(hand);
        if (player.isSneaking()) {
            Ag4trItemUtils.switchActive(stack, world.isClient(), MessageIDs.poweredToolID, "Lighter");
            return new TypedActionResult<>(ActionResult.SUCCESS, stack);
        }
        return new TypedActionResult<>(ActionResult.PASS, stack);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        ItemStack stack = context.getStack();
        if (ItemUtils.isActive(stack) && Energy.of(stack).use(IGNITE_COST)) {
        if (CampfireBlock.method_30035(blockState)) {
            world.playSound(playerEntity, blockPos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
            world.setBlockState(blockPos, (BlockState) blockState.with(Properties.LIT, true), 11);
            return ActionResult.success(world.isClient());
        } else {
            BlockPos blockPos2 = blockPos.offset(context.getSide());
            if (AbstractFireBlock.method_30032(world, blockPos2, context.getPlayerFacing())) {
                world.playSound(playerEntity, blockPos2, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
                BlockState blockState2 = AbstractFireBlock.getState(world, blockPos2);
                world.setBlockState(blockPos2, blockState2, 11);
                return ActionResult.success(world.isClient());
            } else {
                return ActionResult.FAIL;
            }
        }
    }
        else {
            return ActionResult.FAIL;
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (ItemUtils.isActive(stack) && Energy.of(stack).use(IGNITE_COST)) {
            if (entity.getType() == EntityType.CREEPER) {
                if (entity instanceof CreeperEntity) {
                    CreeperEntity creeperAwMan = (CreeperEntity) entity;
                    creeperAwMan.ignite();
                    entity.playSound(ModSounds.CABLE_SHOCK, 1.0F, 1.0F);
                    return ActionResult.SUCCESS;
                } else {
                    return ActionResult.FAIL;
                }
            } else {
                    entity.playSound(ModSounds.CABLE_SHOCK, 1.0F, 1.0F);
                    entity.setOnFireFor(4);
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 15, 2, false, false, false));
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 15, 2, false, false, false));

                    return ActionResult.SUCCESS;
            }
        } else {
            return ActionResult.FAIL;
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

        if (ItemUtils.isActive(stack) && Energy.of(stack).getEnergy()>=IGNITE_COST) {
            entity.playSound(ModSounds.CABLE_SHOCK, 0.1F, 2.0F);
            if (entity instanceof PlayerEntity) {
                PlayerEntity user = (PlayerEntity) entity;

                if (!(user.getMainHandStack() == stack || user.getOffHandStack() == stack)) {
                    if (Energy.of(stack).use(IGNITE_COST/4)) {
                        user.setOnFireFor(1);
                        user.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 15, 2, false, false, false));
                        user.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 15, 2, false, false, false));

                    }
                }
            }
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
        return EnergyTier.MEDIUM;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        Ag4trItemUtils.buildActiveTooltip(stack, tooltip, "On", "Off");
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
