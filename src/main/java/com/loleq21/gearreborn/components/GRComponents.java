package com.loleq21.gearreborn.components;

import com.loleq21.gearreborn.GearReborn;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.util.Identifier;

public class GRComponents implements EntityComponentInitializer {

    public static final ComponentKey<HazmatComponent> HAZMAT_COMPONENT_KEY =
            ComponentRegistry.getOrCreate(new Identifier(GearReborn.MOD_ID, "hazmat"), HazmatComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(HAZMAT_COMPONENT_KEY, HazmatComponent::new, RespawnCopyStrategy.LOSSLESS_ONLY);
    }

}
