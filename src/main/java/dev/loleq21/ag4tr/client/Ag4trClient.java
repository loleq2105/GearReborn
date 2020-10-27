package dev.loleq21.ag4tr.client;

import dev.loleq21.ag4tr.Ag4trStackToolTipHandler;
import dev.loleq21.ag4tr.ArcLighterItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.options.KeyBinding;
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
import techreborn.TechRebornClient;

@Environment(EnvType.CLIENT)
    public class Ag4trClient implements ClientModInitializer {

    public static final KeyBinding NV_KEY_BIND = new KeyBinding("key.ag4tr.toggle_nv",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            "category.ag4tr.title");

    @Override
    public void onInitializeClient() {
        Ag4trStackToolTipHandler.setup();
        KeyBindingHelper.registerKeyBinding(NV_KEY_BIND);
        registerPredicateProvider(
                ArcLighterItem.class,
                new Identifier("ag4tr:inactive"),
                (item, stack, world, entity) -> {
                    if (!stack.isEmpty() && ItemUtils.isActive(stack) && Energy.of(stack).getEnergy()>=ArcLighterItem.IGNITE_COST) {
                        return 1.0F;
                    }
                    return 0.0F;
                }
        );
    }

   //whatever this does

    private static <T extends Item> void registerPredicateProvider(Class<T> itemClass, Identifier identifier, ItemModelPredicateProvider<T> modelPredicateProvider) {
        Registry.ITEM.stream()
                .filter(item -> item.getClass().isAssignableFrom(itemClass))
                .forEach(item -> AccessorModelPredicateProviderRegistry.callRegister(item, identifier, modelPredicateProvider));
    }

    //Need the item instance in a few places, this makes it easier
    private interface ItemModelPredicateProvider<T extends Item> extends ModelPredicateProvider {

        float call(T item, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity);

        @Override
        default float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
            return call((T) stack.getItem(), stack, world, entity);
        }

    }
}
