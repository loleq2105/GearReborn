package dev.loleq21.gearreborn;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import reborncore.common.util.ChatUtils;

import java.util.List;

public class GRItemUtils {
    public GRItemUtils() {
    }

    public static boolean isActive(ItemStack stack) {
        return !stack.isEmpty() && stack.getNbt() != null && stack.getNbt().getBoolean("isActive");
    }

    public static void switchActive(ItemStack stack, boolean isClient, int messageId, String messageTranslationKey) {
        if (!isActive(stack)) {
            stack.getOrCreateNbt().putBoolean("isActive", true);
            if (isClient) {
                ChatUtils.sendNoSpamMessages(messageId, (new TranslatableText(messageTranslationKey).formatted(Formatting.GRAY).append(" ").append(new TranslatableText("gearreborn.misc.deviceon").formatted(Formatting.GOLD))));
            }
        } else {
            stack.getOrCreateNbt().putBoolean("isActive", false);
            if (isClient) {
                ChatUtils.sendNoSpamMessages(messageId, (new TranslatableText(messageTranslationKey).formatted(Formatting.GRAY).append(" ").append(new TranslatableText("gearreborn.misc.deviceoff").formatted(Formatting.GOLD))));
            }
        }

    }
    public static void switchActiveNoMsg(ItemStack stack, boolean isClient) {
        if (!isActive(stack)) {
            stack.getOrCreateNbt().putBoolean("isActive", true);
        } else {
            stack.getOrCreateNbt().putBoolean("isActive", false);
        }

    }
    public static void buildActiveTooltip(ItemStack stack, List<Text> tooltip) {
        if (!isActive(stack)) {
            tooltip.add((new TranslatableText("gearreborn.misc.deviceoff").formatted(Formatting.RED)));
        } else {
            tooltip.add((new TranslatableText("gearreborn.misc.deviceon").formatted(Formatting.GREEN)));
        }

    }
}
