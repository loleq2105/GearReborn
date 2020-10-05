package dev.loleq21.ag4tr;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.util.ItemDurabilityExtensions;
import reborncore.common.util.ItemUtils;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHolder;
import team.reborn.energy.EnergyTier;
import techreborn.utils.InitUtils;

import static dev.loleq21.ag4tr.client.Ag4trClient.NV_KEY_BIND;

public class NightvisionGoggles extends ArmorItem implements EnergyHolder, ItemDurabilityExtensions {

    public NightvisionGoggles(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Settings().group(ItemGroup.COMBAT).maxCount(1).maxDamage(-1));
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    private PlayerEntity user;
    private ServerWorld serverworld;

    private static boolean nvKeyDownOnLast;
    private static boolean nvKeyToggled;

    public static boolean isToggledOn(){
        if(NV_KEY_BIND.isPressed()){
            nvKeyDownOnLast = true;
        }else if(!NV_KEY_BIND.isPressed() && nvKeyDownOnLast && !nvKeyToggled){
            nvKeyDownOnLast = false;
            nvKeyToggled = true;
            return nvKeyToggled;
        }else if(!NV_KEY_BIND.isPressed() && nvKeyDownOnLast && nvKeyToggled){
            nvKeyDownOnLast = false;
            nvKeyToggled = false;
            return nvKeyToggled;
        }
        return nvKeyToggled;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

        if (world instanceof ServerWorld) {
                serverworld = (ServerWorld) world;
                if (entity instanceof PlayerEntity) {
                    user = (PlayerEntity) entity;
                }

                        if ((user.getEquippedStack(EquipmentSlot.HEAD) == stack) && isToggledOn() && Energy.of(stack).use(20)) {
                            user.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 5, 1));
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
        return 20000;
    }

    @Override
    public EnergyTier getTier() {
        return EnergyTier.LOW;
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
