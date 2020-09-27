package dev.loleq21.ag4tr;

import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.util.StringUtils;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHolder;
import team.reborn.energy.EnergySide;

import java.util.List;
import java.util.Map;

public class Ag4trStackToolTipHandler implements ItemTooltipCallback {

    public static final Map<Item, Boolean> ITEM_ID = Maps.newHashMap();

    public static void setup() {
        ItemTooltipCallback.EVENT.register(new Ag4trStackToolTipHandler());
    }

    @Override
    public void getTooltip(ItemStack itemStack, TooltipContext tooltipContext, List<Text> list) {

        Item item = itemStack.getItem();

        if (!ITEM_ID.computeIfAbsent(item, this::isAg4trItem))
            return;

            if (itemStack.getItem() instanceof EnergyHolder) {
            LiteralText line1 = new LiteralText(PowerSystem.getLocaliszedPowerFormattedNoSuffix(Energy.of(itemStack).getEnergy()));
            line1.append("/");
            line1.append(PowerSystem.getLocaliszedPowerFormattedNoSuffix(Energy.of(itemStack).getMaxStored()));
            line1.append(" ");
            line1.append(PowerSystem.getDisplayPower().abbreviation);
            line1.formatted(Formatting.GOLD);

            list.add(1, line1);

            if (Screen.hasShiftDown()) {
                int percentage = percentage(Energy.of(itemStack).getMaxStored(), Energy.of(itemStack).getEnergy());
                Formatting color = StringUtils.getPercentageColour(percentage);
                list.add(2, new LiteralText(color + "" + percentage + "%" + Formatting.GRAY + " Charged"));
                // TODO: show both input and output rates
                list.add(3, new LiteralText(Formatting.GRAY + "I/O Rate: " + Formatting.GOLD + PowerSystem.getLocaliszedPowerFormatted(((EnergyHolder) item).getMaxInput(EnergySide.UNKNOWN))));
            }
        }
        }

    private boolean isAg4trItem(Item item) {
        return Registry.ITEM.getId(item).getNamespace().equals("ag4tr");
    }

    public int percentage(int MaxValue, int CurrentValue) {
        if (CurrentValue == 0)
            return 0;
        return (int) ((CurrentValue * 100.0f) / MaxValue);
    }

    public int percentage(double MaxValue, double CurrentValue) {
        if (CurrentValue == 0)
            return 0;
        return (int) ((CurrentValue * 100.0f) / MaxValue);
    }

}

