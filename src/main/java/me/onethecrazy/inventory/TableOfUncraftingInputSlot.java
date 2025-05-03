package me.onethecrazy.inventory;

import me.onethecrazy.TableOfUncrafting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TableOfUncraftingInputSlot extends Slot {
    private final Inventory output;
    private final World world;

    public TableOfUncraftingInputSlot(Inventory inventory, Inventory outputInventory, int index, int x, int y, World world) {
        super(inventory, index, x, y);
        this.output = outputInventory;
        this.world = world;
    }

    @Override public boolean canInsert(ItemStack stack) { return true; }

    @Override
    public ItemStack takeStack(int amount) {
        ItemStack stack = this.inventory.removeStack(0, amount);
        updateResult(this.inventory, this.output);

        return stack;
    }


    @Override
    public void setStack(ItemStack stack, ItemStack previousStack){
        this.setStackNoCallbacks(stack);
        updateResult(this.inventory, this.output);
    }

    protected void updateResult(Inventory input, Inventory output){
        Item source = input.getStack(0).getItem();

        if(source.getDefaultStack().getItem() == Items.AIR)
            output.clear();

        List<RecipeEntry<CraftingRecipe>> recipes =
                world.getRecipeManager()
                        .listAllOfType(RecipeType.CRAFTING)
                        .stream()
                        .filter(r -> r.value().getResult(world.getRegistryManager()).isOf(source))
                        .toList();

        // Not craftable
        if(recipes.isEmpty())
            return;

        CraftingRecipe recipe = recipes.getFirst().value();

        if (recipe instanceof ShapedRecipe shaped) {
            int width  = shaped.getWidth();
            int height = shaped.getHeight();

            DefaultedList<Ingredient> list = shaped.getIngredients();

            List<Item> itemTypes = new ArrayList<>();

            for (int row = 0; row < height; ++row) {
                for (int col = 0; col < width; ++col) {

                    int index = col + (row * width);
                    int slotIndex = col + (row * 3);

                    Ingredient ingredient = list.get(index);

                    // No stack available (EMPTY ingredient)
                    if(ingredient.getMatchingStacks().length == 0)
                        continue;

                    ItemStack stack = ingredient.getMatchingStacks()[0];

                    if(!itemTypes.contains(stack.getItem()))
                        itemTypes.add(stack.getItem());

                    // Create new Stack since taking the old Stack ref results in unwanted behaviour
                    output.setStack(slotIndex, stack.getItem().getDefaultStack());
                }
            }

            // Remove basic duplication where input is contained in output (e.g. trims)
            Set<Item> set = Set.of(input.getStack(0).getItem());
            if(output.containsAny(set)){
                output.clear();
            }

            // If we have a shaped recipe with only one ingredient, only show if input has enough of that ingredient (e.g. glass panes)
            if(itemTypes.stream().count() == 1 && input.getStack(0).getCount() < shaped.getResult(world.getRegistryManager()).getCount()){
                output.clear();
            }

        } else if (recipe instanceof ShapelessRecipe shapeless) {
            // Too few items
            if(input.getStack(0).getCount() < shapeless.getResult(world.getRegistryManager()).getCount())
                return;

            DefaultedList<Ingredient> list = shapeless.getIngredients();
            for(int i = 0; i < list.size(); i++){
                ItemStack stack = list.get(i).getMatchingStacks()[0];

                // Create new Stack since taking the old Stack ref results in unwanted behaviour
                output.setStack(i, stack.getItem().getDefaultStack());
            }
        }
    }
}
