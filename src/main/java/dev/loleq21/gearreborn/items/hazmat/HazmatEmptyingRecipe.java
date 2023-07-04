package dev.loleq21.gearreborn.items.hazmat;

import dev.loleq21.gearreborn.GRContent;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class HazmatEmptyingRecipe extends SpecialCraftingRecipe
{

    public HazmatEmptyingRecipe(Identifier identifier, CraftingRecipeCategory craftingRecipeCategory) {
        super(identifier, craftingRecipeCategory);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        boolean bl = false;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.isEmpty()) continue;
            if (itemStack.isOf(GRContent.HAZMAT_CHESTPIECE) && HazmatChestPiece.getStoredAir(itemStack)>0){
                if (bl) {
                    return false;
                }
                bl = true;
                continue;
            }
            return false;
        }
        return true;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack itemStack1 = ItemStack.EMPTY;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.isEmpty()) continue;

            itemStack1 = itemStack.copy();
            HazmatChestPiece.setStoredAir(itemStack1, 0);

        }
        return itemStack1;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GRContent.CRAFTING_HAZMAT_EMPTY;
    }
}
