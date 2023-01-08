package dev.loleq21.gearreborn;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class GRItemUtils {

    public static boolean isActive(ItemStack stack) {
        return !stack.isEmpty() && stack.getNbt() != null && stack.getNbt().getBoolean("isActive");
    }

    public static void switchActive(ItemStack stack, boolean isClient, int messageId, String messageTranslationKey, Entity entity) {
        if (!isActive(stack)) {
            stack.getOrCreateNbt().putBoolean("isActive", true);
            if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
                serverPlayerEntity.sendMessage((Text.translatable(messageTranslationKey).formatted(Formatting.GRAY).append(" ").append(Text.translatable("gearreborn.misc.deviceon").formatted(Formatting.GOLD))));
            }
        } else {
            stack.getOrCreateNbt().putBoolean("isActive", false);
            if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
                serverPlayerEntity.sendMessage((Text.translatable(messageTranslationKey).formatted(Formatting.GRAY).append(" ").append(Text.translatable("gearreborn.misc.deviceoff").formatted(Formatting.GOLD))));
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
            tooltip.add((Text.translatable("gearreborn.misc.deviceoff").formatted(Formatting.RED)));
        } else {
            tooltip.add((Text.translatable("gearreborn.misc.deviceon").formatted(Formatting.GREEN)));
        }

    }
}
