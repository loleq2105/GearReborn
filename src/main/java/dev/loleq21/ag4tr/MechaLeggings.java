package dev.loleq21.ag4tr;

import com.google.common.collect.Multimap;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import reborncore.api.items.ItemStackModifiers;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.util.ChatUtils;
import reborncore.common.util.ItemDurabilityExtensions;
import reborncore.common.util.ItemUtils;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHolder;
import team.reborn.energy.EnergyTier;
import techreborn.items.armor.TRArmourItem;
import techreborn.utils.InitUtils;
import techreborn.utils.MessageIDs;

import java.util.List;
import java.util.function.Predicate;

public class MechaLeggings extends TRArmourItem implements EnergyHolder, ItemDurabilityExtensions, ItemStackModifiers {

    Ag4trConfig config = AutoConfig.getConfigHolder(Ag4trConfig.class).getConfig();

    public final double energyCapacity = config.mechLeggingsCapacity;
    public final double accelCost = config.mechLeggingsAccelerationCost;

    public MechaLeggings(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Settings().group(Ag4tr.AG4TR_GROUP).maxCount(1).maxDamage(-1));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity user = (PlayerEntity) entity;
            if (user.getEquippedStack(EquipmentSlot.LEGS) == stack) {
                double playerVelocity = Math.sqrt(Math.pow(Math.abs(user.getVelocity().x), 2) + Math.pow(Math.abs(user.getVelocity().z), 2));
                stack.getOrCreateTag().putDouble("velocity", playerVelocity);
                if (!world.isClient() && stack.getOrCreateTag().getDouble("velocity") > 0.1d) {
                    Energy.of(stack).use(accelCost);
                }

                if (world.isClient()) {
                    ChatUtils.sendNoSpamMessages(MessageIDs.poweredToolID, Text.of(String.valueOf(playerVelocity)));
                }
            }
        }
    }


    //private static Predicate<PlayerEntity> isJustWalkingFfs() {
   //     return p -> !p.isSwimming() && !p.isSprinting() && !p.isSneaking() &&
   // }

    @Override
    public void getAttributeModifiers(EquipmentSlot equipmentSlot, ItemStack stack, Multimap<EntityAttribute, EntityAttributeModifier> attributes) {
        attributes.removeAll(EntityAttributes.GENERIC_MOVEMENT_SPEED);

        if (this.slot == EquipmentSlot.LEGS && equipmentSlot == EquipmentSlot.LEGS) {
            if (Energy.of(stack).getEnergy() > accelCost) {
                attributes.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(MODIFIERS[equipmentSlot.getEntitySlotId()], "Movement Speed", 0.05, EntityAttributeModifier.Operation.ADDITION));
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

    @Environment(EnvType.CLIENT)
    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> itemList) {
        if (!isIn(group)) {
            return;
        }
        InitUtils.initPoweredItems(this, itemList);
    }

    @Override
    public double getMaxStoredPower() {
        return energyCapacity;
    }

    @Override
    public EnergyTier getTier() {
        return EnergyTier.MEDIUM;
    }

}
