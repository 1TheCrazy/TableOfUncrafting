package me.onethecrazy.inventory;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import me.onethecrazy.TableOfUncrafting;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

import java.util.List;

public class TableOfUncraftingOutputSlot extends Slot {
    private final Inventory input;
    private final World world;
    private final ScreenHandlerContext ctx;

    public TableOfUncraftingOutputSlot(Inventory inventory, Inventory inputInventory, ScreenHandlerContext context, World world, int index, int x, int y) {
        super(inventory, index, x, y);
        this.input = inputInventory;
        this.world = world;
        this.ctx = context;
    }

    @Override public boolean canInsert(ItemStack stack) { return false; }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        super.onTakeItem(player, stack);   // keep vanilla stat/advancement hooks

        // Spawn experience if the item is enchanted
        if(this.input.getStack(0).hasEnchantments()){
            this.ctx.run((world, pos) -> {
                if (world instanceof ServerWorld) {
                    ExperienceOrbEntity.spawn((ServerWorld)world, Vec3d.ofCenter(pos), this.getExperience(world));
                }

                world.syncWorldEvent(WorldEvents.GRINDSTONE_USED, pos, 0);
            });
        }

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

    // Stolen from Grindstone
    private int getExperience(World world) {
        int i = 0;
        i += this.getExperience(this.input.getStack(0));
        if (i > 0) {
            int j = (int)Math.ceil(i / 2.0);
            return j + world.random.nextInt(j);
        } else {
            return 0;
        }
    }

    private int getExperience(ItemStack stack) {
        int i = 0;
        ItemEnchantmentsComponent itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(stack);

        for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : itemEnchantmentsComponent.getEnchantmentEntries()) {
            RegistryEntry<Enchantment> registryEntry = (RegistryEntry<Enchantment>)entry.getKey();
            int j = entry.getIntValue();
            if (!registryEntry.isIn(EnchantmentTags.CURSE)) {
                i += registryEntry.value().getMinPower(j);
            }
        }

        return i;
    }
}
