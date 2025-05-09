package me.onethecrazy.inventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.recipe.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableOfUncraftingInputSlot extends Slot {
    private final Inventory output;
    private final World world;
    private static final ArrayList<ItemStack> EMPTY_OUTPUT_FIELD = getEmptyOutputField();

    public TableOfUncraftingInputSlot(Inventory inventory, Inventory outputInventory, int index, int x, int y, World world) {
        super(inventory, index, x, y);
        this.output = outputInventory;
        this.world = world;
    }

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
        ItemStack source = input.getStack(0);

        // Get Recipe
        ArrayList<ItemStack> recipeOutput = GetUncraftRecipe(source, this.world);

        for(int i = 0; i < 9; i++){
            output.setStack(i, recipeOutput.get(i));
        }
    }

    protected static ArrayList<ItemStack> GetUncraftRecipe(ItemStack source, World world){
        ArrayList<ItemStack> recipeOut = getEmptyOutputField();

        List<RecipeEntry<CraftingRecipe>> recipes =
                world.getRecipeManager()
                        .listAllOfType(RecipeType.CRAFTING)
                        .stream()
                        .filter(r -> r.value().getResult(world.getRegistryManager()).isOf(source.getItem()))
                        .toList();

        // Not craftable
        if(recipes.isEmpty() && !isEdgeCase(source.getItem())){
            return getEmptyOutputField();
        }
        // Edge Case
        else if(isNetherite(source.getItem()))
            return getNetheriteRecipe(source.getItem());

        CraftingRecipe recipe;
        // Cover cases for ingots
        if(source.getItem() == Items.IRON_INGOT || source.getItem() == Items.COPPER_INGOT || source.getItem() == Items.NETHERITE_INGOT || source.getItem() == Items.GOLD_INGOT ){
            // Show recipe for a block uncraft if we have enough items, otherwise show nugget uncraft
            if(source.getCount() >= 9){
                // Gold Ingots flip the default order of recipe-output
                recipe = source.getItem() == Items.GOLD_INGOT ? recipes.get(1).value() : recipes.getFirst().value();
            }
            else{
                recipe = source.getItem() == Items.GOLD_INGOT ? recipes.getFirst().value() : recipes.get(1).value();
            }
        }
        else{
            recipe = recipes.getFirst().value();
        }


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
                    recipeOut.set(slotIndex, stack.getItem().getDefaultStack());
                }
            }

            // Remove basic duplication where input is contained in output (e.g. trims)
            if(recipeOut.contains(source)){
                return EMPTY_OUTPUT_FIELD;
            }

            // If we have a shaped recipe with only one ingredient, only show if input has enough of that ingredient (e.g. glass panes)
            if(itemTypes.size() == 1 && source.getCount() < shaped.getResult(world.getRegistryManager()).getCount()){
                return EMPTY_OUTPUT_FIELD;
            }

        } else if (recipe instanceof ShapelessRecipe shapeless) {
            // Too few items
            if(source.getCount() < shapeless.getResult(world.getRegistryManager()).getCount())
                return EMPTY_OUTPUT_FIELD;

            DefaultedList<Ingredient> list = shapeless.getIngredients();
            for(int i = 0; i < list.size(); i++){
                ItemStack stack = list.get(i).getMatchingStacks()[0];

                // Create new Stack since taking the old Stack ref results in unwanted behaviour
                recipeOut.set(i, stack.getItem().getDefaultStack());
            }
        }

        return recipeOut;
    }

    private static ArrayList<ItemStack> getNetheriteRecipe(Item source){
        // The Map to map a Netherite Item to a Diamond Item
        Map<Item, ItemStack> NETHERITE_TO_DIAMOND_MAP = new HashMap<>() {{
            put(Items.NETHERITE_SWORD, Items.DIAMOND_SWORD.getDefaultStack());
            put(Items.NETHERITE_PICKAXE, Items.DIAMOND_PICKAXE.getDefaultStack());
            put(Items.NETHERITE_AXE, Items.DIAMOND_AXE.getDefaultStack());
            put(Items.NETHERITE_SHOVEL, Items.DIAMOND_SHOVEL.getDefaultStack());
            put(Items.NETHERITE_HOE, Items.DIAMOND_HOE.getDefaultStack());
            put(Items.NETHERITE_HELMET, Items.DIAMOND_HELMET.getDefaultStack());
            put(Items.NETHERITE_CHESTPLATE, Items.DIAMOND_CHESTPLATE.getDefaultStack());
            put(Items.NETHERITE_LEGGINGS, Items.DIAMOND_LEGGINGS.getDefaultStack());
            put(Items.NETHERITE_BOOTS, Items.DIAMOND_BOOTS.getDefaultStack());
        }};

        ArrayList<ItemStack> out = getEmptyOutputField();

        out.set(4, NETHERITE_TO_DIAMOND_MAP.get(source));
        out.set(1, Items.NETHERITE_INGOT.getDefaultStack());

        return out;
    }

    private static boolean isNetherite(Item item) {
        if (item instanceof SwordItem sword) {
            return sword.getMaterial() == ToolMaterials.NETHERITE;
        }
        if (item instanceof net.minecraft.item.ToolItem tool) {
            return tool.getMaterial() == ToolMaterials.NETHERITE;
        }
        if (item instanceof ArmorItem armor) {
            return armor.getMaterial() == ArmorMaterials.NETHERITE;
        }
        return false;
    }

    public static boolean isEdgeCase(Item source){
        return isNetherite(source);
    }

    private static ArrayList<ItemStack> getEmptyOutputField(){
        ArrayList<ItemStack> list = new ArrayList<>();

        for(int i = 0; i < 9; i++) {
            list.add(Items.AIR.getDefaultStack());
        }

        return list;
    }
}
