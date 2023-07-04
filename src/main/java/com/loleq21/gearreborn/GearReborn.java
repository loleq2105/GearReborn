package com.loleq21.gearreborn;

import com.loleq21.gearreborn.items.hazmat.HazmatSuitUtils;
import com.loleq21.gearreborn.items.NightVisionGoggles;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GearReborn implements ModInitializer {

    public static final String MOD_ID = "gearreborn";
    public static final EntityType[] bossMobsArray = new EntityType[]{EntityType.ENDER_DRAGON, EntityType.WITHER, EntityType.WARDEN};
    public static final Set<EntityType> bossMobs = new HashSet<EntityType>(Arrays.asList(bossMobsArray));

    public static final Identifier gogglesTogglePacketIdentifier = new Identifier(MOD_ID, "nvg_toggle");

    public static final Identifier NVG_SOUND_ID = new Identifier("gearreborn:goggle_toggle");
    public static SoundEvent NVG_SOUND_EVENT = SoundEvent.of(NVG_SOUND_ID);

    @Override
    public void onInitialize() {
        AutoConfig.register(GRConfig.class, JanksonConfigSerializer::new);
        GRContent.registerGearRebornContent();
        GRItemGroup.registerItemGroups();

        CableElectrocutionEvent.EVENT.register((livingEntity, cableType, blockPos, world, cableBlockEntity) -> {
            if (livingEntity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) livingEntity;
                if (HazmatSuitUtils.playerIsWearingFullHazmat(player)) {
                    return false;
                }
            }
            return true;
        });

        Registry.register(Registries.SOUND_EVENT, GearReborn.NVG_SOUND_ID, NVG_SOUND_EVENT);

        ServerPlayNetworking.registerGlobalReceiver(gogglesTogglePacketIdentifier, (client, player, responseSender, buf, handler) -> {
            ItemStack stack = player.getEquippedStack(EquipmentSlot.HEAD);

            if (stack.getItem() != GRContent.NV_GOGGLES.asItem())
                return;

            World world = player.getEntityWorld();

            ItemUtils.switchActive(stack, NightVisionGoggles.energyPerTickCost, player);

            float beepPitch = 1.1f;
            if (SimpleEnergyItem.getStoredEnergyUnchecked(stack) < NightVisionGoggles.energyPerTickCost) {
                beepPitch = 0.6f;
            }
            if (!ItemUtils.isActive(stack)) {
                beepPitch = 0.9f;
                disableNightVision(player);
            }

            world.playSound(null, player.getBlockPos(), GearReborn.NVG_SOUND_EVENT, SoundCategory.MASTER, 1f, beepPitch);
        });

    }
}

