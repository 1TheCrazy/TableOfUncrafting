package me.onethecrazy.inventory;

import me.onethecrazy.TableOfUncrafting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.screen.slot.Slot;

import java.util.List;

public class TableOfUncraftingOutputSlot extends Slot {
    private final Inventory input;
    public TableOfUncraftingOutputSlot(Inventory inventory, Inventory inputInventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.input = inputInventory;
    }

    @Override public boolean canInsert(ItemStack stack) { return false; }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        super.onTakeItem(player, stack);   // keep vanilla stat/advancement hooks

        // Get the recipe
        Item source = this.input.getStack(0).getItem();

        List<RecipeEntry<CraftingRecipe>> recipes =
                player.getWorld().getRecipeManager()
                        .listAllOfType(RecipeType.CRAFTING)
                        .stream()
                        .filter(r -> r.value().getResult(player.getWorld().getRegistryManager()).isOf(source))
                        .toList();

        CraftingRecipe recipe = recipes.getFirst().value();

        if(recipe instanceof ShapedRecipe shaped){
            // If we have a shaped recipe with only one ingredient, subtract all-together count
            int neededCount = shaped.getResult(player.getWorld().getRegistryManager()).getCount();

            if(this.input.getStack(0).getCount() > 1)
                this.input.getStack(0).setCount(this.input.getStack(0).getCount() - neededCount);
            else
                this.input.setStack(0, ItemStack.EMPTY);
        }
        else if(recipe instanceof ShapelessRecipe shapeless){
            int count = shapeless.getResult(player.getWorld().getRegistryManager()).getCount();

            this.input.getStack(0).setCount(this.input.getStack(0).getCount() - count);
        }
    }

}
