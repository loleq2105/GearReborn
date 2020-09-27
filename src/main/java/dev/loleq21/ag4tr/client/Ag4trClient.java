package dev.loleq21.ag4tr.client;

import dev.loleq21.ag4tr.Ag4trStackToolTipHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.DyeableItem;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
    public class Ag4trClient implements ClientModInitializer {

    public static final KeyBinding NV_KEY_BIND = new KeyBinding("key.ag4tr.toggle_nv",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            "category.ag4tr.title");
    public static final KeyBinding EXOLEGS_JUMP_BOOST_KEY_BIND = new KeyBinding("key.ag4tr.toggle_jb",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "category.ag4tr.title");

    @Override
    public void onInitializeClient() {
        Ag4trStackToolTipHandler.setup();
        KeyBindingHelper.registerKeyBinding(NV_KEY_BIND);
        KeyBindingHelper.registerKeyBinding(EXOLEGS_JUMP_BOOST_KEY_BIND);
    }
}
