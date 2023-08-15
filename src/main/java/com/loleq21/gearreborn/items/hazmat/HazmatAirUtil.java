package com.loleq21.gearreborn.items.hazmat;

import com.loleq21.gearreborn.GRContent;
import com.loleq21.gearreborn.GearReborn;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

import static com.loleq21.gearreborn.GRConfig.CONFIG;

public class HazmatAirUtil {

    /**
     <p>Largely adapted code from {@link team.reborn.energy.api.base.SimpleBatteryItem} for the purpose of handling the air level of the hazmat suit</p>
     <p>An important distinction from how I handle air versus the interface above: <br>
     An item is not considered empty as soon as its air level reaches 0, but rather when its air sub-NBT tag is missing.</p>

     */

    public static final String AIR_KEY = "air";

    public static ItemStack getChestpiece(LivingEntity entity) {
        for (ItemStack itemStack : entity.getArmorItems())
            if (!itemStack.isEmpty() && itemStack.isOf(GRContent.HAZMAT_CHESTPIECE))
                return itemStack;
        return ItemStack.EMPTY;
    }

    public static boolean hasAir(ItemStack stack){
        return hasAir(stack.getNbt());
    }

    public static boolean hasAir(NbtCompound nbt){
        return getStoredAir(nbt) >= 0;
    }

    public static void emptyAir(ItemStack stack){
        stack.removeSubNbt(AIR_KEY);
    }

    public static void fillAir(ItemStack stack){
        setStoredAir(stack, getAirCapacity());
    }

    public static boolean tryUseAir(ItemStack stack, int amount) {
        int newAmount = getStoredAir(stack) - amount;

        if (newAmount < 0) {
            return false;
        } else {
            setStoredAir(stack, newAmount);
            return true;
        }
    }

    public static void setStoredAir(ItemStack stack, int newAmount) {
        if (newAmount < 0) {
            // This side of the if statement isn't actually used in favor of the emptyAir method above, it's more of a safeguard
            stack.removeSubNbt(AIR_KEY);
        } else {
            stack.getOrCreateNbt().putInt(AIR_KEY, newAmount);
        }
    }

    public static int getStoredAir(ItemStack stack) {
        return getStoredAir(stack.getNbt());
    }

    public static int getStoredAir(@Nullable NbtCompound nbt) {
        return (nbt != null && nbt.contains(AIR_KEY)) ? nbt.getInt(AIR_KEY) : -1; //return -1, not the default 0, when the air NBT tag is absent
        /*
          the NbtCompound is additionally checked for containing the air tag check due
          to the fact that damageable armor always has NBT
        */
    }

    public static int getAirCapacity() {
        return CONFIG.hazmatChestpieceAirTicksCapacity;
    }

}
