package dev.loleq21.gearreborn;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import reborncore.common.util.ItemUtils;
import reborncore.mixin.client.AccessorModelPredicateProviderRegistry;
import team.reborn.energy.Energy;

@Environment(EnvType.CLIENT)
public class GearRebornClient implements ClientModInitializer {

    public static final KeyBinding NV_KEY_BIND = new KeyBinding("key.gearreborn.toggle_nv",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            "category.geareborn.title");

    @Override
    public void onInitializeClient() {

        KeyBindingHelper.registerKeyBinding(NV_KEY_BIND);
        //predicates for animated textures
        GRConfig config = AutoConfig.getConfigHolder(GRConfig.class).getConfig();
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
                    if (!stack.isEmpty() && Energy.of(stack).getEnergy()>=config.hazmatChestpieceLavaCoolingEnergyCost *2) {
                        return 1.0F;
                    }
                    return 0.0F;
                }
        );

    }

    private static <T extends Item> void registerPredicateProvider(Class<T> itemClass, Identifier identifier, ItemModelPredicateProvider<T> modelPredicateProvider) {
        Registry.ITEM.stream()
                .filter(item -> item.getClass().isAssignableFrom(itemClass))
                .forEach(item -> AccessorModelPredicateProviderRegistry.callRegister(item, identifier, modelPredicateProvider));
    }

    private interface ItemModelPredicateProvider<T extends Item> extends UnclampedModelPredicateProvider {

        float call(T item, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed);

        @Override
        default float unclampedCall(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
            return call((T) stack.getItem(), stack, world, entity, seed);
        }

    }
}
