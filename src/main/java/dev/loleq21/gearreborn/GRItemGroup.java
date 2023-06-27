package dev.loleq21.gearreborn;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GRItemGroup {
    public static ItemGroup ITEMGROUP = Registry.register(Registries.ITEM_GROUP, new Identifier(GearReborn.MOD_ID, "items"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.gearreborn"))
                    .icon(() -> new ItemStack(GRContent.HAZMAT_HELMET)).entries((displayContext, entries) -> {
                        entries.add(GRContent.HAZMAT_HELMET);
                        entries.add(GRContent.HAZMAT_CHESTPIECE);
                        entries.add(GRContent.HAZMAT_LEGGINGS);
                        entries.add(GRContent.RUBBER_HELMET);
                        entries.add(GRContent.RUBBER_CHESTPLATE);
                        entries.add(GRContent.RUBBER_LEGGINGS);
                        entries.add(GRContent.RUBBER_BOOTS);
                        entries.add(GRContent.NV_GOGGLES);
                        entries.add(GRContent.STUN_GUN);
                        //entries.add(TEBlocks.GUNPOWDER_BARREL);

                    }).build());

    public static void registerItemGroups() {
        // Example of adding to existing Item Group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(GRContent.STUN_GUN);
        });
    }
}
