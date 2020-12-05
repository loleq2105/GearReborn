package dev.loleq21.ag4tr;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
//import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
//import techreborn.api.events.CableElectrocutionEvent;

public class Ag4tr implements ModInitializer {

    public static final String MOD_ID = "ag4tr";
    public static final ItemGroup AG4TR_GROUP = FabricItemGroupBuilder.create(new Identifier(MOD_ID, "items")).icon(() -> new ItemStack(Ag4trContent.RHM_HELMET)).build();


    @Override
    public void onInitialize() {
        AutoConfig.register(Ag4trConfig.class, GsonConfigSerializer::new);
        Ag4trContent.registerAg4trContent();
        /*CableElectrocutionEvent.EVENT.register((livingEntity, cableType, blockPos, world, cableBlockEntity) -> {
            if(livingEntity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) livingEntity;
                if (HazmatSuitUtils.playerIsWearingFullHazmat(player)) {
                    return false;
                }
            }
                return true;
        });*/
    }
}


