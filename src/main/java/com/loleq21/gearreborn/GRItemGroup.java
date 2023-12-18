package com.loleq21.gearreborn;

import com.loleq21.gearreborn.items.hazmat.HazmatUtil;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import reborncore.common.powerSystem.RcEnergyItem;

public class GRItemGroup {
    public static ItemGroup ITEMGROUP = Registry.register(Registries.ITEM_GROUP, new Identifier(GearReborn.MOD_ID, "items"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.gearreborn.items"))
                    .icon(() -> new ItemStack(GRContent.HAZMAT_HELMET)).entries((displayContext, entries) -> {
                        entries.add(GRContent.HAZMAT_HELMET);
                        entries.add(GRContent.HAZMAT_CHESTPIECE);
                        addAired(entries, GRContent.HAZMAT_CHESTPIECE);
                        entries.add(GRContent.RUBBER_BOOTS);
                        entries.add(GRContent.HAZMAT_LEGGINGS);
                        entries.add(GRContent.RUBBER_HELMET);
                        entries.add(GRContent.RUBBER_CHESTPLATE);
                        entries.add(GRContent.RUBBER_LEGGINGS);
                        entries.add(GRContent.NV_GOGGLES);
                        addPowered(entries, GRContent.NV_GOGGLES);
                        entries.add(GRContent.STUN_GUN);
                        addPowered(entries, GRContent.STUN_GUN);
                        entries.add(GRContent.ELECTRIC_HOE);
                        addPowered(entries, GRContent.ELECTRIC_HOE);
                        entries.add(GRContent.ELECTRIC_WRENCH);
                        addPowered(entries, GRContent.ELECTRIC_WRENCH);

                    }).build());

    public static void registerItemGroups() {
        // Example of adding to existing Item Group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> entries.add(GRContent.STUN_GUN));
    }

    private static void addPowered(ItemGroup.Entries entries, Item item) {
        ItemStack charged = new ItemStack(item);
        RcEnergyItem energyItem = (RcEnergyItem)item;
        energyItem.setStoredEnergy(charged, energyItem.getEnergyCapacity());
        entries.add(charged);
    }

    private static void addAired(ItemGroup.Entries entries, Item item) {
        ItemStack aired = new ItemStack(item);
        GRConfig config = new GRConfig();
        HazmatUtil.setStoredAir(aired, config.hazmatChestpieceAirTicksCapacity);
        entries.add(aired);
    }
}
