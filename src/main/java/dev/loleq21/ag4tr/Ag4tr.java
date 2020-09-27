package dev.loleq21.ag4tr;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.ArmorMaterial;

public class Ag4tr implements ModInitializer {

    public static final String MOD_ID = "ag4tr";


    @Override
    public void onInitialize() {

        Ag4trContent.registerAg4trContent();

    }
}
