package me;

import me.onethecrazy.TableOfUncrafting;
import me.onethecrazy.blocks.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends FabricRecipeProvider {
    public RecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter recipeExporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TABLE_OF_UNCRAFTING, 1)
                .pattern(" D ")
                .pattern("A#A")
                .pattern("PPP")
                .input('#', Items.CRAFTING_TABLE)
                .input('P', Items.OAK_PLANKS)
                .input('A', ItemTags.AXES)
                .input('D', Items.DIAMOND)
                .showNotification(true)
                .criterion(FabricRecipeProvider.hasItem(Items.CRAFTING_TABLE), FabricRecipeProvider.conditionsFromItem(Items.CRAFTING_TABLE))
                .group("multi_bench") // Put it in a group called "multi_bench" - groups are shown in one slot in the recipe book
                .offerTo(recipeExporter, Identifier.of("tableofuncrafting", "uncrafting_table"));
    }
}

