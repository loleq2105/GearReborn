package dev.loleq21.gearreborn;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.world.World;
import team.reborn.energy.api.base.SimpleBatteryItem;
import techreborn.api.events.CableElectrocutionEvent;

import static dev.loleq21.gearreborn.NightvisionGoggles.disableNightVision;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GearReborn implements ModInitializer {

    public static final String MOD_ID = "gearreborn";
    public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder(new Identifier(MOD_ID, "items"))
            .displayName(Text.literal("Gear Reborn"))
            .icon(() -> new ItemStack(GRContent.HAZMAT_HELMET))
            .build();

    public static final EntityType[] bossMobsArray = new EntityType[]{EntityType.ENDER_DRAGON, EntityType.WITHER};
    public static final Set<EntityType> bossMobs = new HashSet<EntityType>(Arrays.asList(bossMobsArray));

    public static final Identifier gogglesTogglePacketIdentifier = new Identifier(MOD_ID, "nvg_toggle");

    public static final Identifier NVG_SOUND_ID = new Identifier("gearreborn:goggle_toggle");
    public static SoundEvent NVG_SOUND_EVENT = SoundEvent.of(NVG_SOUND_ID);

    @Override
    public void onInitialize() {
        AutoConfig.register(GRConfig.class, JanksonConfigSerializer::new);
        GRContent.registerGearRebornContent();

        CableElectrocutionEvent.EVENT.register((livingEntity, cableType, blockPos, world, cableBlockEntity) -> {
            if (livingEntity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) livingEntity;
                return !HazmatSuitUtils.playerIsWearingFullHazmat(player);
            }
            return true;
        });

        Registry.register(Registries.SOUND_EVENT, GearReborn.NVG_SOUND_ID, NVG_SOUND_EVENT);

        ServerPlayNetworking.registerGlobalReceiver(gogglesTogglePacketIdentifier, (client, player, responseSender, buf, handler) -> {
            ItemStack stack = player.getEquippedStack(EquipmentSlot.HEAD);

            if (stack.getItem() == GRContent.NV_GOGGLES.asItem()){
                boolean active = stack.getOrCreateNbt().getBoolean("isActive");
            World world = player.getEntityWorld();
            GRConfig config = AutoConfig.getConfigHolder(GRConfig.class).getConfig();

            if (SimpleBatteryItem.getStoredEnergyUnchecked(stack) >= config.nvgActiveEnergyPerTickCost) {
                if (!active) {
                    active = true;
                    world.playSound(null, player.getBlockPos(), GearReborn.NVG_SOUND_EVENT, SoundCategory.MASTER, 1f, 1.1f);
                } else {
                    active = false;
                    disableNightVision(world, player);
                    world.playSound(null, player.getBlockPos(), GearReborn.NVG_SOUND_EVENT, SoundCategory.MASTER, 1f, 0.9f);
                }
            } else {
                world.playSound(null, player.getBlockPos(), GearReborn.NVG_SOUND_EVENT, SoundCategory.MASTER, 1f, 0.6f);
            }
            stack.getOrCreateNbt().putBoolean("isActive", active);

        }

        });



    }
}

