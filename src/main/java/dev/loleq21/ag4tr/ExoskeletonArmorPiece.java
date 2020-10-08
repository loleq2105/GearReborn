package dev.loleq21.ag4tr;

import com.google.common.collect.Multimap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import reborncore.api.items.ArmorTickable;
import reborncore.api.items.ItemStackModifiers;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.util.ItemDurabilityExtensions;
import reborncore.common.util.ItemUtils;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHolder;
import team.reborn.energy.EnergyTier;
import techreborn.utils.InitUtils;
import techreborn.utils.MessageIDs;

import java.util.List;
import java.util.UUID;

import static dev.loleq21.ag4tr.client.Ag4trClient.EXOLEGS_JUMP_BOOST_KEY_BIND;

public class ExoskeletonArmorPiece extends ArmorItem implements EnergyHolder, ItemDurabilityExtensions, ItemStackModifiers {

    public ExoskeletonArmorPiece(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Settings().group(ItemGroup.COMBAT).maxCount(1).maxDamage(-1));
    }

    @Override
    public void getAttributeModifiers(EquipmentSlot equipmentSlot, ItemStack itemStack, Multimap<EntityAttribute, EntityAttributeModifier> multimap) {
        multimap.removeAll(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (this.slot == EquipmentSlot.LEGS && equipmentSlot == EquipmentSlot.LEGS) {
            if (Energy.of(itemStack).getEnergy() > 4) {
                multimap.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(MODIFIERS[equipmentSlot.getEntitySlotId()], "Movement Speed", 0.06D, EntityAttributeModifier.Operation.ADDITION));
            }
        }
    }

    private static final UUID[] MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

        if (entity instanceof PlayerEntity) {
            PlayerEntity user = (PlayerEntity) entity;

            if (user.getEquippedStack(EquipmentSlot.LEGS) == stack) {

                //user.sendMessage(new LiteralText(String.valueOf(Energy.of(stack).getEnergy())), true);

                //jump boost control
                if (EXOLEGS_JUMP_BOOST_KEY_BIND.isPressed()) {
                    if (stack.getCooldown() == 0) {
                        Ag4trItemUtils.switchActive(stack, user.getEntityWorld().isClient(), MessageIDs.poweredToolID, "Jump Boost Enabled", "Jump Boost Disabled");
                        if (!Ag4trItemUtils.isActive(stack)) {
                        } else {
                            user.playSound(SoundEvents.BLOCK_PISTON_EXTEND, 1F, 2F);
                        }
                    }
                    stack.setCooldown(2);
                }

                //jump boost... handling?
                if (ItemUtils.isActive(stack) && Energy.of(stack).use(8)) {
                    user.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 5, 2));
                }

                //playerEntity.sendMessage(new LiteralText(String.valueOf(playerVelocity2D)), true);

                //TODO fix this

                double playerVelocity2D = Math.sqrt(Math.pow(user.getVelocity().getX(), 2) + Math.pow(user.getVelocity().getZ(), 2));
                //0.034 is around the minimum velocity the player can normally go, tested
                boolean playerIsWalking = user.isOnGround() && playerVelocity2D > 0.034 && !user.isSprinting() && !user.isSwimming();

                //sprinting
                if (user.isSprinting() && Energy.of(stack).use(16)) {
                }

                //walking
                if (playerIsWalking && Energy.of(stack).use(4)) {
                }

                    /*
                    a passive energy consumption solution, not too cool if you ask me
                    if (!user.isSprinting() && !user.isSwimming() && !user.isSneaking() && Energy.of(stack).use(4)) {

                    }

                    if (!user.isSprinting() && !user.isSwimming() && user.isSneaking() && Energy.of(stack).use(2)) {

                    }
                     */

                //swimming
                if (user.isSwimming()) {
                    if (Energy.of(stack).use(8)) {
                        user.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 5, 1));
                    }
                }
            } else { return; }
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
        return true;
    }

    @Override
    public int getDurabilityColor(ItemStack stack) {
        return PowerSystem.getDisplayPower().colour;
    }

    @Override
    public double getMaxStoredPower() {
        return 20000;
    }

    @Override
    public EnergyTier getTier() {
        return EnergyTier.MEDIUM;
    }


    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        Ag4trItemUtils.buildActiveTooltip(stack, tooltip, "Jump Boost Enabled", "Jump Boost Disabled");
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
