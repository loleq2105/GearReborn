package com.loleq21.gearreborn;

import com.loleq21.gearreborn.items.StunGunItem;
import com.loleq21.gearreborn.items.hazmat.HazmatAirUtil;
import com.loleq21.gearreborn.items.hazmat.HazmatChestPiece;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import reborncore.common.util.ItemUtils;
import team.reborn.energy.api.base.SimpleBatteryItem;
import team.reborn.energy.api.base.SimpleEnergyItem;
import techreborn.items.armor.BatpackItem;

@Environment(EnvType.CLIENT)
public class GearRebornClient implements ClientModInitializer {

    boolean canToggleGoggles = true;
    public static final KeyBinding NV_KEY_BIND = new KeyBinding("key.gearreborn.toggle_nv",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            "category.gearreborn.title");

    @Override
    public void onInitializeClient() {

        KeyBindingHelper.registerKeyBinding(NV_KEY_BIND);
        GRConfig config = AutoConfig.getConfigHolder(GRConfig.class).getConfig();

        // Register event that sends the packet for toggling NVG

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (NV_KEY_BIND.isPressed()) {
                if (!canToggleGoggles) return;
                canToggleGoggles = false;
                ClientPlayNetworking.send(GearReborn.gogglesTogglePacketIdentifier, PacketByteBufs.empty());
            }
            if (!NV_KEY_BIND.isPressed()){
                canToggleGoggles = true;
            }
        });

        // Register predicate providers for dynamic item models. Part of lifted code, see below

        registerPredicateProvider(
                StunGunItem.class,
                new Identifier("gearreborn:active"),
                (item, stack, world, entity, seed) -> {
                    if (!stack.isEmpty() && ItemUtils.isActive(stack) && StunGunItem.getCapacitorCharge(stack)==config.stungunChargeTicks) {
                        return 1.0F;
                    }
                    return 0.0F;
                }
        );

        registerPredicateProvider(
                HazmatChestPiece.class,
                new Identifier("gearreborn:charged"),
                (item, stack, world, entity, seed) -> {
                    if (!stack.isEmpty() && HazmatAirUtil.hasAir(stack)) {
                        return 1.0F;
                    }
                    return 0.0F;
                });

    };

    // Code lifted from TR's TechRebornClient class
    // Available at: https://github.com/TechReborn/TechReborn/blob/aa4abdfdb5c75568c5c8e9af3320d770e488868b/src/client/java/techreborn/TechRebornClient.java#L227C1-L227C1

    private static <T extends Item> void registerPredicateProvider(Class<T> itemClass, Identifier identifier, ItemModelPredicateProvider modelPredicateProvider) {
        Registries.ITEM.stream()
                .filter(item -> item.getClass().isAssignableFrom(itemClass))
                .forEach(item -> ModelPredicateProviderRegistry.register(item, identifier, modelPredicateProvider));
    }

    private interface ItemModelPredicateProvider<T extends Item> extends ClampedModelPredicateProvider {

        float call(T item, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed);

        @Override
        default float unclampedCall(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
            return call((T) stack.getItem(), stack, world, entity, seed);
        }

    }

    // End of lifted code
}
