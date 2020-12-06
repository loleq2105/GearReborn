package dev.loleq21.ag4tr;

import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
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
    public void getTooltip(ItemStack itemStack, TooltipContext tooltipContext, List<Text> tooltipLines) {

        Item item = itemStack.getItem();

        if (!ITEM_ID.computeIfAbsent(item, this::isAg4trItem))
            return;

        if (item instanceof EnergyHolder) {
            LiteralText line1 = new LiteralText(PowerSystem.getLocalizedPowerNoSuffix(Energy.of(itemStack).getEnergy()));
            line1.append("/");
            line1.append(PowerSystem.getLocalizedPower(Energy.of(itemStack).getMaxStored()));
            line1.formatted(Formatting.GOLD);

            tooltipLines.add(1, line1);

            if (Screen.hasShiftDown()) {
                int percentage = percentage(Energy.of(itemStack).getMaxStored(), Energy.of(itemStack).getEnergy());
                MutableText line2 = StringUtils.getPercentageText(percentage);
                line2.append(" ");
                line2.formatted(Formatting.GRAY);
                line2.append(I18n.translate("reborncore.gui.tooltip.power_charged"));
                tooltipLines.add(2, line2);

                double inputRate = ((EnergyHolder) item).getMaxInput(EnergySide.UNKNOWN);
                double outputRate = ((EnergyHolder) item).getMaxOutput(EnergySide.UNKNOWN);
                LiteralText line3 = new LiteralText("");
                if (inputRate != 0 && inputRate == outputRate) {
                    line3.append(I18n.translate("techreborn.tooltip.transferRate"));
                    line3.append(" : ");
                    line3.formatted(Formatting.GRAY);
                    line3.append(PowerSystem.getLocalizedPower(inputRate));
                    line3.formatted(Formatting.GOLD);
                } else if (inputRate != 0) {
                    line3.append(I18n.translate("reborncore.tooltip.energy.inputRate"));
                    line3.append(" : ");
                    line3.formatted(Formatting.GRAY);
                    line3.append(PowerSystem.getLocalizedPower(inputRate));
                    line3.formatted(Formatting.GOLD);
                } else if (outputRate != 0) {
                    line3.append(I18n.translate("reborncore.tooltip.energy.outputRate"));
                    line3.append(" : ");
                    line3.formatted(Formatting.GRAY);
                    line3.append(PowerSystem.getLocalizedPower(outputRate));
                    line3.formatted(Formatting.GOLD);
                }
                tooltipLines.add(3, line3);
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
