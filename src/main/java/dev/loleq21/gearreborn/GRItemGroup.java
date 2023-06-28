package dev.loleq21.gearreborn;

import dev.loleq21.gearreborn.items.hazmat.HazmatChestPiece;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
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

                        // Filled hazmat suit creative tab
                        ItemStack airedHC = new ItemStack(GRContent.HAZMAT_CHESTPIECE);
                        GRConfig config = new GRConfig();
                        HazmatChestPiece.setStoredAir(airedHC, config.hazmatChestpieceAirTicksCapacity);

                        entries.add(airedHC);
                        entries.add(GRContent.RUBBER_BOOTS);
                        entries.add(GRContent.HAZMAT_LEGGINGS);
                        entries.add(GRContent.RUBBER_HELMET);
                        entries.add(GRContent.RUBBER_CHESTPLATE);
                        entries.add(GRContent.RUBBER_LEGGINGS);
                        entries.add(GRContent.NV_GOGGLES);

                        // Adding charged items to the creative tab
                        ItemStack chargedNVG = new ItemStack(GRContent.NV_GOGGLES);
                        RcEnergyItem energyNVG = (RcEnergyItem)GRContent.NV_GOGGLES;
                        energyNVG.setStoredEnergy(chargedNVG, energyNVG.getEnergyCapacity());
                        entries.add(chargedNVG);

                        // uncharged
                        entries.add(GRContent.STUN_GUN);

                        // charged
                        ItemStack chargedSG = new ItemStack(GRContent.STUN_GUN);
                        RcEnergyItem energySG = (RcEnergyItem)GRContent.STUN_GUN;
                        energySG.setStoredEnergy(chargedSG, energySG.getEnergyCapacity());
                        entries.add(chargedSG);

                        // uncharged
                        entries.add(GRContent.ELECTRIC_HOE);

                        // charged
                        ItemStack chargedEH = new ItemStack(GRContent.ELECTRIC_HOE);
                        RcEnergyItem energyEH = (RcEnergyItem)GRContent.ELECTRIC_HOE;
                        energyEH.setStoredEnergy(chargedEH, energyEH.getEnergyCapacity());
                        entries.add(chargedEH);

                        // uncharged
                        entries.add(GRContent.ELECTRIC_WRENCH);

                        // charged
                        ItemStack chargedEW = new ItemStack(GRContent.ELECTRIC_WRENCH);
                        RcEnergyItem energyEW = (RcEnergyItem)GRContent.ELECTRIC_WRENCH;
                        energyEW.setStoredEnergy(chargedEW, energyEW.getEnergyCapacity());
                        entries.add(chargedEW);
                    }).build());

    public static void registerItemGroups() {
        // Example of adding to existing Item Group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(GRContent.STUN_GUN);
        });
    }
}
