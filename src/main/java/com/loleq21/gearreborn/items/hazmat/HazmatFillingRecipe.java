package com.loleq21.gearreborn.items.hazmat;

import com.loleq21.gearreborn.GRContent;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import techreborn.init.ModFluids;
import techreborn.init.TRContent;

public class HazmatFillingRecipe extends SpecialCraftingRecipe {

    public HazmatFillingRecipe(Identifier identifier, CraftingRecipeCategory craftingRecipeCategory) {
        super(identifier, craftingRecipeCategory);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        boolean bl = false, bl1 = false;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack iteratedStack = inventory.getStack(i);
            if (iteratedStack.isEmpty()) continue;
            if (iteratedStack.isOf(GRContent.HAZMAT_CHESTPIECE) && HazmatUtil.getStoredAir(iteratedStack) < HazmatUtil.getAirCapacity()) {
                if (bl) {
                    return false;
                }
                bl = true;
                continue;
            }
            if ((iteratedStack.getItem() == TRContent.CELL) && TRContent.CELL.getFluid(iteratedStack) == ModFluids.COMPRESSED_AIR.getFluid()) {
                if (bl1) {
                    return false;
                }
                bl1 = true;
                continue;
            }
            return false;
        }
        return (bl && bl1);
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack itemStack1 = ItemStack.EMPTY;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.isEmpty()) continue;

            if (itemStack.getItem() instanceof HazmatChestPiece hazmatChestPiece) {
                itemStack1 = itemStack.copy();
                HazmatUtil.fillAir(itemStack1);
            }
        }
        return itemStack1;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventory) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
        for (int i = 0; i < defaultedList.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if ((stack.getItem() == TRContent.CELL) && TRContent.CELL.getFluid(stack) == ModFluids.COMPRESSED_AIR.getFluid()) {
                defaultedList.set(i, new ItemStack(TRContent.CELL));
            }
        }
        return defaultedList;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GRContent.CRAFTING_HAZMAT_FILL;
    }
}
