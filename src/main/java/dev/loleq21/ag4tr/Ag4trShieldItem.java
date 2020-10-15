package dev.loleq21.ag4tr;

import com.google.common.collect.Multimap;
import me.crimsondawn45.fabricshieldlib.object.AbstractShield;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import reborncore.api.items.ItemStackModifiers;

import java.util.UUID;

public class Ag4trShieldItem extends AbstractShield implements ItemStackModifiers {

    public Ag4trShieldItem(int cooldownTicks, int durability, Item... repairItems) {
        super(new Settings().group(ItemGroup.COMBAT).maxCount(1), cooldownTicks, durability, repairItems);
    }

    private static final UUID[] MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    @Override
    public void getAttributeModifiers(EquipmentSlot equipmentSlot, ItemStack itemStack, Multimap<EntityAttribute, EntityAttributeModifier> multimap) {
        if (equipmentSlot == EquipmentSlot.MAINHAND || equipmentSlot == EquipmentSlot.OFFHAND) {
            multimap.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(MODIFIERS[equipmentSlot.getEntitySlotId()], "Knockback modifier", 0.08, EntityAttributeModifier.Operation.ADDITION));
        }
    }

}
