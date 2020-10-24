package dev.loleq21.ag4tr;

import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import reborncore.common.util.ChatUtils;
import team.reborn.energy.Energy;

import java.util.List;

public class Ag4trItemUtils {
    public Ag4trItemUtils() {
    }

    public static boolean isActive(ItemStack stack) {
        return !stack.isEmpty() && stack.getTag() != null && stack.getTag().getBoolean("isActive");
    }

    public static void switchActive(ItemStack stack, boolean isClient, int messageId, String onMessage, String offMessage) {
        if (!isActive(stack)) {
            stack.getOrCreateTag().putBoolean("isActive", true);
            if (isClient) {
                ChatUtils.sendNoSpamMessages(messageId, (new LiteralText(onMessage)));
            }
        } else {
            stack.getOrCreateTag().putBoolean("isActive", false);
            if (isClient) {
                ChatUtils.sendNoSpamMessages(messageId, (new LiteralText(offMessage)));
            }
        }

    }
    public static void switchActiveNoMsg(ItemStack stack, boolean isClient) {
        if (!isActive(stack)) {
            stack.getOrCreateTag().putBoolean("isActive", true);
        } else {
            stack.getOrCreateTag().putBoolean("isActive", false);
        }

    }
    public static void buildActiveTooltip(ItemStack stack, List<Text> tooltip, String onToolTip, String offToolTip) {
        if (!isActive(stack)) {
            tooltip.add((new LiteralText(offToolTip).formatted(Formatting.GRAY)));
        } else {
            tooltip.add((new LiteralText(onToolTip).formatted(Formatting.GRAY)));
        }

    }
}
