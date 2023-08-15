package com.loleq21.gearreborn;

import com.loleq21.gearreborn.items.NightVisionGoggles;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.world.World;
import reborncore.common.util.ItemUtils;
import team.reborn.energy.api.base.SimpleEnergyItem;
import techreborn.api.events.CableElectrocutionEvent;

import static com.loleq21.gearreborn.items.NightVisionGoggles.disableNightVision;
import static com.loleq21.gearreborn.components.GRComponents.HAZMAT_COMPONENT_KEY;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

public class GearReborn implements ModInitializer {

    public static final String MOD_ID = "gearreborn";
    public static final EntityType[] bossMobsArray = new EntityType[]{EntityType.ENDER_DRAGON, EntityType.WITHER, EntityType.WARDEN};
    public static final Set<EntityType> bossMobs = new HashSet<EntityType>(Arrays.asList(bossMobsArray));

    public static final Identifier gogglesTogglePacketIdentifier = new Identifier(MOD_ID, "nvg_toggle");

    public static final Identifier NVG_SOUND_ID = new Identifier("gearreborn:goggle_toggle");
    public static SoundEvent NVG_SOUND_EVENT = SoundEvent.of(NVG_SOUND_ID);

    public static final EnumMap<EquipmentSlot, Item> hazmatSlotMap = new EnumMap<>(EquipmentSlot.class);

    @Override
    public void onInitialize() {
        GRConfig.init();
        GRContent.registerGearRebornContent();
        GRItemGroup.registerItemGroups();

        Registry.register(Registries.SOUND_EVENT, GearReborn.NVG_SOUND_ID, NVG_SOUND_EVENT);

        // Register receiver for the packet that toggles NVG

        ServerPlayNetworking.registerGlobalReceiver(gogglesTogglePacketIdentifier, (client, player, responseSender, buf, handler) -> {
            ItemStack stack = player.getEquippedStack(EquipmentSlot.HEAD);

            if (stack.getItem() != GRContent.NV_GOGGLES.asItem())
                return;

            ItemUtils.switchActive(stack, NightVisionGoggles.energyPerTickCost, player);

            float beepPitch = 1.1f;
            if (SimpleEnergyItem.getStoredEnergyUnchecked(stack) < NightVisionGoggles.energyPerTickCost) {
                beepPitch = 0.6f;
            }
            if (!ItemUtils.isActive(stack)) {
                beepPitch = 0.9f;
                disableNightVision(player);
            }

            player.playSound(GearReborn.NVG_SOUND_EVENT, SoundCategory.MASTER, 1f, beepPitch);
        });

        // Common events

        CableElectrocutionEvent.EVENT.register((livingEntity, cableType, blockPos, world, cableBlockEntity) -> {
            if (livingEntity instanceof PlayerEntity player) {
                if (HAZMAT_COMPONENT_KEY.get(player).isWearingFullSet()) {
                    return false;
                }
            }
            return true;
        });

        ServerEntityEvents.EQUIPMENT_CHANGE.register((entity, slot, previousStack, currentStack) -> {
            if (entity instanceof PlayerEntity){
                if (slot.getType() != EquipmentSlot.Type.ARMOR) {
                    return;
                }

                if(currentStack.isOf(hazmatSlotMap.get(slot))){
                    HAZMAT_COMPONENT_KEY.get(entity).setBit(slot);
                } else {
                    HAZMAT_COMPONENT_KEY.get(entity).clearBit(slot);
                }

            }
        });

    }
}

