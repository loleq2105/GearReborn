package dev.loleq21.ag4tr;

import com.google.common.collect.Multimap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
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

public class ExoskeletonArmorPiece extends ArmorItem implements ArmorTickable, EnergyHolder, ItemDurabilityExtensions, ItemStackModifiers {

    public ExoskeletonArmorPiece(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Settings().group(ItemGroup.COMBAT).maxCount(1).maxDamage(-1));
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    private static final UUID[] MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    @Override
    public void tickArmor(ItemStack itemStack, PlayerEntity playerEntity) {

        switch (this.slot){
            case LEGS:
                double playerVelocity = Math.sqrt(Math.pow(playerEntity.getVelocity().getX(), 2) + Math.pow(playerEntity.getVelocity().getZ(), 2));
                //playerEntity.sendMessage(new LiteralText(String.valueOf(playerVelocity)), true);
                boolean playerIsWalking = playerEntity.isOnGround() == (playerVelocity > 0.034D) == !playerEntity.isSprinting();
                //playerIsJumpingProbably = !playerEntity.isSneaking() && !playerEntity.isSwimming() && !playerEntity.isOnGround() && playerEntity.getVelocity().getY()>0;
                if (playerEntity.isSprinting() && Energy.of(itemStack).getEnergy() >= 16) {
                    Energy.of(itemStack).use(16);
                }
                //0.034
                if (playerIsWalking && Energy.of(itemStack).getEnergy() >= 4) {
                    Energy.of(itemStack).use(4);
                }
                if (playerEntity.isSwimming() && Energy.of(itemStack).getEnergy() >= 16) {
                    playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 5, 1));
                    Energy.of(itemStack).use(16);
                }

                //i don't feel like this is the best way to do this

                if(EXOLEGS_JUMP_BOOST_KEY_BIND.isPressed()) {
                    if (itemStack.getCooldown() == 0) {
                        Ag4trItemUtils.switchActive(itemStack, playerEntity.getEntityWorld().isClient(), MessageIDs.poweredToolID, "Jump Boost Enabled", "Jump Boost Disabled");
                        if (!Ag4trItemUtils.isActive(itemStack)){
                            playerEntity.playSound(SoundEvents.BLOCK_PISTON_CONTRACT, 1F, 2F);
                        } else {
                            playerEntity.playSound(SoundEvents.BLOCK_PISTON_EXTEND, 1F, 2F);
                        }
                    }
                    itemStack.setCooldown(2);
                }

                if (ItemUtils.isActive(itemStack) && Energy.of(itemStack).getEnergy() >= 8) {
                     playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 5, 2));
                     Energy.of(itemStack).use(8);
                 }
                break;
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
        return 20000;
    }

    @Override
    public EnergyTier getTier() {
        return EnergyTier.MEDIUM;
    }

    @Override
    public void getAttributeModifiers(EquipmentSlot equipmentSlot, ItemStack itemStack, Multimap<EntityAttribute, EntityAttributeModifier> multimap) {
        multimap.removeAll(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (this.slot == EquipmentSlot.LEGS && equipmentSlot == EquipmentSlot.LEGS && Energy.of(itemStack).getEnergy() > 2) {
            multimap.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(MODIFIERS[equipmentSlot.getEntitySlotId()], "Movement Speed", 0.06D, EntityAttributeModifier.Operation.ADDITION));
        }
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
